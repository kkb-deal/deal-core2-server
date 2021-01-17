package cn.deal.core.meta.resource;

import com.google.gson.Gson;

import cn.deal.core.meta.domain.BehaviourMetaData;
import cn.deal.core.meta.service.BehaviourMetaDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@RestController
@Api(value = "行为元数据配置", description = "行为元数据配置",tags={"行为元数据配置"},produces = MediaType.ALL_VALUE)
@RequestMapping("/api/v1.0")
public class BehaviourMetaDataResource {

    @Autowired
    private BehaviourMetaDataService behaviourMetaDataService;

    @ApiOperation(value = "创建或编辑行为属性元数据信息", notes="创建或编辑行为属性元数据信息")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", dataType = "String", name = "app-id", value = "项目id", required = true),
        @ApiImplicitParam(paramType = "path", dataType = "String", name = "action", value = "行为action", required = true),
        @ApiImplicitParam(paramType = "path", dataType = "String", name = "name", value = "行为属性名", required = true),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "type", value = "属性类型", required = false),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "title", value = "属性标题", required = false),
        @ApiImplicitParam(paramType = "query", dataType = "int", name = "index", value = "属性索引", required = false),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "default_value", value = "属性默认值", required = false),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "option_values", value = "属性选项值", required = false),
        @ApiImplicitParam(paramType = "query", dataType = "int", name = "readonly", value = "是否只读属性，1：只读、0：可编辑", required = false)
    })
    @RequestMapping(value = "/app/{app-id}/behaviour/{action}/meta-data/{name}", method = RequestMethod.PUT)
    public BehaviourMetaData createOrUpdateBehaviourMetaData(@PathVariable("app-id") String appId,
                                                             @PathVariable("action") String action,
                                                             @PathVariable("name") String name,
                                                             @RequestParam(name = "type", required = false) String type,
                                                             @RequestParam(name = "title", required = false) String title,
                                                             @RequestParam(name = "index", required = false) Integer index,
                                                             @RequestParam(name = "default_value", required = false) String defaultValue,
                                                             @RequestParam(name = "option_values", required = false) String optionValues,
                                                             @RequestParam(name = "readonly", required = false) Integer readonly){
        return behaviourMetaDataService.createOrUpdateBehaviourMetaData(appId, action, name, type, title, index,
                defaultValue, optionValues, readonly);
    }

    @ApiOperation(value = "获取行为属性元数据列表", notes="获取行为属性元数据列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app-id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "action", value = "行为action", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "start_index", value = "开始索引", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "count", value = "数据条数", required = false)
    })
    @RequestMapping(value = "/app/{app-id}/behaviour/{action}/meta-datas", method = RequestMethod.GET)
    public List<BehaviourMetaData> getBehaviourMetaDataList(@PathVariable("app-id") String appId,
                                                         @PathVariable("action") String action,
                                                         @RequestParam(name = "start_index", required = false) Integer startIndex,
                                                         @RequestParam(name = "count", required = false) Integer count){

        return behaviourMetaDataService.getBehaviourMetaDataList(appId, action, startIndex, count);
    }
}
