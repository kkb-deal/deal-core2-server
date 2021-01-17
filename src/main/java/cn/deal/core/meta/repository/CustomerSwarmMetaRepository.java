package cn.deal.core.meta.repository;


import cn.deal.core.meta.domain.CustomerSwarmMeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 客户分群元数据
 */
public interface CustomerSwarmMetaRepository extends JpaRepository<CustomerSwarmMeta, String> {

    /**
     * 根据appid查询非禁用并且可见的客户分群原信息
     * @param appId
     * @return
     */
    List<CustomerSwarmMeta> findByAppIdAndStatusTrueAndVisibleTrue(String appId);
}
