package cn.deal.core.permission.service;

import cn.deal.component.UserComponent;
import cn.deal.component.domain.User;
import cn.deal.component.kuick.KuickuserUserService;
import cn.deal.component.kuick.domain.KuickUser;
import cn.deal.component.utils.AssertUtils;
import cn.deal.core.app.domain.Department;
import cn.deal.core.app.repository.DepartmentRepository;
import cn.deal.core.app.service.DepartmentService;
import cn.deal.core.permission.domain.AppMemberPermission;
import cn.deal.core.permission.domain.DepartmentPermission;
import cn.deal.core.permission.domain.Permission;
import cn.deal.core.permission.repository.AppMemberPermissionRepository;
import cn.deal.core.permission.repository.DepartmentPermissionRepository;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Date;
import java.util.List;


@Service
public class DepartmentPermissionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AppMemberPermissionRepository appMemberPermissionRepository;

    @Autowired
    private DepartmentPermissionRepository departmentPermissionRepository;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private KuickuserUserService kuickuserUserService;

    @Autowired
    private UserComponent userComponent;

    /**
     *
     * @param appId
     * @return
     */
    List<AppMemberPermission> getAdminDps(String appId) {
        List<AppMemberPermission> rps = Lists.newArrayList();
        List<Department> departments = departmentRepository.findAllByAppId(appId);
        logger.info("getAdminDps.departments: {}, {}", departments.size(), departments);

        departments.forEach(department -> {
            AppMemberPermission rp = new AppMemberPermission();
            rp.setDomainId(department.getId());
            rp.setDomainType(Permission.DomainType.DEPARTMENT.getVal());
            rp.setPerm(Permission.Perm.ADMIN.getVal());
            rps.add(rp);
        });
        
        return rps;
    }

    @Transactional(rollbackOn = Exception.class)
    public List<DepartmentPermission> addDepPerms(String appId, String departmentId, List<DepartmentPermission> dps) {
        delDepartmentPermission(appId, departmentId);
        addDepartmentPermission(appId, departmentId, dps);
        return dps;
    }

    private void delDepartmentPermission(String appId, String departmentId) {
        departmentPermissionRepository.deleteByAppIdAndDepartmentId(appId, departmentId);
    }

    private void addDepartmentPermission(String appId, String departmentId, List<DepartmentPermission> dps) {
        dps.forEach(dp -> {
            AssertUtils.notEmpty(dp.getDomainId(), "domainId can't be null.");
            AssertUtils.notEmpty(dp.getDomainType(), "domainType can't be null.");
            AssertUtils.notEmpty(dp.getPerm(), "perm can't be null.");

            if (!StringUtils.equals(dp.getDomainType(), DepartmentPermission.DomainType.DEPARTMENT.getVal())) {
                dp.setAppId(appId);
                dp.setDepartmentId(departmentId);
                dp.setStatus(DepartmentPermission.Status.USED.getVal());
                dp.setCreatedAt(new Date());
                dp.setUpdatedAt(new Date());
                entityManager.merge(dp);
            }
        });

        entityManager.flush();
        entityManager.clear();
    }


    public List<DepartmentPermission> getDepRes(String appId, String departmentId, String[] domainTypes) {
        List<DepartmentPermission> dps;

        if (domainTypes != null && domainTypes.length > 0) {
            dps = departmentPermissionRepository.findAllByAppIdAndDepartmentIdAndDomainTypeIn(appId, departmentId, domainTypes);
        } else {
            dps = departmentPermissionRepository.findAllByAppIdAndDepartmentId(appId, departmentId);
        }

        return dps;
    }


    public List<AppMemberPermission> getAuthedMembers(String appId, String departmentId, List<String> perms, int withKuickuser) {
        List<AppMemberPermission> amps = appMemberPermissionRepository
                .findByAppIdAndDomainTypeAndAndPermInAndDomainIdIn(
                        appId, DepartmentPermission.DomainType.DEPARTMENT.getVal(), perms, Collections.singletonList(departmentId));
        logger.info("getAuthedMembers.amps: {}", amps);

        if (withKuickuser == AppMemberPermission.WithKuickuser.YES.getVal()
                && amps != null && !amps.isEmpty()) {
            handleUserName(amps);
        }
        return amps;
    }

    private void handleUserName(List<AppMemberPermission> amps) {
        List<String> kuickUserIds = Lists.newArrayList();
        amps.forEach(amp -> {
            kuickUserIds.add(amp.getKuickUserId().toString());
        });

        List<User> kuickUsers = userComponent.getUsersByIds(kuickUserIds, User.IsSimple.YES.getVal());
        kuickUsers.forEach(kuickUser -> {
            amps.forEach(amp -> {
                if (kuickUser.getId().equals(amp.getKuickUserId())) {
                    amp.setUserName(kuickUser.getName());
                }
            });
        });
    }
}
