package cn.deal.core.app.repository;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cn.deal.core.app.domain.Department;

import java.util.List;

/**
 * @ClassName DepartmentRepository
 * @Description TODO
 **/
@Repository
@CacheConfig(cacheNames = "department", cacheManager = "redis")
public interface DepartmentRepository extends JpaRepository<Department, String> {

    List<Department> findAllByAppIdAndName(String appId, String name);

    Department findFirstByAppIdAndName(String appId, String name);

    @Cacheable(key = "'department:appId:'+#p0")
    List<Department> findAllByAppId(String appId);

    List<Department> findAllByParentId(String departmentId);
}
