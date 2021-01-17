package cn.deal.core.customer.engine.handles;

import cn.deal.component.domain.CustomerDomainEvent;
import cn.deal.component.exception.BusinessException;
import cn.deal.component.utils.BeanUtil;
import cn.deal.component.utils.JsonUtil;
import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.engine.interfaces.CustomerContext;
import cn.deal.core.customer.engine.interfaces.CustomerPropTrigger;
import cn.deal.core.customer.engine.interfaces.ErrorCodes;
import cn.deal.core.customer.service.CustomerService;
import cn.deal.core.meta.domain.CustomerMetaData;
import cn.deal.core.meta.service.CustomerMetaDataService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 客户数据构建
 */
@Component
public class CustomerBuildHandler {

	private Logger logger = LoggerFactory.getLogger(CustomerBuildHandler.class);
	
	@Autowired
	private CustomerMetaDataService customerMetaDataService;

	@Autowired
	private CustomerService customerService;
	
	private List<CustomerPropTrigger> triggers = new ArrayList<CustomerPropTrigger>();
	private Map<String, String> specialKeys = new HashMap<String, String>();
	
	@PostConstruct
	public void init() {
		specialKeys.put("isOfficialAccountFans", "is_officialaccount_fans");
	}
	
	public void registerTrigger(CustomerPropTrigger trigger) {
		this.triggers.add(trigger);
	}
	
	public Customer handle(String appId, Customer cus, Map<String, String> data, CustomerContext ctx) {
		String ope = ctx.getOpe();
		
		if (CustomerDomainEvent.CREATE.equals(ope)) {
			cus = new Customer();

			handleBaseCustomerData(cus, data);

			cus.setAppId(appId);
			cus.setStatus(1);
			cus.setCreatedAt(new Date());
			cus.setUpdatedAt(new Date());

			//手动创建和批量导入时promoterId值设置为当前操作的kuickUserId
			if (StringUtils.isBlank(cus.getPromoterId()) && isSimpleCreateWay(cus, data)) {
				cus.setPromoterId(ctx.getKuickUserId());
			}
			
			// 处理头像
			handleHeadportraitUrl(cus, data, ctx);
			
			// 处理来源
			handleSource(cus, data, ctx);
		} else if(CustomerDomainEvent.UPDATE.equals(ope)){
			// 处理头像
			handleHeadportraitUrl(cus, data, ctx);
			
			cus.setUpdatedAt(new Date());

			handleSource(cus, data, ctx);
		}

		handleCustomerProvince(cus, data);
		
		try {
			List<CustomerMetaData> metaDatas = customerMetaDataService.getCustomerMetas(appId);
			
			if (metaDatas!=null && metaDatas.size()>0) {
				for(CustomerMetaData meta: metaDatas) {
					String oldValue = cus.getProperty(meta.getName());
					Object updateCustomer = ctx.get("update");
					if ("1".equals(updateCustomer) || "2".equals(updateCustomer)) {
						if (StringUtils.isNotBlank(oldValue)) {
							continue;
						}
					}

					String newValue = getValue(cus, data, oldValue, meta);
					if (StringUtils.isNotBlank(newValue) && !StringUtils.equals(newValue, oldValue)) {
						cus.setProperty(meta.getName(), newValue);
						triggerPropChanged(cus, meta, newValue, oldValue, ctx);
					}

				}

				for(CustomerMetaData meta: metaDatas) {
					String oldValue = cus.getProperty(meta.getName());
					String newValue = getValue(cus, data, oldValue, meta);
					//为uniqueySlot字段赋值
					if (meta.getVisiable() && meta.getUnique() && StringUtils.isNotBlank(meta.getUniqueSlot()) && StringUtils.isNotBlank(newValue)) {
						String value = cus.getUniqueValue(meta.getName());
						cus.setProperty(meta.getUniqueSlot(), value);
					}
				}
			}
		} catch(BusinessException e) {
			throw e;
		} catch(Exception e) {
			throw new BusinessException("data_validate_error", e.getMessage());
		}

        if(CustomerDomainEvent.MERGE.equals(ope)) {
            // noinspection unchecked
            List<Customer> customers = (List<Customer>) ctx.get(CustomerContext.TO_MERGE_CUSTOMERS);
            for (Customer c : customers) {
                BeanUtil.fillBase(cus, c);
                handleFillExt(cus, c);
            }

            handleUtm(cus, customers);
        }

		return cus;
	}

	private void handleFillExt(Customer target, Customer source) {
		Map<String, String> sourceExt = source.getExtensions();
		if (sourceExt != null && !sourceExt.isEmpty()) {
			Map<String, String> targetExt = target.getExtensions();
			if (targetExt == null || targetExt.isEmpty()) {
				target.setExtensions(sourceExt);
				return ;
			}

			sourceExt.forEach((k, v) -> {
				if (!targetExt.containsKey(k)) {
					targetExt.put(k, v);
				} else if (StringUtils.isBlank(targetExt.get(k))) {
					targetExt.put(k, v);
				}
			});
		}

	}

	private void handleUtm(Customer cus, List<Customer> customers) {
		customers.add(cus);

		customers.stream().min(Comparator.comparing(Customer::getCreatedAt)).ifPresent(old -> {
			logger.info("handleUtm.cus: {}", old);
			Customer customerById = customerService.getCustomerById(old.getId());
			cus.setUtmCampaign(customerById.getUtmCampaign());
			cus.setUtmContent(customerById.getUtmContent());
			cus.setUtmMedium(customerById.getUtmMedium());
			cus.setFrom(customerById.getFrom());
			cus.setBuyedKeyword(customerById.getBuyedKeyword());
			cus.setCreateWay(customerById.getCreateWay());
		});
	}

