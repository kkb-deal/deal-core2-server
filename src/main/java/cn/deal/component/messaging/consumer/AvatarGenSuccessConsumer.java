package cn.deal.component.messaging.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import cn.deal.component.domain.AvatarGenSuccess;
import cn.deal.component.messaging.channel.AvatarGenSuccessChannel;
import cn.deal.component.utils.JsonUtil;
import cn.deal.core.customer.engine.listeners.AvatarGenListener;

@Component
public class AvatarGenSuccessConsumer {

	private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private AvatarGenListener avatarGenListener;
    
    @StreamListener(AvatarGenSuccessChannel.AVATAR_GEN_SUCCESS_INPUT)
    public void receive(AvatarGenSuccess message) throws Throwable{
    	logger.info("receive avatarGenSuccess message:{}", message);
    	
    	try {
    		avatarGenListener.avatarGenSuccess(message.getTaskId(), message.getAvatarUrl(), message.getState());
    		
    		logger.info("handle avatarGenSuccess message ok!, msg:{}", message);
    	} catch(Throwable e) {
    		logger.error("Error in handle avatarGenSuccess message:" + message, e);
    		throw e;
    	}
    }

}
