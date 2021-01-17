package cn.deal.core.app.resource;

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
@Api(value = "项目成员", description = "项目成员",tags={"项目成员"},produces = MediaType.ALL_VALUE)
@RequestMapping("api")
public class AppMemberResource {

	private static final Logger log = LoggerFactory.getLogger(InviteService.class);
	
	@Autowired
	private DealAppMemberService dealAppMemberService;

	@Autowired
	private AppMemberService appMemberService;

	@Autowired
	private InviteService inviteService;

	@Autowired
	private UserComponent userComponent;

	@Autowired
	private KuickuserUserService kuickuserUserService;

	@PostMapping("v1.0/app/{app_id}/member/imports")
	public void importMember(@PathVariable("app_id") String appId, @RequestParam("file") MultipartFile file) {
		appMemberService.importMember(appId, file);
	}
	
    @ApiOperation(value = "设置应用成员角色", notes="设置应用成员角色")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
        @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "kuick_user_id", value = "kucik用户id", required = true),
        @ApiImplicitParam(paramType = "query", dataType = "List<String>", name = "post_roles", value = "角色列表", required = true, defaultValue = ""),
    })
	@RequestMapping(value = "v1.0/app/{app_id}/member/{kuick_user_id}/post-roles", method = RequestMethod.PUT)
	public AppMemberVO updateAppMemberPostRoles(@PathVariable("app_id") String appId,
			@PathVariable("kuick_user_id") Integer kuickUserId,
			@RequestParam(value = "post_roles", defaultValue = "") List<String> postRoles, HttpServletRequest request) {
		String accessToken = TokenUtil.getToken(request);
		return dealAppMemberService.updateAppMember(appId, kuickUserId, postRoles, accessToken);
	}

	@ApiOperation(value = "获取应用成员个数", notes="获取应用成员个数")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
	})
	@RequestMapping(value = "v1.0/app/{app_id}/appmember-count", method = RequestMethod.GET)
	public DealAppMemberCount getAppmemberCount(@PathVariable("app_id") String appId) {
		return new DealAppMemberCount(dealAppMemberService.getAppMemberCount(appId));
	}


	@ApiOperation(value = "获取项目成员分页", notes="获取项目成员分页")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true, defaultValue = "0"),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "department_ids", value = "部门ids", required = true, defaultValue = "0"),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "keyword", value = "关键词", required = true, defaultValue = "0"),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "start_index", value = "起始序号", required = true, defaultValue = "0"),
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "count", value = "数量", required = true, defaultValue = "20"),
	})
	@RequestMapping(value = "v1.6/app/{app_id}/members", method = RequestMethod.GET,produces = {MediaType.APPLICATION_JSON_VALUE})
	public List<AppMemberVO> getAppMembers(
			@PathVariable("app_id") String appId,
			@RequestParam(value = "department_ids", required = false) String departmentIds,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "roles", required = false) List<String> roles,
			@RequestParam(value = "start_index", defaultValue = "0") Integer startIndex,
			@RequestParam(value = "count", defaultValue = "20") Integer count) {
    	// 关键词格式检查
    	if (StringUtils.isBlank(keyword) || "null".equals(keyword)) {
    		keyword = null;
    	}
    	
		List<AppMember> members = dealAppMemberService.getDealAppMembers(
				appId, departmentIds, roles, keyword, AppMember.QueryType.ONE.getVal(), startIndex, count);

		log.info("getAppMembers.members: {}", members);
		return members.stream().map(this::convertToAppMemberVO).collect(Collectors.toList());
	}

	@ApiOperation(value = "获取项目成员分页", notes="获取项目成员分页")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true, defaultValue = "0"),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "department_ids", value = "部门ids", required = true, defaultValue = "0"),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "keyword", value = "关键词", required = true, defaultValue = "0"),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "start_index", value = "起始序号", required = true, defaultValue = "0"),
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "count", value = "数量", required = true, defaultValue = "20"),
	})
	@RequestMapping(value = "v1.0/app/{app_id}/members", method = RequestMethod.GET,produces = {MediaType.APPLICATION_JSON_VALUE})
	public List<AppMemberVO> listAppMembers(
			@PathVariable("app_id") String appId,
			@RequestParam(value = "department_ids", required = false) String departmentIds,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "roles", required = false) List<String> roles,
			@RequestParam(value = "start_index", defaultValue = "0") Integer startIndex,
			@RequestParam(value = "count", defaultValue = "20") Integer count) {
		// 关键词格式检查
		if (StringUtils.isBlank(keyword) || "null".equals(keyword)) {
			keyword = null;
		}

		List<AppMember> members = dealAppMemberService.getDealAppMembers(
				appId, departmentIds, roles, keyword, AppMember.QueryType.ONE.getVal(), startIndex, count);

		log.info("getAppMembers.members: {}", members);
		return members.stream().map(this::convertToAppMemberVO).collect(Collectors.toList());
	}

	private AppMemberVO convertToAppMemberVO(AppMember dealAppMember) {
		AppMemberVO member = new AppMemberVO(dealAppMember);
		
		String appId = dealAppMember.getAppId();
		int kuickUserId = dealAppMember.getKuickUserId();
		
		// 是否为项目创建人
		boolean isOwner = dealAppMemberService.isAppOwner(appId, kuickUserId);
		member.setIsOwner(isOwner);
		member.setOwner(isOwner);

		// 职位信息
		String[] postRoles = new String[] {};
		if (StringUtils.isNotBlank(dealAppMember.getPostRoles())) {
			postRoles = dealAppMember.getPostRoles().split(",");
		}
		member.setPostRoles(dealAppMemberService.getPostRoles(postRoles));
		
		return member;
	}

    
	@ApiOperation(value = "添加项目成员", notes="添加项目成员")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
		@ApiImplicitParam(paramType = "query", dataType = "String", name = "kuick_user_id", value = "kuick用户id", required = true),
		@ApiImplicitParam(paramType = "query", dataType = "String", name = "post_role", value = "权限", required = true)
	})
	@RequestMapping(value = "v1.6/app/{app_id}/members", method = RequestMethod.POST)
	public Map<String, Object> createAppMember(@PathVariable("app_id") String appId, @RequestParam("kuick_user_id")int kuickUserId,
		   @RequestParam(value = "post_role", required = false)String postRole, HttpServletRequest request) throws Exception {
		log.info("createAppMemberByCode.params: {}, {}", kuickUserId, postRole);
		AppMember appMember = dealAppMemberService.createAppMember(appId, null, kuickUserId, "AppMember", postRole, request.getHeader("User-Agent"));
		return ImmutableMap.of("status", 1, "data", appMember);
	}

	
    @ApiOperation(value = "邀请项目成员", notes="邀请项目成员")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "code", value = "邀请码", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "kuick_user_id", value = "kuick用户id", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "post_role", value = "权限", required = true),
    })
	@RequestMapping(value = "v1.6/invites/{code}/members", method = RequestMethod.POST)
	public Map<String, Object> createAppMemberByCode(
			@PathVariable("code")String code, @RequestParam("kuick_user_id")int kuickUserId,
		 	@RequestParam(value = "post_role", required = false)String postRole, HttpServletRequest request) throws Exception {
		log.info("createAppMemberByCode.params: {}, {}, {}", code, kuickUserId, postRole);
		return inviteService.createAppMemberByCode(code, kuickUserId, postRole, request.getHeader("User-Agent"));
	}

    
    @ApiOperation(value = "是否项目成员", notes="是否项目成员")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
			@ApiImplicitParam(paramType = "path", dataType = "String", name = "kuick_user_id", value = "kuick用户id", required = true),

	})
	@GetMapping("v1.0/app/{app_id}/member/{kuick_user_id}/exists")
	public boolean isAppMember(@PathVariable("app_id") String appId, @PathVariable("kuick_user_id")Integer kuickUserId) {
		return dealAppMemberService.isAppMember(appId, kuickUserId);
	}


    @ApiOperation(value = "根据用户获取项目成员", notes="根据用户获取项目成员")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "app_ids", value = "项目id列表", required = true, defaultValue = "0"),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "kuick_user_ids", value = "kuick用户id", required = true, defaultValue = "0"),

	})
	@RequestMapping(value = "v1.0/app-members", method = RequestMethod.GET,produces = {MediaType.APPLICATION_JSON_VALUE})
    public Map<String, Object>  getAppMembers(@RequestParam("app_ids") String appIds,@RequestParam(value = "kuick_user_ids", defaultValue = "0") String kuickUserIds) throws Exception {
	    Map<String, Object> members = new HashMap<>();
	    
	    if(StringUtils.isNotBlank(appIds) && StringUtils.isNotBlank(kuickUserIds)){
	        String [] appIdstrs = appIds.split(",");
	        String [] kuickUserIdstr = kuickUserIds.split(",");
	        members =dealAppMemberService.getDealAppMembers(appIdstrs, kuickUserIdstr);
	    } else {
	        throw new BusinessException("is_blank", "参数不能为空");
	    }
	    
	    return members;
    }


    @ApiOperation("编辑用户备注名")
	@PutMapping("v1.0/app/{app_id}/member/{kuick_user_id}")
	public AppMemberVO editRemarkName(@PathVariable("app_id") String appId,
						  @PathVariable("kuick_user_id")Integer kuickUserId,
						  @RequestParam("remark_name") String remarkName) {
    	return dealAppMemberService.editRemarkName(appId, kuickUserId, remarkName);
	}

	@DeleteMapping("v1.5/app/{app_id}/member/{kuick_user_id}")
	public boolean remove(@PathVariable("app_id")String appId,
							   @PathVariable("kuick_user_id")String kuickUserId) {
    	return dealAppMemberService.remove(appId, kuickUserId);
	}

	@PutMapping("v1.0/app/{app_id}/member/{kuick_user_id}/role")
	public Object changeRole(@PathVariable("app_id")String appId,
							  @PathVariable("kuick_user_id")String kuickUserId,
							  @RequestParam String role) {
		return dealAppMemberService.changeRole(appId, kuickUserId, role);
	}

	@GetMapping("/v1.0/app/{app_id}/member/{kuick_user_id}")
	public AppMember findAppMember(@PathVariable("app_id") String appId,
								   @PathVariable("kuick_user_id") Integer kuickUserId) {
		return dealAppMemberService.findAppMember(appId, kuickUserId);
	}
}
