package cn.deal.core.permission.service;

import cn.deal.component.domain.User;
import cn.deal.component.exception.BusinessException;
import cn.deal.component.kuick.KuickuserUserService;
import cn.deal.component.kuick.domain.KuickUser;
import cn.deal.component.kuick.repository.UserRepository;
import cn.deal.component.utils.JsonFileUtil;
import cn.deal.component.utils.JsonUtil;
import cn.deal.core.app.domain.AppMember;
import cn.deal.core.app.domain.Department;
import cn.deal.core.app.repository.DealAppMemberRepository;
import cn.deal.core.app.repository.DepartmentRepository;
import cn.deal.core.app.resource.vo.AppMemberVO;
import cn.deal.core.app.service.DealAppMemberService;
import cn.deal.core.app.service.DepartmentService;
import cn.deal.core.license.domain.License;
import cn.deal.core.license.service.AppLicenseService;
import cn.deal.core.meta.domain.AppExtensionPoint;
import cn.deal.core.meta.repository.AppExtensionPointRepository;
import cn.deal.core.permission.dao.AppMemberPermissionDao;
import cn.deal.core.permission.domain.*;
import cn.deal.core.permission.repository.AppMemberPermissionRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;


@Service
public class AppMemberPermissionService {
    private static final Logger logger = LoggerFactory.getLogger(AppMemberPermissionService.class);

    private final static String SIDE_MODULE = "cn.kuick.deal-admin:side_menu";
    private final static String SIDE_TYPE = "menu";

    @Autowired
    private AppMemberPermissionDao appMemberPermissionDao;

    @Autowired
    private KuickuserUserService kuickuserUserService;

    @Autowired
    private DealAppMemberService dealAppMemberService;

    @Autowired
    private AppMemberPermissionRepository appMemberPermissionRepository;

    @Autowired
    private DealAppMemberRepository dealappMemberRepository;

    @Autowired
    private AppLicenseService appLicenseService;

    @Autowired
    private VersionService versionService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DealAppMemberRepository dealAppMemberRepository;

    @Autowired
    private DepartmentPermissionService departmentPermissionService;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AppExtensionPointRepository appExtensionPointRepository;

    @Autowired
    private JsonFileUtil jsonFileUtil;


    /**
     * 校验 设置的权限 是否非法
     * true : 有权限
     * false : 无权限
     *
     * @return
     */
    private boolean isInvalidPerms(List<Permission> curUserPerms, AppMemberPermission amp) {
        return curUserPerms.stream().filter(perm ->
            // 匹配需要设置的权限是否在管理权限内
            StringUtils.equals(perm.getDomainId(), amp.getDomainId()) &&
            StringUtils.equals(perm.getDomainType(), amp.getDomainType())
        ).findFirst().orElse(null) != null;
    }


    /**
     * 获取用户可访问资源权限集合
     * <p>
     * 用户资源权限 = 用户可见权限 & 当前项目许可权限
     * 当前项目许可权限 = 标准版本权限 + 附加模块权限
     * 用户可见权限 = 用户所有角色的权限的并集 + 用户自己配置的权限
     *
     * @param appId
     * @param kuickUserId
     * @return
     */
    public List<Permission> findPerms(String appId, int kuickUserId, List<String> domainTypes, List<String> domainIds) {
        // 验证 domainIds
        validate(domainTypes, domainIds);

        try {
            // 获取项目许可权限
            List<Module> versions = getAppLicenseModules(appId);

            // 用户配置的权限
            List<Permission> userPerms = convertAmp2perm(appId, kuickUserId, domainTypes, domainIds);
            logger.info("findPerms userPerms: {}", userPerms);

            // 用户角色关联的权限
            List<Permission> rolePerms = getUserRolesPermissions(appId, kuickUserId);
            logger.info("findPerms rolePerms.size: {}", rolePerms.size());

            // 用户权限和角色关联权限求并集
            userPerms.addAll(rolePerms);
            logger.info("findPerms userAllPerms: {}", userPerms);

            // 所有权限与许可取交集, 然后去重
            return intersectionWithPermAndLicense(appId, kuickUserId, versions, userPerms).stream().collect(
                    collectingAndThen(toCollection(() ->
                            new TreeSet<>(Comparator.comparing(Permission::getDomainId))), ArrayList::new)
            );
        } catch (Exception e) {
            logger.error("findPerms.error", e);
            return Lists.newArrayList();
        }
    }

