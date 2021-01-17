package cn.deal.core.app.resource;

import cn.deal.component.exception.BusinessException;
import cn.deal.core.app.domain.Invite;
import cn.deal.core.app.service.InviteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Api("项目成员邀请")
@RestController
@RequestMapping("/api/v1.1/")
public class InviteResource {

    @Autowired
    private InviteService inviteService;

    @RequestMapping(value = "test/download")
    public ResponseEntity<byte[]> download() throws IOException{
        File file = new File("/kuick/servers/run.sh");
        byte[] body;
        InputStream is = new FileInputStream(file);
        body = new byte[is.available()];
        //noinspection ResultOfMethodCallIgnored
        is.read(body);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attchement;filename=" + file.getName());
        HttpStatus statusCode = HttpStatus.OK;
        return new ResponseEntity<>(body, headers, statusCode);
    }

    @ApiOperation("生成项目成员邀请码")
    @PostMapping("/invites")
    public Invite create(@RequestParam("app_id") String appId,
                         @RequestParam("inviter_id") String inviterId,
                         @RequestParam(value = "department_id", required = false) String departmentId,
                         @RequestParam(value = "post_roles", required = false) String postRoles,
                         @RequestParam(defaultValue = "AppMember") String roles) {
        return inviteService.create(appId, inviterId, departmentId, postRoles, roles);
    }

    @ApiOperation("根据邀请码查询邀请详情")
    @GetMapping("/invite/{code}")
    public Invite getByCode(@PathVariable("code") String code) {
        return inviteService.getByCode(code);
    }

    @ApiOperation("根据邀请码添加项目成员")
    @PostMapping("/invite/{code}/members")
    public Object createAppMember(@PathVariable String code,
                                               @RequestParam("kuick_user_id") Integer kuickUserId,
                                               @RequestParam(value = "post_roles", defaultValue = "") String postRoles,
                                               @RequestHeader(value = "User-Agent", defaultValue = "")String userAgentStr) throws Exception {
         Map<String, Object> map = inviteService.createAppMemberByCode(code, kuickUserId, postRoles, userAgentStr);
         if (map == null) {
             throw new BusinessException("error", "创建成员失败");
         }
         
         if ((int)map.get("status") == 0) {
             throw new BusinessException("error", map.get("msg").toString());
         }
         
         return map.get("data");
    }
}
