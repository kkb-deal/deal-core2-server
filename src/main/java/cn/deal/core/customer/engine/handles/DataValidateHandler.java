package cn.deal.core.customer.engine.handles;

import cn.deal.component.domain.CustomerDomainEvent;
import cn.deal.component.exception.BusinessException;
import cn.deal.component.utils.ParalleUtils;
import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.engine.interfaces.CustomerContext;
import cn.deal.core.customer.engine.interfaces.CustomerPropValidator;
import cn.deal.core.customer.engine.interfaces.ErrorCodes;
import cn.deal.core.customer.engine.validators.*;
import cn.deal.core.meta.domain.CustomerMetaData;
import cn.deal.core.meta.service.CustomerMetaDataService;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * 客户表单数据校验
 */
@Component
public class DataValidateHandler {

    private Logger logger = LoggerFactory.getLogger(DataValidateHandler.class);

    @Autowired
    private SexValidator sexValidator;

    @Autowired
    private EmailValidator emailValidator;

    @Autowired
    private DateValidator dateValidator;

    @Autowired
    private PhoneValidator phoneValidator;

    @Autowired
    private FixedPhoneValidator fixedPhoneValidator;

    @Autowired
    private AgeStateValidator ageStateValidator;

    @Autowired
    private GroupValidator groupValidator;

    @Autowired
    private OwnerValidator ownerValidator;

    @Autowired
    private AppMemberValidator appMemberValidator;

    private Map<String, CustomerPropValidator> validators = Maps.newHashMap();

    @Autowired
    private CustomerMetaDataService customerMetaDataService;


    @PostConstruct
    public void init() {
        validators.put("sex", sexValidator);
        validators.put("date", dateValidator);
        validators.put("email", emailValidator);
        validators.put("phone", phoneValidator);
        validators.put("fixedPhone", fixedPhoneValidator);
        validators.put("ageState", ageStateValidator);
        validators.put("group", groupValidator);
        validators.put("owner", ownerValidator);
        validators.put("appmember", appMemberValidator);
    }

    /**
     * 注册验证器
     *
     * @param type 类型
     * @param validator 校验器
     */
    public void register(String type, CustomerPropValidator validator) {
        if (validator != null) {
            validators.put(type, validator);
        }
    }

    public void handle(String appId, Map<String, String> data, CustomerContext ctx, Map<String, Object> opts) {
        // 检查是否需要验证
        if (!needCheck(appId, data, ctx)) {
            return;
        }

        try {
            List<CustomerMetaData> metaDatas = customerMetaDataService.getCustomerMetas(appId);
            ParalleUtils.forEach(metaDatas, metaData -> {
                this.validateMetaItem(appId, data, metaData, ctx, opts);
            });

            // 保存客户元数据到客户上下文
            ctx.put("metaDatas", metaDatas);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("exception:{}", e);
            throw new BusinessException("data_validate_error", e.getMessage(), e);
        }
    }

    private boolean needCheck(String appId, Map<String, String> data, CustomerContext ctx) {
        // 只有手动创建和批量导入客户需要校验
        String createWay = data.get("create_way");
        if (StringUtils.isNotBlank(createWay)) {
            return Customer.CreateWay.SINGLE_CREATE.getValue() == Integer.parseInt(createWay)
                    || Customer.CreateWay.BATCH_IMPORT.getValue() == Integer.parseInt(createWay);
        }

        return false;
    }

    private void validateMetaItem(String appId, Map<String, String> data, CustomerMetaData meta, CustomerContext ctx, Map<String, Object> opts) {
        // 如果不可见，忽略
        if (!meta.getVisiable()) {
            return;
        }

        String ope = ctx.getOpe();
        String propName = meta.getName();
        String value = data.get(propName);

        // 必填验证，仅创建客户时校验
        if (meta.getRequired() && CustomerDomainEvent.CREATE.endsWith(ope)) {
            if (StringUtils.isBlank(value)) {
                throw new BusinessException(propName + ErrorCodes.SUFFIX_REQUIRED, meta.getTitle() + "必填");
            }
        }

        // 如果有值
        if (StringUtils.isNotBlank(value)) {
            // 只读校验，仅修改客户时校验
            if (!"3".equals(opts.get("updateCustomer")) && CustomerMetaData.TRUE.equals(meta.getReadonly()) && CustomerDomainEvent.UPDATE.equals(ope)) {
                Customer customer = ctx.getOriginCustomer();
                if (customer.isChanged(propName, value)) {
                    throw new BusinessException(propName + ErrorCodes.SUFFIX_READONLY, meta.getTitle() + "只读，不能修改");
                }
            }

            // 校验格式
            CustomerPropValidator validator = this.validators.get(meta.getType());
            if (validator != null && !validator.isValid(value, ctx)) {
                throw new BusinessException(propName + ErrorCodes.SUFFIX_INVALID, meta.getTitle() + "格式不正确");
            }
        }
    }

}
