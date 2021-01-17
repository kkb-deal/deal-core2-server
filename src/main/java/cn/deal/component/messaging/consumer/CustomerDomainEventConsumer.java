package cn.deal.component.messaging.consumer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import cn.deal.component.domain.CustomerDomainEvent;
import cn.deal.component.messaging.channel.CustomerDomainEventChannel;
import cn.deal.component.utils.JsonUtil;
import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customerswarm.service.CustomerSwarmService;

@Component
public class CustomerDomainEventConsumer {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private CustomerSwarmService customerSwarmService;
    
    @StreamListener(CustomerDomainEventChannel.CUSTOMER_DOMAIN_EVENT_INPUT)
    public void receive(String message){
        logger.info(" CustomerDomainEventChannel receive msg: {}", message);
        try {
            CustomerDomainEvent customerDomainEvent = JsonUtil.fromJson(message, CustomerDomainEvent.class);
            if(customerDomainEvent != null){
                String eventType = customerDomainEvent.getEventType();
                if("merge".equals(eventType)){
                    handleMerge(customerDomainEvent);
                }
            }
            logger.info(" CustomerDomainEventChannel receive success");
        } catch (Exception e) {
            logger.info(" CustomerDomainEventChannel failed: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleMerge(CustomerDomainEvent event) throws IOException{
        String newCustomerId = event.getBody().getId();
        String appId = event.getBody().getAppId();
        List<String> oldCustomerIds = new ArrayList<>();
        List<Customer> oldBodys = event.getOldBodys();
        for (Customer customer : oldBodys) {
            oldCustomerIds.add(customer.getId());
        }
        if (oldCustomerIds.size() > 0) {
            customerSwarmService.handleMergeCustomer(appId, newCustomerId, oldCustomerIds);
        }
    }
}