    private List<Permission> findExtensionPointPermission(String appId) {
        List<AppExtensionPoint> points = appExtensionPointRepository.findByAppIdAndPlatformAndTypeAndModule(
                appId, AppExtensionPoint.Platform.WEB.val(), SIDE_TYPE, SIDE_MODULE);
        logger.info("findExtensionPointPermission.points: {}", points);

        List<Permission> pointResult = Lists.newArrayList();
        points.forEach(point -> {
            TypeReference<Resource> type = new TypeReference<Resource>() {};
            Resource resource = JsonUtil.fromJson(point.getConfig(), type);
            delayeringPermission(resource, pointResult);
        });

        logger.info("findExtensionPointPermission.pointResult: {}", pointResult);
        return pointResult;
    }

    private void delayeringPermission(Resource resource, List<Permission> result) {
        if (resource != null) {
            try {
                if (StringUtils.isNotBlank(resource.getId())) {
                    result.add(new Permission(resource.getId(), resource.getDomainType()));
                    if (resource.getChildren() != null) {
                        resource.getChildren().forEach(child -> delayeringPermission(child, result));
                    }
                }
            } catch (Exception e) {
                logger.error("error in delayering ", resource);
            }
        }
    }

    public List<Permission> findPermsV2(String appId, int kuickUserId, List<String> domainTypes, List<String> domainIds) {
        // 验证 domainIds
        validate(domainTypes, domainIds);

        try {
            // 用户配置的权限
            return convertAmp2perm(appId, kuickUserId, domainTypes, domainIds).stream().collect(
                    collectingAndThen(toCollection(() ->
                            new TreeSet<>(Comparator.comparing(Permission::getDomainId))), ArrayList::new)
            );

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Lists.newArrayList();
        }
    }

    private List<Permission> getUserRolesPermissions(String appId, int kuickUserId) {
        // 老版本，获取用户角色列表
        String roles = dealAppMemberService.getMemberRoles(appId, kuickUserId);
        logger.info("findPerms.postRoles: {}", roles);

        // 老版本，获取角色权限
        return findRolePerms(roles);
    }


    private void validate(List<String> domainTypes, List<String> domainIds) {
        if (domainIds != null && !domainIds.isEmpty()) {
            Assert.notEmpty(domainTypes, "domainType can't be null.");
        }
    }

    /**
     *
     * 许可版本 和 用户权限求交集
     *
     * 如果是项目创建人， 则再与自定义权限取并集
     *
     * @param appId 项目id
     * @param modules 模块列表
     * @param perms 权限集
     * @return 权限
     */
    private List<Permission> intersectionWithPermAndLicense(String appId, int kuickUserId, List<Module> modules, List<Permission> perms) {
        List<Permission> result = Lists.newArrayList();

        List<Permission> epps = findExtensionPointPermission(appId);

        boolean appOwner = dealAppMemberService.isAppOwner(appId, kuickUserId);
        if (appOwner) {
            result.addAll(epps);
        }

        for (Permission perm : perms) {
            // 处理许可
            for (Module module : modules) {
                if (module.getId().equalsIgnoreCase(perm.getDomainId())) {
                    result.add(perm);
                }
            }

            if (!appOwner) {
                // 处理扩展菜单
                for (Permission epp : epps) {
                    if (perm.getDomainId().equals(epp.getDomainId())) {
                        result.add(perm);
                    }
                }
            }

            // 处理部门权限
            if (StringUtils.equals(perm.getDomainType(), DepartmentPermission.DomainType.DEPARTMENT.getVal())) {
                result.add(perm);
            }

            if (!StringUtils.equals(perm.getDomainType(), DepartmentPermission.DomainType.DEPARTMENT.getVal())) {
                perm.setDomainType(DepartmentPermission.DomainType.RESOURCE.getVal());
            }
        }

        logger.info("intersectionWithPermAndLicense.result: {}", result);
        return result;
    }


