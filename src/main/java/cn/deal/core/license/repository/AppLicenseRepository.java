package cn.deal.core.license.repository;

import cn.deal.core.license.domain.AppLicense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface AppLicenseRepository extends JpaRepository<AppLicense, String> {

    @Query(nativeQuery=true,value="select DATE_FORMAT(createdAt, '%Y-%m-%d %H:%i:%s') createdAt, app.* from app_license app where app.appId=?1  order by app.createdAt desc")
	public List<AppLicense> findByAppId(@Param("appId") String appId );

   	public AppLicense findByIdAndAppId(@Param("id") String id,@Param("appId") String appId );

    /**
     * 根据ids批量删除
     * @param ids
     */
   	@Modifying
    @Query("delete from AppLicense al where al.appId = ?1 and al.id in (?2)")
    int deleteBatchByIds(String appId, List ids);

    int countById(String appLicenseId);

    @Modifying
    @Query("delete from AppLicense where id = ?1")
    int deleteById(String id);
}
