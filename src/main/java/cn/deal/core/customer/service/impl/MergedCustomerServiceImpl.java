package cn.deal.core.customer.service.impl;

import cn.deal.component.domain.CustomerDomainEvent;
import cn.deal.component.messaging.producer.CustomerDomainEventProducer;
import cn.deal.core.customer.dao.CustomerDao;
import cn.deal.core.customer.dao.CustomerLinkMergeCustomerDao;
import cn.deal.core.customer.dao.MergedCustomerDao;
import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.domain.CustomerLinkMergeCustomer;
import cn.deal.core.customer.repository.CustomerLinkMergeCustomerRepository;
import cn.deal.core.customer.repository.CustomerRepository;
import cn.deal.core.customer.service.CustomerService;
import cn.deal.core.customer.service.MergedCustomerService;
import cn.deal.core.customer.service.SalesCustomerService;
import cn.deal.core.dealuser.service.CustomerLinkDealUserService;
import cn.deal.core.meta.domain.CustomerMetaData;
import cn.deal.core.meta.service.CustomerMetaDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Slf4j
@Service
public class MergedCustomerServiceImpl implements MergedCustomerService {

    @Autowired
    private CustomerDao customerDao;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private MergedCustomerDao mergedCustomerDao;
	@Autowired
	private SalesCustomerService salesCustomerService;
    @Autowired
    private CustomerLinkMergeCustomerDao customerLinkMergeCustomerDao;
    @Autowired
    private CustomerLinkMergeCustomerRepository customerLinkMergeCustomerRepository;
    @Autowired
    private CustomerLinkDealUserService customerLinkDealUserService;
	@Autowired
	private CustomerDomainEventProducer customerDomainEventProducer;
	@Autowired
	private CustomerRepository customerRepository;

    @Autowired
    private CustomerMetaDataService customerMetaDataService;

	@Override
	public List<String> getMergedCustomerIds(String appId, String[] customerIds) {
		return mergedCustomerDao.getMergedCustomerIds(appId, customerIds);
	}
	@Override
	public List<String> getMergedCustomerIdsWithPage(String appId, String swarmId, int startIndex, int count) {
		return mergedCustomerDao.getMergedCustomerIdsWithPage(appId, swarmId, startIndex, count);
	}
	@Override
	public int getMergedCustomerCount(String appId) {
		return mergedCustomerDao.getMergedCustomerCount(appId);
	}
	@Override
    public List<CustomerLinkMergeCustomer> getCustomerByMergeCustomer(String mergeCustomerId) {
        return customerLinkMergeCustomerDao.getByCustomerId(mergeCustomerId);
    }
	/**
	 * 获取合并之前的客户ID
	 */
    private List<String> getCustomerIdsByMergeCustomer(String mergeCustomerId) {
		List<String> cs = new ArrayList<>();
		
		List<CustomerLinkMergeCustomer> clms = customerLinkMergeCustomerDao.getByCustomerId(mergeCustomerId);
		if (clms == null || clms.isEmpty()) {
		    return cs;
		}

        for(CustomerLinkMergeCustomer ms: clms) {
            cs.add(ms.getCustomerId());
        }
		
		return cs;
    }
	
