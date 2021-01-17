package cn.deal.component.messaging.producer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import cn.deal.component.domain.DealUserDomainEvent;
import cn.deal.component.utils.JsonUtil;

@Component
public class DealuserDomainEventProducer {

    public enum IsPush {
        /**
         * 开起
         */
        OPEN("open"),
        CLOSE("close");
    	
        private String val;
        public String getVal() {
            return val;
        }
        IsPush (String val) {
            this.val = val;
        }
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Value("${dealuser.domain.event.isPush}")
    private String isPush;
    
    @Value("${spring.cloud.stream.bindings.dealuser_domain_event_output.destination}")
    private String topic;

    @Autowired
    @Qualifier("rawKafkaTemplate")
    private KafkaTemplate<String, String> kafkaTemplate;

    public void send(DealUserDomainEvent message) {
        if (StringUtils.equals(isPush, IsPush.OPEN.val)) {
            try {
                logger.info("send DealUserDomainEvent begin: {}", message);

                kafkaTemplate.send(topic, message.getDomainId(), JsonUtil.toJson(message));
                logger.info("send DealUserDomainEvent success");
            } catch (Throwable e) {
                logger.error("send DealUserDomainEvent fail:", e);
            }
        }
    }
}
