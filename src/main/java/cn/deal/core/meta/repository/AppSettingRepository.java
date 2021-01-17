package cn.deal.core.meta.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cn.deal.core.meta.domain.AppSetting;

public interface AppSettingRepository extends JpaRepository<AppSetting, String> {
	
	public List<AppSetting> findByAppId(@Param("appId") String appId);
}