    List<Module> getAppLicenseModules(String appId) throws Exception {
        License license = appLicenseService.getAppLimits(appId);
        logger.info("getAppLicenseModules appId:{}, license: {}", appId, license);

        return versionService.getAppModulesByLicense(appId, license);
    }

    /**
     * 数据结构转换
     *
     * @param appId
     * @param kuickUserId
     * @param domainTypes
     * @param domainIds
     * @return
     */
    private List<Permission> convertAmp2perm(String appId, int kuickUserId, List<String> domainTypes, List<String> domainIds) {
        List<AppMemberPermission> amps = mergeDepAndMemberPerms(appId, kuickUserId, domainTypes, domainIds);
        logger.info("convertAmp2perm.amps: {}", amps);
        return amps.stream().map(Permission::new).collect(Collectors.toList());
    }

    /**
     * 获取角色权限
     *
     * @param roles 角色
     * @return
     */
    private List<Permission> findRolePerms(String roles) {
        List<Permission> perms = new ArrayList<>();
        if (roles != null) {
            String[] role = roles.split(",");
            if (role.length > 0) {
                for (String aRole : role) {
                    for (Role permission : versionService.getRolePermissions()) {
                        if (aRole.trim().equals(permission.getDomainId().trim())) {
                            List<Permission> rPers = permission.getPerms();
                            perms.addAll(rPers);
                            break;
                        }
                    }
                }
            }
        }

        return perms;
    }

