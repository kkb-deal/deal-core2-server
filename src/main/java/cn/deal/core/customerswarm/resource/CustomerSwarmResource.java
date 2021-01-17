package cn.deal.core.customerswarm.resource;

import cn.deal.component.exception.BusinessException;
import cn.deal.component.utils.ParamUtils;
import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customerswarm.domain.CustomerSwarm;
import cn.deal.core.customerswarm.service.CustomerSwarmService;
import com.google.common.collect.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@Api(value = "客户分群", description = "客户分群", tags = {"客户分群"}, produces = MediaType.ALL_VALUE)
@RequestMapping(value = "/api/v1.0")
public class CustomerSwarmResource {

    private static final Logger logger = LoggerFactory.getLogger(CustomerSwarmResource.class);

    @Autowired
    private CustomerSwarmService customerSwarmService;

    /**
     * 创建客户分群
     *
     * @param appId：项目ID
     * @param kuickUserId：销售ID
     * @param name：分群的名称
     * @param photoUrl：分群的显示图片
     * @param comment：分群描述
     * @param type:            分群类型，1:普通分群，2:基于filter的虚拟分群
     * @param filterId:        过滤条件ID，
     * @return
     */
    @ApiOperation(value = "创建客户分群", notes = "创建客户分群")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "kuick_user_id", value = "销售id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "name", value = "分群名称", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "photo_url", value = "分群的显示图片", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "comment", value = "分群描述"),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "type", value = "分群类型，1:普通分群，2:基于filter的虚拟分群", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "filter_id", value = "过滤条件ID，当type为2时，该参数为必填", required = false),
    })
    @PostMapping("app/{app_id}/customer-swarms")
    public CustomerSwarm create(
            @PathVariable("app_id") String appId,
            @RequestParam(name = "kuick_user_id") String kuickUserId,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "photo_url") String photoUrl,
            @RequestParam(name = "type", defaultValue = "1") int type,
            @RequestParam(name = "filter_id", required = false) String filterId,
            @RequestParam(name = "comment", required = false) String comment,
            HttpServletRequest request) {
        if (type == CustomerSwarm.Type.VIRTUAL.getVal() && StringUtils.isBlank(filterId)) {
            throw new BusinessException("invalid_param_filter_id", "当type为2时，filter_id必填");
        }

        Map<String, String> data = ParamUtils.extractParams(request);
        return customerSwarmService.create(appId, kuickUserId, name, photoUrl, comment, type, filterId, data);
    }

    /**
     * 根据分群ID删除分群
     */
    @ApiOperation(value = "根据分群ID删除分群", notes = "根据分群ID删除分群")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "swarm_id", value = "分群id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "kuick_user_id", value = "销售id", required = true),
    })
    @DeleteMapping("app/{app_id}/customer-swarm/{swarm_id}")
    public boolean delete(
            @PathVariable("app_id") String appId,
            @PathVariable("swarm_id") String swarmId,
            @RequestParam(name = "kuick_user_id") String kuickUserId) {
        return customerSwarmService.delete(appId, swarmId, kuickUserId);
    }

    /**
     * 修改客户分群
     */
    @ApiOperation(value = "修改客户分群", notes = "修改客户分群")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "swarm_id", value = "分群id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "kuick_user_id", value = "销售id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "name", value = "分群名称"),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "photo_url", value = "分群显示图片"),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "comment", value = "分群描述"),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "filter_id", value = "过滤条件ID，当type为2时，该参数为必填", required = false),
    })
    @PutMapping("app/{app_id}/customer-swarm/{swarm_id}")
    public CustomerSwarm update(
            @PathVariable("app_id") String appId,
            @PathVariable("swarm_id") String swarmId,
            @RequestParam("kuick_user_id") String kuickUserId,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "photo_url", required = false) String photoUrl,
            @RequestParam(name = "filter_id", required = false) String filterId,
            @RequestParam(name = "comment", required = false) String comment,
            HttpServletRequest request) {
        Map<String, String> data = ParamUtils.extractParams(request);
        return customerSwarmService.update(appId, swarmId, kuickUserId, name, photoUrl, comment, filterId, data);
    }

    /**
     * 获取分群列表
     */
    @ApiOperation(value = "获取分群列表", notes = "获取分群列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "kuick_user_id", value = "销售id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "start_index", value = "页数", required = true, defaultValue = "0"),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "count", value = "每页记录数", required = true, defaultValue = "20"),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "keyword", value = "关键字"),
    })
    @GetMapping("app/{app_id}/member/{kuick_user_id}/customer-swarms")
    public List<Map<String, Object>> list(
            @PathVariable("app_id") String appId,
            @PathVariable("kuick_user_id") String kuickUserId,
            @RequestParam(name = "start_index", defaultValue = "0") int startIndex,
            @RequestParam(name = "count", defaultValue = "20") int count,
            @RequestParam(name = "keyword", required = false) String keyword) {
        return customerSwarmService.list(appId, kuickUserId, startIndex, count, keyword);
    }

    /**
     * 删除分群共享
     */
    @ApiOperation(value = "删除分群共享", notes = "删除分群共享")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "swarm_id", value = "分群id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "kuick_user_id", value = "销售id", required = true),
    })
    @DeleteMapping("app/{app_id}/customer-swarm/{swarm_id}/shares")
    public boolean deleteShare(
            @PathVariable("app_id") String appId,
            @PathVariable("swarm_id") String swarmId,
            @RequestParam("kuick_user_id") String kuickUserId) {
        return customerSwarmService.deleteShareSwarm(appId, swarmId, kuickUserId);
    }

    /**
     * 更新分群共享
     */
    @ApiOperation(value = "更新分群共享", notes = "更新分群共享")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "swarm_id", value = "分群id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "kuick_user_id", value = "销售id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "target_type", value = "目标类型", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "target_ids", value = "目标id集合", required = true),
    })
    @PutMapping("app/{app_id}/customer-swarm/{swarm_id}/shares")
    public Map<String, Object> updateShare(
            @PathVariable("app_id") String appId,
            @PathVariable("swarm_id") String swarmId,
            @RequestParam("kuick_user_id") String kuickUserId,
            @RequestParam("target_type") int targetType,
            @RequestParam("target_ids") String targetIds) {
        return customerSwarmService.updateShareSwarm(appId, swarmId, kuickUserId, targetType, targetIds);
    }

    /**
     * 获取分群的共享记录
     */
    @ApiOperation(value = "获取分群的共享记录", notes = "获取分群的共享记录")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "swarm_id", value = "分群id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "kuick_user_id", value = "销售id", required = true),
    })
    @GetMapping("app/{app_id}/customer-swarm/{swarm_id}/shares")
    public Map<String, Object> getSharesBySwarmId(
            @PathVariable("app_id") String appId,
            @PathVariable("swarm_id") String swarmId,
            @RequestParam("kuick_user_id") String kuickUserId) {
        return customerSwarmService.fetchSwarmSharesBySwarmId(appId, kuickUserId, swarmId);
    }

    /**
     * 分群添加客户  （添加/复制）
     */
    @ApiOperation(value = "分群添加客户  （添加/复制）", notes = "分群添加客户  （添加/复制）")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "swarm_id", value = "分群id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "kuick_user_id", value = "销售id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "customer_ids", value = "客户id集合", required = true),
    })
    @PostMapping("app/{app_id}/customer-swarm/{swarm_id}/members")
    public boolean addCustomers(
            @PathVariable("app_id") String appId,
            @PathVariable("swarm_id") String swarmId,
            @RequestParam("kuick_user_id") String kuickUserId,
            @RequestParam("customer_ids") String customerIds,
            @RequestParam(name = "synch", defaultValue = "0") int synch,
            HttpServletRequest request) {
        return customerSwarmService.addCustomers(appId, swarmId, kuickUserId, customerIds, synch, request.getHeader("User-Agent"));
    }

    /**
     * 分群移除客户 （删除）
     */
    @ApiOperation(value = "分群移除客户 （删除）", notes = "分群移除客户 （删除）")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "swarm_id", value = "分群id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "kuick_user_id", value = "销售id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "customer_ids", value = "客户id集合", required = true),
    })
    @DeleteMapping("app/{app_id}/customer-swarm/{swarm_id}/members")
    public boolean removeCustomers(
            @PathVariable("app_id") String appId,
            @PathVariable("swarm_id") String swarmId,
            @RequestParam("kuick_user_id") String kuickUserId,
            @RequestParam("customer_ids") String customerIds,
            HttpServletRequest request) {
        logger.info("swarm remove customer params:kuickUserId:{}, customerIds: {}", kuickUserId, customerIds);
        return customerSwarmService.removeCustomers(appId, swarmId, kuickUserId, customerIds, request.getHeader("User-Agent"));
    }

    /**
     * 转移客户从一个分群到另一个分群（剪切）
     */
    @ApiOperation(value = "转移客户从一个分群到另一个分群（剪切）", notes = "转移客户从一个分群到另一个分群（剪切）")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "kuick_user_id", value = "销售id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "from_swarm_id", value = "来源分群id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "to_swarm_id", value = "目标分群id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "customer_ids", value = "客户id集合", required = true),
    })
    @PostMapping("app/{app_id}/swarm-member-transfers")
    public boolean swarmMemberTransfer(
            @PathVariable("app_id") String appId,
            @RequestParam("kuick_user_id") String kuickUserId,
            @RequestParam("from_swarm_id") String fromSwarmId,
            @RequestParam("to_swarm_id") String toSwarmId,
            @RequestParam("customer_ids") String customerIds) {
        logger.info("swarmMemberTransfer appId: {}, kuickUserId: {}, fromSwarmId: {}, toSwarmId: {}, customerIds: {}", appId, kuickUserId, fromSwarmId, toSwarmId, customerIds);
        return customerSwarmService.swarmMemberTransfer(appId, kuickUserId, fromSwarmId, toSwarmId, customerIds);
    }

    /**
     * 查询分群客户列表
     */
    @ApiOperation(value = "查询分群客户列表", notes = "查询分群客户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "swarm_id", value = "分群id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "kuick_user_id", value = "销售id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "customer_owner_id", value = "客户归属人id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "group_id", value = "组id"),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "keyword", value = "关键字"),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "start_index", value = "页数", required = true, defaultValue = "0"),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "count", value = "每页记录条数", required = true, defaultValue = "20"),
    })
    @GetMapping("app/{app_id}/customer-swarm/{swarm_id}/members")
    public List<Customer> getMembers(
            @PathVariable("app_id") String appId,
            @PathVariable("swarm_id") String swarmId,
            @RequestParam("kuick_user_id") String kuickUserId,
            @RequestParam("customer_owner_id") String customerOwnerId,
            @RequestParam(name = "group_id", required = false) String groupId,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "start_index", defaultValue = "0") int startIndex,
            @RequestParam(name = "count", defaultValue = "20") int count) {
        return customerSwarmService.getSwarmMembers(appId, swarmId, kuickUserId, customerOwnerId, groupId, keyword, startIndex, count);
    }

    /**
     * 查询分群客户id列表
     *
     * @return
     */
    @ApiOperation(value = "查询分群客户列表", notes = "查询分群客户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "swarm_id", value = "分群id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "kuick_user_id", value = "销售id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "customer_owner_id", value = "客户归属人id", required = true)
    })
    @GetMapping("app/{app_id}/customer-swarm/{swarm_id}/member-ids")
    public List<String> getMemberIds(
            @PathVariable("app_id") String appId,
            @PathVariable("swarm_id") String swarmId,
            @RequestParam("kuick_user_id") String kuickUserId,
            @RequestParam("customer_owner_id") String customerOwnerId) {
        List<Customer> customers = customerSwarmService.getSwarmMemberIds(appId, swarmId, kuickUserId, customerOwnerId);
        List<String> ids = Lists.newArrayList();
        if (customers != null) {
            customers.forEach(c -> {
                ids.add(c.getId());
            });
        }
        return ids;
    }

    /**
     * 获取分群客户数
     */
    @ApiOperation(value = "获取分群客户数", notes = "获取分群客户数")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "swarm_id", value = "分群id", required = true),
    })
    @GetMapping(value = "app/{app_id}/swarm/{swarm_id}/member-count")
    public long countMember(
            @PathVariable("app_id") String appId,
            @PathVariable("swarm_id") String swarmId) {
        return customerSwarmService.getMemberCount(appId, swarmId);
    }

    @ApiOperation(value = "添加/删除分群成员", notes = "添加/删除分群成员")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "app_id", value = "项目id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "action", value = "动作", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "swarm_name", value = "分群名称", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "swarm_owner_id", value = "分群所属人", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "customer_ids", value = "客户id集合", required = true),
    })
    @RequestMapping(value = "app/{app_id}/swarm_members", method = RequestMethod.PUT)
    public boolean addOrDeleteSwarmMembers(@PathVariable("app_id") String appId,
                                           @RequestParam("action") String action,
                                           @RequestParam("swarm_name") String swarmName,
                                           @RequestParam(value = "swarm_owner_id", required = false) String swarmOwnerId,
                                           @RequestParam("customer_ids") String customerIds,
                                           HttpServletRequest request) {
        logger.info("action: {}, appId: {}, swarm_name: {}, swarm_owner_id: {}, customer_ids: {}", action, appId, swarmName, swarmOwnerId, customerIds);
        boolean resp = false;
        if ("add".equals(action)) {
            customerSwarmService.addCustomersWithSwarmNameV2(appId, swarmName, swarmOwnerId, customerIds, request.getHeader("User-Agent"));
            resp = true;
        } else if ("remove".equals(action)) {
            customerSwarmService.removeCustomersWithSwarmName(appId, swarmName, customerIds, request.getHeader("User-Agent"));
            resp = true;
        } else if ("move".equals(action)) {
            customerSwarmService.moveCustomersWithSwarmName(appId, swarmName, customerIds, request.getHeader("User-Agent"));
            resp = true;
        }
        return resp;
    }

    @ApiOperation(value = "触发分群客户数任务", notes = "触发分群客户数任务")
    @GetMapping("/trigger_swarm_customer_count_task")
    public String trigger() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                customerSwarmService.swarmCustomerCount();
            }
        }).start();
        return "ok";
    }
}
