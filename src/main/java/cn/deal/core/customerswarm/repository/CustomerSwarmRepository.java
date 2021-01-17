package cn.deal.core.customerswarm.repository;

import cn.deal.core.customerswarm.domain.CustomerSwarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerSwarmRepository extends JpaRepository<CustomerSwarm, String> {

    List<CustomerSwarm> findAllByIdIn(List<String> swarmIds);

}