    /**
     * @param list
     * @return List<RolePermission>    返回类型
     * @throws
     * @Title: removeDuplicate
     * @Description: TODO(去重)
     */
    public List<Permission> removeDuplicate(List<Permission> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).getDomainId().equals(list.get(i).getDomainId())) {
                    list.remove(j);
                }
            }
        }
        return list;
    }

    /**
     * @param key
     * @param resource
     * @return List<Resource>    返回类型
     * @throws
     * @Title: getResources
     * @Description: TODO(暂时不用)
     */
    public List<Resource> getResources(String key, Resource resource) {
        List<Resource> list = new ArrayList<>();
        resource.setCategory(key);
        List<Resource> children = resource.getChildren();
        if (children != null && children.size() > 0) {
            for (Resource res : children) {
                res.setCategory(key);
                res.setParentId(resource.getId());
                List<Resource> childrenList = res.getChildren();
                if (childrenList != null && childrenList.size() > 0) {
                    for (Resource childrenRes : children) {
                        childrenRes.setCategory(key);
                        childrenRes.setParentId(res.getId());

                    }

                    list.addAll(childrenList);
                }
            }

            list.addAll(children);
        }

        list.add(resource);

        return list;
    }


    private List<AppMemberPermission> mergeDepAndMemberPerms(String appId, int kuickUserId, List<String> domainTypes, List<String> domainIds) {
        logger.info("mergeDepAndMemberPerms.params: {}, {}, {}, {}", appId, kuickUserId, domainTypes, domainIds);

        List<AppMemberPermission> result = appMemberPermissionDao.getAppMemberPermissionList(
                appId, kuickUserId, domainTypes, domainIds);
        logger.info("mergeDepAndMemberPerms.list: {}", result);

        List<AppMemberPermission> depAmps = findDepPerms(appId, kuickUserId);
        if (depAmps != null && !depAmps.isEmpty()) {
            result.addAll(depAmps);
        }

        return result;
    }


    @SuppressWarnings("unchecked")
    private List<AppMemberPermission> findDepPerms(String appId, int kuickUserId) {
        List<AppMemberPermission> result;

        if (dealAppMemberService.isAppOwner(appId, kuickUserId)) {
            result = departmentPermissionService.getAdminDps(appId);
        } else {
            result = handleDepartmentPerm(appId, kuickUserId);
        }

        return result == null ? Collections.EMPTY_LIST : result;
    }


    /**
     * department -> appMemberPermission
     *
     * @param appId
     * @param kuickUserId
     * @return
     */
    private List<AppMemberPermission> handleDepartmentPerm(String appId, Integer kuickUserId) {
        List<Department> deps = departmentService.getAdminDepartmentByKuickUserId(appId, kuickUserId, null, Department.Cascade.YES.getVal());
        logger.info("handleDepartmentPerm.deps: {}", deps);
        return deps.stream().map(dep -> new AppMemberPermission(dep, kuickUserId)).collect(Collectors.toList());
    }


    /**
     * 设置权限
     * @param appId
     * @param kuickUserId
     * @param perms
     * @param user
     * @return
     */
    @Transactional(rollbackOn = Exception.class)
    public boolean updateAppMemberPermissions(String appId, int kuickUserId, List<AppMemberPermission> perms, User user) {
        AppMember member = dealAppMemberRepository.findByAppIdAndStatusAndKuickUserId(appId, AppMember.Status.VALID.getValue(), kuickUserId);
        logger.info("updateAppMemberPermissions.member: {}", member);

        if (member == null) {
            throw new BusinessException("not_found", "member is not found. kuickUserId: " + kuickUserId);
        } else {
            updateOldMemberRole(member);
        }

        // 操作人权限
        List<Permission> curUserPerms = findPerms(appId, user.getId(), null, null);
        logger.info("updateAppMemberPermissions.curUserPerms: {}", curUserPerms);

        // 被操作人权限
        List<Permission> userPerms = findPerms(appId, kuickUserId, null, null);

        // 删除可删除的权限
        matchPermsAndRemove(appId, kuickUserId, curUserPerms, userPerms);

        // 过滤得到可設置的权限
        List<AppMemberPermission> validPerms = perms.stream().filter(perm -> isInvalidPerms(curUserPerms, perm)).collect(Collectors.toList());
        logger.info("updateAppMemberPermissions.validPerms: {}", validPerms);

        if (!validPerms.isEmpty()) {
            putValidPerm(member, validPerms);
        }

        return true;
    }


    /**
     * 匹配可以操作的权限, 然后删除
     *
     * @param appId
     * @param kuickUserId
     * @param curUserPerms
     * @param userPerms
     */
    private void matchPermsAndRemove(String appId, Integer kuickUserId, List<Permission> curUserPerms, List<Permission> userPerms) {
        userPerms.forEach(userPerm -> {
            curUserPerms.forEach(curUserPerm -> {
                if (StringUtils.equals(userPerm.getDomainType(), curUserPerm.getDomainType()) &&
                        StringUtils.equals(userPerm.getDomainId(), curUserPerm.getDomainId())) {
                    appMemberPermissionRepository.deleteByAppIdAndKuickUserIdAndDomainTypeAndDomainId(
                            appId, kuickUserId, userPerm.getDomainType(), userPerm.getDomainId()
                    );
                }
            });
        });
    }


    private void putValidPerm(AppMember member, List<AppMemberPermission> validPerms) {
        validPerms.forEach(validPerm -> {
            validPerm.setId(UUID.randomUUID().toString());
            validPerm.setAppId(member.getAppId());
            validPerm.setKuickUserId(member.getKuickUserId());
            entityManager.merge(validPerm);
        });

        entityManager.flush();
        entityManager.clear();
    }


    /**
     * 更新老版本成员角色
     *
     * @param member
     */
    private void updateOldMemberRole(AppMember member) {
        if (!StringUtils.equals(member.getRole(), AppMember.Role.APP_MEMBER.getVal())) {
            member.setPostRoles(null);
            member.setRole(AppMember.Role.APP_MEMBER.getVal());
            dealAppMemberRepository.saveAndFlush(member);
        }
    }

    /**
     * 校验成员的老组织架构权限
     *
     * @param member
     * @param amp
     */
    private void validateOldPerm(AppMember member, AppMemberPermission amp) {
        if (StringUtils.equals(amp.getDomainType(), DepartmentPermission.DomainType.DEPARTMENT.getVal())
                && StringUtils.equals(amp.getPerm(), AppMemberPermission.Perm.ADMIN.getVal())) {
            if (StringUtils.containsAny(member.getRole(), AppMember.Role.ADMIN.getVal())
                    || StringUtils.contains(member.getRole(), AppMember.Role.MASTER.getVal())) {
                throw new BusinessException("no_same_be_admin_and_departmentadmin", "不能同时配置为管理员和部门管理员，请到老版组织架构移除管理员角色");
            }
        }
    }

    /**
     * 获取kuickuser所管辖的销售，返回值包含该kuickuser
     *
     * @param appId
     * @param kuickUserId
     * @return
     */
    public List<KuickUser> getManagedSales(String appId, int kuickUserId) {
        return this.getManagedSales(appId, kuickUserId, 0, null);
    }

    /**
     * 获取kuickuser所管辖的销售，返回值不包含该kuickuser
     *
     * @param appId
     * @param kuickUserId
     * @return
     */
    public List<KuickUser> getManagedSales(String appId, int kuickUserId, int category, String keyword) {
        logger.info("getManagedSales.params: {}", appId, kuickUserId, category, keyword);

        AppMember appMember = dealappMemberRepository.findByAppIdAndKuickUserId(appId, kuickUserId);
        logger.info("getManagedSales.appMember: {}", appMember);
        List<Integer> sales = new ArrayList<>();

        if (category == 0) {
            sales.add(kuickUserId);
        }

        boolean isAppAdmin = dealAppMemberService.isAppOwner(appId, kuickUserId);
        if (isAppAdmin) {
            List<AppMember> appMembers = dealAppMemberService.getAppMembers(appId);
            appMembers.forEach((item) -> {
                sales.add(item.getKuickUserId());
            });

        } else if (appMember != null) {
            List<AppMemberPermission> amps = appMemberPermissionDao.getAppMemberPermissionList(appId, kuickUserId, null, null);
            logger.info("getManagedSales.amps: {}", amps);
            { // 原有逻辑
                List<String> kuickUserIds = new ArrayList<>();
                if ("Master".equals(appMember.getRole())) {
                    for (AppMemberPermission appMemberPermission : amps) {
                        kuickUserIds.add(appMemberPermission.getDomainId());
                    }
                } else {
                    kuickUserIds.add(kuickUserId + "");
                }
                List<AppMemberPermission> appMemberPermissions = appMemberPermissionDao.getAppMemberPermissionByDomainTypeAndKuickuserids(appId, kuickUserIds, "Sales");
                if (appMemberPermissions != null) {
                    for (AppMemberPermission appMemberPermission : appMemberPermissions) {
                        sales.add(Integer.parseInt(appMemberPermission.getDomainId()));
                    }
                }
            }

            { // 新组织架构
                List<AppMemberVO> members = Lists.newArrayList();
                // 权限部门
                amps.forEach(amp -> {
                    if (StringUtils.equals(amp.getDomainType(), DepartmentPermission.DomainType.DEPARTMENT.getVal())) {
                        List<Department> deps = departmentService.getBatch(appId, amp.getDomainId(),
                                StringUtils.equals(amp.getPerm(), AppMemberPermission.Perm.ADMIN.getVal()) ? 9 : 0
                                , null, Department.WithMembers.YES.getVal(), true);
                        handleMemberByDeps(deps, members);
                    }
                });

                members.forEach(member -> {
                    sales.add(member.getKuickUserId());
                });
            }
        }

        logger.info("getManagedSales, sales: {}", sales);
        /*
        Specification<User> specification = (root, query, criteriaBuilder) -> {
            Path<Integer> path = root.get("id");
            CriteriaBuilder.In<Integer> in = criteriaBuilder.in(path);
            sales.forEach(in::value);
            return criteriaBuilder.and(
                    in,
                    StringUtils.isNotBlank(keyword) ?
                    criteriaBuilder.or(
                            criteriaBuilder.like(root.get("phoneNum"), "%" + keyword + "%"),
                            criteriaBuilder.like(root.get("email"), "%" + keyword + "%"),
                            criteriaBuilder.like(root.get("name"), "%" + keyword + "%")
                    ) : criteriaBuilder.conjunction()
            );
        };

        List<KuickUser> kuickUsers = userRepository.findAllKuickUser(specification);*/
        List<User> users = null;
        if (StringUtils.isBlank(keyword)) {
            users = userRepository.findAllByIdIn(sales);
        } else {
            users = userRepository.findKuickUserByKeword(keyword, appId, sales);
        }
        if (CollectionUtils.isEmpty(users)) {
            logger.info("未获取到kuickuser");
            return new ArrayList<>();
        }
        if (category != 0 && users.size() > 0) {
            users = users.stream().filter(k -> k.getId() != kuickUserId).collect(Collectors.toList());
        }
        return users.parallelStream().map(u -> new KuickUser(u.getId(), u.getPhotoURI(), u.getName())).collect(Collectors.toList());
    }

    protected void handleMemberByDeps(List<Department> deps, List<AppMemberVO> members) {
        if (deps != null && !deps.isEmpty()) {
            deps.forEach(dep -> {
                if (dep.getMembers() != null && !dep.getMembers().isEmpty()) {
                    members.addAll(dep.getMembers());
                }

                if (dep.getChildren() != null && !dep.getChildren().isEmpty()) {
                    handleMemberByDeps(dep.getChildren(), members);
                }
                logger.info("handleMemberByDeps.depIdAndMembers: {}, {}", dep.getId(), dep.getMembers());
            });
        }
    }

    /**
     * @Description 返回appmember格式的成员
     * @Param [appId, kuickUserId, category]
     * @Return java.util.List<cn.deal.core.app.domain.AppMember>
     */
    public List<AppMemberVO> getManagedAppMember(String appId, int kuickUserId, int category) {
        List<KuickUser> kuickUsers = this.getManagedSales(appId, kuickUserId, category, null);
        if (CollectionUtils.isEmpty(kuickUsers)) {
            return Lists.newArrayList();
        }
        return kuickUsers.stream().map(ku -> {
            AppMember dam = dealappMemberRepository.findOneByAppIdAndKuickUserId(appId, ku.getId());
            dam.setUser(ku);
            return new AppMemberVO(dam);
        }).collect(Collectors.toList());
    }

    @Transactional(rollbackOn = Exception.class)
    public void editAuthedMembers(String appId, String departmentId, List<Integer> kuickUserIds) {
        dealAppMemberRepository.findAllByAppIdAndKuickUserIdIn(appId, kuickUserIds).forEach(this::updateOldMemberRole);

        logger.info("editAuthedMembers.params: {}, {}, {}", appId, departmentId, kuickUserIds);
        appMemberPermissionRepository.deleteByAppIdAndDomainTypeInAndDomainId(appId, DepartmentPermission.DomainType.DEPARTMENT.getVal(), departmentId);

        kuickUserIds.forEach(kuickUserId -> {
            AppMemberPermission amp = new AppMemberPermission();
            amp.setDomainType(DepartmentPermission.DomainType.DEPARTMENT.getVal());
            amp.setDomainId(departmentId);
            amp.setPerm(Permission.Perm.ADMIN.getVal());
            amp.setAppId(appId);
            amp.setKuickUserId(kuickUserId);
            entityManager.merge(amp);
        });

        entityManager.flush();
        entityManager.clear();
    }
}
