package cn.deal.core.meta.resource;

import cn.deal.core.meta.domain.WorkingTimeConfig;
import cn.deal.core.meta.service.WorkingTimeConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(value = "WorkingTimeConfig 服务", description = "WorkingTimeConfig 服务",tags={"WorkingTimeConfig 服务"},produces = MediaType.ALL_VALUE)
@RequestMapping("/api/v1.0/")
public class WorkingTimeConfigResource {

    @Autowired
    private WorkingTimeConfigService workingTimeConfigService;

    @ApiOperation(value = "获取WorkingTimeConfig", notes="获取WorkingTimeConfig")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true)
    })
    @RequestMapping(value = "app/{app_id}/working-time-configs", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<WorkingTimeConfig> getWorkingTimeConfig(@PathVariable("app_id") String appId){
        return workingTimeConfigService.getWorkingTimeConfigByAppId(appId);
    }
}
