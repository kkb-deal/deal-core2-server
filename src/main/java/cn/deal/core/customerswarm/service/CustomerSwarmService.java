package cn.deal.core.customerswarm.service;

import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customerswarm.domain.CustomerSwarm;

import java.util.List;
import java.util.Map;

public interface CustomerSwarmService {
	
	CustomerSwarm create(String appId, String kuickUserId, String name, String photoUrl, String comment, Integer type, String filterId, Map<String, String> data);
	
	boolean delete(String appId, String swarmId, String kuickUserId);
	
	CustomerSwarm update(String appId, String swarmId, String kuickUserId, String name, String photoUrl, String comment, String filterId, Map<String, String> data);
	
	List<Map<String, Object>> list(String appId, String kuickUserId, int startIndex, int count, String keyword);

	Map getWithExt(String appId, String id);
	
	Map<String,Object> shareSwarm(String appId, String swarmId, String kuickUserId, int targetType, String targetIds);
	
	boolean deleteShareSwarm(String appId, String swarmId, String kuickUserId);
	
	Map<String,Object> fetchSwarmSharesBySwarmId(String appId, String kuickUserId, String swarmId);
	
	boolean addCustomers(String appId, String swarmId, String kuickUserId, String customerIds, int sync, String userAgent);
	
	boolean removeCustomers(String appId, String swarmId, String kuickUserId, String customerIds, String userAgent);
	
	boolean swarmMemberTransfer(String appId, String kuickUserId, String fromSwarmId, String toSwarmId, String customerIds);

	List<Customer> getSwarmMembers(String appId, String swarmId, String kuickUserId, String customerOwnerId, String groupId, String keyword, int startIndex, int count);

	void handleMergeCustomer(String appId, String newCustomerId, List<String> oldCustomerIds);

	Map<String, Object> updateShareSwarm(String appId, String swarmId, String kuickUserId, int targetType, String targetIds);

	void addCustomersWithSwarmName(String appId, String swarmName, String customerIds, String userAgent);

	void addCustomersWithSwarmNameV2(String appId, String swarmName, String swamOwnerId, String customerIds, String userAgent);

	void removeCustomersWithSwarmName(String appId, String swarmName, String customerIds, String userAgent);

	void swarmCustomerCount();

	long getMemberCount(String appId, String swarmId);
	
	int addSwarmMember(String appId, String swarmId, String customerId);

    void moveCustomersWithSwarmName(String appId, String swarmName, String customerIds, String userAgent);

	List<Customer> getSwarmMemberIds(String appId, String swarmId, String kuickUserId, String customerOwnerId);
}
