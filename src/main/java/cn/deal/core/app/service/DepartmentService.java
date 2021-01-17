package cn.deal.core.app.service;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cn.deal.component.domain.User;
import cn.deal.component.exception.BusinessException;
import cn.deal.component.kuick.KuickuserUserService;
import cn.deal.component.kuick.domain.KuickUser;
import cn.deal.component.kuick.repository.UserRepository;
import cn.deal.core.app.domain.AppMember;
import cn.deal.core.app.domain.Department;
import cn.deal.core.app.repository.DealAppMemberRepository;
import cn.deal.core.app.repository.DepartmentRepository;
import cn.deal.core.app.resource.vo.AppMemberVO;
import cn.deal.core.permission.domain.AppMemberPermission;
import cn.deal.core.permission.domain.DepartmentPermission;
import cn.deal.core.permission.domain.Permission;
import cn.deal.core.permission.repository.AppMemberPermissionRepository;
import cn.deal.core.permission.repository.DepartmentPermissionRepository;

/**
 * @ClassName DepartmentService
 * @Description 组织架构相关业务处理
 **/
@Service
@CacheConfig(cacheNames = "department", cacheManager = "redis")
public class DepartmentService {
    private static Logger log = LoggerFactory.getLogger(DepartmentService.class);

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DealAppMemberRepository appMemberRepository;

    @Autowired
    private DealAppMemberService dealAppMemberService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private DepartmentPermissionRepository departmentPermissionRepository;

    @Autowired
    private AppMemberPermissionRepository appMemberPermissionRepository;

    @Autowired
    private UserRepository userRepository;

    public void conflictValidate(String appId) {
        if (appMemberPermissionRepository.countByAppIdAndDomainTypeIn(appId, ImmutableList.of("Sales", "Admin")) > 0) {
            throw new BusinessException("conflict_with_old_organization", "当前应用不兼容新组织架构");
        }
        List<AppMember> appMembers = appMemberRepository.findAllByAppIdAndStatus(appId, AppMember.Status.VALID.getValue());
        AppMember appMember = appMembers.parallelStream().filter(a -> StringUtils.isNotBlank(a.getRole())
                && (a.getRole().contains("Admin") || a.getRole().contains("Master"))).findAny().orElse(null);
        if (appMember != null) {
            throw new BusinessException("conflict_with_old_organization", "当前应用不兼容新组织架构");
        }
    }

    /**
     * @Description 添加部门
     * @Param [appId, name, parentId, inheritParent]
     * @Return cn.deal.core.department.domain.Department
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(key = "'department:appId:' + #appId")
    public Department create(String appId, String name, String parentId, Integer inheritParent, int kuickUserId) {
        conflictValidate(appId);
        List<Department> exists = departmentRepository.findAllByAppIdAndName(appId, name);
        if (!CollectionUtils.isEmpty(exists)) {
            throw new BusinessException("exists", "该部门名称已存在");
        }
        Department department = Department.builder().appId(appId).name(name).parentId(parentId)
                .createdAt(new Date()).updatedAt(new Date()).build();
        departmentRepository.saveAndFlush(department);
        if (!dealAppMemberService.isAppOwner(appId, kuickUserId)) {
            AppMemberPermission ap = new AppMemberPermission();
            ap.setAppId(appId);
            ap.setKuickUserId(kuickUserId);
            ap.setDomainType(DepartmentPermission.DomainType.DEPARTMENT.getVal());
            ap.setDomainId(department.getId());
            ap.setPerm(Permission.Perm.ADMIN.getVal());
            appMemberPermissionRepository.saveAndFlush(ap);
        }
        if (inheritParent == 1 && StringUtils.isNotBlank(parentId)) {
            List<DepartmentPermission> dps = departmentPermissionRepository.findAllByAppIdAndDepartmentId(appId, parentId);
            List<DepartmentPermission> newDps = new ArrayList<>(dps.size());
            if (CollectionUtils.isNotEmpty(dps)) {
                dps.forEach(dp -> {
                    DepartmentPermission newDp = new DepartmentPermission();
                    BeanUtils.copyProperties(dp, newDp);
                    newDp.setId(null);
                    newDp.setDepartmentId(department.getId());
                    newDp.setCreatedAt(new Date());
                    newDp.setUpdatedAt(new Date());
                    newDps.add(newDp);
                });
                departmentPermissionRepository.save(newDps);
            }
        }
        return department;
    }

    /**
     * @Description 修改部门信息
     * @Param [appId, id, name]
     * @Return cn.deal.core.department.domain.Department
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(key = "'department:appId:'+#appId")
    public Department modify(String appId, String id, String name) {
        Department department = departmentRepository.findOne(id);
        if (department == null || !appId.equals(department.getAppId())) {
            throw new BusinessException("not_exists", "部门不存在");
        }
        department.setName(name);
        return departmentRepository.saveAndFlush(department);
    }

    /**
     * @Description 查询单个部门信息
     * @Param [appId, id]
     * @Return cn.deal.core.department.domain.Department
     */
    public Department getById(String appId, String id) {
        Department department = departmentRepository.findOne(id);
        return department == null ? null : (appId.equals(department.getAppId()) ? department : null);
    }

