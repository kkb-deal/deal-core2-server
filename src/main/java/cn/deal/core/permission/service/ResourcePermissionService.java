package cn.deal.core.permission.service;

import cn.deal.component.utils.JsonUtil;
import cn.deal.core.app.service.DealAppMemberService;
import cn.deal.core.license.domain.License;
import cn.deal.core.license.service.AppLicenseService;
import cn.deal.core.meta.domain.AppExtensionPoint;
import cn.deal.core.meta.repository.AppExtensionPointRepository;
import cn.deal.core.permission.domain.Permission;
import cn.deal.core.permission.domain.Resource;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;


@Service
public class ResourcePermissionService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${app.admin.resource.ids}")
    private String adminResourceId;

    private final static String SIDE_MODULE = "cn.kuick.deal-admin:side_menu";
    private final static String SIDE_TYPE = "menu";

    @Autowired
    private DealAppMemberService dealAppMemberService;

    @Autowired
    private AppLicenseService appLicenseService;
    
    @Autowired
    private VersionService versionService;

    @Autowired
    private AppMemberPermissionService appMemberPermissionService;

    @Autowired
    private AppExtensionPointRepository appExtensionPointRepository;

    /**
     * 获取用户可访问资源权限集合
     * 
     * 用户资源权限 = 用户可见权限 & 当前项目许可权限
     * 当前项目许可权限 = 标准版本权限 + 附加模块权限
     * 用户可见权限 = 用户所有角色的权限的并集 + 用户自己配置的权限
     * 
     * @param appId
     * @param kuickUserId
     * @param types
     * @return
     * @throws Exception 
     */
    public List<Resource> getResources(String appId, Integer kuickUserId, List<String> types) throws Exception {
    	License license = appLicenseService.getAppLimits(appId);
    	
        // 用户可见资源（兼容历史数据）
        List<Permission> rps = appMemberPermissionService.findPerms(appId, kuickUserId, null, null);

        // 用户可见资源Id集合
        Set<String> resourceIds = Sets.newHashSet();
        rps.forEach(rp -> {
            resourceIds.add(rp.getDomainId());
        });
        logger.info("getResources.resourceIds: {}, {}", resourceIds.size(), resourceIds);

        // 结果集，根据MenuItem展开
        List<Resource> result = Lists.newArrayList();
        for(Resource resource: versionService.getResourceTreeByLicense(license)) {
            loopResource(resource, types, resourceIds, result);
        }

        // 去除重复的Resource
        result = result.stream().collect(
                collectingAndThen(toCollection(() ->
                        new TreeSet<>(Comparator.comparing(Resource::getId))), ArrayList::new));

        List<Resource> eprs = findExtensionPointResource(appId);
        if (dealAppMemberService.isAppOwner(appId, kuickUserId)) {
            if (!eprs.isEmpty()) {
                result.addAll(eprs);
            }
            return result;

        } else {
            handleExtensionPointResource(eprs, rps, result);
            List<String> rids = Arrays.asList(adminResourceId.split(","));
            return result.stream().filter(r -> !rids.contains(r.getId())).collect(Collectors.toList());
        }
    }

    private void handleExtensionPointResource(List<Resource> eprs, List<Permission> rps, List<Resource> result) {
        if (!eprs.isEmpty()) {
            rps.forEach(rp -> {
                eprs.forEach(epr -> {
                    if (rp.getDomainId().equals(epr.getId())) {
                        result.add(epr);
                    }
                });
            });
        }
    }

    private List<Resource> findExtensionPointResource(String appId) {
        List<AppExtensionPoint> points = appExtensionPointRepository.findByAppIdAndPlatformAndTypeAndModule(
                appId, AppExtensionPoint.Platform.WEB.val(), SIDE_TYPE, SIDE_MODULE);
        logger.info("findExtensionPointResource.points: {}", points);

        List<Resource> pointResult = Lists.newArrayList();
        points.forEach(point -> {
            TypeReference<Resource> type = new TypeReference<Resource>() {};
            Resource resource = JsonUtil.fromJson(point.getConfig(), type);
            logger.info("findExtensionPointResource.resource: {}", resource);
            delayeringResource(resource, pointResult);
        });

        logger.info("findExtensionPointResource.pointResult: {}", pointResult);
        return pointResult;
    }

    private void delayeringResource(Resource resource, List<Resource> result) {
        if (resource != null) {
            try {
                Resource r = new Resource();
                BeanUtils.copyProperties(resource, r, "children");
                result.add(r);

                if (resource.getChildren() != null) {
                    resource.getChildren().forEach(child -> delayeringResource(child, result));
                }
            } catch (Exception e) {
                logger.error("delayering in error", resource);
            }
        }
    }

    private void loopResource(Resource resource, List<String> types, Set<String> resourceIds, List<Resource> result) {
        boolean isNeeded = types == null || types.isEmpty() || types.contains(resource.getType());
        
        // 结果集属于用户可见资源集合
        if (isNeeded && resourceIds.contains(resource.getId())) {
            handleResource(resource, result);
        }

        List<Resource> children = resource.getChildren();
        if (children != null && !children.isEmpty()) {
            children.forEach(child -> {
                loopResource(child, types, resourceIds, result);
            });
        }
    }

    private void handleResource(Resource resource, List<Resource> result) {
        result.add(new Resource(resource));

        if (Resource.Type.MENU_ITEM.getVal().equals(resource.getType())) {
            List<Resource> children = resource.getChildren();
            
            if (children != null && !children.isEmpty()) {
                result.addAll(children);
            }
        }
    }

}
