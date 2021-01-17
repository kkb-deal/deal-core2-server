package cn.deal.core.customer.engine.filters;

import cn.deal.component.domain.CustomerDomainEvent;
import cn.deal.component.kuick.domain.ResponseVO;
import cn.deal.component.utils.AssertUtils;
import cn.deal.core.app.domain.AppMember;
import cn.deal.core.app.service.DealAppMemberService;
import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.engine.helpers.CustomerFilterManager;
import cn.deal.core.customer.engine.interfaces.CustomerContext;
import cn.deal.core.customer.service.MergedCustomerService;
import cn.deal.core.customer.service.SalesCustomerService;
import cn.deal.core.meta.domain.AppSetting;
import cn.deal.core.meta.service.AppSettingService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 客户合并后所属销售分配
 *
 */
@Component
public class CustomerMergeMainKuickUserFilter extends ActionFilterAdapter {

    public static final String ALLOC_LATEAT = "1"; //分配给创建时间最晚
    public static final String ALLOC_EARLEST = "0"; //分配给创建时间最早
    public static final String TARGET_KUICK_USER_ID = "mergeTargetKuickUserId";

    private Logger logger = LoggerFactory.getLogger(CustomerMergeMainKuickUserFilter.class);

    @Autowired
    private CustomerFilterManager customerFilterManger;

    @Autowired
    private SalesCustomerService salesCustomerService;

    @Autowired
    private DealAppMemberService dealAppMemberService;

    @Autowired
    private AppSettingService appSettingService;

    @Autowired
    private MergedCustomerService mergedCustomerService;

    @PostConstruct
    public void init() {
        customerFilterManger.register(this);
    }

    @Override
    public void doBefore(Customer customer, CustomerContext ctx) {
        AssertUtils.notNull(customer, "customer can not be null");
        AssertUtils.notNull(ctx, "customer context can not be null");

        String ope = ctx.getOpe();
        if (!CustomerDomainEvent.MERGE.equals(ope)) {
            return;
        }

        String appId = ctx.getAppId();
        List<Customer> toMergeCustomers = ctx.getToMergeCustomers();
        String mainKuickUserId = ctx.getMainKuickUserId();
        List<Customer> normaledToMergeCustomers = mergedCustomerService.normalCustomers(customer, toMergeCustomers);

        logger.info("appId:{}", appId);
        logger.info("customer:{}", customer);
        logger.info("toMergeCustomers:{}", toMergeCustomers);
        logger.info("normaledtoMergeCustomers:{}", normaledToMergeCustomers);
        logger.info("mainKuickUserId:{}", mainKuickUserId);

        String targetKuickUserId = this.allocMember(appId, mainKuickUserId, customer, normaledToMergeCustomers);
        logger.info("allocMember: {}", targetKuickUserId);

        customer.setKuickUserId(targetKuickUserId);
        ctx.put(TARGET_KUICK_USER_ID, targetKuickUserId);
    }

    @Override
    public void doAfter(Customer customer, CustomerContext ctx) {
        AssertUtils.notNull(customer, "customer can not be null");
        AssertUtils.notNull(ctx, "customer context can not be null");

        String ope = ctx.getOpe();
        if (!CustomerDomainEvent.MERGE.equals(ope)) {
            return;
        }

        String appId = ctx.getAppId();
        String targetKuickUserId = (String)ctx.get(TARGET_KUICK_USER_ID);

        // 转让客户
        if (StringUtils.isNotBlank(targetKuickUserId)) {
            // 强制转让客户
            ResponseVO result = salesCustomerService.customerTransfer(appId, "", customer.getKuickUserId(),
                    targetKuickUserId, customer.getId(), true, "", 0);
            logger.info("customerTransfer result: {}", result);
        }
    }

    /**
     * 客户合并时分配项目成员
     *
     * @param appId
     * @param mainKuickUserId
     * @param customer
     * @param toMergeCustomers
     * @return
     */
    protected String allocMember(String appId, String mainKuickUserId, Customer customer, List<Customer> toMergeCustomers) {
        // 如果合并时指定了成员，则直接返回
        if (StringUtils.isNotBlank(mainKuickUserId)) {
            logger.info("allocMember mainKuickUserId: {}", mainKuickUserId);
            return mainKuickUserId;
        }

        // 拼接所有客户
        List<Customer> customers = new ArrayList<Customer>();
        customers.add(customer);

        if (toMergeCustomers != null && !toMergeCustomers.isEmpty()) {
            customers.addAll(toMergeCustomers);
        }

        // 查询客户所属销售
        List<String> kuickUserIds = customers.stream()
                .map(Customer::getKuickUserId)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());

        logger.info("allocMember kuickUserIds: {}", kuickUserIds);
        List<AppMember> members = dealAppMemberService.getDealAppMembers(appId, kuickUserIds.toArray(new String[kuickUserIds.size()]));
        logger.info("allocMember members: {}", members);

        // 查询销售岗位
        List<AppMember> sales = members.stream()
                .filter(AppMember::checkSalesPost)
                .collect(Collectors.toList());

        logger.info("allocMember sales: {}", sales);

        // 如果有销售，按项目开关取最早或者最晚的
        if (sales!=null && sales.size()>0) {
            return handleByTime(appId, customers, sales);
        } else {
            return handleByTime(appId, customers, members);
        }
    }

    /**
     * 获取客户列表是否按更新时间排序
     *
     * @param appId
     * @return
     */
    protected String getCustomerAllocationWay(String appId) {
        AppSetting as = appSettingService.getSetting(appId, "merge_allocation_way");

        if (as != null) {
            if (org.apache.commons.lang3.StringUtils.isNoneBlank(as.getValue())){
                return as.getValue();
            } else {
                return as.getDefaultValue();
            }
        }

        return ALLOC_EARLEST;
    }

    private String handleByTime(String appId, List<Customer> customers, List<AppMember> sales) {
        String allocWay = this.getCustomerAllocationWay(appId);
        logger.info("handleByTime allocWay: {}", allocWay);


        List<String> selasIds = sales.stream()
                .map(AppMember::getStringKuickUserId)
                .collect(Collectors.toList());

        logger.info("handleByTime selasIds: {}", selasIds);

        List<String> kuickUserIds = customers.stream()
                .filter(c->selasIds.contains(c.getKuickUserId()))
                .sorted()
                .map(Customer::getKuickUserId)
                .collect(Collectors.toList());

        logger.info("handleByTime kuickUserIds: {}", kuickUserIds);
        if (kuickUserIds==null || kuickUserIds.size()==0) {
            return "";
        }

        if (ALLOC_EARLEST.equals(allocWay)) {
            return kuickUserIds.get(0);
        } else if (ALLOC_LATEAT.equals(allocWay)){
            return kuickUserIds.get(kuickUserIds.size()-1);
        } else {
            return "";
        }
    }
}
