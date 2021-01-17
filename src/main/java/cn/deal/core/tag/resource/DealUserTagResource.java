package cn.deal.core.tag.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.deal.core.customer.domain.Customer;
import cn.deal.core.tag.domain.AppTag;
import cn.deal.core.tag.domain.CustomerTag;
import cn.deal.core.tag.domain.DealUserTag;
import cn.deal.core.tag.service.DealUserTagService;

/**   
*    
* 项目名称：deal-core-server2   
* 类名称：DealUserTagResource   
* 类描述：   dealTag 服务 接口输出
*/
@RestController
@Api(value = "dealTag 服务", description = "dealTag 服务",tags={"dealTag 服务"},produces = MediaType.ALL_VALUE)
@RequestMapping("/")
public class DealUserTagResource {
	private static final Log log = LogFactory.getLog(DealUserTagResource.class);
	@Autowired
	private DealUserTagService dealUserTagService;
		
	/** 
	* @Title: addDealUserTags 
	* @Description: TODO(DealUser添加标签) 
	* @param appId
	* @param dealUserId
	* @param tags
	* @return 设定文件 
	* @return List<DealUserTag>    返回类型 
	* @throws 
	*/

    @ApiOperation(value = "DealUser添加标签", notes="DealUser添加标签")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
        @ApiImplicitParam(paramType = "path", dataType = "String", name = "deal_user_id", value = "deal用户id", required = true),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "tag", value = "标签", required = true),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "id", value = "id"),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "created_at", value = "创建时间"),
    })
	@RequestMapping(value = "app/{app_id}/deal-user/{deal_user_id}/tags", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
	public DealUserTag addDealUserTags(@PathVariable("app_id") String appId,
			@PathVariable("deal_user_id") String dealUserId,
			@RequestParam(name = "tag") String tag,
			@RequestParam(name = "id", required=false) String id,
			@RequestParam(name = "created_at", required = false) String createdAt) {
		Date createdTime = new Date();
		try {
			if (StringUtils.isNotBlank(tag)) {

				tag = URLDecoder.decode(tag, "UTF-8");

				log.info("addDealUserTags:tag " + tag);
			}

			if (StringUtils.isBlank(id)) {
				id = UUID.randomUUID().toString();
			}

			if (StringUtils.isNotBlank(createdAt)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				createdTime = sdf.parse(createdAt);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		return dealUserTagService.addDealUserTag(id, appId, dealUserId, tag, createdTime);
	}
	
}