    /**
     * @Description 批量查询，返回树形结构
     * @Param [appId, parentId, depth, keyword, withMembers]
     * @Return java.util.List<cn.deal.core.department.domain.Department>
     */
    public List<Department> getBatch(String appId, String parentId, int depth, String keyword, int withMembers, boolean isNeedCurrentDep) {
        List<Department> departments = departmentRepository.findAllByAppId(appId);
        departments.forEach(department -> {
            boolean hasChild = departments.stream().anyMatch(department1 -> department.getId().equals(department1.getParentId()));
            department.setHasChild(hasChild);
        });
        List<Department> roots = departments.stream().filter(dp -> {
            if (StringUtils.isBlank(parentId)) {
                return StringUtils.isBlank(dp.getParentId());
            } else if (isNeedCurrentDep) {
                return parentId.equals(dp.getId());
            } else {
                return parentId.equals(dp.getParentId());
            }
        }).filter(dp -> {
            if (StringUtils.isBlank(keyword)) {
                return true;
            } else {
                return dp.getName().contains(keyword);
            }
        }).collect(Collectors.toList());
        List<Department> parents = roots;
        while (--depth > 0 && parents.size() > 0) {
            parents = findAndFilterChildren(parents, departments);
        }
        if (StringUtils.isBlank(parentId)) {
            Department department = new Department();
            department.setAppId(appId);
            department.setHasChild(false);
            roots.add(department);
        }
        if (withMembers == 1 && CollectionUtils.isNotEmpty(roots)) {
            fillMember(roots, appId);
        }
        return roots;
    }

    /**
     * @Description 填充appmember
     * @Param [roots, appId]
     * @Return void
     */
    private void fillMember(List<Department> roots, String appId) {
        List<AppMember> members = appMemberRepository.findAllByAppIdAndStatus(appId, AppMember.Status.VALID.getValue());
        dealAppMemberService.handleRole(appId, members);
        if (CollectionUtils.isNotEmpty(members)) {
            List<Department> departments = roots;
            do {
                departments.forEach(d -> {
                    List<AppMemberVO> ms = members.stream().filter(m -> (StringUtils.isBlank(d.getId()) && StringUtils.isBlank(m.getDepartmentId())) || (StringUtils.isNotBlank(d.getId()) && d.getId().equals(m.getDepartmentId()))).map(m -> {
                        AppMemberVO am = new AppMemberVO(m);
                        return am;
                    }).collect(Collectors.toList());
                    ms = fillKuickuser(ms);
                    d.setMembers(ms);
                });
                departments = departments.stream().filter(p -> CollectionUtils.isNotEmpty(p.getChildren())).flatMap(d -> d.getChildren().stream()).collect(Collectors.toList());
            } while (CollectionUtils.isNotEmpty(departments));
        }
    }

