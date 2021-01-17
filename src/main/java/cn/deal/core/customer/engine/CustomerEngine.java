package cn.deal.core.customer.engine;

import cn.deal.component.domain.CustomerDomainEvent;
import cn.deal.component.exception.BusinessException;
import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.engine.handles.CustomerBuildHandler;
import cn.deal.core.customer.engine.handles.DataValidateHandler;
import cn.deal.core.customer.engine.helpers.CustomerFilterManager;
import cn.deal.core.customer.engine.interfaces.CustomerContext;
import cn.deal.core.customer.service.CustomerService;
import cn.deal.core.customer.service.MergedCustomerService;
import cn.deal.core.customer.service.SalesCustomerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 客户处理引擎
 */
@Service
@Slf4j
public class CustomerEngine {

	private static final Logger logger = LoggerFactory.getLogger(CustomerEngine.class);


	@Autowired
	private CustomerFilterManager customerFilterManger;
	
	@Autowired
	private DataValidateHandler dataValidateHandler;
	
	@Autowired
	private CustomerBuildHandler customerBuildHandler;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private MergedCustomerService mergedCustomerService;
	
	@Autowired
	private SalesCustomerService salesCustomerService;

	@Autowired
	private RedissonClient redissonClient;

	@Value("${redis.update.customer.lockleasetime}")
	private Long updateCustomerLockLeaseTime;
	
	/**
	 * 客户创建
	 * 
	 * @param appId
	 * @param data
	 * @param opts
	 * @return
	 */
	public Customer handleCreate(String appId, Map<String, String> data, Map<String, Object> opts) {
		log.info("start handleCreate, params: {}, {}, {}", appId, data, opts);
		
		// 客户上下文
		CustomerContext ctx = new CustomerContext(opts);
		ctx.put(CustomerContext.APPID, appId);
		ctx.put(CustomerContext.OPE, CustomerDomainEvent.CREATE);

		// 验证客户数据
		dataValidateHandler.handle(appId, data, ctx, opts);
		
		// 构建客户
		Customer customer = customerBuildHandler.handle(appId, null, data, ctx);
		
		log.info("after buildCustomer, result: {}", customer);
		
		// 创建客户
		try {
			customerFilterManger.doBefore(customer, ctx);
			customer = customerService.createCustomer(customer);
			customerFilterManger.doAfter(customer, ctx);
		} finally {
			customerFilterManger.doFinally(customer, ctx);
		}
		
		return customer;
	}

	/**
	 * 客户更新
	 * 
	 * @param appId
	 * @param customerId
	 * @param data
	 * @param opts
	 * @return
	 */
	public Customer handleUpdate(String appId, String customerId, Map<String, String> data, Map<String, Object> opts) {
		log.info("start handleUpdate, params: {}, {}, {}, {}", appId, customerId, data, opts);
		
		String lockName = "update_customer_lock:" + appId + ":" + customerId;
		RLock rLock = redissonClient.getReadWriteLock(lockName).writeLock();
		Customer customer = null;
		try {
			if (rLock.tryLock(1L, updateCustomerLockLeaseTime, TimeUnit.SECONDS)) {
				// 查询老客户
				customer = customerService.getCustomerById(customerId);

				if (customer!=null) {
					// 客户上下文
					CustomerContext ctx = new CustomerContext(opts);
					ctx.put(CustomerContext.APPID, appId);
					ctx.put(CustomerContext.OPE, CustomerDomainEvent.UPDATE);
					ctx.put(CustomerContext.ORIGIN_CUSTOMER, customer);

					// 验证客户数据
					dataValidateHandler.handle(appId, data, ctx, opts);

					// 构建客户
					customer = customerBuildHandler.handle(appId, customer, data, ctx);
					log.info("after build customer, result: {}", customer);

					// 更新客户
					try {
						customerFilterManger.doBefore(customer, ctx);
						customer = customerService.update(customer);
						customerFilterManger.doAfter(customer, ctx);
					} finally {
						customerFilterManger.doFinally(customer, ctx);
					}
				}
			}
		} catch (Exception e) {
			logger.warn("update customer[{}] try lock occur exception", customerId, e);
		} finally {
			if (rLock != null && rLock.isHeldByCurrentThread()) {
				rLock.unlock();
			}
		}
		
		return customer;
	}

	/**
	 * 客户合并
	 * 
	 * @param appId
	 * @param mergeCustomerIds
	 * @param mainCustomerId
	 * @param mainKuickUserId
	 * @param data
	 * @param opts
	 * @return
	 */
	public Customer handleMerge(String appId, String[] mergeCustomerIds, String mainCustomerId, String mainKuickUserId,
			Map<String, String> data, Map<String, Object> opts) {
		// 查询待合并客户
		Customer customer = customerService.getCustomerById(mainCustomerId);
		List<Customer> toMergeCustomers = customerService.getCustomerByIds(mergeCustomerIds);
			
		if (customer!=null && toMergeCustomers.size()>0) {
			// 客户上下文
			CustomerContext ctx = new CustomerContext(opts);
			ctx.put(CustomerContext.APPID, appId);
			ctx.put(CustomerContext.OPE, CustomerDomainEvent.MERGE);
			ctx.put(CustomerContext.ORIGIN_CUSTOMER, customer);
			ctx.put(CustomerContext.TO_MERGE_CUSTOMERS, toMergeCustomers);
			ctx.put(CustomerContext.MAIN_KUICK_USER_ID, mainKuickUserId);
			
			// 验证客户数据
			dataValidateHandler.handle(appId, data, ctx, opts);
			
			// 构建客户
			customer = customerBuildHandler.handle(appId, customer, data, ctx);
			
			// 执行合并
			try {
				customerFilterManger.doBefore(customer, ctx);
				log.info("handleMerge. before. target customer: {}", customer);
				customer = mergedCustomerService.mergeCustomers(customer, toMergeCustomers);
                log.info("handleMerge. after. target customer: {}", customer);
                customerFilterManger.doAfter(customer, ctx);
			} finally {
				customerFilterManger.doFinally(customer, ctx);
			}
		}
		
		return customer;
	}

	/**
	 * 删除客户
	 * 
	 * @param appId
	 * @param customerId
	 * @return
	 */
	public Customer handleDelete(String appId, String customerId) {
		Customer cus = customerService.getCustomerById(customerId);
		
		if (cus!=null && StringUtils.equals(appId, cus.getAppId())) {
			salesCustomerService.deleteByCustomer(cus.getId());
			customerService.markDeleteCustomer(cus);
			
			return cus;
		} else {
			throw new BusinessException("not_exist", "该项目下不存在这个客户！");
		}
	}

}
