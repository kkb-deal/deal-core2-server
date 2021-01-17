package cn.deal.core.customerswarm.dao;

import cn.deal.core.customerswarm.domain.CustomerSwarm;

import java.util.List;

public interface CustomerSwarmDao {

	List<CustomerSwarm> getSharedCustomerSwarmList(String appId, String kuickUserId, int startIndex, int count, String keyword);

    List<CustomerSwarm> getSharedAndSelfCustomerSwarmList(String appId, String kuickUserId, int startIndex, int count, String keyword);

    List<CustomerSwarm> getSelfCustomerSwarmList(String appId, String kuickUserId, int startIndex, int count, String keyword);
	List<String> getIdsWithPage(int startIndex, int count);
}
