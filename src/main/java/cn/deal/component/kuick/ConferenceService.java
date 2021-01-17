package cn.deal.component.kuick;

import java.util.Map;

import cn.deal.component.kuick.domain.Conference;


public interface ConferenceService {

    /**
     * 创建会议记录
     * @param kuickUserId
     * @param name
     * @param description
     * @return
     */
    Conference createConference(String kuickUserId, String name, String description, String accessToken);

    Map<String, Object> joinConference(int conferenceId, int kuickUserId);

    Map<String, Object> updateConferenceSetting(int kuickUserId, int conferenceId, String logoURL, String theme, String bgURL, String type);

    Conference addConference(int kuickUserId, String name, String description);
    
    void updateConference(int conferenceId, String conferenceTitle, String conferenceContent);
}
