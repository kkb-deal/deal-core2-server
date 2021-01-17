package cn.deal.core.dealuser.repository;

import cn.deal.core.dealuser.domain.CustomerLinkDealUser;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

@CacheConfig(cacheManager = "redis", cacheNames = "dealuser")
public interface CustomerLinkDealUserRepository extends JpaRepository<CustomerLinkDealUser, String> {

	/**
	 * 查询是否存在
	 * 
	 * @return
	 */
	CustomerLinkDealUser findByCustomerIdAndDealUserId(String customerId, String dealUserId);

	/**
	 * 根据客户ID，删除客户关联的DealUsers
	 * 
	 * @param customerId
	 */
	void deleteByCustomerId(String customerId);

	int countByDealUserId(String id);

	int count(Specification<CustomerLinkDealUser> specification);

	@Cacheable(value = "customer-link-deal-users", unless = "#result==null")
	CustomerLinkDealUser findFirstByDealUserId(String dealUserId);
}
