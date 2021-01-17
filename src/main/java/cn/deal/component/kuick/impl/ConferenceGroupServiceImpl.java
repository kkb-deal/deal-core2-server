package cn.deal.component.kuick.impl;

import cn.deal.component.config.ServiceConfig;
import cn.deal.component.kuick.ConferenceGroupService;
import cn.deal.component.kuick.domain.ConferenceGroup;
import cn.deal.component.utils.JsonUtil;

import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;


@Service
public class ConferenceGroupServiceImpl implements ConferenceGroupService{

    private static final Log log = LogFactory.getLog(ConferenceGroupServiceImpl.class);

    /**
     * 默认父分组id
     */
    private static final String DEFAULT_PARENT_ID = "-1";

    @Autowired
    private ServiceConfig serviceConfig;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 创建项目组记录
     * @param kuickUserId
     * @param name
     * @param parentId
     * @param accessToken
     * @return
     */
    public ConferenceGroup createConferenceGroup(String kuickUserId, String name, String parentId, String accessToken){

        ConferenceGroup conferenceGroup = null;
        String apiURL = serviceConfig.getKuickApiBaseUrl() + "conference/groups?access_token=" + accessToken;
        log.info("---ConferenceGroup------apiURL :"+apiURL);
        if(StringUtils.isBlank(parentId)){
            parentId = DEFAULT_PARENT_ID;
        }

        MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<String, Object>();
        postParameters.add("user_id", kuickUserId);
        postParameters.add("name", name);
        postParameters.add("parent_id", parentId);

        HttpEntity<MultiValueMap<String, Object>> requestEntity  = new HttpEntity<MultiValueMap<String, Object>>(postParameters);

        String resultStr = restTemplate.postForObject(apiURL, requestEntity, String.class);
        log.info("create_conference_group_result: " + resultStr);

        if (StringUtils.isNotBlank(resultStr)) {
            JSONObject jsonObj = new JSONObject(resultStr);

            Type clazzType = new TypeToken<ConferenceGroup>(){}.getType();
            String json = JsonUtil.getReturnJson(jsonObj);
            conferenceGroup = (ConferenceGroup) JsonUtil.parseJson(json, clazzType);
        }

        return conferenceGroup;
    }
    
}
