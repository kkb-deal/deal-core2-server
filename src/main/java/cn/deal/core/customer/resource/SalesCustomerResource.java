package cn.deal.core.customer.resource;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.deal.component.spring.MultipartResource;
import cn.deal.component.utils.AssertUtils;
import cn.deal.component.utils.MapUtils;
import cn.deal.component.utils.ParamUtils;
import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.domain.CustomerOpt;
import cn.deal.core.customer.domain.SalesCustomer;
import cn.deal.core.customer.engine.CustomerEngine;
import cn.deal.core.customer.service.CustomerExcelImportService;
import cn.deal.core.customer.service.CustomerExcelImportService.ImportResult;
import cn.deal.core.customer.service.CustomerService;
import cn.deal.core.customer.service.SalesCustomerService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/v1.0")
public class SalesCustomerResource {

    @Autowired
    private SalesCustomerService salesCustomerService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerEngine customerEngine;
    
    @Autowired
    private CustomerExcelImportService customerExcelImportService;
    
    
    @ApiOperation(value = "销售创建客户", notes = "销售创建客户")
    @ApiImplicitParams({
	    @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
	    @ApiImplicitParam(paramType = "path", dataType = "String", name = "kuick_user_id", value = "销售的KuickUserid", required = true)
    })
    @RequestMapping(value = "/app/{app_id}/member/{kuick_user_id}/customers", method = RequestMethod.POST)
    public Customer createCustomer(@PathVariable("app_id") String appId, @PathVariable("kuick_user_id") String kuickUserId,
    		@RequestParam(name="deal_user_id", required = false) String dealUserId,
    		HttpServletRequest request) {
    	Map<String, String> data = ParamUtils.extractParams(request);
    	Map<String, Object> opts = MapUtils.from(new Object[] {
			"kuick_user_id", kuickUserId,
			"deal_user_id", dealUserId
		});
    	
    	// 扩展属性
    	String extsStr = request.getParameter("exts");
    	if (StringUtils.isNotBlank(extsStr)) {
    		Map<String, String> exts = ParamUtils.decodeBase64JSONAsMap(extsStr);
    		data.putAll(exts);
    	}
    	
        return customerEngine.handleCreate(appId, data, opts);
    }
    
