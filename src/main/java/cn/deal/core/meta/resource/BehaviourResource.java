package cn.deal.core.meta.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import cn.deal.core.meta.domain.Behaviour;
import cn.deal.core.meta.service.BehaviourService;

import java.util.List;


@RestController
@Api(value = "行为配置", description = "行为配置",tags={"行为配置"},produces = MediaType.ALL_VALUE)
@RequestMapping("/api/v1.0")
public class BehaviourResource {

    @Autowired
    private BehaviourService behaviourService;

    @ApiOperation(value = "创建或编辑行为配置信息", notes="创建或编辑行为配置信息")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", dataType = "String", name = "app-id", value = "项目id", required = true),
        @ApiImplicitParam(paramType = "path", dataType = "String", name = "action", value = "行为action", required = true),
        @ApiImplicitParam(paramType = "query", dataType = "int", name = "type", value = "行为类型：1：系统行为，2：自定义行为", required = false),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "name", value = "行为名称", required = true),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "description", value = "行为描述", required = true)
    })
    @RequestMapping(value = "/app/{app-id}/behaviour/{action}", method = RequestMethod.PUT)
    public Behaviour createOrUpdateBehaviour(@PathVariable("app-id") String appId,
                                             @PathVariable("action") String action,
                                             @RequestParam(name = "type", required = false) Integer type,
                                             @RequestParam(name = "name", required = false) String name,
                                             @RequestParam(name = "description", required = false) String description){
        return behaviourService.createOrUpdateBehaviour(appId, action, type, name, description);
    }

    @ApiOperation(value = "获取行为配置信息列表", notes="获取行为配置信息列表")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", dataType = "String", name = "app-id", value = "项目id", required = true),
        @ApiImplicitParam(paramType = "query", dataType = "int", name = "type", value = "行为类型：1：系统行为，2：自定义行为", required = false),
        @ApiImplicitParam(paramType = "query", dataType = "int", name = "with_meta_count", value = "是否返回元数据个数，1：返回，0：不返回，默认0", required = false),
        @ApiImplicitParam(paramType = "query", dataType = "int", name = "start_index", value = "开始索引", required = false),
        @ApiImplicitParam(paramType = "query", dataType = "int", name = "count", value = "数据条数", required = false)
    })
    @RequestMapping(value = "/app/{app-id}/behaviours", method = RequestMethod.GET)
    public List<Behaviour> getBehavioursByType(@PathVariable("app-id") String appId,
                                               @RequestParam(name = "type", required = false) Integer type,
                                               @RequestParam(name = "with_meta_count", required = false) Integer withMetaCount,
                                               @RequestParam(name = "start_index", required = false) Integer startIndex,
                                               @RequestParam(name = "count", required = false) Integer count){
        return behaviourService.getBehavioursByType(appId, type, withMetaCount, startIndex, count);
    }
}
