package cn.deal.core.app.service;

import cn.deal.component.UserComponent;
import cn.deal.component.exception.BusinessException;
import cn.deal.core.app.domain.AppMember;
import cn.deal.core.app.domain.DealApp;
import cn.deal.core.app.domain.Invite;
import cn.deal.core.app.repository.DealAppRepository;
import cn.deal.core.app.repository.InviteRepository;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InviteService {

    private static final Logger logger = LoggerFactory.getLogger(InviteService.class);

    @Autowired
    private InviteRepository inviteRepository;

    @Autowired
    private DealAppMemberService dealAppMemberService;

    @Autowired
    private UserComponent userComponent;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DealAppRepository dealAppRepository;

    private static String CHARS_STR = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public Map<String, Object> createAppMemberByCode(String code, int kuickUserId, String postRoles, String userAgentStr) throws Exception {
        logger.info("createAppMemberByCode.params: {}, {}, {}, {}", code, kuickUserId, postRoles, userAgentStr);
        Invite invite = inviteRepository.findOneByCode(code);
        if (invite != null && StringUtils.isNotBlank(invite.getAppId())) {
            if (StringUtils.isNotBlank(postRoles)) {
                invite.setPostRoles(postRoles);
            }

            logger.info("createAppMemberByCode.invite: {}", invite);
            AppMember appMember;
            if (StringUtils.isNotBlank(invite.getDepartmentId())) {
                Set<Integer> kids = new HashSet<>(1);
                kids.add(kuickUserId);
                List<AppMember> appMembers = departmentService.addMemebers(invite.getAppId(), invite.getDepartmentId(), kids);
                appMember = appMembers.get(0);
            } else {
                appMember = dealAppMemberService.createAppMember(invite.getAppId(), invite.getDepartmentId(), kuickUserId,
                        invite.getRoles(), null, userAgentStr);
            }
            return ImmutableMap.of("status", 1, "data", appMember);
        } else {
            Map<String, Object> map = Maps.newHashMap();
            map.put("status", 0);
            map.put("msg", "invite is null");
            return map;
        }
    }

    public Invite create(String appId, String inviterId, String departmentId, String postRoles, String roles) {
        Invite invite = inviteRepository.findOneByAppIdAndInviterIdAndDepartmentId(appId, inviterId, departmentId);
        if (invite != null) {
            return invite;
        }
        
        DealApp dealApp = dealAppRepository.findOne(appId);
        if (dealApp == null) {
            throw new BusinessException("not_exists", "项目不存在");
        }
        
        String code;
        do {
            code = generateCode(8);
            if (inviteRepository.findOneByCode(code) == null) {
                break;
            }
        } while (true);
        
        invite = Invite.builder().appId(appId).inviterId(inviterId).departmentId(departmentId).postRoles(null)
                .roles(roles).code(code).createdAt(new Date()).build();
        inviteRepository.saveAndFlush(invite);
        
        return invite;
    }

    public String generateCode(int len) {
        char[] chars = new char[len];
        
        for(int i = 0; i < chars.length; i++) {
            chars[i] = CHARS_STR.charAt(RandomUtils.nextInt(0, CHARS_STR.length()));
        }
        
        return new String(chars);
    }

    public Invite getByCode(String code) {
        return inviteRepository.findOneByCode(code);
    }
}
