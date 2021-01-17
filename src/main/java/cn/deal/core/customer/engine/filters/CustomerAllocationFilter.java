package cn.deal.core.customer.engine.filters;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.deal.component.domain.CustomerDomainEvent;
import cn.deal.component.utils.AssertUtils;
import cn.deal.core.app.domain.AppMember;
import cn.deal.core.app.domain.DealApp;
import cn.deal.core.app.service.DealAppMemberService;
import cn.deal.core.app.service.DealAppService;
import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.engine.helpers.CustomerFilterManager;
import cn.deal.core.customer.engine.interfaces.CustomerContext;
import cn.deal.core.customer.service.SalesCustomerService;
import cn.deal.core.meta.domain.AppSetting;
import cn.deal.core.meta.service.AppSettingService;

/**
 * 客户分配过滤器
 * 
 * 当新创建的客户，没有所属人时，需要根据系统开关控制是
 * 随机分配客户还是固定指给项目创建人
 *
 */
@Component
public class CustomerAllocationFilter extends ActionFilterAdapter {

    public static final String NUM_PRIFIX = "random_alloc:";
	public static final String ALLOC_APP_CREATOR = "1"; //分配给项目创建人
    public static final String RANDOM_ALLOC = "0"; //随机分配
    
	@Autowired
    private AppSettingService appSettingService;
    
	@Autowired
	private SalesCustomerService salesCustomerService;
	
	@Autowired
	private DealAppService dealAppService;
	
	@Autowired
	private DealAppMemberService dealAppMemberService;
	
	@Autowired
	private RedissonClient redissonClient;
	
	@Autowired
	private CustomerFilterManager customerFilterManger;
	
	@PostConstruct
	public void init() {
		customerFilterManger.register(this);
	}
	
	@Override
	public void doBefore(Customer customer, CustomerContext ctx) {
		String ope = ctx.getOpe();
		if (!CustomerDomainEvent.CREATE.equals(ope)) {
			return;
		}
		
		String appId = ctx.getAppId();

		if (StringUtils.isBlank(customer.getKuickUserId())) {
			String targetUserId = allocMember(appId, customer, ctx);
			customer.setKuickUserId(targetUserId);
		} 
	}
	
	@Override
	public void doAfter(Customer customer, CustomerContext ctx) {
		String ope = ctx.getOpe();
		if (!CustomerDomainEvent.CREATE.equals(ope)) {
			return;
		}
		
		String appId = ctx.getAppId();

		if (StringUtils.isNotBlank(customer.getKuickUserId())) {
			salesCustomerService.createAppSalesCustomer(appId, customer, customer.getKuickUserId());
		} 
	}

	/**
	 * 分配销售
	 * 
	 * @param appId
	 * @param customer
	 * @param ctx
	 * @return
	 */
	protected String allocMember(String appId, Customer customer, CustomerContext ctx) {
		// 优先推广人ID
		String targetUserId = customer.getPromoterId();
		
		// 其次当前KuickUserId
		if (StringUtils.isBlank(targetUserId)) {
			if (StringUtils.isNotBlank(ctx.getKuickUserId())) {
				if (dealAppMemberService.isAppMember(appId, NumberUtils.toInt(ctx.getKuickUserId()))) {
					targetUserId = ctx.getKuickUserId();
				}
			}
		}
		
		// 最后根据配置开关
		if (StringUtils.isBlank(targetUserId)) {
			String allocWay = this.getCustomerAllocationWay(appId);
			
			if (ALLOC_APP_CREATOR.equals(allocWay)) {
				targetUserId = this.allocAppCreator(appId, customer, ctx);
			} else {
				targetUserId = this.randomAlloc(appId, customer, ctx);
			}
		}
		
		return targetUserId;
	}

	/**
	 * 分配给项目创建人
	 * 
	 * @param appId
	 * @param customer
	 * @param ctx
	 * @return
	 */
	protected String allocAppCreator(String appId, Customer customer, CustomerContext ctx) {
		DealApp dealApp = dealAppService.getDealAppInfo(appId);
		AssertUtils.notNull(dealApp, "项目ID不存在");
		
		// 返回部门管理员ID
		return dealApp.getCreatorId();
	}

	/**
	 * 随机分配
	 * 
	 * @param appId
	 * @param customer
	 * @param ctx
	 * @return
	 */
	protected String randomAlloc(String appId, Customer customer, CustomerContext ctx) {
		// 成员总数
		long memberCount = dealAppMemberService.getAppMemberCount(appId);
		
		// 当前序号
		RAtomicLong num = redissonClient.getAtomicLong(NUM_PRIFIX + appId);
		int index = Long.valueOf((num.incrementAndGet() % memberCount)).intValue();
		
		// 查询销售
		List<AppMember> members = dealAppMemberService.getAppMembers(appId, index, 1);
		if (members!=null && members.size()>0) {
			return String.valueOf(members.get(0).getKuickUserId());
		} else {
			return this.allocAppCreator(appId, customer, ctx);
		}
	}

	/**
	 * 获取客户列表是否按更新时间排序
	 * 
	 * @param appId
	 * @return
	 */
	protected String getCustomerAllocationWay(String appId) {
		AppSetting as = appSettingService.getSetting(appId, "allocation_way");
		
		if (as != null) {
			if (StringUtils.isNoneBlank(as.getValue())){
				return as.getValue();
			} else {
				return as.getDefaultValue();
			}
		}
		
		return ALLOC_APP_CREATOR;
	}
}
