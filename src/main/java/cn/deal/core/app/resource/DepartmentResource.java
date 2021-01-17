package cn.deal.core.app.resource;

import cn.deal.component.exception.BusinessException;
import cn.deal.core.app.domain.AppMember;
import cn.deal.core.app.domain.Department;
import cn.deal.core.app.resource.vo.AppMemberVO;
import cn.deal.core.app.service.DepartmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ClassName DepartmentResource
 * @Description TODO
 **/
@Api("组织架构部门")
@RestController
@RequestMapping("/api/v1.0/app")
public class DepartmentResource {

    private static Logger log = LoggerFactory.getLogger(DepartmentResource.class);

    @Autowired
    private DepartmentService departmentService;
    
    /**
     * @Description add a department
     */
    @ApiOperation("创建部门")
    @PostMapping("/{app_id}/departments")
    public Department create(@PathVariable("app_id") String appId,
            @RequestParam String name,
            @RequestParam(value = "parent_id", required = false) String parentId,
            @RequestParam(value = "inherit_parent", defaultValue="1")Integer inheritParent,
                             @RequestParam("kuick_user_id") Integer kuickUserId) {
        return departmentService.create(appId, name, parentId, inheritParent, kuickUserId);
    }

    /**
     * @Description change department name
     * @Param [appId, id, name]
     * @Return cn.deal.core.department.domain.Department
     */
    @ApiOperation("修改部门")
    @PutMapping("/{app_id}/department/{department_id}")
    public Department modify(@PathVariable("app_id") String appId, @PathVariable("department_id") String id,
                             @RequestParam String name){
        if (StringUtils.isBlank(name)) {
            throw new BusinessException("is_blank", "部门名称不能为空");
        }
        return departmentService.modify(appId, id, name);
    }

    @ApiOperation("查询部门")
    @GetMapping("/{app_id}/department/{department_id}")
    public Department getById(@PathVariable("app_id") String appId, @PathVariable("department_id") String id){
        return departmentService.getById(appId, id);
    }

    @ApiOperation("批量查询部门")
    @GetMapping("/{app_id}/departments")
    public List<Department> getBatch(@PathVariable("app_id") String appId,
                                     @RequestParam(value = "parent_id", required = false) String parentId,
                                     @RequestParam(defaultValue = "1") int depth,
                                     @RequestParam(required = false) String keyword,
                                     @RequestParam(value = "with_members", defaultValue = "0") int withMembers){
        if (depth < 1) {
            throw new BusinessException("error_param", "depth不能小于1");
        }
        if (StringUtils.isNotBlank(keyword)) {
            depth = 1;
        }
        return departmentService.getBatch(appId, parentId, depth, keyword, withMembers, false);
    }

    /**
     * @Description
     * 路径参数：
     *   app_id string  项目ID
     *   department_id string 部门ID
     *
     * 查询参数：
     * cascading int 可选 是否级联删除子部门
     *
     * 业务逻辑：
     * 如果该部门下没有销售，也没有子部门，则直接删除部门
     * 如果该部门下有销售
     * 则返回错误码 exist_sales
     *    如果该部门下有销售
     *    则返回错误码 exist_child_department
     *    如果 cascading为1， 则执行级联删除
     *     先将该部门下所有的销售所属部门修改为父级部门，如果没有父级部门则改为空
     *      然后从最底层部门依次删除部门
     *权限检查：
     *
     *  具有部门管理权限的成员可以调用
     * @Param [appId, id, cascading]
     * @Return cn.deal.core.department.domain.Department
     * @return
     */
    @ApiOperation("删除部门")
    @DeleteMapping("/{app_id}/department/{department_id}")
    public Integer remove(@PathVariable("app_id") String appId, @PathVariable("department_id") String id,
                      @RequestParam(defaultValue = "0") int cascading){
        return departmentService.remove(appId, id, cascading);
    }

    @ApiOperation("获取部门下成员")
    @GetMapping("/{app_id}/department/{department_id}/members")
    public List<AppMemberVO> getMemebers(@PathVariable("app_id") String appId, @PathVariable("department_id") String id,
                                       @RequestParam(required = false) String keyword){
        return departmentService.getMemebers(appId, id, keyword);
    }

    /**
     * @Description 业务逻辑：
     *       遍历每一个 kuick_user_id
     *              如果该 kuick_user_id 是 项目成员，直接修改 该项目成员所属部门
     *              如果该 kuick_user_id 不是项目成员，先添加为项目成员，然后设置所属部门
     *        如果该成员没有任何菜单权限，则从部门复制菜单权限
     * @Param [appId, id, keyword]
     * @Return cn.deal.core.department.domain.Department
     * @return
     */
    @ApiOperation("部门下新增成员")
    @PostMapping("/{app_id}/department/{department_id}/members")
    public List<AppMember> addMemebers(@PathVariable("app_id") String appId, @PathVariable("department_id") String id,
                                           @RequestParam(value = "kuick_user_ids") String kuickUserIds){
        if (StringUtils.isBlank(kuickUserIds)) {
            throw new BusinessException("param_error", "kuick_user_ids不能为空");
        }
        Set<Integer> kuickUserIdSet = Stream.of(kuickUserIds.split(",")).mapToInt(i -> Integer.parseInt(i)).boxed().collect(Collectors.toSet());
        return departmentService.addMemebers(appId, id, kuickUserIdSet);
    }

    /**
     * @Description 从某个部门移除项目成员，设置该项目成员所属部门ID为空
     * @Param [appId, id, kuickUserIds]
     * @Return cn.deal.core.department.domain.Department
     * @return
     */
    @ApiOperation("部门下移除成员")
    @DeleteMapping("/{app_id}/department/{department_id}/members")
    public int removeMemebers(@PathVariable("app_id") String appId, @PathVariable("department_id") String id,
                              @RequestParam(value = "kuick_user_ids") String kuickUserIds){
        if (StringUtils.isBlank(kuickUserIds)) {
            throw new BusinessException("param_error", "kuick_user_ids不能为空");
        }
        Set<Integer> kuickUserIdSet = Stream.of(kuickUserIds.split(",")).mapToInt(i -> Integer.valueOf(i)).boxed().collect(Collectors.toSet());
        return departmentService.removeMemebers(appId, id, kuickUserIdSet);
    }


    @ApiOperation("查询项目成员管理的部门列表")
    @GetMapping("{app_id}/member/{kuick_user_id}/admin-departments")
    public List<Department> getAdminDepartmentByKuickUserId(
            @PathVariable("app_id") String appId,
            @PathVariable("kuick_user_id") int kuickUserId,
            @RequestParam(value = "department_ids", required = false) List<String> departmentIds,
            @RequestParam(value = "cascade", defaultValue = "0") int cascade
    ) {
        return departmentService.getAdminDepartmentByKuickUserId(appId, kuickUserId, departmentIds, cascade);
    }

}
