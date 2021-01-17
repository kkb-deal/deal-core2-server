package cn.deal.core.customer.service;

import java.util.List;

import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.domain.CustomerLinkMergeCustomer;


public interface MergedCustomerService {

	/**
	 * 合并多个客户
	 * 
	 * @param mainCustomer
	 * @param otherCustomer
	 * @return
	 */
	Customer mergeCustomers(Customer target, List<Customer> customers);
	
	/**
	 * 获取合并之后的客户ID列表
	 * 
	 * @param appId
	 * @param customerIds
	 * @return
	 */
	List<String> getMergedCustomerIds(String appId, String[] customerIds);
	
	/**
	 * 分页获取合并之后的客户
	 * 
	 * @param appId
	 * @param swarmId
	 * @param startIndex
	 * @param count
	 * @return
	 */
	List<String> getMergedCustomerIdsWithPage(String appId, String swarmId, int startIndex, int count);

	/**
	 * 获取合并后的客户总数
	 * 
	 * @param appId
	 * @return
	 */
	int getMergedCustomerCount(String appId);

	/**
	 * 根据客户获取合并
	 * 
	 * @param mergeCustomerId
	 * @return
	 */
	List<CustomerLinkMergeCustomer> getCustomerByMergeCustomer(String mergeCustomerId);


	/**
	 * 合并客户去重主客户
	 *
	 * @param target
	 * @param customers
	 * @return
	 */
	List<Customer> normalCustomers(Customer target, List<Customer> customers);

}
