package cn.deal.component.messaging.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import cn.deal.component.domain.AppMemberDomainEvent;
import cn.deal.component.utils.JsonUtil;

@Component
public class AppMemberDomainEventProducer {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Value("${spring.cloud.stream.bindings.appmember_domain_event_output.destination}")
    private String topic;

    @Autowired
    @Qualifier("rawKafkaTemplate")
    private KafkaTemplate<String, String> kafkaTemplate;

    public void send(AppMemberDomainEvent message) {
        try {
            logger.info("send AppMemberDomainEvent begin: {}", message);

            kafkaTemplate.send(topic, message.getDomainId(), JsonUtil.toJson(message));
            logger.info("send AppMemberDomainEvent success");
        } catch (Throwable e) {
            logger.error("send AppMemberDomainEvent fail:", e);
        }
    }
}
