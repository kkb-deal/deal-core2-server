package cn.deal.core.meta.resource;

import cn.deal.core.meta.domain.AppExtensionPoint;
import cn.deal.core.meta.domain.AppSign;
import cn.deal.core.meta.service.AppExtensionPointService;
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
@Api(value = "项目扩展点", description = "项目扩展点",tags={"项目扩展点"},produces = MediaType.ALL_VALUE)
@RequestMapping("/api/v1.0")
public class AppExtensionPointsResource {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private AppExtensionPointService appExtensionPointService;

    @ApiOperation(value = "根据app-id获取扩展点", notes="根据app-id获取扩展点")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "platform", value = "平台"),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "module", value = "模块"),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "type", value = "类型"),
    })
	@GetMapping("/app/{app_id}/extension-points")
	public List<AppExtensionPoint> getExtensionPoints(@PathVariable("app_id")String appId,
			@RequestParam(name = "platform", required = false) String platform,
			@RequestParam(name = "module", required = false) String module,
			@RequestParam(name = "type", required = false) String type){
		List<AppExtensionPoint> points = appExtensionPointService.getExtensionPointsByAppId(appId, platform, module, type);
		logger.info("get extension points appId: {}, extension points: {}", appId, points);
		return points;
	}
	
    @ApiOperation(value = "获取签名", notes="获取签名")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "app_id", value = "项目id", required = true),
    })
	@PostMapping("/app/{app_id}/signs")
	public AppSign getSign(@PathVariable("app_id")String appId,@RequestParam("data")String data){
		return appExtensionPointService.getSign(appId, data);
	}
	
}
