package cn.deal.core.meta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cn.deal.core.meta.domain.Behaviour;


public interface BehaviourRepository extends JpaRepository<Behaviour, String> {

    /**
     * 根据项目id和action获取行为记录
     * @param appId
     * @param type
     * @param action
     * @return
     */
    @Query("select b from Behaviour b where b.appId=?1 and b.type=?2 and b.action=?3 ")
    Behaviour getBehaviourByAppIdAndTypeAndAction(String appId, Integer type, String action);
}