    @ApiOperation(value = "批量导入销售的客户", notes = "批量导入销售的客户")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
        @ApiImplicitParam(paramType = "path", dataType = "String", name = "kuick_user_id", value = "销售KuickUserId", required = true),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "is_belonged_me", value = "是否属于自己,1/是、0/否"),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "customer_group_id", value = "客户分组查询"),
        @ApiImplicitParam(paramType = "query", dataType = "File", name = "file", value = "客户Excel文件")
    }) 
    @PostMapping("/app/{app_id}/member/{kuick_user_id}/batch-add-customer")
    public ImportResult batchAddCustomers(@PathVariable("app_id") String appId, @PathVariable("kuick_user_id") String targetKuickUserIds,
    		@RequestHeader(value="kd_kuick_user_id") String kuickUserId,
    		@RequestParam(name = "is_belonged_me", required = false) String isBelongedMe,
    		@RequestParam(name = "customer_group_id", required = false) String customerGroupId,
    		@RequestParam(name = "file", required = false) MultipartFile excelFile) {
    	AssertUtils.notEmpty(appId, "项目ID不能为空");
    	AssertUtils.notEmpty(kuickUserId, "销售KuickUserId不能为空");
    	
    	Map<String, Object> opts = MapUtils.from(new Object[] {
        	CustomerOpt.KUICK_USER_ID, kuickUserId,
        	"isBelongedMe", isBelongedMe,
        	"customerGroupId", customerGroupId
        });
    	
    	Resource excel = new MultipartResource(excelFile);
    	return customerExcelImportService.batchAddCustomerFromExcelFile(appId, excel, opts);
    }
    
    @ApiOperation(value = "销售更新客户", notes = "销售更新客户")
    @ApiImplicitParams({
	    @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
	    @ApiImplicitParam(paramType = "path", dataType = "String", name = "kuick_user_id", value = "销售的KuickUserid", required = true),
	    @ApiImplicitParam(paramType = "path", dataType = "String", name = "customer_id", value = "客户ID", required = true)
    })
    @RequestMapping(value = "app/{app_id}/member/{kuick_user_id}/customer/{customer_id}", method = RequestMethod.PUT)
    public Customer updateCustomer(@PathVariable("app_id") String appId, @PathVariable("kuick_user_id") String kuickUserId,
    		@PathVariable("customer_id") String customerId,
    		HttpServletRequest request) {
    	Map<String, String> data = ParamUtils.extractParams(request);
    	Map<String, Object> opts = MapUtils.from(new Object[] {
    		"kuick_user_id", kuickUserId
    	});
    	
    	// 扩展属性
    	String extsStr = request.getParameter("exts");
    	if (StringUtils.isNotBlank(extsStr)) {
    		Map<String, String> exts = ParamUtils.decodeBase64JSONAsMap(extsStr);
    		data.putAll(exts);
    	}
    	
        return customerEngine.handleUpdate(appId, customerId, data, opts);
    }
    
    @ApiOperation(value = "销售删除客户", notes = "销售删除客户")
    @ApiImplicitParams({
	    @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
	    @ApiImplicitParam(paramType = "path", dataType = "String", name = "kuick_user_id", value = "销售的KuickUserid", required = true),
	    @ApiImplicitParam(paramType = "path", dataType = "String", name = "customer_id", value = "客户ID", required = true)
    })
    @RequestMapping(value = "app/{app_id}/member/{kuick_user_id}/customer/{customer_id}", method = RequestMethod.DELETE)
    public Customer deleteCustomer(@PathVariable("app_id") String appId, @PathVariable("kuick_user_id") String kuickUserId,
    		@PathVariable("customer_id") String customerId) {
        return customerEngine.handleDelete(appId, customerId);
    }
    
    @ApiOperation(value = "合并客户", notes = "合并客户")
    @ApiImplicitParams({
	    @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
	    @ApiImplicitParam(paramType = "path", dataType = "String", name = "kuick_user_id", value = "销售的KuickUserid", required = true),
	    @ApiImplicitParam(paramType = "form", dataType = "String", name = "main_customer_id", value = "主客户ID", required = true),
	    @ApiImplicitParam(paramType = "form", dataType = "String", name = "merge_user_ids", value = "合并的用户ID", required = true),
	    @ApiImplicitParam(paramType = "form", dataType = "String", name = "merge_target_id", value = "目标用户ID", required = true)
    })
    @RequestMapping(value = "/app/{app_id}/member/{kuick_user_id}/customer/merge", method = RequestMethod.POST)
    public Customer mergeCustomer(HttpServletRequest request, 
    		@PathVariable("app_id") String appId, @PathVariable("kuick_user_id") String kuickUserId,
    		@RequestParam(name = "merge_user_ids") String mergeCustomerIdsStr,
    		@RequestParam(name = "main_customer_id") String mainCustomerId,
    		@RequestParam(name = "merge_target_id") String mainTargetUserId
    		) {
    	String[] mergeCustomerIds = mergeCustomerIdsStr.split(",");
    	Map<String, String> data = ParamUtils.extractParams(request);
    	Map<String, Object> opts = MapUtils.from(new Object[] {
    		CustomerOpt.KUICK_USER_ID, kuickUserId
    	});
    	
    	// 扩展属性
    	String extsStr = request.getParameter("exts");
    	if (StringUtils.isNotBlank(extsStr)) {
    		Map<String, String> exts = ParamUtils.decodeBase64JSONAsMap(extsStr);
    		data.putAll(exts);
    	}
    	
        return customerEngine.handleMerge(appId, mergeCustomerIds, mainCustomerId, mainTargetUserId, data, opts);
    }
    
    @ApiOperation(value = "查询销售的客户", notes = "查询销售的客户")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "kuick_user_id", value = "销售KuickUserId", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "phone", value = "根据手机号查询"),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "phone", value = "根据邮箱查询"),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "deal_user_id", value = "DealUserID查询条件"),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "customer_group_id", value = "客户分组查询"),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "start_index", value = "页数", required = true, defaultValue = "0"),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "count", value = "每页记录数", required = true, defaultValue = "20"),
    })
    @GetMapping("/app/{app_id}/member/{kuick_user_id}/customers")
    public List<Customer> getCustomerList(@PathVariable("app_id") String appId, @PathVariable("kuick_user_id") String targetKuickUserIds,
    		@RequestHeader(value="kd_kuick_user_id") String kuickUserId,
    		@RequestParam(name = "phone", required = false) String phone,
    		@RequestParam(name = "email", required = false) String email,
    		@RequestParam(name = "deal_user_id", required = false) String dealUserId,
    		@RequestParam(name = "customer_group_id", required = false) String customerGroupId,
    		@RequestParam(name = "start_index", defaultValue = "0") int startIndex,
            @RequestParam(name = "count", defaultValue = "20") int count) {
    	AssertUtils.notEmpty(appId, "项目ID不能为空");
    	AssertUtils.notEmpty(kuickUserId, "销售KuickUserId不能为空");
    	
    	// 最多返回100条数据
    	if (count > 100) {
    		count = 100;
    	}
    	
    	if (StringUtils.isNoneBlank(dealUserId)) {
    		return customerService.getCustomerListByDealuserIds(Arrays.asList(dealUserId));
    	} else {
	        return salesCustomerService.getSalesCustomers(appId, kuickUserId, targetKuickUserIds, customerGroupId, phone, email, startIndex, count);
	    }
    }
    
    /**
     * 查询客户所属销售
     * 
     * @param customerId
     * @return
     */
    @GetMapping("/app/{app_id}/customer/{customer_id}/member")
    public SalesCustomer getSalesCustomerByCustomerId(@PathVariable("app_id") String appId, @PathVariable("customer_id") String customerId) {
        return salesCustomerService.getSalesCustomerByCustomerId(customerId);
    }

	@GetMapping("/app/{app_id}/member/{kuick_user_id}/customer/search")
	public List<Customer> search(@PathVariable("app_id")String appId,
								 @PathVariable("kuick_user_id")String kuickUserId,
								 @RequestParam(value = "has_phone", defaultValue = "0")int hasPhone,
								 @RequestParam(value = "condition", required = false)String condition,
								 @RequestParam(value = "customer_group_id", defaultValue = "all")String customerGroupId,
								 @RequestHeader(value="kd_kuick_user_id") String currentUserId){
		return salesCustomerService.searchSalesCustomers(appId, kuickUserId, hasPhone, condition, customerGroupId, currentUserId);
	}
}
