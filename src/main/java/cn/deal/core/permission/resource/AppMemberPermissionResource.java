package cn.deal.core.permission.resource;

import cn.deal.component.UserComponent;
import cn.deal.component.domain.User;
import cn.deal.component.kuick.domain.KuickUser;
import cn.deal.component.utils.JsonUtil;
import cn.deal.component.utils.TokenUtil;
import cn.deal.core.app.resource.vo.AppMemberVO;
import cn.deal.core.app.service.DepartmentService;
import cn.deal.core.permission.domain.AppMemberPermission;
import cn.deal.core.permission.domain.Permission;
import cn.deal.core.permission.service.AppMemberPermissionService;
import com.google.gson.reflect.TypeToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
@RestController
@Api(value = "项目成员权限", description = "项目成员权限",tags={"项目成员权限"},produces = MediaType.ALL_VALUE)
public class AppMemberPermissionResource {

    private static final Logger logger = LoggerFactory.getLogger(AppMemberPermissionResource.class);

    @Autowired
    private AppMemberPermissionService appMemberPermissionService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private UserComponent userComponent;

    /**
     * @param appId
     * @param kuickUserId
     * @param domainTypes
     * @param domainIds
     * @param groups      groups 为 my时，只返回 app_member_permission 表中配置的权限
     * @return List<RolePermission>    返回类型
     * @throws
     * @Title: getAppmemberPermissions
     * @Description: TODO(获取项目成员权限)
     */

