package cn.deal.component;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.util.UriEncoder;

import cn.deal.component.exception.BusinessException;

@Component
public class AvatarGenComponent {
    private Logger logger = LoggerFactory.getLogger(AvatarGenComponent.class);

    @Value("${avatar.gen.server.inner.url}")
    private String avatarGenServerInnerURL;

    private static String[] COLORS = {"E1C33C", "E18C50", "E66978", "E17DC8", "AA6EE6", "8296E6", "1BB1A1", "6EB441"};
    
    public static String DEFAULT_HEAD = "https://img-prod.kuick.cn/user/header/guest.png";
    
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 随机生成一个颜色
     * 
     * @param name
     * @return
     */
    public static String randColor(String name) {
        int index = Math.abs(name.hashCode() % COLORS.length);
        String bgColor = COLORS[index];
        
        return bgColor;
    }
    
    /**
     * 根据filterId获取筛选条件
     * @param appId
     * @param filterId
     * @return
     * @throws IOException
     */
    public String generater(String name){
    	if (StringUtils.isBlank(name)) {
    		return DEFAULT_HEAD;
    	}
    	
    	String encodeName = UriEncoder.encode(name);
    	String bgColor = randColor(name);
        String url = avatarGenServerInnerURL + "/api/generater/headportrait?name=" + encodeName + "&bgColor=" + bgColor;
        logger.info("generater url：{}", url);
        
        String result = null;
        
        try{
            result = restTemplate.getForObject(url, String.class);
            logger.info("generater result: {}", result);
            
            return result;
        } catch (Exception e){
            logger.error("generater failed: {}", e.getMessage());
            throw new RuntimeException("generater failed");
        }
    }
    

    /**
     * 异步生成头像
     * 
     * @param name
     * @param state
     * @param source
     * @return
     */
    public String asyncGenerater(String name, String state, String source){
    	if (StringUtils.isBlank(name)) {
    		return DEFAULT_HEAD;
    	}
    	
    	String bgColor = randColor(name);
        String url = avatarGenServerInnerURL + "/api/generater/headportrait";
        logger.info("asyncGenerater url：{}", url);
        
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();

        data.add("name", name);
        data.add("bgColor", bgColor);
        data.add("async", 1);
        data.add("state", state);
        data.add("source", source);

        logger.info("asyncGenerater.params: {}", data);
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<MultiValueMap<String, Object>>(data, headers);

        try {
            String result = restTemplate.postForObject(url, entity, String.class);
            logger.info("asyncGenerater.result: {}", result);
            return result;
        } catch (Exception e){
            logger.error(e.getMessage(), e);
            throw new BusinessException("async_gen_avatar_fail", "异步生成头像任务提交失败：" + e.getMessage());
        }
    }
}
