package cn.deal.core.app.resource;

import cn.deal.core.app.domain.AppMember;
import cn.deal.core.app.resource.vo.AppMemberVO;
import cn.deal.core.app.service.DealAppMemberService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api")
public class AppMemberResourceV2 {

    @Autowired
    private DealAppMemberService dealAppMemberService;

    @ApiOperation("老版本获取成员详情")
    @GetMapping("v1.6/app/{app_id}/member/{kuick_user_id}")
    public AppMemberVO getAppMember(
            @PathVariable("app_id") String appId, @PathVariable("kuick_user_id")Integer kuickUserId,
            @RequestParam(value = "with_department", defaultValue = "0") int withDepartment,
            @RequestParam(value = "with_kuickuser", defaultValue = "0") int withKuickuser
    ) {
        return dealAppMemberService.getAppMemberByKuickUserId(appId, kuickUserId, withDepartment, withKuickuser);
    }

    @ApiOperation("获取成员详情")
    @GetMapping("v1.8/app/{app_id}/member/{kuick_user_id}")
    public AppMemberVO getAppMemberV2(
            @PathVariable("app_id") String appId, @PathVariable("kuick_user_id")Integer kuickUserId,
            @RequestParam(value = "with_department", defaultValue = "0") int withDepartment,
            @RequestParam(value = "with_kuickuser", defaultValue = "0") int withKuickuser
    ) {
        return dealAppMemberService.getAppMemberByKuickUserIdV2(appId, kuickUserId, withDepartment, withKuickuser);
    }

}
