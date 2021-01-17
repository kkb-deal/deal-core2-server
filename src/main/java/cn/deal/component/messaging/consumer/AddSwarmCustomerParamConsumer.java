package cn.deal.component.messaging.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import cn.deal.component.domain.AddSwarmCustomerParam;
import cn.deal.component.messaging.channel.SwarmCustomerParamChannel;
import cn.deal.core.customerswarm.service.CustomerSwarmService;

@Component
public class AddSwarmCustomerParamConsumer {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private CustomerSwarmService customerSwarmService;
    
    @StreamListener(SwarmCustomerParamChannel.ADD_SWARM_CUSTOMER_PARAM_INPUT)
    public void receive(AddSwarmCustomerParam message){
        logger.info(" AddSwarmCustomerParamConsumer receive msg: {}", message);
        try {
            customerSwarmService.addSwarmMember(message.getAppId(), message.getSwarmId(), message.getCustomerId());
            logger.info(" AddSwarmCustomerParamConsumer success");
        } catch (Exception e) {
            logger.info(" AddSwarmCustomerParamConsumer failed: {}", e.getMessage());
            e.printStackTrace();
        }
    }

}
