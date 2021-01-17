package cn.deal.component.kuick.impl;

import cn.deal.component.config.ServiceConfig;
import cn.deal.component.kuick.ConferenceGroupMemberService;
import cn.deal.component.kuick.domain.ConferenceGroupMember;
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

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.Map;


@Service
public class ConferenceGroupMemberServiceImpl implements ConferenceGroupMemberService{

    private static final Log log = LogFactory.getLog(ConferenceGroupMemberServiceImpl.class);

    @Autowired
    private ServiceConfig serviceConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Resource
    private RestTemplate kuickApiRestTemplate;

    /**
     * 创建会议组成员记录
     * @param conferenceGroupId
     * @param conferenceId
     * @param accessToken
     */
    @Override
    public void createConferenceGroupMember(String conferenceGroupId, String conferenceId, String accessToken){

        ConferenceGroupMember conferenceGroupMember = null;
        String apiURL = serviceConfig.getKuickApiBaseUrl() + "conference/groups/" + conferenceGroupId + "/members?access_token=" + accessToken;

        MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<String, Object>();
        postParameters.add("conference_id", conferenceId);

        HttpEntity<MultiValueMap<String, Object>> requestEntity  = new HttpEntity<MultiValueMap<String, Object>>(postParameters);

        String resultStr = restTemplate.postForObject(apiURL, requestEntity, String.class);
        log.info("create_conference_group_member_result: " + resultStr);

        if (StringUtils.isNotBlank(resultStr)) {
            JSONObject jsonObj = new JSONObject(resultStr);

            Type clazzType = new TypeToken<ConferenceGroupMember>(){}.getType();
            String json = JsonUtil.getReturnJson(jsonObj);
            conferenceGroupMember = (ConferenceGroupMember) JsonUtil.parseJson(json, clazzType);
            log.info(conferenceGroupMember.toString());
        }
    }


//    /**
//     * 创建会议组成员记录
//     * @param conferenceGroupId
//     * @param conferenceId
//     */
//    @Override
//    public ConferenceGroupMember createConferenceGroupMember(String conferenceGroupId, String conferenceId){
//
//        ConferenceGroupMember conferenceGroupMember = null;
//        String apiURL = serviceConfig.getKuickApiBaseUrl() + "conference/groups/" + conferenceGroupId + "/members";
//
//        MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<String, Object>();
//        postParameters.add("conference_id", conferenceId);
//
//        HttpEntity<MultiValueMap<String, Object>> requestEntity  = new HttpEntity<MultiValueMap<String, Object>>(postParameters);
//
//        String resultStr = restTemplate.postForObject(apiURL, requestEntity, String.class);
//        log.info("create_conference_group_member_result: " + resultStr);
//
//        if (StringUtils.isNotBlank(resultStr)) {
//            JSONObject jsonObj = new JSONObject(resultStr);
//
//            Type clazzType = new TypeToken<ConferenceGroupMember>(){}.getType();
//            String json = JsonUtil.getReturnJson(jsonObj);
//            conferenceGroupMember = (ConferenceGroupMember) JsonUtil.parseJson(json, clazzType);
//            log.info(conferenceGroupMember.toString());
//        }
//        return conferenceGroupMember;
//    }

    @Override
    public Map createConferenceGroupMember(String conferenceGroupId, int conferenceId){
        String apiURL = serviceConfig.getKuickApiBaseUrl() + "conference/groups/" + conferenceGroupId + "/members";

        MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<String, Object>();
        postParameters.add("conference_id", conferenceId);

        HttpEntity<MultiValueMap<String, Object>> requestEntity  = new HttpEntity<MultiValueMap<String, Object>>(postParameters);

        Map map = kuickApiRestTemplate.postForObject(apiURL, requestEntity, Map.class);
        log.info("create_conference_group_member_result_map: " + map);

        return map;
    }
}
