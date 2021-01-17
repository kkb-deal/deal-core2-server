package cn.deal.core.meta.resource;

import cn.deal.component.kuick.domain.ResponseVO;
import cn.deal.core.meta.domain.CustomerSwarmMeta;
import cn.deal.core.meta.service.CustomerSwarmMetaService;
import com.google.gson.Gson;
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
@Api(value="客户分群数据", tags={"客户分群数据"}, produces = MediaType.ALL_VALUE)
@RequestMapping("/api/v1.0")
public class CustomerSwarmMetaResource {

    private static final Logger logger = LoggerFactory.getLogger(CustomerSwarmMetaResource.class);

    @Autowired
    private CustomerSwarmMetaService customerSwarmMetaService;


    /**
     * 获取客户分群数据
     * @param appId
     * @return
     */
    @ApiOperation(value = "获取客户分群数据", notes="获取客户分群数据")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true)
    })
    @GetMapping("/app/{app_id}/customer-swarm-metas")
    public List<CustomerSwarmMeta> getCustomerSwarmMetas(@PathVariable("app_id") String appId) {
        logger.info("customer-swarm-metas params: appid:{}", appId);
        List<CustomerSwarmMeta> list = customerSwarmMetaService.findByAppId(appId);
        logger.info("根据appid:{}获取客户分群元数据信息:{}", appId, new Gson().toJson(list));
        return list;
    }

    /**
     *
     * 刷新客户分群元数据信息缓存
     * @param appId
     * @return
     */
    @ApiOperation(value = "客户分群数据缓存刷新", notes="客户分群数据缓存刷新")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true)
    })
    @GetMapping("/app/{app_id}/customer-swarm-metas/refresh")
    public ResponseVO refreshCustomerSwarmMetasCache(@PathVariable("app_id") String appId) {
        logger.info("customer-swarm-meta refresh cache params: appid:{}", appId);
        customerSwarmMetaService.refreshCustomerSwarmMetaCacheByAppId(appId);
        return new ResponseVO(ResponseVO.Status.OK.getVal(), "刷新客户分群元数据成功");
    }


}
