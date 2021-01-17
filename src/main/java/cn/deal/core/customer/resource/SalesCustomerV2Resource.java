package cn.deal.core.customer.resource;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.deal.component.utils.AssertUtils;
import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.service.CustomerService;
import cn.deal.core.customer.service.SalesCustomerService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/v1.1")
public class SalesCustomerV2Resource {

    @Autowired
    private SalesCustomerService salesCustomerService;

    @Autowired
    private CustomerService customerService;

    @ApiOperation(value = "查询销售的客户", notes = "查询销售的客户")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "kuick_user_id", value = "销售KuickUserId", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "phone", value = "根据手机号查询"),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "phone", value = "根据邮箱查询"),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "deal_user_id", value = "DealUserID查询条件"),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "customer_ids", value = "客户ID列表"),
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
    		@RequestParam(name = "customer_ids", required = false) String customerIdListStr,
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
    	} else if (StringUtils.isNoneBlank(customerIdListStr)){ 
    		String[] customerIds = customerIdListStr.split(",");
    		return customerService.findCustomerByIdsAndPage(appId, customerIds, startIndex, count);
    	} else {
	        return salesCustomerService.getSalesCustomers(appId, kuickUserId, targetKuickUserIds, customerGroupId, phone, email, startIndex, count);
	    }
    }
}
