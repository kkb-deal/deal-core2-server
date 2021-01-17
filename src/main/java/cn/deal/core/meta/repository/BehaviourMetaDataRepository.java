package cn.deal.core.meta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cn.deal.core.meta.domain.BehaviourMetaData;


public interface BehaviourMetaDataRepository extends JpaRepository<BehaviourMetaData, String> {

    /**
     * 获取行为元数据信息
     * @param appId 项目id
     * @param action 行为Action
     * @param name 行为属性名
     * @return 行为元数据信息
     */
    @Query("select b from BehaviourMetaData b where b.appId=?1 and b.action=?2 and b.name=?3 ")
    BehaviourMetaData getBehaviourMetaDataByAppIdAndActionAndName(String appId, String action, String name);
}
