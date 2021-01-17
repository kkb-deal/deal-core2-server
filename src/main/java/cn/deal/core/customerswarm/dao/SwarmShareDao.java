package cn.deal.core.customerswarm.dao;

import java.util.List;

import cn.deal.core.customerswarm.domain.SwarmShare;

public interface SwarmShareDao {
	
	int batchInsert(String appId, String swarmId, int targetType, List<String> targetIds);

	List<SwarmShare> fetchBySwarmId(String appId, String swarmId);

	void deleteBySwarmIdAndTargetId(String appId, String swarmId, int targetType, List<String> idList);

	int deleteBySwarmIdAndTargetType(String appId, String swarmId, int targetType);

	int deleteBySwarmId(String appId, String swarmId);

	List<SwarmShare> fetchByTargetIdAndSwarmId(String appId, String kuickUserId, String swarmId);
}
