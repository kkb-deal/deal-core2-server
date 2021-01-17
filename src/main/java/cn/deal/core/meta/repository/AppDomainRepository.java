package cn.deal.core.meta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cn.deal.core.meta.domain.AppDomain;

public interface AppDomainRepository extends JpaRepository<AppDomain, String>{

	@Query("select ad from AppDomain ad where ad.appId=:appId")
	AppDomain getAppDomainByAppId(@Param("appId") String appId);
	
}