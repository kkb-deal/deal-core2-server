package cn.deal.component.messaging.channel;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.MessageChannel;

public interface AvatarGenSuccessChannel {

    String AVATAR_GEN_SUCCESS_INPUT = "avatar_gen_success_input";
    
    @Input(AVATAR_GEN_SUCCESS_INPUT)
    MessageChannel input();
}
