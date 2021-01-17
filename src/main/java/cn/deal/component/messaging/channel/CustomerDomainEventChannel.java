package cn.deal.component.messaging.channel;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface CustomerDomainEventChannel {

    String CUSTOMER_DOMAIN_EVENT_INPUT = "customer_domain_event_input";
    
    @Input(CUSTOMER_DOMAIN_EVENT_INPUT)
    SubscribableChannel receive();
}
