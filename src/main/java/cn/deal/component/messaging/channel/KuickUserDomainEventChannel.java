package cn.deal.component.messaging.channel;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface KuickUserDomainEventChannel {

    String KUICKUSER_DOMAIN_EVENT_INPUT = "kuick_user_domain_event_input";
    
    @Input(KUICKUSER_DOMAIN_EVENT_INPUT)
    SubscribableChannel receive();
}