	private boolean isSimpleCreateWay(Customer cus, Map<String,String> data) {
		int singleCreate = Customer.CreateWay.SINGLE_CREATE.getValue();
		int batchImport = Customer.CreateWay.BATCH_IMPORT.getValue();

		Integer createWay = null;
		if (StringUtils.isNotBlank(data.get("create_way"))) {
			createWay = Integer.valueOf(data.get("create_way"));
		} else if (StringUtils.isNotBlank(data.get("createWay"))){
			createWay = Integer.valueOf(data.get("createWay"));
		} else if (cus.getCreateWay() != null) {
			createWay = cus.getCreateWay();
		} 

		if(createWay != null){
			return createWay.intValue() == singleCreate
					|| createWay.intValue() == batchImport;
		}

		return false;
	}

	private void handleCustomerProvince(Customer cus, Map<String, String> data) {
		if (data.containsKey("province") && !data.containsKey("city") && !data.containsKey("county")) {
			String[] pcc = StringUtils.split(data.get("province"), '-');
			if (pcc.length == 3) {
				cus.setProvince(pcc[0]);
				cus.setCity(pcc[1]);
				cus.setCounty(pcc[2]);
			} else {
				throw new BusinessException("province" + ErrorCodes.SUFFIX_INVALID, "公司地址格式不正确, 省-市-区");
			}
		}
	}

	private void handleBaseCustomerData(Customer cus, Map<String, String> data) {
		Customer c = JsonUtil.fromJsonV2(JsonUtil.toJson(data), new TypeReference<Customer>() {});
		BeanUtils.copyProperties(c, cus);
	}

	private void handleHeadportraitUrl(Customer cus, Map<String, String> data, CustomerContext ctx) {
		String newValue = data.get("headportraitUrl");
		if (StringUtils.isNotBlank(newValue)) {
			cus.setHeadportraitUrl(newValue);
			return;
		}
		
		newValue = data.get("head_portrait_url");
		if (StringUtils.isNotBlank(newValue)) {
			cus.setHeadportraitUrl(newValue);
			return;
		}
		
		newValue = data.get("photoURL");
		if (StringUtils.isNotBlank(newValue)) {
			cus.setHeadportraitUrl(newValue);
			return;
		}
		
		newValue = cus.getHeadportraitUrl();
		if (StringUtils.isNotBlank(newValue)) {
			cus.setHeadportraitUrl(newValue);
			return;
		}
		
		cus.setHeadportraitUrl(Customer.DEFAULT_HEAD_URL); // 设置默认头像
	}

	/**
	 * 触发属性变更事件
	 * 
	 * @param cus
	 * @param meta
	 * @param newValue
	 * @param oldValue
	 * @param ctx
	 */
	public void triggerPropChanged(Customer cus, CustomerMetaData meta, String newValue, String oldValue, CustomerContext ctx) {
		if (this.triggers!=null && this.triggers.size()>0) {
			for(CustomerPropTrigger trigger: this.triggers) {
				trigger.propChanged(cus, meta, newValue, oldValue, ctx);
			}
		}
	}
	
	private void handleSource(Customer cus, Map<String, String> data, CustomerContext ctx) {
		String origin = data.get("source");
		if (StringUtils.isBlank(origin)) {
			origin = "default";
		}
		
		Map<String, String> source = null;
		if (CustomerDomainEvent.CREATE.equals(ctx.getOpe())) {
			source = Maps.newHashMap();
			if (origin.contains("origin")) {
				try {
					source = JsonUtil.jsonToMap(origin);
				} catch (Exception e) {
					logger.warn("warn in handleSource parse JSON origin:" + origin, e);
				}
			} else {
				source.put("origin", origin);
			}
		} else if (CustomerDomainEvent.UPDATE.equals(ctx.getOpe())) {
			Map<String, String> originSource = JsonUtil.jsonToMap(origin);
			try {
				source = JsonUtil.jsonToMap(cus.getSource());
				source.putAll(originSource);
			} catch (Exception e) {
				logger.warn("warn in handleSource parse JSON origin:" + origin, e);
			}
		}
		
		origin = JsonUtil.toJson(source);
		logger.info("handleSource origin:{}", origin);
		
		cus.setSource(origin);
	}

	private String getValue(Customer cus, Map<String, String> data, String oldValue, CustomerMetaData meta) {
		String propName = meta.getName();
		String value = data.get(propName);
		
		// 兼容下划线和驼峰命名参数
		if (StringUtils.isBlank(value)) {
			value = data.get(cn.deal.component.utils.StringUtils.toSnakeCase(propName));
		}
		
		// 特殊Key适配
		if (StringUtils.isBlank(value)) {
			String specKey = specialKeys.get(propName);
			if (specKey!=null) {
				value = data.get(specKey);
			}
		}
		
		// 使用旧值
		if (StringUtils.isBlank(value)) {
			value = oldValue;
		}
		
		// 使用默认值
		if (StringUtils.isBlank(value)) {
			value = meta.getDefaultValue();
		}
		
		return value;
	}

}
