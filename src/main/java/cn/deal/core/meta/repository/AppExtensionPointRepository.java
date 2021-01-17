package cn.deal.core.meta.repository;

import cn.deal.core.meta.domain.AppExtensionPoint;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@CacheConfig(cacheNames = "extpoint", cacheManager = "local")
public interface AppExtensionPointRepository extends JpaRepository<AppExtensionPoint, String>{

	@Query(" select ep from AppExtensionPoint ep where ep.appId=:appId")
	List<AppExtensionPoint> getExtensionPointsByAppId(@Param("appId")String appId);

	@Cacheable
	List<AppExtensionPoint> findByAppIdAndPlatformAndTypeAndModule(String appId, String platform, String type, String module);
}
