package cn.deal.component.messaging.channel;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface SwarmCustomerParamChannel {

    String ADD_SWARM_CUSTOMER_PARAM_OUTPUT = "add_swarm_customer_output";
    String ADD_SWARM_CUSTOMER_PARAM_INPUT = "add_swarm_customer_input";
    
    @Output(ADD_SWARM_CUSTOMER_PARAM_OUTPUT)
    MessageChannel output();
    
    @Input(ADD_SWARM_CUSTOMER_PARAM_INPUT)
    MessageChannel input();
}
