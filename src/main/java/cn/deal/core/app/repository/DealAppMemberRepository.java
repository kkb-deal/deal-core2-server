package cn.deal.core.app.repository;

import cn.deal.core.app.domain.AppMember;
import org.hibernate.annotations.Cache;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;


@CacheConfig(cacheNames = "appmember", cacheManager = "redis")
public interface DealAppMemberRepository extends JpaRepository<AppMember, String> {

    /**
     * @param appId
     * @param kuickUserId
     * @return DealAppMember    返回类型
     * @throws
     * @Title: findByAppIdAndKuickUserId
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    @Query(nativeQuery = true, value = "select app.* from app_member app where app.appId=?1 and  kuickUserId=?2 and status=0 limit 0,1")
    public AppMember findByAppIdAndKuickUserId(@Param("appId") String appId, @Param("kuickUserId") int kuickUserId);

    @Query(nativeQuery = true, value = "select * from app_member where appid = ?1 and kuickUserId = ?2")
    AppMember findOneByAppIdAndKuickUserId(String appId, Integer kuickUserId);

    @Query(nativeQuery = true, value = "select a.* from app_member a, user u where a.kuickUserId = u.id and u.status < 2 and a.appId = ?1 " +
            "and a.status = 0 and a.departmentId  = ?2")
    List<AppMember> findAllByAppIdAndDepartmentId(String appId, String departmentId);

    List<AppMember> findAllByAppIdAndStatusAndKuickUserIdIn(String appId, Integer status, Iterable<Integer> kuickUserIds);

    @Modifying
    @Query("update AppMember a set a.departmentId = null where a.appId = ?1 and a.departmentId = ?2 and a.kuickUserId in (?3)")
    @CacheEvict(key = "'appMember:appId:'+#p0")
    int updateDepartmentIdByAppIdAndDepartmentIdAndKuickUserId(String appId, String departmentId, Iterable<Integer> kuickUserIds);

    @Modifying
    @Query("update AppMember a set a.departmentId = ?3 where a.appId = ?1 and a.departmentId in (?2)")
    @CacheEvict(key = "'appMember:appId:'+#p0")
    int updateDepartmentIdByAppIdAndDepartmentIds(String appId, Set<String> departmentIds, String departmentId);

    List<AppMember> findAllByAppIdAndDepartmentIdIn(String appId, List<String> departmentIds);

    @Cacheable(key = "'appMember:appId:'+#p0")
    @Query(nativeQuery = true, value = "select a.* from app_member a, user u where a.kuickUserId = u.id and u.status < 2 and a.appId = ?1 and a.status = ?2")
    List<AppMember> findAllByAppIdAndStatus(String appId, Integer status);

    @Override
    @CacheEvict(key = "'appMember:appId:'+#p0.appId")
    <S extends AppMember> S saveAndFlush(S entity);

    @Override
    @CacheEvict(key = "'appMember:appId:'+#p0.appId")
    <S extends AppMember> S save(S entity);

    AppMember findByAppIdAndStatusAndKuickUserId(String appId, Integer value, int kuickUserId);

    List<AppMember> findAllByAppIdAndKuickUserIdIn(String appId, List<Integer> kuickUserIds);
}
