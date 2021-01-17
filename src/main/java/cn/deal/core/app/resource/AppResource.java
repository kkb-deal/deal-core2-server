package cn.deal.core.app.resource;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import cn.deal.component.utils.TokenUtil;
import cn.deal.core.app.domain.DealApp;
import cn.deal.core.app.resource.vo.AppVO;
import cn.deal.core.app.service.DealAppService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "项目管理", description = "项目管理",tags={"项目管理"},produces = MediaType.ALL_VALUE)
@RequestMapping("/api/v1.0")
public class AppResource {

	@Autowired
	private DealAppService dealAppService;

    @ApiOperation(value = "添加项目", notes="添加项目")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "kuick_user_id", value = "kuick用户id", required = true),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "name", value = "项目名称", required = true),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "description", value = "项目描述"),
        @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "trial_type", value = "类型", required = true, defaultValue="10"),
    })
	@RequestMapping(value = "/apps", method = RequestMethod.POST)
	public AppVO createDealApp(@RequestParam(name = "kuick_user_id") String kuickUserId,
			@RequestParam(name = "name") String name,
			@RequestParam(name = "description", required=false) String description,
			@RequestParam(name = "trial_type",defaultValue="10") Integer trialType,
			HttpServletRequest request) throws Exception {
		String accessToken = TokenUtil.getToken(request);

		DealApp dealApp = dealAppService.createDealApp(kuickUserId, name, description, accessToken, trialType);
		AppVO app = new AppVO(dealApp);
		
		return app;
	}


    @ApiOperation(value = "修改项目", notes="修改项目")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "name", value = "项目名称", required = true),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "description", value = "项目描述"),
    })
	@RequestMapping(value = "/app/{app_id}", method = RequestMethod.PUT)
    public AppVO updateDealApp(@PathVariable("app_id") String appId,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "description", required=false) String description,
            HttpServletRequest request) throws Exception {
        DealApp dealApp = dealAppService.updateDealApp(appId, name, description);
        AppVO app = new AppVO(dealApp);
        
        return app;
    }
	

    @ApiOperation(value = "获取项目信息", notes="获取项目信息")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", dataType = "String", name = "app-id", value = "项目id", required = true),
    })
	@RequestMapping(value = "/app/{app-id}", method = RequestMethod.GET)
	public DealApp getDealAppInfo(@PathVariable("app-id") String appId) {
		DealApp dealApp = dealAppService.getDealAppInfo(appId);
		return dealApp;
	}

    
    @ApiOperation(value = "获取所有项目", notes="获取所有项目")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "start_index", value = "起始序号", required = true, defaultValue = "0"),
        @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "count", value = "数量", required = true, defaultValue = "20"),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "keyword", value = "关键字", required = true, defaultValue = ""),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "setting_key", value = "设置key"),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "setting_value", value = "设置value"),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "kuick_user_id", value = "kuick用户id"),
    })
	@RequestMapping(value = "/apps", method = RequestMethod.GET)
	public List<DealApp> getAllApps(@RequestParam(value = "start_index", defaultValue = "0") Integer startIndex,
			@RequestParam(value = "count", defaultValue = "20") Integer count,
			@RequestParam(value = "keyword", defaultValue = "") String keyword,
			@RequestParam(value = "setting_key", required=false) String settingKey,
			@RequestParam(value = "setting_value", required=false) String settingValue,
			@RequestParam(name = "kuick_user_id", required=false) String kuickUserId) throws Exception {
	    List<DealApp> apps =new ArrayList<>();
	    
	    if(StringUtils.isNotBlank(kuickUserId)){
	        apps = dealAppService.getAppsByCreatorId(kuickUserId, keyword);
	    } else {
	        apps = dealAppService.getAllDealApps(startIndex, count, keyword,settingKey,settingValue);
	    }
	    
		return apps;
	}

    @ApiOperation(value = "获取项目数量", notes="获取项目数量")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "keyword", value = "关键字", required = true, defaultValue = ""),
    })
	@RequestMapping(value = "/app-count", method = RequestMethod.GET)
	public int getAllAppsCount(@RequestParam(value = "keyword", defaultValue = "") String keyword) {
		int count = dealAppService.getAppCount(keyword);
		return count;
	}


    @ApiOperation(value = "kuickUser的项目列表", notes="kuickUser的项目列表")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "expired", value = "过期"),
    })
    @RequestMapping(value = "/my-apps", method = RequestMethod.GET)
    public List<DealApp> getApps(HttpServletRequest request,@RequestParam(name = "expired", required =false) Integer expired
            ) throws Exception{
        String accessToken = TokenUtil.getToken(request);
        
        if(StringUtils.isBlank(accessToken)){
            throw new Exception("token is null");
        }
        
        return dealAppService.getKuickUserApps(accessToken, expired);
	}
}

