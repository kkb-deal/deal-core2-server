package cn.deal.core.customer.service;

import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.domain.CustomerId;
import cn.deal.core.customer.domain.vo.CustomerSearchVO;
import cn.deal.core.meta.domain.CustomerMetaData;

import java.util.List;
import java.util.Map;


public interface CustomerService {

	/**
	 * 根据appId获取自定义模版title
	 * @param appId
	 */
	List<String> getCustomerTitlesByAppId(String appId) throws Exception;

	List<Customer> getScreeningCustomer(String appId, String filterId, Integer startIndex, Integer count);

    List<Customer> exactMatch(String appId, String phone, String email, int startIndex, int count);

    List<Customer> exactMatchV2(CustomerSearchVO params);

    List<Customer> getSwarmCustomers(String swarmId, String appId, String groupId, List<String> kuickUserIds,
            int startIndex, int count, String keyword);

    long getTotalByAppId(String appId);

    List<CustomerId> getCustomerIdBySwarmIdAndAppId(String appId, String swarmId, int startIndex, int count);

    List<Customer> getTagCustomers(String tags, String appId, int kuickUserId, Integer greateTagCount);

    long getTagCustomerCount(String appId, String tag);

    long getTotalByAppId(String appId, List<String> swarmIdsList);

    List getCustomerBySwarmIdAndAppId(String appId, String swarmId, int startIndex, int count,
            Map<String, Integer> attributesMap);

    List getCustomerByAppId(String appId, int startIndex, int count, Map<String, Integer> attributesMap);

    List<CustomerId> getCustomerIdByAppId(String appId, int startIndex, int count);

    /**
     * 获取客户的最新信息，如果客户被合并过，需要返回被合并之后的客户信息
     */
    Customer getLatestCustomer(String appId, String customerId);

    /**
     * 根据客户ID获取客户
     * 
     * @param customerId
     * @return
     */
    Customer getCustomerById(String customerId);

	/**
	 * 根据客户ID获取客户
	 * @param appId 项目id
	 * @param customerId 客户id
	 * @param withKuickuser 是否查询销售 1 查； 0 不查
	 * @return
	 */
	Customer getCustomerById(String appId, String customerId, Integer withKuickuser);

    /**
     * 获取合并的客户总数
     * 
     * @param appId
     * @param swarmIdsList
     * @return
     */
    long getTotalByAppIdWithMerged(String appId, List<String> swarmIdsList);

    /**
     * 根据 dealuser的id列表获取对应的customer列表
     * 
     * @param dealuserIdList
     *            dealuser的id集合
     * @return
     */
    List<Customer> getCustomerListByDealuserIds(List<String> dealuserIdList);

	/**
	 * 查询项目下的所有客户数，包括合并之前的
	 * @param appId
	 * @return
	 */
    long getRawCustomerCount(String appId);

    /**
     * 查询客户
     * @param appId
     * @param startIndex
     * @param count
     * @return
     */
    List<Customer> getRawCustomer(String appId, Integer startIndex, Integer count);

    /**
     * 获取客户列表
     * 
     * @param appId
     * @param customerIds
     * @param startIndex
     * @param count
     * @return
     */
	List<Customer> findCustomerByIdsAndPage(String appId, String[] customerIds, int startIndex, int count);

	/**
	 * 获取客户列表，根据客户ID
	 * 
	 * @param customerIds
	 * @return
	 */
	List<Customer> getCustomerByIds(String[] customerIds);
	
	/**
	 * 创建客户
	 * 
	 * @param customer
	 * @return
	 */
	Customer createCustomer(Customer customer);

	/**
	 * 更新客户
	 * 
	 * @param customer
	 * @return
	 */
	Customer update(Customer customer);

	/**
	 * 根据客户属性查询客户
	 * 
	 * @param appId
	 * @param value
	 * @param startIndex
	 * @param count
	 * @return
	 */
	List<Customer> findCustomerByPropNameAndPage(String appId, CustomerMetaData metaData, String value, int startIndex, int count);

	/**
	 * 标记客户已合并
	 * 
	 * @param customer
	 * @param mcs 
	 * @param targetCustomerId
	 */
	void markMerged(Customer customer, String mergedCustomerId, List<String> mergedCustomerIds, List<String> dealUserIds);

	/**
	 * 标记删除客户
	 * 
	 * @param appId
	 * @param customerId
	 * @return
	 */
	void markDeleteCustomer(Customer customer);

    /**
     * 添加用户
     * @param customer
     * @return
     */
    Customer create(Customer customer);

	/**
	 * 根据标签获取客户集合
	 */
    List<CustomerId> getCustomerByTag(String appId, String tag, long startIndex, int count);

    long countCustomerByFilter(String appId, String filterId);

    Boolean batchTransferAscription(String appId, String sourceKuickUserId, String targetKuickUserId,
									String customerIds, boolean isAutomatic, Integer isJudgeOwner);

    Customer findByDealUserId(String dealUserId);
}