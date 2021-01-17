package cn.deal.core.permission.resource;

import cn.deal.core.permission.domain.Resource;
import cn.deal.core.permission.service.ResourcePermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("api/v1.0/app/{app_id}")
@Api(value = "资源权限", description = "资源权限", tags={"资源权限"}, produces = MediaType.ALL_VALUE)
public class ResourcePermissionResource {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ResourcePermissionService resourcePermissionService;

    @ApiOperation(value = "获取资源权限", notes="获取资源权限")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "kuick_user_id", value = "用户id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "domain_types", value = "类型集", required = true),
    })
    @GetMapping("member/{kuick_user_id}/resources")
    public List<Resource> getResources(@PathVariable("app_id") String appId, @PathVariable("kuick_user_id") Integer kuickUserId,
    		@RequestParam(value = "types", required = false) List<String> types) throws Exception {
        logger.info("getResources.params: {}, {}, {}", appId, kuickUserId, types);
        return resourcePermissionService.getResources(appId, kuickUserId, types);
    }

}
