package cn.deal.core.dealmeta.resource;

import cn.deal.core.dealmeta.domain.DealMetaData;
import cn.deal.core.dealmeta.domain.KeyWord;
import cn.deal.core.dealmeta.service.DealMetaDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName Deal
 */
@RestController
@Api(value = "dealMetaData服务", description = "dealMetaData服务", tags = {"dealMetaData服务"}, produces = MediaType.ALL_VALUE)
@RequestMapping("/api/v1.0")
public class DealMetaDataResource {

    @Autowired
    private DealMetaDataService dealMetaDataService;

    @ApiOperation(value = "获取成交记录元数据", notes = "获取成交记录元数据")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true)
    })
    @GetMapping("/app/{app_id}/deal-meta-datas")
    public List<DealMetaData> getDealMetaData(@PathVariable(value = "app_id") String appId) throws Exception {
        if (StringUtils.isBlank(appId)) {
            throw new MissingServletRequestParameterException("app_id", "String");
        }
        return dealMetaDataService.getDealMetaDataByAppId(appId);
    }

    @ApiOperation(value = "获取成交记录元数据选项值", notes = "获取成交记录元数据选项值")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "meta_name", value = "元数据名称", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "keyword", value = "搜索关键词", required = true)
    })
    @GetMapping("/app/{app_id}/deal-meta-data/{meta_name}/option-values")
    public List<KeyWord> getOptionValue(@PathVariable(value = "app_id") String appId,
                                        @PathVariable(value = "meta_name") String names,
                                        @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword) {
        return dealMetaDataService.getOptionValue(appId, names, keyword);
    }
}
