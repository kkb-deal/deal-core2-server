package cn.deal.component.kuick;

import cn.deal.component.kuick.domain.ConferenceGroup;


public interface ConferenceGroupService {

    /**
     * 创建项目组记录
     * @param kuickUserId
     * @param name
     * @param parentId
     * @param accessToken
     * @return
     */
    ConferenceGroup createConferenceGroup(String kuickUserId, String name, String parentId, String accessToken);
}
