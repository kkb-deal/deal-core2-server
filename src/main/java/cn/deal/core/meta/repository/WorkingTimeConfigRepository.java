package cn.deal.core.meta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cn.deal.core.meta.domain.WorkingTimeConfig;

import java.util.List;


@Repository
public interface WorkingTimeConfigRepository extends JpaRepository<WorkingTimeConfig, String> {


    /**
     * 根据项目id获取配置的工作时间段
     * @param appId
     * @return
     */
    @Query("select wtc from WorkingTimeConfig wtc where wtc.appId=:appId")
    List<WorkingTimeConfig> getWorkingTimeConfigByAppId(@Param("appId") String appId);
}
