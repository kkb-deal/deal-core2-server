package cn.deal.core.customer.dao;

import cn.deal.component.domain.filter.Condition;
import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.domain.CustomerId;
import cn.deal.core.customer.domain.vo.CustomerSearchVO;
import cn.deal.core.dealuser.domain.CustomerWithDealuserId;
import cn.deal.core.meta.domain.CustomerMetaData;

import java.util.List;
import java.util.Map;

/**
 * 客户数据访问层
 */
public interface CustomerDao {

	/**
	 * 根据筛选条件查询客户
	 * 
	 * @param appId 项目ID
	 * @param conditions 条件列表
	 * @param customerSortByUpdatedat 是否根据更新时间排序
	 * @param startIndex 开始索引
	 * @param count 记录条数
	 * @return
	 */
	List<Customer> findCustomersByConditions(String appId, List<Condition> conditions, String customerSortByUpdatedat,
			Integer startIndex, Integer count);

	/**
	 * 根据手机号和邮箱查询客户
	 * 
	 * @param appId
	 * @param phone
	 * @param email
	 * @param startIndex
	 * @param count
	 * @return
	 */
	List<Customer> findCustomersByExactMatch(String appId, String phone, String email, int startIndex, int count);


	/**
	 * 根据手机号和邮箱查询客户
	 *
	 * @param params
	 * @return
	 */
	List<Customer> findCustomersByExactMatchV2(CustomerSearchVO params);

	/**
	 * 获取分群客户
	 * 
	 * @param appId
	 * @param swarmId
	 * @param groupId
	 * @param kuickUserIds
	 * @param keyword
	 * @param startIndex
	 * @param count
	 * 
	 * @return
	 */
	List<Customer> findCustomersBySwarm(String appId, String swarmId, String groupId, List<String> kuickUserIds,
			String keyword, int startIndex, int count);

	/**
	 * 根据属性查询分群客户
	 * 
	 * @param appId
	 * @param swarmId
	 * @param attributesMap
	 * @param startIndex
	 * @param count
	 * @return
	 */
	List findCustomersBySwarmAndAttributes(String appId, String swarmId, Map<String, Integer> attributesMap, int startIndex,
			int count);

	/**
	 * 根据分群查询客户ID
	 * 
	 * @param appId
	 * @param swarmId
	 * @param startIndex
	 * @param count
	 * @return
	 */
	List<CustomerId> findCustomerIdsBySwarm(String appId, String swarmId, int startIndex, int count);

	/**
	 * 获取项目总客户数据
	 * 
	 * @param appId
	 * @return
	 */
	long getCustomerCountByAppId(String appId, boolean withSales);

	/**
	 * 根据标签查询客户
	 * @param appId
	 * @param kuickUserIds
	 * @param tags
	 * @param greateTagCount
	 * 
	 * @return
	 */
	@Deprecated
	List<Customer> findCustomersByTag(String appId, List<String> kuickUserIds, List<String> tags, Integer greateTagCount);

	/**
	 * 查询标签的客户数
	 * 
	 * @param appId
	 * @param tag
	 * @return
	 */
	@Deprecated
	long getCustomerCountByTag(String appId, String tag);

	/**
	 * 根据属性查询客户
	 * 
	 * @param appId
	 * @param startIndex
	 * @param count
	 * @param attributesMap
	 * @return
	 */
	List findCustomersByAttributes(String appId, int startIndex, int count, Map<String, Integer> attributesMap);

	/**
	 * 查询项目下的客户ID
	 * 
	 * @param appId
	 * @param startIndex
	 * @param count
	 * @return
	 */
	List<CustomerId> findCustomerIds(String appId, int startIndex, int count);
	
	/**
	 * 获取几个分群客户去重总数
	 * 
	 * @param appId
	 * @param swarmIdsList
	 * @return
	 */
	long getSwarmMemberCountBySwarmAndAppId(String appId, List<String> swarmIdsList);

	/**
	 * 获取客户详情
	 * 
	 * @param customerId
	 * @return
	 */
	Customer getCustomerById(String customerId);

	/**
	 * 获取DealUser关联的客户
	 * 
	 * @param dealUserId
	 * @return
	 */
	List<Customer> findCustomerByDealUserId(String dealUserId);

	/**
	 * 根据DealUserID列表获取客户列表
	 * 
	 * @param dealuserIdList
	 * @return
	 */
    List<CustomerWithDealuserId> getCustomerListByDealuserIds(List<String> dealuserIdList);
	
    /**
     * 获取合并之前客户总数
     * 
     * @param appId
     * @return
     */
    long getRawCustomerCount(String appId);

    /**
     * 获取原始客户
     * 
     * @param appId
     * @param startIndex
     * @param count
     * @return
     */
    List<Customer> getRawCustomers(String appId, Integer startIndex, Integer count);

    /**
     * 获取合并之后的客户
     * 
     * @param appId
     * @param customerIds
     * @param startIndex
     * @param count
     * @return
     */
	List<Customer> findCustomersByMergeCustomserIds(String appId, String[] customerIds, int startIndex, int count);
	
	
	/**
	 * 分页查询客户
	 * 
	 * @param appId
	 * @param kuickUserIds
	 * @param phone
	 * @param customerGroupId
	 * @param sortByUpdateTime
	 * @param startIndex
	 * @param count
	 * @return
	 */
	List<Customer> findCustomersBySales(String appId, String[] kuickUserIds, String phone, String email, 
			String customerGroupId, boolean sortByUpdateTime,
			int startIndex, int count);

	/**
	 * 查询客户，根据客户ID列表
	 * 
	 * @param appId
	 * @param customerIds
	 * @param startIndex
	 * @param count
	 * @return
	 */
	List<Customer> findCustomerByIdsAndPage(String appId, String[] customerIds, int startIndex, int count);

	/**
	 * 查询客户
	 * 
	 * @param customerIds
	 * @return
	 */
	List<Customer> findCustomerIds(String[] customerIds);

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
	 * 更新扩展字段
	 * 
	 * @param id
	 * @param extensions
	 */
	void updateExtensions(String id, Map<String, String> extensions);

	/**
	 * 查询客户
	 * 
	 * @param appId
	 * @param kuickUserIds
	 * @param hasPhone
	 * @param condition
	 * @param customerGroupId
	 * @return
	 */
    List<Customer> findByAppIdAndKuickUserIds(String appId, List<String> kuickUserIds, int hasPhone, String condition, String customerGroupId);

	List<CustomerId> findByTag(String appId, String tag, long startIndex, int count);

    long countByCondition(String appId, List<Condition> conditions);


}
