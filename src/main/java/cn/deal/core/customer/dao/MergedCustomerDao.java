package cn.deal.core.customer.dao;

import java.util.List;


public interface MergedCustomerDao {

	List<String> getMergedCustomerIds(String appId, String[] customerIds);

	/**
	 * 根据合并前的客户ID查询合并后的客户ID
	 */
	List<String> getCustomerIdByMergedCustomerId(String customerId);
	
	/**
	 * 根据appId和swarmId获取客户合并之前的客户id。
	 * @param appId
	 * @param swarmId
	 * @param startIndex
	 * @param count
	 * @return
	 */
	List<String> getMergedCustomerIdsWithPage(String appId, String swarmId, int startIndex, int count);

	int getMergedCustomerCount(String appId);

}
