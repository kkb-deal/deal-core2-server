package cn.deal.core.customer.resource;

import cn.deal.component.spring.MultipartResource;
import cn.deal.component.utils.AssertUtils;
import cn.deal.component.utils.ExcelUtils;
import cn.deal.component.utils.MapUtils;
import cn.deal.component.utils.ParamUtils;
import cn.deal.core.customer.dao.impl.CustomerDaoJdbcTemplateImpl;
import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.domain.CustomerEnum;
import cn.deal.core.customer.domain.CustomerId;
import cn.deal.core.customer.domain.vo.CustomerSearchVO;
import cn.deal.core.customer.engine.CustomerEngine;
import cn.deal.core.customer.service.CustomerExcelImportService;
import cn.deal.core.customer.service.CustomerService;
import cn.deal.core.customer.service.MergedCustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


@RestController
@Api(value = "客户管理", description = "客户管理", tags = {"客户管理"}, produces = MediaType.ALL_VALUE)
@RequestMapping("/api/v1.0")
public class CustomerResource {

	private Logger logger = LoggerFactory.getLogger(CustomerDaoJdbcTemplateImpl.class);
	
    @Autowired
    private CustomerService customerService;

    @Autowired
    private MergedCustomerService mergedCustomerService;

    @Autowired
    private CustomerEngine customerEngine;

    @Autowired
    private CustomerExcelImportService customerExcelImportService;

    @Value(value = "classpath:excel/upload_customer_template.xls")
    private Resource template;

