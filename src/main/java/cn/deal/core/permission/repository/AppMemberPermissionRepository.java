package cn.deal.core.permission.repository;

import cn.deal.core.permission.domain.AppMemberPermission;
import cn.deal.core.permission.domain.DepartmentPermission;
import com.google.common.collect.ImmutableList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface AppMemberPermissionRepository extends JpaRepository<AppMemberPermission, String> {

    void deleteByAppIdAndDomainTypeAndDomainId(String appId, String domainType, String domainId);

    List<AppMemberPermission> findByAppIdAndDomainType(String appId, String domainType);

    List<AppMemberPermission> findByAppIdAndDomainTypeAndAndPermInAndDomainIdIn(String appId, String val, List<String> perms, List<String> domainId);

    List<AppMemberPermission> findByAppIdAndKuickUserIdAndDomainType(String appId, int kuickUserId, String val);

    void deleteByAppIdAndKuickUserIdAndDomainTypeAndDomainId(String appId, int kuickUserId, String domainType, String domainId);

    List<AppMemberPermission> findByAppIdAndDomainTypeAndPermAndKuickUserIdIn(String appId, String val, String admin, List<Integer> kuickUserIds);

    void deleteByAppIdAndKuickUserId(String appId, int kuickUserId);

    int countByAppIdAndDomainTypeIn(String appId, List<String> domainTypes);

    void deleteByAppIdAndKuickUserIdAndDomainTypeIn(String appId, int kuickUserId, List<String> domainTypes);

    void deleteByAppIdAndKuickUserIdAndDomainType(String appId, int kuickUserId, String domainType);

    void deleteByAppIdAndDomainTypeInAndDomainIdAndKuickUserIdIn(String appId, String val, String departmentId, List<Integer> kuickUserIds);

    int deleteByAppIdAndDomainTypeAndDomainIdIn(String appId, String domainType, Set<String> depIds);

    void deleteByAppIdAndDomainTypeAndDomainIdAndKuickUserIdIn(String appId, String domainType, String domainId, Set<Integer> kuickUserIdSet);

    void deleteByAppIdAndDomainTypeInAndDomainId(String appId, String val, String departmentId);
}
