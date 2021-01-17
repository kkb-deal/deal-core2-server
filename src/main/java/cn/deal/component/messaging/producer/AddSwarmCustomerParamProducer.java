package cn.deal.component.messaging.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import cn.deal.component.domain.AddSwarmCustomerParam;
import cn.deal.component.messaging.channel.SwarmCustomerParamChannel;

@Component
public class AddSwarmCustomerParamProducer {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private SwarmCustomerParamChannel channel;

    public void send(AddSwarmCustomerParam message) {
        try {
            logger.info("send AddSwarmCustomerParam begin: {}", message);
            channel.output().send(MessageBuilder.withPayload(message).build());
            logger.info("send AddSwarmCustomerParam success");
        } catch (Throwable e) {
            logger.error("send AddSwarmCustomerParam failed:", e);
        }
    }
}
