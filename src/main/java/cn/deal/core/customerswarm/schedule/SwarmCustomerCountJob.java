package cn.deal.core.customerswarm.schedule;

import cn.deal.core.customerswarm.service.CustomerSwarmService;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import org.springframework.beans.factory.annotation.Autowired;


public class SwarmCustomerCountJob implements SimpleJob {

	@Autowired
	private CustomerSwarmService customerSwarmService;
	
	@Override
	public void execute(ShardingContext shardingContext) {
		customerSwarmService.swarmCustomerCount();
	}

}
