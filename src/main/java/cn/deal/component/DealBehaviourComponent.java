package cn.deal.component;

import cn.deal.component.kuick.domain.KuickUser;
import cn.deal.component.utils.DateUtils;
import cn.deal.component.utils.JsonUtil;
import cn.deal.component.utils.UserAgentUtils;
import cz.mallat.uasparser.UserAgentInfo;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

@Component
public class DealBehaviourComponent {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${deal.behaviour.inner.baseurl}")
    private String dealBehaviourBaseUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Resource
    private RestTemplate kuickApiRestTemplate;

    public void createAppMemberBehaviourLog(String appId, int kuickUserId, String action, String desc, KuickUser kuickUser,
                                            String userAgentStr) throws Exception {
        String url = dealBehaviourBaseUrl + "app/" + appId + "/member/" + kuickUserId + "/behaviour-datas";

        logger.info("createAppMemberBehaviourLog.log -> {}", url);

        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, Object> data = new LinkedMultiValueMap();

        data.add("kuick_user_name", kuickUser.getName());
        data.add("action", action);
        data.add("description", desc);
        data.add("content", "{kuick_user_id:" + kuickUserId + "}");
        data.add("when", new Date());

        if(StringUtils.isNotBlank(userAgentStr)){
            UserAgentInfo ua = UserAgentUtils.uasParser.parse(userAgentStr);
            if(ua != null){
                data.add("client_name", ua.getUaFamily());
                data.add("client_version", ua.getBrowserVersionInfo());
                data.add("os", ua.getOsFamily());
            }
        }

        logger.info("createAppMemberBehaviourLog.param.map: {}", data);
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity(data, headers);

        try {
            String result = restTemplate.postForObject(url, entity, String.class);
            logger.info("createAppMemberBehaviourLog.result: {}", result);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
    }

    public void createCustomerBehaviourLog(String appId, String kuickUserId, String customerId, String customerName,
                                           String action, String description, String content, String accessToken){
        String customerBehaviourApiURL = dealBehaviourBaseUrl + "app/" + appId + "/customer/" + customerId + "/behaviour-datas" +
                "?access_token=" + accessToken;
        logger.info("customerBehaviourApiURL: " + customerBehaviourApiURL);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
        data.add("kuick_user_id", kuickUserId);

        JSONObject dataObj = new JSONObject();
        dataObj.put("action", action);
        dataObj.put("customerName", customerName);
        dataObj.put("desc", description);
        dataObj.put("content", content);
        dataObj.put("when", DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        StringBuilder datas = new StringBuilder();
        datas.append("[").append(dataObj.toString()).append("]");
        data.add("datas", datas.toString());
        logger.info("customerBehaviourApiParams: " + data.toSingleValueMap());
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<MultiValueMap<String, Object>>(data, headers);
        String jsonStr = restTemplate.postForObject(customerBehaviourApiURL, entity, String.class);
        logger.info("customerBehaviourApiResult: " + jsonStr);
    }

    /**
     * 发送客户转让事件
     * @param appId appid
     * @param kuickUserId 销售id
     * @param action 行为
     * @param desc  描述
     * @param contentMap content
     * @param kuickUser 当前登录人
     * @param userAgentStr 浏览器userAgent
     * @throws Exception
     */
    public void createBehaviourLog(String appId, int kuickUserId, String action, String desc, KuickUser kuickUser, Map<String, Object> contentMap,
                                            String userAgentStr) throws Exception {
        String url = dealBehaviourBaseUrl + "app/" + appId + "/member/" + kuickUserId + "/behaviour-datas";

        logger.info("createAppMemberBehaviourLog.log -> {}", url);

        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, Object> data = new LinkedMultiValueMap();

        data.add("kuick_user_name", kuickUser.getName());
        data.add("action", action);
        data.add("description", desc);

        data.add("content", JsonUtil.toJson(contentMap));
        data.add("when", DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));

        if(StringUtils.isNotBlank(userAgentStr)){
            UserAgentInfo ua = UserAgentUtils.uasParser.parse(userAgentStr);
            if(ua != null){
                data.add("client_name", ua.getUaFamily());
                data.add("client_version", ua.getBrowserVersionInfo());
                data.add("os", ua.getOsFamily());
            }
        }

        logger.info("createAppMemberBehaviourLog.param.map: {}", data);
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity(data, headers);

        try {
            String result = restTemplate.postForObject(url, entity, String.class);
            logger.info("createAppMemberBehaviourLog.result: {}", result);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
    }

}
