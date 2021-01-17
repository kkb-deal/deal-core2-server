package cn.deal.core.meta.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import cn.deal.core.meta.domain.CustomerMetaData;
import cn.deal.core.meta.service.CustomerMetaDataService;

/**   
*    
* 项目名称：deal-core-server2   
* 类名称：CustomerMetaDataResource   
* 类描述：   客户源数据后端服务
*/
@RestController
@Api(value = "客户源数据", description = "客户源数据",tags={"客户源数据"},produces = MediaType.ALL_VALUE)
@RequestMapping("/api/v1.0")
public class CustomerMetaDataResource {
	
	@Autowired
	private CustomerMetaDataService customerMetaDataService;

	/**
	 * @throws Exception  
	* @Title: getCustomerMetas 
	* @Description: TODO(获取客户基本字段列表) 
	* @param appId
	* @return 设定文件 
	* @return List<CustomerMeta>    返回类型 
	* @throws 
	*/

    @ApiOperation(value = "获取客户基本字段列表", notes="获取客户基本字段列表")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
    })
	@RequestMapping(value = "/app/{app_id}/customer-metas", method = RequestMethod.GET,produces = {MediaType.APPLICATION_JSON_VALUE})
	public List<CustomerMetaData> getCustomerMetas(@PathVariable("app_id") String appId) throws Exception{
		return customerMetaDataService.getCustomerMetas(appId);
	}
	

    @ApiOperation(value = "获取客户基本字段列表", notes="获取客户基本字段列表")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "app_id", value = "项目id", required = true),
    })
	@RequestMapping(value = "/metadata/customer", method = RequestMethod.GET,produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<CustomerMetaData> getCustomerMeta(@RequestParam("app_id") String appId) throws Exception{
        return customerMetaDataService.getCustomerMetas(appId);
    }
	
	
	/**
	 * @throws Exception  
	* @Title: updateCustomerMeta 
	* @Description: TODO(修改客户基本字段) 
	* @param appId
	* @param isExt
	* @param unique
	* @param required
	* @param visiable
	* @param index
	* @param supportFilter
	* @param readonly
	* @param optionValues
	* @return 设定文件 
	* @return CustomerMeta    返回类型 
	* @throws 
	*/

    @ApiOperation(value = "修改客户基本字段", notes="修改客户基本字段")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "isExt", value = "isExt", required = true, defaultValue = ""),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "unique", value = "唯一", required = true, defaultValue = ""),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "required", value = "必填", required = true, defaultValue = ""),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "visiable", value = "可见", required = true, defaultValue = ""),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "index", value = "序号", required = true, defaultValue = ""),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "supportFilter", value = "支持过滤", required = true, defaultValue = ""),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "readonly", value = "只读", required = true, defaultValue = ""),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "optionValues", value = "选项值", required = true, defaultValue = ""),
        @ApiImplicitParam(paramType = "query", dataType = "String", name = "title", value = "标题", required = true, defaultValue = ""),
    })
	@RequestMapping(value = "/app/{app_id}/customer-meta/{meta_name}", method = RequestMethod.PUT)
	public CustomerMetaData updateCustomerMeta(@PathVariable("app_id") String appId,@PathVariable("meta_name") String name,
			@RequestParam(value = "isExt", defaultValue = "") String isExt,
			@RequestParam(value = "unique", defaultValue = "") String unique,
			@RequestParam(value = "required", defaultValue = "") String required,
			@RequestParam(value = "visiable", defaultValue = "") String visiable,
			@RequestParam(value = "index", defaultValue = "") String index,
			@RequestParam(value = "supportFilter", defaultValue = "") String supportFilter,
			@RequestParam(value = "readonly", defaultValue = "") String readonly,
			@RequestParam(value = "optionValues", defaultValue = "") String optionValues,
		    @RequestParam(value = "visible_in_list", required = false) String visibleInList,
		    @RequestParam(value = "index_in_list", required = false) Integer indexInList,
		    @RequestParam(value = "width_in_list", required = false) String widthInList,
			@RequestParam(value = "title", defaultValue = "") String title) throws Exception {
		
		return customerMetaDataService.update(
				appId, name, isExt, unique, required, visiable, index, supportFilter, readonly, optionValues, visibleInList, indexInList, widthInList, title);
	}

	/**
	 *
	 * @param appId
	 * @param name
	 * @param unique
	 * @param required
	 * @param visiable
	 * @param index
	 * @param supportFilter
	 * @param readonly
	 * @param optionValues
	 * @param title
	 * @return
	 */
	@ApiOperation(value = "添加客户字段", notes="添加客户字段")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "type", value = "类型，可选text/option", required = true, defaultValue = ""),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "unique", value = "唯一", required = true, defaultValue = ""),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "required", value = "必填", required = true, defaultValue = ""),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "visiable", value = "可见", required = true, defaultValue = ""),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "index", value = "序号", required = true, defaultValue = ""),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "supportFilter", value = "支持过滤", required = true, defaultValue = ""),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "readonly", value = "只读", required = true, defaultValue = ""),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "optionValues", value = "选项值", required = true, defaultValue = ""),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "title", value = "标题", required = true, defaultValue = ""),
	})
	@RequestMapping(value = "/app/{app_id}/extension/customer-metas", method = RequestMethod.POST)
	public CustomerMetaData addExtensionCustomerMeta(@PathVariable("app_id") String appId,
			@RequestParam("meta_name") String name,
			@RequestParam(value = "type") String type,
			@RequestParam(value = "unique", defaultValue = "false") boolean unique,
			@RequestParam(value = "required", defaultValue = "false") boolean required,
			@RequestParam(value = "visiable", defaultValue = "false") boolean visiable,
			@RequestParam(value = "index", required = false) Integer index,
			@RequestParam(value = "supportFilter", defaultValue = "false") boolean supportFilter,
			@RequestParam(value = "readonly", defaultValue = "0") int readonly,
			@RequestParam(value = "defaultValue", defaultValue = "") String defaultValue,
			@RequestParam(value = "optionValues", defaultValue = "") String optionValues,
			@RequestParam(value = "visible_in_list", required = false) String visibleInList,
			@RequestParam(value = "index_in_list", required = false) Integer indexInList,
			@RequestParam(value = "width_in_list", required = false) String widthInList,
			@RequestParam(value = "title", defaultValue = "") String title) throws Exception {
		return customerMetaDataService.addExtensionCustomerMeta(appId, name, type, unique, required, visiable,
				index, supportFilter, readonly, defaultValue, optionValues, visibleInList, indexInList, widthInList, title);
	}

	@ApiOperation(value = "删除客户扩展字段", notes="删除客户扩展字段")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
			@ApiImplicitParam(paramType = "path", dataType = "String", name = "customer_meta_id", value = "元数据记录id", required = true),
	})
	@DeleteMapping("/app/{app_id}/extension/customer-meta/{id}")
	public boolean deleteExtensionCustomerMeta(@PathVariable("app_id") String appId, @PathVariable("id") String customerMetaId){
		return customerMetaDataService.deleteExtensionCustomerMeta(appId, customerMetaId);
	}
}
