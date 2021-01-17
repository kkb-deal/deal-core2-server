package cn.deal.core.customerswarm.dao;

import java.util.List;
import java.util.Map;

import cn.deal.core.customerswarm.domain.SwarmMember;

public interface SwarmMemberDao {

	int batchInsert(String appId, String swarmId, List<String> newCustomerIdList);

	int batchDelete(String appId, String swarmId, List<String> custIdList);

	List<SwarmMember> fetchByCustomerIds(String appId, List<String> oldCustomerIds);

	int batchInsert(String appId, List<String> swarmIds, String customerId);

	int batchDelete(List<SwarmMember> members);

	int getCountBySwarmId(String id);
	
	long getMergedCountBySwarmIds(List<String> ids);
	
	Map<String, Integer> countEverySwarmMember(List<String> swarmIds);
	
	int singleInsert(String appId, String swarmId, String customerId);
}