    /**
     * @param members
     * @Description AppMember中加入Kuickuser
     * @Param [members]
     * @Return void
     */
    private List<AppMemberVO> fillKuickuser(List<AppMemberVO> members) {
        List<Integer> kids = members.stream().map(m -> m.getKuickUserId()).collect(Collectors.toList());
        List<KuickUser> kuickUsers = userRepository.findByIds(kids, User.IsSimple.NO);
        members.forEach(m -> {
            KuickUser ku = kuickUsers.stream().filter(k -> m.getKuickUserId().equals(k.getId())).findFirst().orElse(null);
            String appId = m.getAppId();
            int kuickUserId = m.getKuickUserId();

            // 是否为项目创建人
            boolean isOwner = dealAppMemberService.isAppOwner(appId, kuickUserId);
            m.setIsOwner(isOwner);
            m.setUser(ku);
        });
        return members.stream().filter(m -> m.getUser() != null).collect(Collectors.toList());
    }

    private List<Department> findAndFilterChildren(List<Department> parents, List<Department> origins) {
        parents.forEach(p -> {
            p.setChildren(origins.stream().filter(c -> p.getId().equals(c.getParentId())).collect(Collectors.toList()));
        });
        parents = parents.stream().filter(p -> CollectionUtils.isNotEmpty(p.getChildren())).flatMap(p -> p.getChildren().stream()).collect(Collectors.toList());
        return parents;
    }

    /**
     * @Description 删除部门
     * @Param [appId, id, cascading]
     * @Return int
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(key = "'department:appId:' + #appId")
    public int remove(String appId, String id, int cascading) {
        List<Department> appAll = departmentRepository.findAllByAppId(appId);
        List<Department> toRms = appAll.stream().filter(department -> department.getId().equals(id)).collect(Collectors.toList());
        if (toRms.size() == 0) {
            throw new BusinessException("not_exists", "部门不存在");
        }
        // 处理非级联删除
        if (cascading == 0) {
            // 判断是否有子部门
            if (appAll.stream().anyMatch(p -> p.getParentId().equals(id))) {
                throw new BusinessException("exist_child_department", "存在子部门");
            }
            // 判断是否存在成员
            if (appMemberRepository.findAllByAppIdAndDepartmentId(appId, id).size() > 0) {
                throw new BusinessException("exist_sales", "存在销售");
            }
            departmentRepository.delete(id);
            departmentPermissionRepository.deleteByAppIdAndDepartmentId(appId, id);
            return 1;
        } else {
            // 处理级联删除
            String parentId = toRms.get(0).getParentId();
            Set<String> parentIds = toRms.stream().map(Department::getId).collect(Collectors.toSet());
            List<Department> children;
            while (!CollectionUtils.isEmpty(parentIds)) {
                Set<String> finalParentIds = parentIds;
                children = appAll.stream().filter(department -> finalParentIds.contains(department.getParentId())).collect(Collectors.toList());
                parentIds = children.stream().map(Department::getId).collect(Collectors.toSet());
                toRms.addAll(children);
            }
            Set<String> depIds = toRms.stream().map(Department::getId).collect(Collectors.toSet());
            appMemberRepository.updateDepartmentIdByAppIdAndDepartmentIds(appId,
                    depIds, parentId);
            departmentRepository.delete(toRms);
            appMemberPermissionRepository.deleteByAppIdAndDomainTypeAndDomainIdIn(appId, DepartmentPermission.DomainType.DEPARTMENT.getVal(), depIds);
            departmentPermissionRepository.deleteByAppIdAndDepartmentIdIn(appId, depIds);
            return toRms.size();
        }
    }

    /**
     * @Description 批量获取部门成员
     * @Param [appId, id, keyword]
     * @Return java.util.List<cn.deal.core.app.domain.DealAppMember>
     */
    public List<AppMemberVO> getMemebers(String appId, String id, String keyword) {
        if (StringUtils.isBlank(keyword)) {
            List<AppMemberVO> appMembers = new ArrayList(0);
            List<AppMember> members = appMemberRepository.findAllByAppIdAndDepartmentId(appId, id);
            if (CollectionUtils.isNotEmpty(members)) {
                dealAppMemberService.handleRole(appId, members);
                appMembers = members.stream().map(m -> new AppMemberVO(m)).collect(Collectors.toList());
                appMembers = fillKuickuser(appMembers);
            }
            return appMembers;
        } else {
            throw new BusinessException("param_error", "暂不支持keyword查询");
        }
    }

