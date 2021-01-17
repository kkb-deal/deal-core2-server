package cn.deal.core.permission.repository;

import cn.deal.core.permission.domain.DepartmentPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;


public interface DepartmentPermissionRepository extends JpaRepository<DepartmentPermission, String>{

	List<DepartmentPermission> findAllByAppIdAndDepartmentIdAndDomainTypeIn(String appId, String departmentId, String[] split);

	List<DepartmentPermission> findAllByAppIdAndDepartmentId(String appId, String departmentId);

	void deleteByAppIdAndDepartmentId(String appId, String departmentId);

	int deleteByAppIdAndDepartmentIdIn(String appId, Set<String> departMentIds);
}