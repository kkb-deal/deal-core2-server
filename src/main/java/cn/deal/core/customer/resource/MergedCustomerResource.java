package cn.deal.core.customer.resource;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.deal.component.utils.AssertUtils;
import cn.deal.component.utils.MapUtils;
import cn.deal.component.utils.ParamUtils;
import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.domain.CustomerLinkMergeCustomer;
import cn.deal.core.customer.domain.CustomerOpt;
import cn.deal.core.customer.engine.CustomerEngine;
import cn.deal.core.customer.service.MergedCustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;


@RestController
@Api(value = "合并客户", description = "合并客户",tags={"合并客户"},produces = MediaType.ALL_VALUE)
@RequestMapping("/api/v1.0")
public class MergedCustomerResource {

	@Autowired
	private MergedCustomerService mergedCustomerService;

    @Autowired
    private CustomerEngine customerEngine;


    /**
     * 合并客户
     * @param request
     * @param appId
     * @param kuickUserId
     * @param mergeCustomerIdsStr
     * @param mainCustomerId
     * @param mainTargetUserId
     * @return
     */
    @ApiOperation(value = "合并客户", notes = "合并客户")
    @ApiImplicitParams({
	    @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
	    @ApiImplicitParam(paramType = "path", dataType = "String", name = "kuick_user_id", value = "销售的KuickUserid", required = true),
	    @ApiImplicitParam(paramType = "form", dataType = "String", name = "main_customer_id", value = "主客户ID", required = true),
	    @ApiImplicitParam(paramType = "form", dataType = "String", name = "merge_user_ids", value = "合并的用户ID", required = true),
	    @ApiImplicitParam(paramType = "form", dataType = "String", name = "merge_target_id", value = "目标用户ID", required = true)
    })
    @RequestMapping(value = "/app/{app_id}/customer/merges", method = RequestMethod.POST)
    public Customer mergeCustomer(HttpServletRequest request, 
    		@PathVariable("app_id") String appId, 
    		@RequestParam(name = "kuick_user_id", required=false) String kuickUserId,
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
    
    /**
     * 获取合并客户集合
     * 
     * @param appId
     * @param customerIdsStr
     * @return
     * @throws MissingServletRequestParameterException
     */
    @SuppressWarnings("unchecked")
    @ApiOperation(value = "获取合并客户集合", notes="获取合并客户集合")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "customer_ids", value = "客户id集合", required = true),
    })
	@PostMapping("app/{app_id}/merged-customer-ids")
    public List<String> getMergedCustomerIds(
    		@PathVariable("app_id") String appId,
            @RequestParam(name = "customer_ids") String customerIdsStr
    ) throws MissingServletRequestParameterException {
        if (StringUtils.isBlank(appId)) {
            throw new MissingServletRequestParameterException("app_id","String");
        }

        if (StringUtils.isBlank(customerIdsStr)) {
            throw new MissingServletRequestParameterException("customer_ids","String");
        }
        
        String[] customerIds= customerIdsStr.split(",");
        if (customerIds.length ==0) {
        	return Collections.EMPTY_LIST;
        }
        
        if (customerIds.length > 1000) {
        	throw new RuntimeException("the customer_ids count more than 1000");
        }
        
        return mergedCustomerService.getMergedCustomerIds(appId, customerIds);
    }
    
    /**
     * 获取合并客户数量
     * 
     * @param appId
     * @return
     */
    @ApiOperation(value = "获取合并客户数量", notes="获取合并客户数量")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
    })
    @GetMapping("app/{app_id}/merge-customer-count")
    public int getMergeCustomerCount(@PathVariable("app_id")String appId){
    	return mergedCustomerService.getMergedCustomerCount(appId);
    }
    
    /**
     * 查询合并客户
     * 
     * @param merge_customer_id
     * @return
     */
    @ApiOperation(value = "查询合并客户", notes="查询合并客户")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", dataType = "String", name = "merge_customer_id", value = "合并客户id", required = true),
    })
    @GetMapping(value = "/merge-customer/{merge_customer_id}/customer")
    public List<CustomerLinkMergeCustomer> getCustomerByMergeCustomer(@PathVariable("merge_customer_id") String merge_customer_id) {
        AssertUtils.notNull(merge_customer_id, "merge_customer_id不能为空");
        return mergedCustomerService.getCustomerByMergeCustomer(merge_customer_id);
    }
}
