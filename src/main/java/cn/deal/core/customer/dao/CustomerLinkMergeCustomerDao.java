package cn.deal.core.customer.dao;

import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.domain.CustomerLinkMergeCustomer;

import java.util.List;

public interface CustomerLinkMergeCustomerDao {

	/**
	 * 根据客户ID查询合并客户
	 * 
	 * @param customerId
	 * @return
	 */
    List<CustomerLinkMergeCustomer> getByCustomerId(String customerId);

    /**
     * 批量添加客户合并的客户
     * 
     * @param target
     * @param mergedCustomers
     */
	void batchAddCustomerLinkMergeCustomer(String customerId, List<String> mergedCustomers);

}
