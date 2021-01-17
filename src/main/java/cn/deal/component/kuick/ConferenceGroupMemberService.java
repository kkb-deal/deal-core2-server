package cn.deal.component.kuick;

import cn.deal.component.config.ServiceConfig;
import cn.deal.component.kuick.domain.ConferenceGroup;
import cn.deal.component.kuick.domain.ConferenceGroupMember;
import cn.deal.component.utils.JsonUtil;

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

import java.util.Map;


public interface ConferenceGroupMemberService {

    /**
     * 创建会议组成员记录
     * @param conferenceGroupId
     * @param conferenceId
     * @param accessToken
     */
    void createConferenceGroupMember(String conferenceGroupId, String conferenceId, String accessToken);

    /**
     *
     * 创建会议组成员记录
     * @param conferenceGroupId
     * @param conferenceId
     * @param conferenceGroupId
     * @param conferenceId
     * @return
     */
    Map createConferenceGroupMember(String conferenceGroupId, int conferenceId);
}
