package cn.deal.core.permission.resource;

import cn.deal.component.utils.JsonUtil;
import cn.deal.core.permission.domain.AppMemberPermission;
import cn.deal.core.permission.domain.DepartmentPermission;
import cn.deal.core.permission.service.DepartmentPermissionService;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 * 部门权限
 */
@RestController
@RequestMapping("api/v1.0/app/{app_id}/department")
@Api(value = "部门权限", description = "部门权限", tags={"部门权限"}, produces = MediaType.ALL_VALUE)
@SuppressWarnings("SpellCheckingInspection")
public class DepartmentPermissionResource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DepartmentPermissionService departmentPermissionService;

    @ApiOperation(value = "获取部门下的成员", notes="获取部门下的成员")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "department_id", value = "部门id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "perms", value = "权限", required = true),
    })
    @GetMapping("{department_id}/authed-members")
    public List<AppMemberPermission> getAuthedMembers(
            @PathVariable("app_id") String appId, @PathVariable("department_id") String departmentId,
            @RequestParam(value = "perms", defaultValue = "ADMIN") List<String> perms,
            @RequestParam(value = "with_kuickuser", defaultValue = "0") int withKuickuser

    ) {
        return departmentPermissionService.getAuthedMembers(appId, departmentId, perms, withKuickuser);
    }


    @ApiOperation(value = "修改部门权限", notes="修改部门权限")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "department_id", value = "部门id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "perms", value = "权限集json", required = true),
    })
    @PutMapping("{department_id}/permissions")
    public List<DepartmentPermission> addDepPerms(@PathVariable("app_id") String appId, @PathVariable("department_id") String departmentId,
                                                  @RequestParam("perms") String perms) {

        logger.info("addDepPerms.param: {}, {}, {}", appId, departmentId, perms);
        return departmentPermissionService.addDepPerms(appId, departmentId, json2list(perms));
    }


    @ApiOperation(value = "获取部门权限", notes="获取部门权限")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "department_id", value = "部门id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "domain_types", value = "类型集", required = false),
    })
    @GetMapping("{department_id}/permissions")
    public List<DepartmentPermission> getDp(@PathVariable("app_id") String appId, @PathVariable("department_id") String departmentId,
                                            @RequestParam(value = "domain_types", required = false) String domainTypes) {

        logger.info("getDp.param: {}, {}, {}", appId, departmentId, domainTypes);
        return departmentPermissionService.getDepRes(appId, departmentId,
                StringUtils.isNotBlank(domainTypes) ? domainTypes.split(",") : null);
    }


    private List<DepartmentPermission> json2list(String perms) {
        return JsonUtil.fromJson(perms, new TypeReference<List<DepartmentPermission>>() {});
    }

}
