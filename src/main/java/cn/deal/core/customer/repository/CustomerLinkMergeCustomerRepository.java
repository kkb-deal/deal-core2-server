package cn.deal.core.customer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.domain.CustomerLinkMergeCustomer;

@Repository
public interface CustomerLinkMergeCustomerRepository extends JpaRepository<CustomerLinkMergeCustomer, String> {

	/**
	 * 根据合并之前的客户ID，删除合并记录
	 * 
	 * @param customerId
	 */
	void deleteByMergeCustomerId(String customerId);

}

