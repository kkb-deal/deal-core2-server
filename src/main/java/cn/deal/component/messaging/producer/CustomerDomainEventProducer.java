package cn.deal.component.messaging.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import cn.deal.component.domain.CustomerDomainEvent;
import cn.deal.component.utils.JsonUtil;

@Component
public class CustomerDomainEventProducer {
	private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Value("${spring.cloud.stream.bindings.customer_domain_event_output.destination}")
    private String topic;

    @Autowired
    @Qualifier("rawKafkaTemplate")
    private KafkaTemplate<String, String> kafkaTemplate;
    
    public void send(CustomerDomainEvent message) {
        try {
            logger.info("send CustomerDomainEventProducer begin: {}", message);

            kafkaTemplate.send(topic, message.getDomainId(), JsonUtil.toJson(message));
            logger.info("send CustomerDomainEventProducer success");
        } catch (Throwable e) {
            logger.error("send CustomerDomainEvent fail:", e);
        }
    }
}
