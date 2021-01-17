package cn.deal.core.tag.resource;

import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.domain.CustomerId;
import cn.deal.core.customer.service.CustomerService;
import cn.deal.core.tag.domain.CustomerTag;
import cn.deal.core.tag.service.DealUserTagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@Api(value = "deal标签服务", description = "deal标签服务",tags={"deal标签服务"},produces = MediaType.ALL_VALUE)
@RequestMapping("/api/v1.0")
public class TagResource {
	private Logger logger = LoggerFactory.getLogger(TagResource.class);
	
	@Autowired
	private DealUserTagService dealUserTagService;
	@Autowired
	private CustomerService customerService;
	
    @ApiOperation(value = "获取客户标签", notes="获取客户标签")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "app_id", value = "项目id", required = true),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "deal_user_ids", value = "deal客户id", required = true),
        @ApiImplicitParam(paramType = "query", dataType = "int", name = "time_range_type", value = "时间区间类型", required = true),
    })
	@GetMapping("/tags")
	public List<CustomerTag> getCustomerTags(
			@RequestParam("app_id") String appId,
			@RequestParam("deal_user_ids") String dealUserIds,
			@RequestParam("time_range_type") int timeRangeType) {
		return dealUserTagService.getCustomerTags(appId, dealUserIds, timeRangeType);
	}

	@ApiOperation(value = "根据标签搜索客户", notes="根据标签搜索客户")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "kuick_user_id", value = "销售id", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "tags", value = "标签集合", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "greate_tag_count", value = "描述", required = true, defaultValue = ""),
	})
	@GetMapping("/app/{app_id}/tag-search/customers")
	public List<Customer> getCustomers(
			@PathVariable("app_id") String appId,
			@RequestParam("kuick_user_id") Integer kuickUserId,
			@RequestParam("tags") String tags,
			@RequestParam(name = "greate_tag_count", defaultValue = "") Integer greatTagCount) {
		return customerService.getTagCustomers(tags, appId, kuickUserId, greatTagCount);
	}

	@ApiOperation(value = "根据标签搜索客户id", notes="根据标签搜索客户id")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "tag", value = "标签", required = true)
	})
	@GetMapping("/app/{app_id}/tag/customer")
	public Map<String, Object> getCustomerByTag(
			@PathVariable("app_id") String appId,
			@RequestParam("tag") String tag,
			@RequestParam("start_index") long startIndex,
			@RequestParam("count") int count) throws MissingServletRequestParameterException {
    	if(StringUtils.isBlank(appId) || StringUtils.isBlank(tag)){
    		throw new MissingServletRequestParameterException("appid, tag", "string");
		}
		List<CustomerId> customers = customerService.getCustomerByTag(appId, tag, startIndex, count);
        List<String> customerIds = new ArrayList<>();
        for(CustomerId customerId : customers){
            customerIds.add(customerId.getId());
        }
        Map<String, Object> map = new HashMap<>(1);
    	map.put("customerIds", customerIds);
    	return map;
	}

    @ApiOperation(value = "根据标签查询客户数", notes="根据标签查询客户数")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "app_id", value = "项目id", required = true),
		@ApiImplicitParam(paramType = "query", dataType = "String", name = "tag", value = "标签", required = true),
    })
	@GetMapping("/app/{app_id}/tag-customer-count")
	public long getTagCustomerCount(
			@PathVariable("app_id") String appId,
			@RequestParam("tag") String tag){
		if(StringUtils.isNotBlank(tag)){
			try {
				tag = URLDecoder.decode(tag, "UTF-8");
				logger.info("---getTagCustomerCount---"+tag);
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage() , e);
			}
		}
		return customerService.getTagCustomerCount(appId, tag);
	}

    @ApiOperation(value = "获取某个客户下的标签", notes="获取某个客户下的标签")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
		@ApiImplicitParam(paramType = "path", dataType = "String", name = "customer_id", value = "客户id", required = true),
    })
	@GetMapping("/app/{app_id}/customer/{customer_id}/tags")
	public List<CustomerTag> getTagsByCustomerId(
			@PathVariable("app_id") String appId,
			@PathVariable("customer_id") String customerId){
		return dealUserTagService.getDealUserTagsByCustomerId(appId, customerId);
	}

    @ApiOperation(value = "获取某个客户下的标签", notes="获取某个客户下的标签")
    @ApiImplicitParams({
		@ApiImplicitParam(paramType = "path", dataType = "String", name = "customer_id", value = "客户id", required = true),
    })
	@GetMapping("/customer/{customer_id}/tags")
	public List<CustomerTag> getTagsByCustomerId(
			@PathVariable("customer_id") String customerId){
		//查询客户
		Customer customer = customerService.getCustomerById(customerId);
		if(customer == null){
			logger.info("getTagsByCustomerId customerId 没有查到客户");
			return new ArrayList<>();
		}
		return dealUserTagService.getDealUserTagsByCustomerId(customer.getAppId(), customerId);
	}

}
