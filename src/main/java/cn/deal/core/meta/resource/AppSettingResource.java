package cn.deal.core.meta.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import cn.deal.core.meta.domain.AppSetting;
import cn.deal.core.meta.service.AppSettingService;

/**   
*    
* 项目名称：deal-core-server2   
* 类名称：AppSettingResource   
* 类描述：   项目设置后端服务
*/
@RestController
@Api(value = "项目设置", description = "项目设置",tags={"项目设置"},produces = MediaType.ALL_VALUE)
@RequestMapping("/api/v1.0")
public class AppSettingResource {
	@Autowired
	private AppSettingService appSettingService;
	
	/** 
	* @Title: getAppSettings 
	* @Description: TODO(获取项目配置列表) 
	* @param appId
	* @return 设定文件 
	* @return List<AppSetting>    返回类型 
	* @throws 
	*/

    @ApiOperation(value = "获取项目配置列表", notes="获取项目配置列表")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
    })
	@RequestMapping(value = "/app/{app_id}/settings", method = RequestMethod.GET,produces = {MediaType.APPLICATION_JSON_VALUE})
	public List<AppSetting> getAppSettings(@PathVariable("app_id") String appId){
		return appSettingService.getSettings(appId);
	}
	
	/**
	 * @throws Exception  
	* @Title: updateAppSetting 
	* @Description: TODO(修改项目设置) 
	* @param appId
	* @param id
	* @param key
	* @param type
	* @param value
	* @param defaultValue
	* @return 设定文件 
	* @return AppSetting    返回类型 
	* @throws 
	*/

    @ApiOperation(value = "修改项目设置", notes="修改项目设置")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
			@ApiImplicitParam(paramType = "path", dataType = "String", name = "setting_key", value = "设置key", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "value", value = "设置value", required = true),
    })
	@RequestMapping(value = "/app/{app_id}/setting/{setting_key}", method = RequestMethod.PUT)
	public AppSetting updateAppSetting(@PathVariable("app_id") String appId,@PathVariable("setting_key") String key,
			@RequestParam("value") String value) throws Exception {
		
		return appSettingService.updateAppSetting(appId,key, value);
	}
	
	
	/** 
	* @Title: getAppSettingByItemName 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param appId
	* @param itemName
	* @return
	* @throws Exception 设定文件 
	* @return AppSetting    返回类型 
	* @throws 
	*/

    @ApiOperation(value = "获取项目设置", notes="获取项目设置")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
			@ApiImplicitParam(paramType = "path", dataType = "String", name = "item_name", value = "设置key", required = true),
    })
	@RequestMapping(value = "/app/{app_id}/settings/{item_name}", method = RequestMethod.GET)
	public AppSetting getAppSettingByItemName(@PathVariable("app_id") String appId,@PathVariable("item_name") String itemName) throws Exception {
		
		return appSettingService.getSetting(appId, itemName);
	}
}