    /**
     * @Description 批量添加部门成员
     * @Param [appId, id, kuickUserIds]
     * @Return java.util.List<cn.deal.core.app.domain.DealAppMember>
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "appmember", key = "'appMember:appId:'+#p0")
    public List<AppMember> addMemebers(String appId, String departmentId, Set<Integer> kuickUserIds) {
        Department department = departmentRepository.findOne(departmentId);
        if (department == null) {
            throw new BusinessException("not_exists", "部门不存在");
        }
        // 需要创建新成员
        Set<Integer> newers = kuickUserIds;
        // 需要继承部门权限列表
        Set<Integer> inheritPerms = kuickUserIds;
        List<AppMember> exists = appMemberRepository.findAllByAppIdAndStatusAndKuickUserIdIn(appId, AppMember.Status.VALID.getValue(), kuickUserIds);
        if (!CollectionUtils.isEmpty(exists)) {
            newers = Sets.difference(kuickUserIds, exists.stream().map(AppMember::getKuickUserId)
                    .collect(Collectors.toSet()));
            inheritPerms = new HashSet<>(newers);
            inheritPerms.addAll(exists.stream().filter(e -> StringUtils.isBlank(e.getDepartmentId())).map(AppMember::getKuickUserId).collect(Collectors.toSet()));
            exists.forEach(d -> {
                d.setDepartmentId(departmentId);
            });
            appMemberRepository.save(exists);
        }

        //添加新应用成员
        newers.forEach(kuickUserId -> {
            try {
                AppMember result = dealAppMemberService.createAppMember(appId, departmentId, kuickUserId, AppMember.Role.APP_MEMBER.getVal(), null,
                        request.getHeader("User-Agent"));
                if (result != null) {
                    log.info("add from conference user[{}] success", kuickUserId, result);
                } else {
                    log.warn("add from conferce user[{}] failed", kuickUserId);
                }
            } catch (Exception e) {
                log.error("add from conference user[{}] error", kuickUserId, e);
            }
        });

        // 处理新加成员继承权限
        if (CollectionUtils.isNotEmpty(inheritPerms)) {
            dealNewerPerms(inheritPerms, exists, appId, departmentId);
        }

        return exists;
    }

    /**
     * @Description 处理新成员权限继承
     * @Param [newers, exists]
     * @Return void
     */
    private void dealNewerPerms(Set<Integer> newers, List<AppMember> exists, String appId, String id) {
        List<AppMember> newMembers = appMemberRepository.findAllByAppIdAndStatusAndKuickUserIdIn(appId, AppMember.Status.VALID.getValue(), newers);
        if (CollectionUtils.isNotEmpty(newers)) {
            List<DepartmentPermission> dps = departmentPermissionRepository.findAllByAppIdAndDepartmentId(appId, id);
            if (CollectionUtils.isNotEmpty(dps)) {
                newMembers.stream().forEach(am -> {
                    List<AppMemberPermission> amps = dps.stream().map(dp -> {
                        AppMemberPermission amp = new AppMemberPermission();
                        BeanUtils.copyProperties(dp, amp);
                        amp.setId(null);
                        amp.setKuickUserId(am.getKuickUserId());
                        amp.setCreatedAt(new Date());
                        return amp;
                    }).collect(Collectors.toList());
                    appMemberPermissionRepository.save(amps);
                });
            }
            exists.addAll(newMembers);
        }
    }

