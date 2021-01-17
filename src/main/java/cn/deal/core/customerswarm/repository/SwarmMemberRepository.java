package cn.deal.core.customerswarm.repository;

import cn.deal.core.customerswarm.domain.SwarmMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SwarmMemberRepository extends JpaRepository<SwarmMember, String> {

    List<SwarmMember> findAllByAppIdAndCustomerIdIn(String appId, String[] customerIds);

    /**
     * 客户出群
     * @param appId   项目id
     * @param swarmId  分群id
     * @param customerId  客户id
     * @return
     */
    int deleteByAppIdAndSwarmIdAndCustomerId(String appId, String swarmId, String customerId);

}
