package cn.deal.core.customer.engine.filters;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.deal.component.domain.CustomerDomainEvent;
import cn.deal.component.exception.BusinessException;
import cn.deal.component.utils.ReadWriteLockProvider;
import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.engine.helpers.CustomerFilterManager;
import cn.deal.core.customer.engine.interfaces.CustomerContext;
import cn.deal.core.customer.engine.interfaces.ErrorCodes;
import cn.deal.core.customer.service.CustomerService;
import cn.deal.core.meta.domain.CustomerMetaData;
import cn.deal.core.meta.service.CustomerMetaDataService;
import org.springframework.util.CollectionUtils;

/**
 * 客户唯一性约束验证
 */
@Component
public class CustomerUniqueValidator extends ActionFilterAdapter {

	public static final String META_DATAS_KEY = "metaDatas";

	private Logger logger = LoggerFactory.getLogger(CustomerUniqueValidator.class);
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private ReadWriteLockProvider readWriteLockProvider;

	@Autowired
	private CustomerMetaDataService customerMetaDataService;
	
	private long leaseTime = 60; //1分钟
	
	@Autowired
	private CustomerFilterManager customerFilterManger;
	
	@PostConstruct
	public void init() {
		customerFilterManger.register(this);
	}
	
	@Override
	public int getOrder() {
		return 5;
	}
	
	@Override
	public boolean isAsync() {
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void doBefore(Customer customer, CustomerContext ctx) {
		String appId = ctx.getAppId();
		String ope = ctx.getOpe();
		
		if (!CustomerDomainEvent.CREATE.equals(ope) && !CustomerDomainEvent.UPDATE.equals(ope) && !CustomerDomainEvent.MERGE.equals(ope)) {
			return;
		}
		
		logger.info("start unique validate customer:{}, ctx:{}", customer, ctx);
		
		try {
			List<CustomerMetaData> metaDatas = (List<CustomerMetaData>)ctx.get(META_DATAS_KEY);
			if (metaDatas==null) {
				metaDatas = customerMetaDataService.getCustomerMetas(appId);
			}
			
			if (metaDatas!=null && metaDatas.size()>0) {
				metaDatas.stream().forEach(meta->{
					this.validateMetaItem(appId, customer, meta, ctx);
				});
			}
		} catch(BusinessException e) {
			throw e;
		} catch(Exception e) {
			throw new BusinessException("data_validate_error", e.getMessage(), e);
		}
	}

	@Override
	public void doFinally(Customer customer, CustomerContext ctx) {
		String appId = ctx.getAppId();
		String ope = ctx.getOpe();
		
		if (!CustomerDomainEvent.CREATE.equals(ope) && !CustomerDomainEvent.UPDATE.equals(ope) && !CustomerDomainEvent.MERGE.equals(ope)) {
			return;
		}
		
		try {
			this.releaseMetaDataLock(appId, customer, ctx);
		} catch(BusinessException e) {
			throw e;
		} catch(Exception e) {
			throw new BusinessException("data_validate_error", e.getMessage(), e);
		}
	}
	
	protected boolean checkRepeat(String appId, String value, CustomerContext ctx, CustomerMetaData metaData, Customer customer) {
		boolean repeated = false;
		
		List<Customer> customerList = customerService.findCustomerByPropNameAndPage(appId, metaData, value, 0, 1);
		if (customerList!=null && customerList.size()>0) {
			Customer cus = customerList.get(0);
			if(Objects.nonNull(cus) && Objects.nonNull(customer) && cus.getId().equals(customer.getId())) {
				return false;
			}
			if (CustomerDomainEvent.MERGE.equals(ctx.getOpe()) && Objects.nonNull(cus) && Objects.nonNull(customer)) {
				List<Customer> toMergeCustomers = ctx.getToMergeCustomers();
				if (!CollectionUtils.isEmpty(toMergeCustomers)) {
					if (toMergeCustomers.stream().map(Customer::getId).collect(Collectors.toList()).contains(cus.getId())) {
						return false;
					}
				}

			}
			repeated = true;
		}
		
		return repeated;
	}
	
	protected String makeLockKey(String appId, String propName, String value) {
		return this.getClass().getSimpleName() + ":" + appId + ":" + propName + ":" + value;
	}
	
	protected void validateMetaItem(String appId, Customer customer, CustomerMetaData meta, CustomerContext ctx) {
		String propName = meta.getName();
		String value = customer.getProperty(propName);
		
		// 校验唯一性，当前只支持基本属性
		if (meta.getVisiable() && StringUtils.isNotBlank(value) 
				&& meta.getUnique()) {
			String key = makeLockKey(appId, propName, value);
			logger.info("get lock from redisson, key is : {}", key);
			Lock lock = readWriteLockProvider.getReadWriteLock(key).writeLock();

			try {
				if(lock.tryLock(leaseTime, TimeUnit.SECONDS)) {
					boolean repeated = checkRepeat(appId, value, ctx, meta, customer);
					
					if (repeated) {
						lock.unlock();
						throw new BusinessException(propName + ErrorCodes.SUFFIX_UNIQUE, meta.getTitle() + "重复了");
					} else {
						logger.info("get CustomerUnique lock: {}", key);
					}
				} else {
					throw new BusinessException(propName + "wait_lock_error", meta.getTitle() + "获取锁错误！");
				}
			} catch(InterruptedException e) {
				logger.warn("Error in lock: {}", key);
			} finally {
				ctx.put(key, lock);
			}
		}
	}

	protected void releaseMetaDataLock(String appId, Customer customer, CustomerContext ctx) {
		// 客户创建结束，释放唯一性约束锁
		for(String key: ctx.keySet()) {
			if (key.startsWith(this.getClass().getSimpleName())) {
				Object lockObj = ctx.get(key);
				
				if (lockObj!=null && lockObj instanceof Lock) {
					logger.info("release CustomerUnique lock: {}", key);
					try {
						((Lock)lockObj).unlock();
					} catch (Exception e) {
						logger.warn("release CustomerUnique lock error", e);
					}
				}
			}
		}
	}
	
}