    @ApiOperation(value = "获取项目成员权限", notes="获取项目成员权限")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
        @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "kuick_user_id", value = "kuick用户id", required = true),
        @ApiImplicitParam(paramType = "query", dataType = "List<String>", name = "domain_types", value = "域类型", required = true, defaultValue = ""),
        @ApiImplicitParam(paramType = "query", dataType = "List<String>", name = "domain_ids", value = "域id", required = true, defaultValue = ""),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "groups", value = "组", required = true, defaultValue = "all"),
    })
    @RequestMapping(value = "api/v1.0/app/{app_id}/member/{kuick_user_id}/permissions", method = RequestMethod.GET)
    public List<Permission> getAppmemberPermissions(@PathVariable("app_id") String appId,
                                                        @PathVariable("kuick_user_id") Integer kuickUserId,
                                                        @RequestParam(value = "domain_types", defaultValue = "") List<String> domainTypes,
                                                        @RequestParam(value = "domain_ids", defaultValue = "") List<String> domainIds) {

        return appMemberPermissionService.findPerms(appId, kuickUserId, domainTypes, domainIds);

    }

    @ApiOperation(value = "获取项目成员权限", notes="获取项目成员权限")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
        @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "kuick_user_id", value = "kuick用户id", required = true),
        @ApiImplicitParam(paramType = "query", dataType = "List<String>", name = "domain_types", value = "域类型", required = true, defaultValue = ""),
        @ApiImplicitParam(paramType = "query", dataType = "List<String>", name = "domain_ids", value = "域id", required = true, defaultValue = ""),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "groups", value = "组", required = true, defaultValue = "all"),
    })
    @RequestMapping(value = "api/v1.8/app/{app_id}/member/{kuick_user_id}/permissions", method = RequestMethod.GET)
    public List<Permission> getAppmemberPermissionsV2(@PathVariable("app_id") String appId,
                                                        @PathVariable("kuick_user_id") Integer kuickUserId,
                                                        @RequestParam(value = "domain_types", defaultValue = "") List<String> domainTypes,
                                                        @RequestParam(value = "domain_ids", defaultValue = "") List<String> domainIds) {

        return appMemberPermissionService.findPermsV2(appId, kuickUserId, domainTypes, domainIds);

    }

    /**
     * @param appId
     * @param kuickUserId
     * @param perms
     * @return boolean    返回类型
     * @throws
     * @Title: updateAppMemberPermissions
     * @Description: TODO(设置权限)
     */

    @ApiOperation(value = "设置权限", notes="设置权限")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
        @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "kuick_user_id", value = "kuick用户id", required = true),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "list", value = "权限列表", required = true, defaultValue = ""),
    })
    @RequestMapping(value = "api/v1.0/app/{app_id}/member/{kuick_user_id}/permissions", method = RequestMethod.PUT)
    public boolean updateAppMemberPermissions(
            @PathVariable("app_id") String appId, @PathVariable("kuick_user_id") Integer kuickUserId,
            @RequestParam(value = "perms") String perms, HttpServletRequest request) {
        logger.info("updateAppMemberPermissions.perms: {}", perms);

        Type type = new TypeToken<ArrayList<AppMemberPermission>>() {
        }.getType();

        List<AppMemberPermission> permissions = (List<AppMemberPermission>) JsonUtil.parseJson(perms, type);

        String accessToken = TokenUtil.getToken(request);
        User user = userComponent.getUserByToken(accessToken);
        return appMemberPermissionService.updateAppMemberPermissions(appId, kuickUserId, permissions, user);
    }

    @ApiOperation(value = "设置权限", notes="设置权限V2")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
        @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "kuick_user_id", value = "kuick用户id", required = true),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "list", value = "权限列表", required = true, defaultValue = ""),
    })
    @RequestMapping(value = "api/v1.8/app/{app_id}/member/{kuick_user_id}/permissions", method = RequestMethod.PUT)
    public boolean updateAppMemberPermissionsV2(
            @PathVariable("app_id") String appId, @PathVariable("kuick_user_id") Integer kuickUserId,
            @RequestParam(value = "perms") String perms, HttpServletRequest request) {
        logger.info("updateAppMemberPermissions.perms: {}", perms);
        departmentService.conflictValidate(appId);

        Type type = new TypeToken<ArrayList<AppMemberPermission>>() {
        }.getType();

        List<AppMemberPermission> permissions = (List<AppMemberPermission>) JsonUtil.parseJson(perms, type);

        String accessToken = TokenUtil.getToken(request);
        User user = userComponent.getUserByToken(accessToken);
        return appMemberPermissionService.updateAppMemberPermissions(appId, kuickUserId, permissions, user);
    }

    
    @ApiOperation(value = "获取销售", notes="获取销售")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
        @ApiImplicitParam(paramType = "path", dataType = "String", name = "kuick_user_id", value = "kuick用户id", required = true),
        @ApiImplicitParam(paramType = "query", dataType = "int", name = "category", value = "分类", required = true, defaultValue = "0"),
    })
    @GetMapping("api/v1.0/app/{app_id}/{kuick_user_id}/admin-sales")
    public List<KuickUser> getManagedSales(
            @PathVariable("app_id") String appId, @PathVariable("kuick_user_id") int kuickUserId,
            @RequestParam(value = "category", defaultValue = "0") int category,
            @RequestParam(value = "keyword", required = false) String keyword
    ) {
        return appMemberPermissionService.getManagedSales(appId, kuickUserId, category, keyword);
    }

    
    @ApiOperation(value = "获取appmember格式")
    @GetMapping("api/v1.0/app/{app_id}/{kuick_user_id}/admin-appmembers")
    public List<AppMemberVO> getManagedAppMember(@PathVariable("app_id") String appId, @PathVariable("kuick_user_id") int kuickUserId,
                                           @RequestParam(value = "category", defaultValue = "0") int category) {
        return appMemberPermissionService.getManagedAppMember(appId, kuickUserId, category);
    }

    @ApiOperation(value = "删除部门管理员及权限")
    @PutMapping("api/v1.0/app/{app_id}/department/{department_id}/authed-members")
    public void editAmp(@PathVariable("app_id") String appId, @PathVariable("department_id") String departmentId,
                       @RequestParam("kuick_user_ids") List<Integer> kuickUserIds) {
        appMemberPermissionService.editAuthedMembers(appId, departmentId, kuickUserIds);
    }
}
