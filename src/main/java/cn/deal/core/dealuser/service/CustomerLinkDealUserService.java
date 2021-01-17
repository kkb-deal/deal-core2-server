package cn.deal.core.dealuser.service;

import cn.deal.core.customer.dao.CustomerDao;
import cn.deal.core.customer.domain.Customer;
import cn.deal.core.dealuser.dao.CustomerLinkDealUserDao;
import cn.deal.core.dealuser.domain.CustomerLinkDealUser;
import cn.deal.core.dealuser.domain.DealUser;
import cn.deal.core.dealuser.domain.DealUserId;
import cn.deal.core.dealuser.repository.CustomerLinkDealUserRepository;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class CustomerLinkDealUserService {
	private Logger logger = LoggerFactory.getLogger(CustomerLinkDealUserService.class);

	@Autowired
	private RedissonClient redissonClient;

	@Autowired
	private CustomerDao customerDao;

	@Autowired
	private CustomerLinkDealUserDao customerLinkDealUserDao;
	
	@Autowired
	private CustomerLinkDealUserRepository customerLinkDealUserRepository;

	/**
	 * 获取客户关联的DealUser 
	 * 
	 * @param customerId
	 * @return
	 */
	public List<CustomerLinkDealUser> getCustomerLinkDealUserByCustomerId(String customerId) {
		List<CustomerLinkDealUser> list = new ArrayList<CustomerLinkDealUser>();

		if (StringUtils.isNotBlank(customerId)) {
			CustomerLinkDealUser example = new CustomerLinkDealUser();
			example.setCustomerId(customerId);
			list = customerLinkDealUserRepository.findAll(Example.of(example));
		}

		logger.debug("getCustomerLinkDealUserByCustomerId result: {}", list);
		
		return list;
	}

	/**
	 * 获取客户关联的DealUser 
	 * 
	 * @param customerId
	 * @return
	 */
	public List<DealUserId> getDealUserIdsByCustomer(String customerId) {
		List<DealUserId> dealUserIds = new ArrayList<>();
		
		if (StringUtils.isNotBlank(customerId)) {
			List<CustomerLinkDealUser> list = this.getCustomerLinkDealUserByCustomerId(customerId);
			 
			if (list != null && !list.isEmpty()) {
				for (CustomerLinkDealUser cdu : list) {
					DealUserId dealUserId = new DealUserId();
					dealUserId.setId(cdu.getDealUserId());
					dealUserIds.add(dealUserId);
				}
			}
		}
		
		logger.debug("getDealUserIdsByCustomer result: {}", dealUserIds);
		
		return dealUserIds;
	}

	/**
	 * 获取客户关联的DealUser 
	 * 
	 * @param customerId
	 * @return
	 */
	public List<String> getDealUserIdsByCustomerId(String customerId) {
		List<String> dealUserIds = new ArrayList<>();
		
		if (StringUtils.isNotBlank(customerId)) {
			List<CustomerLinkDealUser> list = this.getCustomerLinkDealUserByCustomerId(customerId);

			if (list != null && !list.isEmpty()) {
				for (CustomerLinkDealUser cdu : list) {
					dealUserIds.add(cdu.getDealUserId());
				}
			}
		}
		
		logger.debug("getDealUserIdsByCustomerId result: {}", dealUserIds);
		
		return dealUserIds;
	}

	/**
	 * 保存客户和DealUser的关系
	 * 
	 * @param customer
	 * @param dealUser
	 */
	public CustomerLinkDealUser getAndCreateLink(Customer customer, DealUser dealUser) {
		CustomerLinkDealUser link = null;
		String lockKey = "link:" + dealUser.getId();
		RLock lock = null;

		try {
			lock = redissonClient.getReadWriteLock(lockKey).writeLock();
			lock.lock(5, TimeUnit.SECONDS);

			int count = customerLinkDealUserRepository.countByDealUserId(dealUser.getId());
			logger.info("getAndCreateLink.count: {}, {}", count, dealUser.getId());

			if (count == 0) {
				link = customerLinkDealUserRepository.findByCustomerIdAndDealUserId(customer.getId(), dealUser.getId());
				if (link == null) {
					link = new CustomerLinkDealUser();
					link.setId(UUID.randomUUID().toString());
					link.setCustomerId(customer.getId());
					link.setDealUserId(dealUser.getId());
					link.setCreatedAt(new Date());

					link = customerLinkDealUserRepository.saveAndFlush(link);
				}
			}
		} finally {
			if (lock != null) {
				lock.unlock();
			}
		}

		return link;
	}

	/**
	 * 根据客户ID删除
	 * 
	 * @param customerId
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void deleteByCustomer(String customerId) {
		customerLinkDealUserRepository.deleteByCustomerId(customerId);
	}

	/**
	 * 批量添加客户关联的DealUsers
	 * 
	 * @param target
	 * @param dealUserIds
	 */
	public void batchAddCustomerLinkDealUser(Customer target, List<String> dealUserIds) {
		logger.info("batchAddCustomerLinkDealUser.target: {}, dealUserIds: {}", target, dealUserIds);
		if (dealUserIds == null || dealUserIds.isEmpty()) {
			return ;
		}

		Specification<CustomerLinkDealUser> specification = (root, query, criteriaBuilder) -> {
			CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("dealUserId"));
			dealUserIds.forEach(in::value);
			return criteriaBuilder.and(in);
		};


		int count = customerLinkDealUserRepository.count(specification);
		if (count != 0) {
			logger.warn("batchAddCustomerLinkDealUser.count: {}", count);
			return ;
		}

		customerLinkDealUserDao.batchAddCustomerLinkDealUser(target.getId(), dealUserIds);
	}

	/**
	 * 根据dealUserId查询客户
	 * 
	 * @param dealUserId
	 * @return
	 */
	public Customer getCustomerByDealUserId(String dealUserId) {
		// TODO Auto-generated method stub
		List<Customer> cus = customerDao.findCustomerByDealUserId(dealUserId);
		if (cus != null && !cus.isEmpty()) {
			return cus.get(0);
		} else {
			return null;
		}
	}
}
