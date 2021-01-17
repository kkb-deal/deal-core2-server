package cn.deal.core.dealuser.resource;

import cn.deal.component.utils.Base64Utils;
import cn.deal.component.utils.IPUtils;
import cn.deal.component.utils.MapUtils;
import cn.deal.component.utils.ParamUtils;
import cn.deal.core.dealuser.domain.DealUser;
import cn.deal.core.dealuser.service.DealUserService;
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

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@RestController
@Api(value = "dealUser服务", description = "dealUser服务",tags={"dealUser服务"},produces = MediaType.ALL_VALUE)
@RequestMapping("/api/v1.0")
public class DealUserResource {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private DealUserService dealUserService;

	@ApiOperation(value = "查询客户", notes = "查询客户")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "open_id", value = "微信openId"),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "union_id", value = "微信unionId"),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "device_id", value = "设备id"),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "app_user_id", value = "app用户id"),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "customer_id", value = "客户id")
	})
	@GetMapping("/app/{app_id}/deal-user")
	public DealUser get(
			@PathVariable(value = "app_id", required = false) String appId,
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "open_id", required = false) String openId,
			@RequestParam(value = "union_id", required = false) String unionId,
			@RequestParam(value = "device_id", required = false) String deviceId,
			@RequestParam(value = "app_user_id", required = false) String appUserId,
			@RequestParam(value = "customer_id", required = false) String customerId) {
		return dealUserService.getDealUserByKey(appId, id, openId, unionId, deviceId, appUserId, customerId);
	}

	/**
	 * @Description: TODO(根据客户ID获取客户关联的dealUsers )
	 * @param justIds  如果为1，则只返回ID列表
	 */
	@Deprecated
	@ApiOperation(value = "根据客户ID获取客户关联的dealUsers", notes="根据客户ID获取客户关联的dealUsers")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
			@ApiImplicitParam(paramType = "path", dataType = "String", name = "customer_id", value = "客户id", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "just_ids", value = "只返回id列表")
	})
	@GetMapping("/app/{app_id}/customer/{customer_id}/deal-users")
	public List<?> getDealUsersByCustomerId(
	        @PathVariable("app_id") String appId,
            @PathVariable("customer_id") String customerId,
            @RequestParam(name = "just_ids",required=false) Integer justIds) {
		return dealUserService.findDealUserByCustomerId(customerId, justIds);
	}

    @ApiOperation(value = "根据客户ID获取客户关联的dealUser实体", notes="根据客户ID获取客户关联的dealUser实体")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "customer_id", value = "客户id", required = true)
    })
    @GetMapping("/app/{app_id}/customer/{customer_id}/dealusers")
    public List<DealUser> getByCustomer(
            @PathVariable("app_id") String appId,
            @PathVariable("customer_id") String customerId,
            @RequestParam(name = "page", required=false, defaultValue = "0") Integer currentPage,
            @RequestParam(name = "size", required=false, defaultValue = "20") Integer pageSize) {
        return dealUserService.findTinyDealUserByCustomerIdAndPage(appId, customerId, currentPage, pageSize);
    }

	/**
	 * 查询DealUser列表
	 * @param appId
	 * @param dealUserIds 根据ID列表查询
	 * @param customerId 根据客户ID查询
	 * @param justIds
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "查询DealUser列表", notes="查询DealUser列表")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "deal_user_ids", value = "deal用户集合"),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "customer_id", value = "客户id"),
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "just_ids", value = "只返回id列表"),
	})
	@GetMapping("/deal-users")
	public List<?> getDealUsersByIds(
            @RequestParam(name="deal_user_ids", required=false) String dealUserIds,
            @RequestParam(name="customer_id", required=false) String customerId,
            @RequestParam(name = "just_ids",required=false) Integer justIds) throws Exception {
		List<DealUser> list = new ArrayList<>();

		if (StringUtils.isNotBlank(dealUserIds)) {
			String str[] = dealUserIds.split(",");
			List<String> ids = Arrays.asList(str);
			list = dealUserService.findDealUserByIds(ids);
		} else if (StringUtils.isNotBlank(customerId)) {
			return dealUserService.findDealUserByCustomerId(customerId, justIds);
		}
		
		return list;
	}

	/**
	 * 查询DealUser列表
	 * @param appId
	 * @param dealUserIds 根据ID列表查询
	 * @param customerId 根据客户ID查询
	 * @param justIds
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "查询DealUser列表", notes="查询DealUser列表")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "deal_user_ids", value = "deal用户集合"),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "customer_id", value = "客户id"),
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "just_ids", value = "只返回id列表"),
	})
	@GetMapping("/app/{app_id}/deal-users")
	public List<?> getDealUsersByIdsWithAppId(
	        @PathVariable("app_id") String appId,
            @RequestParam(name="deal_user_ids", required=false) String dealUserIds,
            @RequestParam(name="customer_id", required=false) String customerId,
            @RequestParam(name = "just_ids",required=false) Integer justIds) throws Exception {
		List<DealUser> list = new ArrayList<>();

		if (StringUtils.isNotBlank(dealUserIds)) {
			String str[] = dealUserIds.split(",");
			List<String> ids = Arrays.asList(str);
			list = dealUserService.findDealUserByIdsWithAppId(appId, ids);
		} else if (StringUtils.isNotBlank(customerId)) {
			return dealUserService.findDealUserByCustomerId(customerId, justIds);
		}
		
		return list;
	}
	
	/**
	 * 注册DealUser
	 * @param appKey
	 * @param dealUser
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "注册DealUser", notes="注册DealUser")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "appKey", value = "appKey", required = true),
	})
	@PostMapping("deal-users")
	public DealUser register(
	        @RequestParam("appKey") String appKey, DealUser dealUser) {
		return dealUserService.registerDealUser(dealUser, appKey);
	}

	@ApiOperation(value = "批量注册DealUser", notes="批量注册DealUser")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "app-id", value = "app-id", required = true),
	})
	@PostMapping("app/{app-id}/batch/deal-users")
	public List<DealUser> registers(@PathVariable("app-id") String appId, DealUser dealUser) {
		return dealUserService.batch(appId, dealUser);
	}

	/**
	 * 注册DealUser
	 * @param appKey
	 * @param dealUser
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "注册DealUser", notes="注册DealUser")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "app_id", required = true),
	})
	@PostMapping("/app/{app_id}/deal-users")
	public DealUser registerWithAppId(
			@PathVariable("app_id") String appKey, DealUser dealUser) {
		return dealUserService.registerDealUser(dealUser, appKey);
	}
	
	/**
	 * 查询单个DealUser
	 *
	 * @param dealUserId
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "查询单个DealUser", notes="查询单个DealUser")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "path", dataType = "String", name = "deal_user_id", value = "deal用户id", required = true),
	})
	@GetMapping("deal-user/{deal_user_id}")
	public DealUser getDealUserById(
	        @PathVariable("deal_user_id") String dealUserId) throws Exception {
		return dealUserService.getDealUserById(dealUserId);
	}

	/**
	 * 绑定客户
	 * 
	 * @param dealUserId
	 * @param customerId
	 * @return
	 */
	@ApiOperation(value = "DealUser绑定客户", notes="DealUser绑定客户")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "path", dataType = "String", name = "deal_user_id", value = "deal用户id", required = true),
		@ApiImplicitParam(paramType = "query", dataType = "String", name = "customer_id", value = "客户id", required = true),
	})
	@PostMapping("deal-user/{deal_user_id}/bind-customer")
	public DealUser bindCustomer(
	        @PathVariable("deal_user_id") String dealUserId,
	        @RequestParam(name = "customer_id") String customerId) {
		return dealUserService.bindCustomer(dealUserId, customerId);
	}
	
	/**
	 * 实名化客户
	 * 
	 * @param req
	 * @param dealUserId
	 * @param name
	 * @param title
	 * @param email
	 * @param phone
	 * @param company
	 * @param exts
	 * @param updateCustomer
	 * @param kuickUserId
	 * @param fromType
	 * @param createWay
	 * @param fromInfo
	 * @return
	 */
	@ApiOperation(value = "实名化DealUser", notes="实名化DealUser")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "path", dataType = "String", name = "deal_user_id", value = "deal用户id", required = true),
		
		@ApiImplicitParam(paramType = "form", dataType = "String", name = "name", value = "客户名称", required = true),
		@ApiImplicitParam(paramType = "form", dataType = "String", name = "title", value = "客户职位", required = false),
		@ApiImplicitParam(paramType = "form", dataType = "String", name = "email", value = "客户邮箱", required = false),
		@ApiImplicitParam(paramType = "form", dataType = "String", name = "phone", value = "客户手机号", required = false),
		@ApiImplicitParam(paramType = "form", dataType = "String", name = "company", value = "客户所在公司名称", required = false),
		@ApiImplicitParam(paramType = "form", dataType = "String", name = "exts", value = "扩展字段", required = false),
		
		@ApiImplicitParam(paramType = "form", dataType = "String", name = "updateCustomer", value = "更新客户方式", required = false),
		@ApiImplicitParam(paramType = "form", dataType = "String", name = "kuick_user_id", value = "客户分配的销售", required = false),
		@ApiImplicitParam(paramType = "form", dataType = "String", name = "from_type", value = "来源类型", required = false),
		@ApiImplicitParam(paramType = "form", dataType = "String", name = "create_way", value = "创建方式", required = false),
		@ApiImplicitParam(paramType = "form", dataType = "String", name = "from_info", value = "来源信息", required = false)
	})
	@PostMapping("deal-user/{deal_user_id}/named")
	public DealUser namedDealUser(
			HttpServletRequest req,
	        @PathVariable("deal_user_id") String dealUserId,
	        
	        @RequestParam(name = "name") String name,
	        @RequestParam(name = "title", required=false) String title,
	        @RequestParam(name = "email", required=false) String email,
	        @RequestParam(name = "phone", required=false) String phone,
	        @RequestParam(name = "company", required=false) String company,
	        @RequestParam(name = "exts", required=false) String exts,
	        
	        @RequestParam(name = "updateCustomer", required=false, defaultValue="0") String updateCustomer,
	        @RequestParam(name = "kuick_user_id", required=false) String kuickUserId,
	        @RequestParam(name = "from_type", required=false) String fromType,
	        @RequestParam(name = "create_way", required=false) String createWay,
	        @RequestParam(name = "auto_transfer", defaultValue = "0") String autoTransfer,
	        @RequestParam(name = "from_info", required=false) String fromInfo
	    ) {
		Map<String, String> params = ParamUtils.decodeBase64JSONAsMap(exts);
		logger.info("namedDealUser.exts: {}", params);

		Map<String, Object> opts = MapUtils.from(new Object[] {
			"updateCustomer", updateCustomer,
			"kuick_user_id", kuickUserId,
			"from_type", fromType,
			"create_way", createWay,
			"auto_transfer", autoTransfer,
			"from_info", Base64Utils.decodeStr(fromInfo),
			"ip", IPUtils.getPublicIp(req)
		});
		
		return dealUserService.namedDealUser(dealUserId, name, title, email, phone, company, params, opts);
	}
	
	/**
	 * 实名化多个DealUser
	 * 
	 * @param req
	 * @param dealUserId
	 * @param dealUserIdsStr
	 * @param name
	 * @param title
	 * @param email
	 * @param phone
	 * @param company
	 * @param exts
	 * @param fromType
	 * @param createWay
	 * @param fromInfo
	 * @return
	 */
	@ApiOperation(value = "实名化DealUser, 根据DealUserId", notes="实名化DealUser, 根据另一个DealUser")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "path", dataType = "String", name = "deal_user_id", value = "deal用户id", required = true),
		@ApiImplicitParam(paramType = "form", dataType = "String", name = "deal_user_ids", value = "DealUserIds", required = true),
		
		@ApiImplicitParam(paramType = "form", dataType = "String", name = "name", value = "客户名称", required = false),
		@ApiImplicitParam(paramType = "form", dataType = "String", name = "title", value = "客户职位", required = false),
		@ApiImplicitParam(paramType = "form", dataType = "String", name = "email", value = "客户邮箱", required = false),
		@ApiImplicitParam(paramType = "form", dataType = "String", name = "phone", value = "客户手机号", required = false),
		@ApiImplicitParam(paramType = "form", dataType = "String", name = "company", value = "客户所在公司名称", required = false),
		@ApiImplicitParam(paramType = "form", dataType = "String", name = "exts", value = "扩展字段", required = false),
		
		@ApiImplicitParam(paramType = "form", dataType = "String", name = "from_type", value = "来源类型", required = false),
		@ApiImplicitParam(paramType = "form", dataType = "String", name = "create_way", value = "创建方式", required = false)
	})
	@PostMapping("deal-user/{deal_user_id}/named-by-dealuser")
	public List<DealUser> namedDealUserByDealUserId(
			HttpServletRequest req,
	        @PathVariable("deal_user_id") String dealUserId,
	        @RequestParam(name = "deal_user_ids") String dealUserIdsStr,
	        
	        @RequestParam(name = "name", required=false) String name,
	        @RequestParam(name = "title", required=false) String title,
	        @RequestParam(name = "email", required=false) String email,
	        @RequestParam(name = "phone", required=false) String phone,
	        @RequestParam(name = "company", required=false) String company,
	        @RequestParam(name = "exts", required=false) String exts,

			@RequestParam(name = "updateCustomer", required=false, defaultValue="1") String updateCustomer,
			@RequestParam(name = "kuick_user_id", required=false) String kuickUserId,
			@RequestParam(name = "from_type", required=false) String fromType,
			@RequestParam(name = "create_way", required=false) String createWay,
			@RequestParam(name = "auto_transfer", defaultValue = "0") String autoTransfer,
			@RequestParam(name = "from_info", required=false) String fromInfo
	) {
		String[] dealUserIds = dealUserIdsStr.split(",");
		Map<String, String> params = ParamUtils.decodeBase64JSONAsMap(exts);
		Map<String, Object> opts = MapUtils.from(new Object[] {
			"updateCustomer", updateCustomer,
			"kuick_user_id", kuickUserId,
			"auto_transfer", autoTransfer,
			"from_type", fromType,
			"create_way", createWay,
			"from_info", Base64Utils.decodeStr(fromInfo),
			"ip", IPUtils.getPublicIp(req)
		});
		
		return dealUserService.namedDealUserWithDealUserIds(dealUserId, dealUserIds, name, title, email, phone, company, params, opts);
	}
	
	/**
	 * 实名化另一个DealUser(AppUserId)
	 * 
	 * @param req
	 * @param dealUserId
	 * @param appUserId
	 * @param name
	 * @param title
	 * @param email
	 * @param phone
	 * @param company
	 * @param exts
	 * @param fromType
	 * @param createWay
	 * @param fromInfo
	 * @return
	 */
	@ApiOperation(value = "实名化DealUser, 根据appUserId", notes="实名化DealUser, 根据另一个DealUser")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "path", dataType = "String", name = "deal_user_id", value = "deal用户id", required = true),
		@ApiImplicitParam(paramType = "form", dataType = "String", name = "app_user_id", value = "AppUserID", required = false),
		
		@ApiImplicitParam(paramType = "form", dataType = "String", name = "name", value = "客户名称", required = true),
		@ApiImplicitParam(paramType = "form", dataType = "String", name = "title", value = "客户职位", required = false),
		@ApiImplicitParam(paramType = "form", dataType = "String", name = "email", value = "客户邮箱", required = false),
		@ApiImplicitParam(paramType = "form", dataType = "String", name = "phone", value = "客户手机号", required = false),
		@ApiImplicitParam(paramType = "form", dataType = "String", name = "company", value = "客户所在公司名称", required = false),
		@ApiImplicitParam(paramType = "form", dataType = "String", name = "exts", value = "扩展字段", required = false),
		
		@ApiImplicitParam(paramType = "form", dataType = "String", name = "from_type", value = "来源类型", required = false),
		@ApiImplicitParam(paramType = "form", dataType = "String", name = "create_way", value = "创建方式", required = false),
		@ApiImplicitParam(paramType = "form", dataType = "String", name = "from_info", value = "来源信息", required = false)
	})
	@PostMapping("deal-user/{deal_user_id}/named-by-appuser")
	public List<DealUser> namedDealUserByAppUserId(
			HttpServletRequest req,
	        @PathVariable("deal_user_id") String dealUserId,
	        @RequestParam(name = "app_user_id") String appUserId,
	        
	        @RequestParam(name = "name", required=false) String name,
	        @RequestParam(name = "title", required=false) String title,
	        @RequestParam(name = "email", required=false) String email,
	        @RequestParam(name = "phone", required=false) String phone,
	        @RequestParam(name = "company", required=false) String company,
	        @RequestParam(name = "exts", required=false) String exts,

			@RequestParam(name = "updateCustomer", required=false, defaultValue="1") String updateCustomer,
			@RequestParam(name = "kuick_user_id", required=false) String kuickUserId,
			@RequestParam(name = "from_type", required=false) String fromType,
			@RequestParam(name = "create_way", required=false) String createWay,
			@RequestParam(name = "auto_transfer", defaultValue = "0") String autoTransfer,
			@RequestParam(name = "from_info", required=false) String fromInfo
		) {
		Map<String, String> params = ParamUtils.decodeBase64JSONAsMap(exts);
		Map<String, Object> opts = MapUtils.from(new Object[] {
			"updateCustomer", updateCustomer,
			"from_type", fromType,
			"auto_transfer", autoTransfer,
			"kuick_user_id", kuickUserId,
			"create_way", createWay,
			"from_info", Base64Utils.decodeStr(fromInfo),
			"ip", IPUtils.getPublicIp(req)
		});
		
		return dealUserService.namedDealUserWithAppUserId(dealUserId, appUserId, name, title, email, phone, company, params, opts);
	}
}
