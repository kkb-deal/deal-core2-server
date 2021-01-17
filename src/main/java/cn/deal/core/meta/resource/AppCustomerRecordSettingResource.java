package cn.deal.core.meta.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.deal.core.meta.domain.AppCustomerRecordSetting;
import cn.deal.core.meta.service.AppCustomerRecordSettingService;

/**
 *
 * 项目名称：deal-core-server2
 * 类名称：AppCustomerRecordSettingResource
 * 类描述：   项目记录设置API
 */
@RestController
@Api(value = "项目记录设置API", description = "项目记录设置API",tags={"项目记录设置API"},produces = MediaType.ALL_VALUE)
@RequestMapping("/api/v1.0")
public class AppCustomerRecordSettingResource {
    private static final Log log = LogFactory.getLog(AppCustomerRecordSettingResource.class);

    @Autowired
    private AppCustomerRecordSettingService appCustomerRecordSettingService;

    /**
     * @throws Exception
     * @Title: getCustomerRecordSettings
     * @Description: TODO(获取客户appRecords列表 )
     * @param appId
     * @return 设定文件
     * @return List<AppCustomerRecordSetting>    返回类型
     * @throws
     */

    @ApiOperation(value = "获取客户appRecords列表", notes="获取客户appRecords列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "type", value = "类型"),
    })
    @RequestMapping(value = "/app/{app_id}/customer-record-settings", method = RequestMethod.GET)
    public List<AppCustomerRecordSetting> getCustomerRecordSettings(@PathVariable("app_id") String appId,
                                                                    @RequestParam(name = "type", required = false) String type) throws Exception {
        log.info("getCustomerRecordSettings  appId:" + appId);

        return appCustomerRecordSettingService.getAppCustomerRecordSettings(appId, type);


    }

    /**
     * @throws Exception
     * @Title: saveAppCustomerRecordSetting
     * @Description: TODO(增加记录项)
     * @param appId
     * @param name
     * @param action
     * @param index
     * @param description
     * @return 设定文件
     * @return AppCustomerRecordSetting    返回类型
     * @throws
     */

    @ApiOperation(value = "增加记录项", notes="增加记录项")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "name", value = "名称", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "action", value = "动作", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "index", value = "序号"),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "description", value = "描述"),
    })
    @RequestMapping(value = "/app/{app_id}/customer-record-settings", method = RequestMethod.POST)
    public AppCustomerRecordSetting saveAppCustomerRecordSetting(@PathVariable("app_id") String appId,
                                                                 @RequestParam("name") String name,
                                                                 @RequestParam("action") String action,
                                                                 @RequestParam(name= "index", required=false ) Integer index,
                                                                 @RequestParam(name = "description", required = false) String description) throws Exception {

        return appCustomerRecordSettingService.saveCustomerRecordSetting(appId, name, action, index, description);

    }

    /**
     * @throws Exception
     * @Title: updateAppCustomerRecordSetting
     * @Description: TODO(修改记录项)
     * @param appId
     * @param name
     * @param index
     * @param description
     * @return 设定文件
     * @return AppCustomerRecordSetting    返回类型
     * @throws
     */

    @ApiOperation(value = "修改记录项", notes="修改记录项")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "record_id", value = "记录id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "name", value = "名称"),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "index", value = "序号"),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "description", value = "描述"),
    })
    @RequestMapping(value = "/app/{app_id}/customer-record-settings/{record_id}", method = RequestMethod.PUT)
    public AppCustomerRecordSetting updateAppCustomerRecordSetting(@PathVariable("app_id") String appId,
                                                                   @PathVariable("record_id") String recordId,
                                                                   @RequestParam(name= "name", required=false ) String name,
                                                                   @RequestParam(name= "index", required=false ) String index,
                                                                   @RequestParam(name = "description", required = false) String description) throws Exception {

        return appCustomerRecordSettingService.updateCustomerRecordSetting(appId, name, recordId, index, description);

    }

    /**
     * @Title: deleteAppCustomerRecordSetting
     * @Description: TODO(删除记录项)
     * @param appId
     * @param recordId
     * @return 设定文件
     * @return boolean    返回类型
     * @throws
     */

    @ApiOperation(value = "删除记录项", notes="删除记录项")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "record_id", value = "记录id", required = true),
    })
    @RequestMapping(value = "/app/{app_id}/customer-record-settings/{record_id}", method = RequestMethod.DELETE)
    public boolean deleteAppCustomerRecordSetting(@PathVariable("app_id") String appId,
                                                  @PathVariable("record_id") String recordId) {
        return appCustomerRecordSettingService.deleteCustomerRecordSetting(appId, recordId);
    }

}