	/**
	 * 合并客户
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Customer mergeCustomers(Customer target, List<Customer> customers) {
		List<String> mergedCustomerIds = new ArrayList<>(),
                dealUserIds = new ArrayList<>();
		
		for(Customer c: customers) {
//            目标客户和当前客户ID不相同才能合并
			if (StringUtils.equals(c.getId(), target.getId())) {
			    continue;
			}

            this.markCustomerMerged(c, dealUserIds, mergedCustomerIds, target);
            mergedCustomerIds.add(c.getId());
		}

		this.mergeTo(target, customers, dealUserIds, mergedCustomerIds);
		return target;
	}

	/**
	 * 标记单个客户被合并
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void markCustomerMerged(Customer c, List<String> dealUsers, List<String> mergedCustomers, Customer target) {
		List<String> dealUserIdsByCustomerId = customerLinkDealUserService.getDealUserIdsByCustomerId(c.getId()),
                customerIdsByMergeCustomer = this.getCustomerIdsByMergeCustomer(c.getId());
		
		if (dealUserIdsByCustomerId != null && !dealUserIdsByCustomerId.isEmpty()) {
			dealUsers.addAll(dealUserIdsByCustomerId);
		}
		if (customerIdsByMergeCustomer != null && !customerIdsByMergeCustomer.isEmpty()) {
			mergedCustomers.addAll(customerIdsByMergeCustomer);
		}
		
		salesCustomerService.deleteByCustomer(c.getId());
		customerLinkDealUserService.deleteByCustomer(c.getId());
		customerLinkMergeCustomerRepository.deleteByMergeCustomerId(c.getId());
		customerService.markMerged(c, target.getId(), customerIdsByMergeCustomer, dealUserIdsByCustomerId);
	}
	
	/**
	 * 生成新的合并客户
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void mergeTo(Customer target, List<Customer> customers, List<String> dealUsers, List<String> mergedCustomers) {
		// 查询修改前信息
		Customer oldBody = customerDao.getCustomerById(target.getId());

		// 使用最早的客户来源，覆盖来源字段
		log.info("mergeTo. before overrideTargetSource target:{}", target);
		this.overrideTargetSource(target, customers);

		// 补全其他字段
		log.info("mergeTo. before completedTarget target:{}", target);
		this.completedTarget(target, customers);

		// 更新客户信息
        log.info("mergeTo. before save target:{}", target);
		customerService.update(target);
        log.info("mergeTo. after save target:{}", target);

		if (target.getExtensions() != null && !target.getExtensions().isEmpty()) {
			customerDao.updateExtensions(target.getId(), target.getExtensions());
		}

		// 合并DealUser
		if (dealUsers != null && !dealUsers.isEmpty()) {
			customerLinkDealUserService.batchAddCustomerLinkDealUser(target, dealUsers);
			dealUsers.forEach(this::clearCache);
		}

        // 合并Customer
        if (mergedCustomers != null && !mergedCustomers.isEmpty()) {
            customerLinkMergeCustomerDao.batchAddCustomerLinkMergeCustomer(target.getId(), mergedCustomers);
        }

		// 发送领域事件
		CustomerDomainEvent event = new CustomerDomainEvent(target.getId(), CustomerDomainEvent.MERGE, target, oldBody, customers);
		customerDomainEventProducer.send(event);
	}



	@CacheEvict(value = "customer-link-deal-users", allEntries = true)
	public void clearCache(String dealUserId) {
		log.info("clearCache.dealUserId: {}", dealUserId);
	}

	/**
	 * 补全目标
	 *
	 * @param target
	 * @param customers
	 */
    protected void completedTarget(Customer target, List<Customer> customers) {
	    if(target == null || customers == null || customers.isEmpty()){
	        log.warn("mergeProperties. target is null or customers is null.");
	        return;
        }

		// 归整待合并客户
		List<Customer> customersWithoutSelf = normalCustomers(target, customers);
        if(customersWithoutSelf.isEmpty()){
            log.warn("mergeProperties. customersWithoutSelf is null.");
            return;
        }

        // 按照创建时间从早到晚排列
        Collections.sort(customersWithoutSelf);

        // 查询客户元数据
        List<CustomerMetaData> metas = customerMetaDataService.getCustomerMetas(target.getAppId());

        // 补全客户
        for(Customer c : customersWithoutSelf){
            this.completedTarget(metas, target, c);
        }
    }

	/**
	 * 覆盖目标客户来源字段
	 *
	 * @param target
	 * @param customers
	 */
	protected void overrideTargetSource(Customer target, List<Customer> customers) {
		if(target == null || customers == null || customers.isEmpty()){
			log.warn("mergeProperties. target is null or customers is null.");
			return;
		}

		// 归整待合并客户
		List<Customer> customersWithoutSelf = normalCustomers(target, customers);
		if(customersWithoutSelf.isEmpty()){
			log.warn("mergeProperties. customersWithoutSelf is null.");
			return;
		}

		// 查询客户元数据
		List<CustomerMetaData> metas = customerMetaDataService.getCustomerMetas(target.getAppId());

		// 获取创建时间最早的客户，包括主客户
		customersWithoutSelf.add(target);
		Collections.sort(customersWithoutSelf);
		Customer oldest = customersWithoutSelf.get(0);

		// 覆盖主客户的来源字段
		for(CustomerMetaData meta: metas) {
			if (Customer.isSourceProperty(meta.getName())) {
				String newValue = oldest.getProperty(meta.getName());
				target.setProperty(meta.getName(), newValue);
			}
		}
	}

	public List<Customer> normalCustomers(Customer target, List<Customer> customers) {
		List<Customer> customersWithoutSelf = new ArrayList<>();
		for(Customer c : customers){
			if(c == null || c.getId().equals(target.getId())){
				continue;
			}
			customersWithoutSelf.add(c);
		}

		if(customersWithoutSelf.isEmpty()){
			log.warn("mergeProperties. customersWithoutSelf is null.");
			return null;
		}

		return customersWithoutSelf;
	}


	/**
     * 补全主客户上为空的字段，排除来源字段
     *
     * @param metas
     * @param from
     * @param to
     */
    protected void completedTarget(List<CustomerMetaData> metas, Customer from, Customer to) {
		for (CustomerMetaData meta : metas) {
			// 来源字段跳过
			if (Customer.isSourceProperty(meta.getName())) {
				continue;
			}

			String oldValue = from.getProperty(meta.getName());

			if (StringUtils.isBlank(oldValue)) {
				String newValue = to.getProperty(meta.getName());

				if (!StringUtils.isBlank(newValue)) {
					from.setProperty(meta.getName(), newValue);
				}
			}
		}
	}

}
