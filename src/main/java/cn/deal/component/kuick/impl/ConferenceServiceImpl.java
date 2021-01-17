package cn.deal.component.kuick.impl;

import java.lang.reflect.Type;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.google.gson.reflect.TypeToken;

import cn.deal.component.config.ServiceConfig;
import cn.deal.component.kuick.ConferenceService;
import cn.deal.component.kuick.domain.Conference;
import cn.deal.component.utils.JsonUtil;


@Service
public class ConferenceServiceImpl implements ConferenceService {

    private static final Logger logger = LoggerFactory.getLogger(ConferenceServiceImpl.class);

    @Autowired
    private ServiceConfig serviceConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Resource
    private RestTemplate kuickApiRestTemplate;

    /**
     * 创建会议记录
     * @param kuickUserId
     * @param name
     * @param description
     * @return
     */
    @Override
    public Conference createConference(String kuickUserId, String name, String description, String accessToken){

        Conference conference = null;
        String apiURL = serviceConfig.getKuickApiBaseUrl() + "conference/data/create?access_token=" + accessToken;
        logger.info("------Conference---apiURL :"+apiURL);
        MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<String, Object>();
        postParameters.add("user_id", kuickUserId);
        postParameters.add("conference_title", name);
        postParameters.add("conference_content", description);

        HttpEntity<MultiValueMap<String, Object>> requestEntity  = new HttpEntity<MultiValueMap<String, Object>>(postParameters);

        String resultStr = restTemplate.postForObject(apiURL, requestEntity, String.class);
        logger.info("create_conference_result: " + resultStr);

        if (StringUtils.isNotBlank(resultStr)) {
            JSONObject jsonObj = new JSONObject(resultStr);

            Type clazzType = new TypeToken<Conference>(){}.getType();
            String json = JsonUtil.getReturnJson(jsonObj);
            conference = (Conference) JsonUtil.parseJson(json, clazzType);
        }

        return conference;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Map<String, Object> joinConference(int conferenceId, int kuickUserId) {
        String url = serviceConfig.getKuickApiBaseUrl() + "conference/joiner/join";

        logger.info("joinConference.url: " + url);

        MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<String, Object>();
        postParameters.add("conference_id", conferenceId);
        postParameters.add("user_id", kuickUserId);
        HttpEntity<MultiValueMap<String, Object>> requestEntity  = new HttpEntity<MultiValueMap<String, Object>>(postParameters);

        Map map = kuickApiRestTemplate.postForObject(url, requestEntity, Map.class);

        logger.info("joinConference.result: " + map);
        return map;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Map<String, Object> updateConferenceSetting(int kuickUserId, int conferenceId, String logoURL, String theme, String bgURL, String type) {
        String url = serviceConfig.getKuickApiBaseUrl() + "conference/data/update";

        MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<String, Object>();
        postParameters.add("user_id", kuickUserId);
        postParameters.add("conference_id", conferenceId);
        postParameters.add("logo_url", logoURL);
        postParameters.add("theme", theme);
        postParameters.add("bg_url", bgURL);
        postParameters.add("type", type);
        HttpEntity<MultiValueMap<String, Object>> requestEntity  = new HttpEntity<MultiValueMap<String, Object>>(postParameters);

        Map map = kuickApiRestTemplate.postForObject(url, requestEntity, Map.class);

        logger.info("updateConferenceSetting.result: " + map);
        return map;
    }

    @Override
    public Conference addConference(int kuickUserId, String name, String description){
        Conference conference = null;
        String apiURL = serviceConfig.getKuickApiBaseUrl() + "conference/data/create";
        logger.info("------Conference---apiURL :"+apiURL);
        MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<String, Object>();
        postParameters.add("user_id", kuickUserId);
        postParameters.add("conference_title", name);
        postParameters.add("conference_content", description);

        HttpEntity<MultiValueMap<String, Object>> requestEntity  = new HttpEntity<MultiValueMap<String, Object>>(postParameters);

        String resultStr = kuickApiRestTemplate.postForObject(apiURL, requestEntity, String.class);
        logger.info("create_conference_result: " + resultStr);

        if (StringUtils.isNotBlank(resultStr)) {
            JSONObject jsonObj = new JSONObject(resultStr);

            Type clazzType = new TypeToken<Conference>(){}.getType();
            String json = JsonUtil.getReturnJson(jsonObj);
            conference = (Conference) JsonUtil.parseJson(json, clazzType);
        }

        return conference;
    }

    @Override
    public void updateConference(int conferenceId, String conferenceTitle, String conferenceContent) {
        String url = serviceConfig.getKuickApiBaseUrl() + "conference/data/update";
        MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<String, Object>();
        postParameters.add("conference_id", String.valueOf(conferenceId));
        postParameters.add("conference_title", conferenceTitle);
        postParameters.add("conference_content", conferenceContent);
        HttpEntity<MultiValueMap<String, Object>> requestEntity  = new HttpEntity<MultiValueMap<String, Object>>(postParameters);
        logger.info(" updateConference url: {}, param: {} ", url, requestEntity);
        String result = kuickApiRestTemplate.postForObject(url, requestEntity, String.class);
        logger.info(" updateConference result: " + result);
    }

}
