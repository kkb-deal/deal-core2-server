package cn.deal.component;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import cn.deal.component.domain.PhoneLocation;

@Component
public class Phone2LocationComponent {

	private static final String SOURCE = "dealcore2server";

	private Logger logger = LoggerFactory.getLogger(Phone2LocationComponent.class);

    @Value("${phone2locationserver.baseurl}")
    private String phonelocationServerURL;

    @Autowired
    private RestTemplate restTemplate;
    
    /**
     * 根据手机号查询位置信息
     * 
     * @param appId
     * @param filterId
     * @return
     * @throws IOException
     */
    public PhoneLocation getPhoneLocationAndISP(String phone){
        String url = phonelocationServerURL + "/api/v1.0/location?source=" + SOURCE + "&phone=" + phone;
        logger.info("getPhoneLocationAndISP url：{}", url);
        
        try{
        	PhoneLocation result = restTemplate.getForObject(url, PhoneLocation.class);
            logger.info("getPhoneLocationAndISP result: {}", result);
            
            return result;
        } catch (Exception e){
            logger.error("getPhoneLocationAndISP failed: {}", e.getMessage());
            return null;
        }
    }
	 
}
