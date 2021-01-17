package cn.deal.component.messaging.consumer;

import cn.deal.component.domain.KuickUserDomainEvent;
import cn.deal.component.domain.User;
import cn.deal.component.kuick.repository.UserRepository;
import cn.deal.component.messaging.channel.KuickUserDomainEventChannel;
import cn.deal.component.utils.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;


@Component
public class KuickUserDomainEventConsumer {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserRepository userRepository;

    @StreamListener(KuickUserDomainEventChannel.KUICKUSER_DOMAIN_EVENT_INPUT)
    public void receive(String event){
        logger.info("receive.event: {}", event);
        try {
            String json = getJson(event);
            logger.info("receive.json: {}", json);
            KuickUserDomainEvent kuickUserDomainEvent = JsonUtil.fromJson(json, new TypeReference<KuickUserDomainEvent>() {
                @Override
                public Type getType() {
                    return super.getType();
                }
            });

            handle(kuickUserDomainEvent);
        } catch (Throwable e) {
            logger.error("receive.handle.", e);
            throw e;
        }
    }

    private String getJson(String event) {
        int start = 0;
        if (StringUtils.indexOf(event, "{") != 0) {
            start = StringUtils.indexOf(event, "text/plain") + 11;
        }
        return StringUtils.substring(event, start);
    }

    private void handle(KuickUserDomainEvent event) {
        logger.info("handle.event: {}", event);
        switch (event.getEvent_type()) {
            case "create":
                save(event);
                break;

            case "update":
                save(event);
                break;

            case "delete"
:               delete(event);
                break;

            default:
                logger.info("handle.ignore.type: {}", event.getEvent_type());
        }
    }

    private void save(KuickUserDomainEvent event) {
        User user = new User();
        try {
            BeanUtils.copyProperties(user, event.getBody());
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        userRepository.saveAndFlush(user);
    }

    private void delete(KuickUserDomainEvent event) {
        userRepository.delete(event.getBody().getId());
    }

}