    @ApiOperation(value = "下载客户excel模板", notes = "根据项目ID获取客户元数据，导出excel模版")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
    })
    @GetMapping("/app/{app_id}/customer-import-template")
    public void customerImportTemplate(@PathVariable("app_id") String appId, HttpServletRequest request, HttpServletResponse response) {
        AssertUtils.notEmpty(appId, "导出excel模版失败，项目appId不能为空");
        try {
            List<String> list = customerService.getCustomerTitlesByAppId(appId);
            ExcelUtils.expExcel("upload_customer_template", list, request, response);
        } catch (Exception e) {
            logger.error("导出excel模版失败，appId:{}", appId, e);
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "创建客户", notes = "创建客户")
    @ApiImplicitParams({
	    @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
	    @ApiImplicitParam(paramType = "path", dataType = "String", name = "kuick_user_id", value = "销售的KuickUserid", required = true)
    })
    @RequestMapping(value = "/app/{app_id}/customers", method = RequestMethod.POST)
    public Customer createCustomer(@PathVariable("app_id") String appId, 
    		@RequestParam(name="kuick_user_id", required = false) String kuickUserId,
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
    
    @ApiOperation(value = "批量导入客户", notes = "批量导入客户")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
        @ApiImplicitParam(paramType = "path", dataType = "String", name = "kuick_user_id", value = "销售KuickUserId", required = true),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "is_belonged_me", value = "是否属于自己,1/是、0/否"),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "customer_group_id", value = "客户分组查询"),
        @ApiImplicitParam(paramType = "query", dataType = "File", name = "file", value = "客户Excel文件")
    }) 
    @PostMapping("/app/{app_id}/batch-add-customer")
    public void batchAddCustomers(@PathVariable("app_id") String appId,
    		@RequestHeader(value="kd_kuick_user_id") String kuickUserId,
    		@RequestParam(name = "is_belonged_me", required = false) String isBelongedMe,
    		@RequestParam(name = "customer_group_id", required = false) String customerGroupId,
    		@RequestParam(name = "file", required = false) MultipartFile excelFile) {
    	AssertUtils.notEmpty(appId, "项目ID不能为空");
    	
    	Map<String, Object> opts = MapUtils.from(new Object[] {
        	"kuickUserId", kuickUserId,
        	"isBelongedMe", isBelongedMe,
        	"customerGroupId", customerGroupId
        });
    
    	Resource excel = new MultipartResource(excelFile);
    	customerExcelImportService.batchAddCustomerFromExcelFile(appId, excel, opts);
    }
    
    @ApiOperation(value = "更新客户", notes = "更新客户")
    @ApiImplicitParams({
	    @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
	    @ApiImplicitParam(paramType = "path", dataType = "String", name = "kuick_user_id", value = "销售的KuickUserid", required = true),
	    @ApiImplicitParam(paramType = "path", dataType = "String", name = "customer_id", value = "客户ID", required = true)
    })
    @RequestMapping(value = "app/{app_id}/customer/{customer_id}", method = RequestMethod.PUT)
    public Customer updateCustomer(@PathVariable("app_id") String appId, @PathVariable("customer_id") String customerId,
    		@RequestParam(name="kuick_user_id", required = false) String kuickUserId,
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
    
    
    @ApiOperation(value = "删除客户", notes = "删除客户")
    @ApiImplicitParams({
	    @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
	    @ApiImplicitParam(paramType = "path", dataType = "String", name = "customer_id", value = "客户ID", required = true)
    })
    @RequestMapping(value = "app/{app_id}/customer/{customer_id}", method = RequestMethod.DELETE)
    public Customer deleteCustomer(@PathVariable("app_id") String appId, 
    		@PathVariable("customer_id") String customerId) {
        return customerEngine.handleDelete(appId, customerId);
    }
    
    @ApiOperation(value = "查询客户详细", notes = "查询客户详细")
    @ApiImplicitParams({
	    @ApiImplicitParam(paramType = "path", dataType = "String", name = "customer_id", value = "客户ID", required = true)
    })
    @RequestMapping(value = "customer/{customer_id}/detail", method = RequestMethod.GET)
    public Customer getCustomer(@PathVariable("customer_id") String customerId,
    		HttpServletRequest request) {
        return customerService.getCustomerById(customerId);
    }

    /**
     * 查询客户信息 注意：不会返回销售密码和unionid！
     * @param appId
     * @param customerId
     * @param withKuickuser
     * @return
     */
    @ApiOperation(value = "查询客户详细", notes = "查询客户详细")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目ID", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "customer_id", value = "客户ID", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "with_kuickuser", value = "是否查询销售信息: 1 查； 0 不查", required = true)
    })
    @RequestMapping(value = "app/{app_id}/customer/{customer_id}/detail", method = RequestMethod.GET)
    public Customer getCustomer(@PathVariable("app_id") String appId,
                                @PathVariable("customer_id") String customerId,
                                @RequestParam(value = "with_kuickuser", required = false) Integer withKuickuser) {
        return customerService.getCustomerById(appId, customerId, withKuickuser);
    }

    @ApiOperation(value = "筛选客户", notes = "筛选客户")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "filter_id", value = "过滤器id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "start_index", value = "起始序号"),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "count", value = "数量"),})
    @RequestMapping(value = "/app/{app_id}/screening/customers", method = RequestMethod.GET)
    public List<Customer> getScreeningCustomer(@PathVariable("app_id") String appId,
                                               @RequestParam(value = "filter_id") String filterId,
                                               @RequestParam(value = "start_index", required = false) Integer startIndex,
                                               @RequestParam(value = "count", required = false) Integer count) {
        return customerService.getScreeningCustomer(appId, filterId, startIndex, count);
    }

    @ApiOperation(value = "筛选客户", notes = "筛选客户")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "filter_id", value = "过滤器id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "start_index", value = "起始序号"),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "count", value = "数量"),})
    @RequestMapping(value = "/app/{app_id}/screening/customer-ids", method = RequestMethod.GET)
    public Map<String, Object> getScreeningCustomerIds(@PathVariable("app_id") String appId,
                                               @RequestParam(value = "filter_id") String filterId,
                                               @RequestParam(value = "start_index", required = false) Integer startIndex,
                                               @RequestParam(value = "count", required = false) Integer count) {
        List<Customer> customers = customerService.getScreeningCustomer(appId, filterId, startIndex, count);
        List<String> customerIds = new ArrayList<>();
        for(Customer customer : customers){
            customerIds.add(customer.getId());
        }
        Map<String, Object> map = new HashMap<>(1);
        map.put("customerIds", customerIds);
        return map;
    }

    @ApiOperation(value = "获取筛选客户数量", notes = "获取筛选客户数量")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "filter_id", value = "过滤器id", required = true)})
    @GetMapping("/app/{app_id}/screening/customers/count")
    public long countCustomerByFilter(
            @PathVariable("app_id") String appId,
            @RequestParam(value = "filter_id") String filterId) {
        return customerService.countCustomerByFilter(appId, filterId);
    }

    @ApiOperation(value = "查找客户", notes = "查找客户")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "phone", value = "电话"),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "email", value = "email"),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "start_index", value = "起始序号", required = true, defaultValue = "0"),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "count", value = "数量", required = true, defaultValue = "10"),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "deal_user_ids", value = "deal用户ID，如有多个，用逗号分隔,与其他条件不同时满足", required = false),
    })
    @RequestMapping(value = "app/{app_id}/inner-search/customers", method = RequestMethod.GET)
    public List<Customer> exactMatch(@PathVariable("app_id") String appId,
                                     @RequestParam(name = "phone", required = false) String phone,
                                     @RequestParam(name = "email", required = false) String email,
                                     @RequestParam(name = "union_id", required = false) String unionId,
                                     @RequestParam(name = "start_index", defaultValue = "0") int startIndex,
                                     @RequestParam(name = "count", defaultValue = "10") int count,
                                     @RequestParam(name = "deal_user_ids", required = false) String dealUserIds,
                                     @RequestParam(name = "with_kuickuser", defaultValue = "0") int withKuickUser
    ) {
        if (StringUtils.isNotBlank(dealUserIds)) {
            String[] dealUserIdArr = dealUserIds.split(",");
            List<String> dealuserIdList = new ArrayList<>(Arrays.asList(dealUserIdArr));
            return customerService.getCustomerListByDealuserIds(dealuserIdList);
        } else {
            CustomerSearchVO params = CustomerSearchVO.builder()
                    .appId(appId).phone(phone).email(email).unionId(unionId).withKuickUser(withKuickUser).startIndex(startIndex).count(count).build();
            logger.info("exactMatch.params: {}", params);
            return customerService.exactMatchV2(params);
        }
    }

    /**
     * 根据分群id，获取客户集合
     *
     * @param appId              项目id
     * @param swarmId            分群id
     * @param isThin             是否返回瘦信息，取值true/false
     * @param startIndex         页数，默认值：0（第一页）
     * @param count              每页记录数，默认20，最大1000
     * @param attributes         返回对象的属性列表，英文逗号分隔
     * @param notBlankAttributes 非空属性条件列表，英文逗号分隔
     * @return 当isThin为true时，只返回customer_id属性。为false时，返回全部属性。
     */
    @SuppressWarnings("rawtypes")

    @ApiOperation(value = "获取客户集合", notes = "获取客户集合")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "swarm_id", value = "分群id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "boolean", name = "is_thin", value = "是否返回瘦信息", required = true, defaultValue = "true"),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "start_index", value = "页数", required = true, defaultValue = "0"),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "count", value = "每页记录数", required = true, defaultValue = "20"),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "attributes", value = "返回对象的属性列表，英文逗号分隔"),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "notBlankAttributes", value = "非空属性条件列表，英文逗号分隔"),
    })
    @GetMapping("app/{app_id}/customer-swarm/{swarm_id}/inner-members")
    public List getCustomerSwarm(
            @PathVariable("app_id") String appId,
            @PathVariable("swarm_id") String swarmId,
            @RequestParam(name = "is_thin", defaultValue = "true") boolean isThin,
            @RequestParam(name = "start_index", defaultValue = "0") int startIndex,
            @RequestParam(name = "count", defaultValue = "20") int count,
            @RequestParam(value = "attributes", required = false) String attributes,
            @RequestParam(value = "notBlankAttributes", required = false) String notBlankAttributes
    ) throws MissingServletRequestParameterException {

        if (StringUtils.isBlank(appId)) {
            throw new MissingServletRequestParameterException("app_id", "String");
        }
        if (StringUtils.isBlank(swarmId)) {
            throw new MissingServletRequestParameterException("swarm_id", "String");
        }

        if (startIndex < 0) {
            startIndex = 0;
        }
        
        if (count <= 0 || count > 1000) {
            count = 20;
        }
        
        if (isThin) {
            return customerService.getCustomerIdBySwarmIdAndAppId(appId, swarmId, startIndex, count);
        } else if (!StringUtils.isBlank(attributes)) {
            Map<String, Integer> attributesMap = getAttributesMap(attributes, notBlankAttributes);
            return customerService.getCustomerBySwarmIdAndAppId(appId, swarmId, startIndex, count, attributesMap);
        } else {
            return customerService.getCustomerBySwarmIdAndAppId(appId, swarmId, startIndex, count, null);
        }
    }

    /**
     * 根据项目id，获取客户集合
     *
     * @param appId              项目id
     * @param isThin             是否返回瘦信息，取值true/false
     * @param startIndex         页数，默认值：0（第一页）
     * @param count              每页记录数，默认20，最大1000
     * @param attributes         返回对象的属性列表，英文逗号分隔
     * @param notBlankAttributes 非空属性条件列表，英文逗号分隔
     * @return 当isThin为true时，只返回customer_id属性。为false时，返回全部属性。
     */
    @SuppressWarnings("rawtypes")

    @ApiOperation(value = "根据项目id，获取客户集合", notes = "根据项目id，获取客户集合")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "boolean", name = "is_thin", value = "是否返回瘦信息", required = true, defaultValue = "true"),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "start_index", value = "页数", required = true, defaultValue = "0"),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "count", value = "每页记录数", required = true, defaultValue = "20"),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "attributes", value = "返回对象的属性列表，英文逗号分隔"),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "notBlankAttributes", value = "非空属性条件列表，英文逗号分隔"),})
    @GetMapping("app/{app_id}/customers")
    public List getCustomers(@PathVariable("app_id") String appId,
                             @RequestParam(name = "is_thin", defaultValue = "true") boolean isThin,
                             @RequestParam(name = "start_index", defaultValue = "0") int startIndex,
                             @RequestParam(name = "count", defaultValue = "20") int count,
                             @RequestParam(value = "attributes", required = false) String attributes,
                             @RequestParam(value = "notBlankAttributes", required = false) String notBlankAttributes)
            throws MissingServletRequestParameterException {

        if (StringUtils.isBlank(appId)) {
            throw new MissingServletRequestParameterException("app_id", "String");
        }

        if (startIndex < 0) {
            startIndex = 0;
        }
        if (count <= 0 || count > 1000) {
            count = 20;
        }

        if (isThin) {
            return customerService.getCustomerIdByAppId(appId, startIndex, count);
        } else if (!StringUtils.isBlank(attributes)) {
            Map<String, Integer> attributesMap = getAttributesMap(attributes, notBlankAttributes);
            return customerService.getCustomerByAppId(appId, startIndex, count, attributesMap);
        } else {
            return customerService.getCustomerByAppId(appId, startIndex, count, null);
        }
    }

    private Map<String, Integer> getAttributesMap(String attributes, String notBlankAttributes) {
        List<String> attributesList = Arrays.asList(attributes.split(","));
        List<String> notBlankList = null;
        List<String> illegalList = new LinkedList<>(); // 非法的输入内容集合
        Map<String, Integer> attributesMap = new HashMap<>(); // V: 0-可以为空
        // 1-不能为空
        int index = 0;

        if (!StringUtils.isBlank(notBlankAttributes)) {
            notBlankList = Arrays.asList(notBlankAttributes.split(","));
        }

        for (int i = 0; i < attributesList.size(); i++) {
            String attribute = attributesList.get(i);
            for (CustomerEnum customerEnum : CustomerEnum.values()) {
                if (attribute.equals(customerEnum.getValue())) {
                    index = 1; // 查找是否在枚举类中
                    break;
                }
            }
            if (index == 1) {
                if (notBlankList != null && notBlankList.contains(attribute)) {
                    attributesMap.put(attribute, 1);
                } else {
                    attributesMap.put(attribute, 0);
                }
            } else {
                illegalList.add(attribute);
            }
            index = 0;
        }
        return attributesMap;
    }

    /**
     * 根据分群ID，获取客户ID列表以及已经合并之前的客户ID列表
     *
     * @param appId      项目ID
     * @param swarmId    分群ID
     * @param startIndex 开始索引，默认0
     * @param count      记录条数，默认20，最大1000
     * @return
     * @throws MissingServletRequestParameterException
     */

    @ApiOperation(value = "根据分群ID，获取客户ID列表以及已经合并之前的客户ID列表", notes = "根据分群ID，获取客户ID列表以及已经合并之前的客户ID列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "swarm_id", value = "分群id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "start_index", value = "页数", required = true, defaultValue = "0"),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "count", value = "每页记录数", required = true, defaultValue = "20"),})
    @GetMapping("app/{app_id}/customer-swarm/{swarm_id}/members-with-merged-customers")
    public Map<String, Object> getSwarmMemberIdsWithMergedCustomerIds(@PathVariable("app_id") String appId,
                                                                      @PathVariable("swarm_id") String swarmId,
                                                                      @RequestParam(name = "start_index", defaultValue = "0") int startIndex,
                                                                      @RequestParam(name = "count", defaultValue = "20") int count)
            throws MissingServletRequestParameterException {
        if (StringUtils.isBlank(appId)) {
            throw new MissingServletRequestParameterException("app_id", "String");
        }

        if (StringUtils.isBlank(swarmId)) {
            throw new MissingServletRequestParameterException("swarm_id", "String");
        }

        if (startIndex < 0) {
            startIndex = 0;
        }
        if (count <= 0 || count > 10000) {
            count = 20;
        }

        Map<String, Object> result = new LinkedHashMap<>();

        List<String> customerIds = new ArrayList<>();
        List<String> mergedCustomerIds = new ArrayList<>();

        List<CustomerId> members = customerService.getCustomerIdBySwarmIdAndAppId(appId, swarmId, startIndex, count);

        if (members != null && members.size() > 0) {
            for (CustomerId id : members) {
                customerIds.add(id.getId());
            }
            mergedCustomerIds = mergedCustomerService.getMergedCustomerIdsWithPage(appId, swarmId, startIndex, count);
        }

        result.put("customerIds", customerIds);
        result.put("mergedCustomerIds", mergedCustomerIds);

        return result;
    }

    /**
     * TODO 微信群发接口不用的话，可以删除 根据appid和根据分群id集合（英文逗号分隔），获取去重后的客户数量
     *
     * @param appId     项目id
     * @param swarm_ids 分群id集合
     * @return 客户数量(long)
     * @throws MissingServletRequestParameterException
     */

    @ApiOperation(value = "根据appid和根据分群id集合（英文逗号分隔），获取去重后的客户数量", notes = "根据appid和根据分群id集合（英文逗号分隔），获取去重后的客户数量")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "swarm_ids", value = "分群id"),})
    @GetMapping("app/{app_id}/customer-count")
    @Deprecated
    public long getTotal(@PathVariable("app_id") String appId,
                         @RequestParam(value = "swarm_ids", required = false) String swarm_ids)
            throws MissingServletRequestParameterException {

        if (StringUtils.isBlank(appId)) {
            throw new MissingServletRequestParameterException("appId", "String");
        }
        long customers;
        if (StringUtils.isBlank(swarm_ids)) {
            customers = customerService.getTotalByAppId(appId, null);
        } else {
            List<String> swarmIdsList = Arrays.asList(swarm_ids.split(","));
            customers = customerService.getTotalByAppId(appId, swarmIdsList);
        }
        logger.info("客户数:" + String.valueOf(customers));
        return customers;
    }

    /**
     * TODO 微信群发接口不用的话，可以删除 根据appid和根据分群id集合（英文逗号分隔），获取客户数量（包含合并以前的）
     *
     * @param appId     项目id
     * @param swarm_ids 分群id集合
     * @return 客户数量(long)
     * @throws MissingServletRequestParameterException
     */
    @Deprecated

    @ApiOperation(value = "根据appid和根据分群id集合（英文逗号分隔），获取客户数量（包含合并以前的）", notes = "根据appid和根据分群id集合（英文逗号分隔），获取客户数量（包含合并以前的）")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "swarm_ids", value = "分群id集合"),})
    @GetMapping("app/{app_id}/customer-count-with-merged")
    public long getTotalCountMap(@PathVariable("app_id") String appId,
                                 @RequestParam(value = "swarm_ids", required = false) String swarm_ids)
            throws MissingServletRequestParameterException {

        if (StringUtils.isBlank(appId)) {
            throw new MissingServletRequestParameterException("appId", "String");
        }
        long customers;
        if (StringUtils.isBlank(swarm_ids)) {
            customers = customerService.getTotalByAppIdWithMerged(appId, null);
        } else {
            List<String> swarmIdsList = Arrays.asList(swarm_ids.split(","));
            customers = customerService.getTotalByAppIdWithMerged(appId, swarmIdsList);
        }
        logger.info("客户数:" + String.valueOf(customers));
        // 查询所有分群客户数，包括合并之前的
        return customers;
    }

    @ApiOperation(value = "获取最新客户详情", notes = "获取最新客户详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "customer_id", value = "客户id", required = true),})
    @RequestMapping(value = "/app/{app_id}/customer/{customer_id}/latest-detail", method = RequestMethod.GET)
    public Customer getLatestCustomer(@PathVariable("app_id") String appId,
                                      @PathVariable("customer_id") String customerId) {
        return customerService.getLatestCustomer(appId, customerId);
    }

    @ApiOperation(value = "分页获取customer", notes = "分页获取customer")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
    })
    @RequestMapping(value = "/app/{app_id}/raw/customers", method = RequestMethod.GET)
    public List<Customer> getRawCustomer(@PathVariable("app_id") String appId,
                                         @RequestParam(name = "start_index", defaultValue = "0") Integer startIndex,
                                         @RequestParam(name = "count", defaultValue = "20") Integer count) {
        return customerService.getRawCustomer(appId, startIndex, count);
    }

    @ApiOperation(value = "获取customer总数,包括合并之前的", notes = "分页获取customer")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
    })
    @RequestMapping(value = "/app/{app_id}/raw/customer-count", method = RequestMethod.GET)
    public long getRawCustomerCount(@PathVariable("app_id") String appId) {
        return customerService.getRawCustomerCount(appId);
    }

}