    /**
     * @Description 移除部门成员
     * @Param [appId, departmentId, kuickUserIdSet]
     * @Return int
     */
    @Transactional(rollbackFor = Exception.class)
    public int removeMemebers(String appId, String departmentId, Set<Integer> kuickUserIdSet) {
        int count = appMemberRepository.updateDepartmentIdByAppIdAndDepartmentIdAndKuickUserId(appId, departmentId, kuickUserIdSet);
//        appMemberPermissionRepository.deleteByAppIdAndDomainTypeAndDomainIdAndKuickUserIdIn(appId, DepartmentPermission.DomainType.DEPARTMENT.getK(), departmentId, kuickUserIdSet);
        return count;
    }


    public List<Department> getAdminDepartmentByKuickUserId(String appId, int kuickUserId, List<String> departmentIds, int cascade) {
        log.info("getAdminDepartmentByKuickUserId.params: {}, {}, {}, {}", appId, kuickUserId, departmentIds, cascade);
        List<AppMemberPermission> amps = getDepPerms(appId, kuickUserId);
        log.info("getAdminDepartmentByKuickUserId.amps: {}", amps);
        return getDepByPerms(amps, departmentIds, cascade);
    }


    private List<Department> getDepByPerms(
            List<AppMemberPermission> amps, List<String> departmentIds, int cascade
    ) {
        log.info("getDepByPerms.params: {}, {}, {}", departmentIds, cascade, amps);

        List<Department> result = Lists.newArrayList();
        if (amps != null && !amps.isEmpty()) {
            amps.forEach(amp -> {
                // 本部门
                List<Department> departments = Lists.newArrayList();

                // 是否递归子部门
                int depth = cascade == Department.Cascade.YES.getVal() ? Integer.MAX_VALUE : 0;
                List<Department> batch = getBatch(
                        amp.getAppId(), amp.getDomainId(), depth, null, Department.WithMembers.NO.getVal(), true);
                log.info("getDepByPerms.batch: {}", batch);

                handleDepartmentTrees(batch, departments);
                log.info("getDepByPerms.departments: {}", departments);

                if (departmentIds != null && !departmentIds.isEmpty()) {
                    departments.forEach(department -> {
                        if (departmentIds.contains(department.getId())) {
                            result.add(department);
                        }
                    });

                } else {
                    result.addAll(departments);
                }
            });
        }

        return result.stream().collect(
                collectingAndThen(toCollection(() ->
                        new TreeSet<>(Comparator.comparing(Department::getId))), ArrayList::new));
    }

    private void handleDepartmentTrees(List<Department> departments, List<Department> result) {
        if (departments != null && !departments.isEmpty()) {
            departments.forEach(department -> {
                result.add(department);
                if (department.getChildren() != null && !department.getChildren().isEmpty()) {
                    handleDepartmentTrees(department.getChildren(), result);
                }
            });
        }
    }


    private List<AppMemberPermission> getDepPerms(String appId, int kuickUserId) {
        List<AppMemberPermission> amps;
        if (dealAppMemberService.isAppOwner(appId, kuickUserId)) {
            List<Department> deps = departmentRepository.findAllByAppId(appId);
            if (deps != null && !deps.isEmpty()) {
                amps = Lists.newArrayList();
                deps.forEach(dep -> {
                    AppMemberPermission amp = new AppMemberPermission();
                    amp.setAppId(appId);
                    amp.setDomainId(dep.getId());
                    amps.add(amp);
                });
            } else {
                amps = null;
            }

        } else {
            amps = appMemberPermissionRepository.findByAppIdAndKuickUserIdAndDomainType(
                    appId, kuickUserId, DepartmentPermission.DomainType.DEPARTMENT.getVal());
        }

        return amps;
    }
}
