package cn.deal.core.category.resource;

import cn.deal.component.UserComponent;
import cn.deal.component.exception.BusinessException;
import cn.deal.component.kuick.KuickuserUserService;
import cn.deal.component.utils.TokenUtil;
import cn.deal.core.app.domain.AppMember;
import cn.deal.core.app.resource.vo.AppMemberVO;
import cn.deal.core.app.resource.vo.DealAppMemberCount;
import cn.deal.core.app.service.AppMemberService;
import cn.deal.core.app.service.DealAppMemberService;
import cn.deal.core.app.service.InviteService;
import cn.deal.core.category.domain.Category;
import cn.deal.core.category.service.CategoryService;
import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@Api(value = "系统类别", description = "系统类别",tags={"系统类别"},produces = MediaType.ALL_VALUE)
@RequestMapping("/api/v1.0")
public class CategoryResource {

	private static final Logger log = LoggerFactory.getLogger(CategoryResource.class);
	

	@Autowired
	private CategoryService categoryService;

	@ApiOperation(value = "获取系统类别列表", notes="获取系统类别列表")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true, defaultValue = "0"),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "start_index", value = "起始序号", required = true, defaultValue = "0"),
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "count", value = "数量", required = true, defaultValue = "20"),
	})
	@RequestMapping(value = "app/{app_id}/categorys", method = RequestMethod.GET,produces = {MediaType.APPLICATION_JSON_VALUE})
	public List<Category> getCategorys(
			@PathVariable("app_id") String appId,
			@RequestParam(value = "start_index", defaultValue = "0") Integer startIndex,
			@RequestParam(value = "count", defaultValue = "20") Integer count) {

		List<Category> categoryList = categoryService.getCategorys(appId, startIndex, count);
		log.info("getCategorys: {}", categoryList);
		return categoryList;
	}


}
