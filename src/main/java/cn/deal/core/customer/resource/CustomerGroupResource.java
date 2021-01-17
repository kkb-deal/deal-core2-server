package cn.deal.core.customer.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;

import cn.deal.core.customer.domain.CustomerGroups;
import cn.deal.core.customer.service.CustomerGroupService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@Api(value = "客户分组", description = "客户分组",tags={"客户分组"},produces = MediaType.ALL_VALUE)
@RequestMapping("/api/v1.0")
public class CustomerGroupResource {

    @Autowired
    CustomerGroupService customerGroupService;

    /**
     * 获取项目分组
     * @param appId 项目id
     * @return 分组列表
     * @throws MissingServletRequestParameterException 传值丢失
     */

    @ApiOperation(value = "获取项目分组", notes="获取项目分组")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
    })
    @GetMapping("/app/{app_id}/customer-groups")
    public List<CustomerGroups> getCustomerGroups(@PathVariable("app_id") String appId) throws MissingServletRequestParameterException {

        if (StringUtils.isBlank(appId)) {
            throw new MissingServletRequestParameterException("app_id", "String");
        }

        List<CustomerGroups> customerGroups = customerGroupService.getCustomerGroups(appId);
        return customerGroups;
    }

    /**
     * 添加分组
     * @param appId 项目id
     * @param name  分组名字
     * @param index 分组序号，默认0
     * @return 新创建的分组
     * @throws MissingServletRequestParameterException 传值丢失
     */

    @ApiOperation(value = "添加分组", notes="添加分组")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "name", value = "分组名称", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "index", value = "分组序号", required = true, defaultValue = "0"),
    })
    @PostMapping("/app/{app_id}/customer-groups")
    public ResponseEntity<Map<String,Object>> addCustomerGroups(
            @PathVariable("app_id") String appId,
            @RequestParam("name")   String name,
            @RequestParam(name = "index", defaultValue = "0")  Integer index
    ) throws MissingServletRequestParameterException {

        if (StringUtils.isBlank(appId)) {
            throw new MissingServletRequestParameterException("app_id", "String");
        }
        if (StringUtils.isBlank(name)) {
            throw new MissingServletRequestParameterException("name", "String");
        }
        Map<String,Object> map = new HashMap<>();
        CustomerGroups customerGroups = customerGroupService.addCustomerGroups(appId, name, index);
        if (customerGroups != null) {
            map.put("info", "创建成功");
            map.put("data", customerGroups);
            map.put("code", "1");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
        map.put("message", "未找到该项目id,不能在该项目下创建分组");
        map.put("code", "-1");
        return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
    }

    /**
     * 删除分组
     * @param appId     项目id
     * @param groupId   分组id
     * @return  只有分组下没有客户才可以删除,如删除失败返回false
     * @throws MissingServletRequestParameterException 传值丢失
     */

    @ApiOperation(value = "删除分组", notes="删除分组")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "group_id", value = "分组id", required = true),
    })
    @DeleteMapping("/app/{app_id}/customer-group/{group_id}")
    public ResponseEntity<Map<String,Object>> deleteCustomerGroups(
            @PathVariable("app_id")     String appId,
            @PathVariable("group_id")   String groupId
    ) throws MissingServletRequestParameterException {

        if (StringUtils.isBlank(appId)) {
            throw new MissingServletRequestParameterException("app_id", "String");
        }
        if (StringUtils.isBlank(groupId)) {
            throw new MissingServletRequestParameterException("group_id", "String");
        }

        Map<String,Object> map = new HashMap<>();

        int state = customerGroupService.deleteCustomerGroups(appId, groupId);

        if (state == 0) {
            map.put("message", "未找到该分组,不能删除");
            map.put("code", "0");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        } else if (state == 2) {
            map.put("message", "该分组下有客户,不能删除");
            map.put("code", "2");
            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        } else {
            map.put("info", "删除成功");
            map.put("code", "1");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    /**
     * 修改分组
     *
     * @param appId   项目id
     * @param groupId 分组id
     * @return boolean 修改是否成功
     * @throws MissingServletRequestParameterException 传值丢失
     */

    @ApiOperation(value = "修改分组", notes="修改分组")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "group_id", value = "分组id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "name", value = "名称"),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "index", value = "序号"),
    })
    @PutMapping("/app/{app_id}/customer-group/{group_id}")
    public ResponseEntity<Map<String,Object>> updateCustomerGroups(
            @PathVariable("app_id")         String appId,
            @PathVariable("group_id")       String groupId,
            @RequestParam(name = "name",required = false)    String name,
            @RequestParam(name = "index",required = false)   Integer index
    ) throws MissingServletRequestParameterException {

        if (StringUtils.isBlank(appId)) {
            throw new MissingServletRequestParameterException("app_id", "String");
        }
        if (StringUtils.isBlank(groupId)) {
            throw new MissingServletRequestParameterException("group_id", "String");
        }

        Map<String,Object> map = new HashMap<>();

        CustomerGroups customerGroups = customerGroupService.updateCustomerGroups(appId, groupId, name, index);

        if (customerGroups == null) {
            map.put("message", "未找到该分组,不能修改");
            map.put("code", "0");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        } else {
            map.put("id", customerGroups.getId());
            map.put("appId",customerGroups.getAppId());
            map.put("name",customerGroups.getName());
            map.put("createdAt",customerGroups.getCreatedAt());
            map.put("updatedAt",customerGroups.getUpdatedAt());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    /**
     * 分组交换顺序
     *
     * @param appId   项目id
     * @param groupId 分组id
     * @param groupId2 分组id2
     * @return boolean 交换是否成功
     * @throws MissingServletRequestParameterException 传值丢失
     */

    @ApiOperation(value = "分组交换顺序", notes="分组交换顺序")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "group_id", value = "分组id1", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "group_id2", value = "分组id2", required = true),
    })
    @PostMapping("/app/{app_id}/customer-group-exchange")
    public ResponseEntity<Map<String,Object>> exchangeCustomerGroups(
            @PathVariable("app_id")         String appId,
            @RequestParam("group_id")       String groupId,
            @RequestParam("group_id2")       String groupId2
    ) throws MissingServletRequestParameterException {

        if (StringUtils.isBlank(appId)) {
            throw new MissingServletRequestParameterException("app_id", "String");
        }
        if (StringUtils.isBlank(groupId)) {
            throw new MissingServletRequestParameterException("group_id", "String");
        }
        if (StringUtils.isBlank(groupId2)) {
            throw new MissingServletRequestParameterException("group_id2", "String");
        }

        Map<String,Object> map = new HashMap<>();
        Boolean exchange;

        try {
            exchange = customerGroupService.exchangeCustomerGroups(appId, groupId, groupId2);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("message", "修改失败已经回滚");
            map.put("code", "-1");
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (!exchange) {
            map.put("message", "未找到该分组,不能修改");
            map.put("code", "404");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

}
