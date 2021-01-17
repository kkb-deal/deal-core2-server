package cn.deal.core.dealmeta.repository;

import cn.deal.core.dealmeta.domain.DealMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DealMetaDataRepository  extends JpaRepository<DealMetaData, String> {

    /**
     * 获取成交记录元数据
     * @param appId 项目id
     * @return
     */
    List<DealMetaData> findAllByAppId(String appId);

    /**
     * 获取成交记录元数据选项值
     * @param appId 项目id
     * @param name 元数据名称
     * @return
     */
    DealMetaData findByAppIdAndName(String appId, String name);
}
