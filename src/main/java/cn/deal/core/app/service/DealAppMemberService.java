package cn.deal.core.app.service;

import cn.deal.component.DealBehaviourComponent;
import cn.deal.component.RedisService;
import cn.deal.component.config.ServiceConfig;
import cn.deal.component.domain.AppMemberDomainEvent;
import cn.deal.component.domain.AppMemberDomainEventTypeEnum;
import cn.deal.component.domain.User;
import cn.deal.component.exception.BusinessException;
import cn.deal.component.kuick.ConferenceGroupMemberService;
import cn.deal.component.kuick.ConferenceService;
import cn.deal.component.kuick.KuickuserUserService;
import cn.deal.component.kuick.domain.Conference;
import cn.deal.component.kuick.domain.KuickUser;
import cn.deal.component.kuick.repository.UserRepository;
import cn.deal.component.messaging.producer.AppMemberDomainEventProducer;
import cn.deal.component.utils.JsonUtil;
import cn.deal.core.app.dao.DealAppMemberDao;
import cn.deal.core.app.domain.AppMember;
import cn.deal.core.app.domain.DealApp;
import cn.deal.core.app.repository.DealAppMemberRepository;
import cn.deal.core.app.repository.DealAppRepository;
import cn.deal.core.app.repository.DepartmentRepository;
import cn.deal.core.app.resource.vo.AppMemberVO;
import cn.deal.core.license.domain.License;
import cn.deal.core.license.service.AppLicenseService;
import cn.deal.core.meta.domain.AppSetting;
import cn.deal.core.meta.service.AppSettingService;
import cn.deal.core.permission.domain.AppMemberPermission;
import cn.deal.core.permission.domain.Permission;
import cn.deal.core.permission.repository.AppMemberPermissionRepository;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 项目名称：deal-core-server2 类名称：DealAppMemberServiceImpl 类描述： 创建人：panpan
 * 创建时间：2017年11月30日 下午3:26:47
 */
@Service
public class DealAppMemberService {
    private static final Logger log = LoggerFactory.getLogger(DealAppMemberService.class);

    @Autowired
    private DealAppMemberRepository dealAppMemberRepository;

    @Autowired
    private DealAppMemberDao dealAppMemberDao;

    @Autowired
    private KuickuserUserService kuickuserUserService;

    @Autowired
    private ConferenceService conferenceService;

    @Autowired
    private DealAppRepository dealAppRepository;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private AppLicenseService appLicenseService;

    @Autowired
    private DealAppMemberService dealAppMemberService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private DealAppService dealAppService;

    @Autowired
    private DealBehaviourComponent dealBehaviourComponent;

    @Autowired
    private ConferenceGroupMemberService conferenceGroupMemberService;
    @Autowired
    private AppMemberDomainEventProducer appMemberDomainEventProducer;

    @Autowired
    private AppSettingService appSettingService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private AppMemberPermissionRepository appMemberPermissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceConfig serviceConfig;

    @Autowired
    private RestTemplate restTemplate;

    public AppMember createAppMember(String appId, String departmentId, int kuickUserId, String role, String postRole, String userAgentStr) throws Exception {
        String lockKey = "app_member_" + appId + "_" + kuickUserId;
        RLock lock = redissonClient.getReadWriteLock(lockKey).writeLock();
        AppMember dealAppMember;
        Map<String, Object> map = Maps.newHashMap();
        try {
            lock.lock(5, TimeUnit.SECONDS);
            License license = appLicenseService.getAppLimits(appId);

            if (license.isExpired()) {
                throw new BusinessException("error", "项目已经过期!");
            }

            int count = dealAppMemberService.getAppMemberCount(appId);
            if (count >= license.getMaxAppMemberCount()) {
                throw new BusinessException("error", "项目成员超限!");
            }

            dealAppMember = this.getAppMemberByAppIdAndKuickUserId(appId, kuickUserId);
            log.info("createAppMember.dealAppMember: {}", dealAppMember);

            if (dealAppMember != null) {
                if (AppMember.Status.VALID.getValue().equals(dealAppMember.getStatus())) {
                    return dealAppMember;
                }
                dealAppMember.setStatus(AppMember.Status.VALID.getValue());
                dealAppMember.setDepartmentId(departmentId);
                dealAppMember.setRole(role);
                dealAppMember.setPostRoles(postRole);
                String key = getAppMemberRedisKey(appId, kuickUserId);
                redisService.setex(key, 30 * 60, JsonUtil.toJson(dealAppMember));
                dealAppMemberRepository.saveAndFlush(dealAppMember);

            } else {
                dealAppMember = this.createAppMemberData(appId, departmentId, kuickUserId, role, postRole);
                if (dealAppMember == null) {
                    throw new BusinessException("error", "项目成员创建失败!");
                }
            }

            DealApp dealApp = this.getDealAppByAppId(appId);
            if (dealApp == null) {
                throw new BusinessException("error", "项目不存在!");
            }

            conferenceService.joinConference(dealApp.getConferenceId(), kuickUserId);

            dealAppMemberService.updateAppMemberConferenceId(dealAppMember);

            KuickUser kuickUser = kuickuserUserService.getUserById(kuickUserId);

            String action = "add_appmember";
            String desc = dealApp.getName() + "项目增加了新成员" + kuickUser.getName();

            dealBehaviourComponent.createAppMemberBehaviourLog(appId, kuickUserId, action, desc, kuickUser, userAgentStr);

            Map cgmMap = conferenceGroupMemberService.createConferenceGroupMember(dealApp.getConferenceGroupId(),
                    dealAppMember.getConferenceId());

            // 发送appmember领域事件
            sendAppMemberDomainEventToKafka(AppMemberDomainEventTypeEnum.create, dealAppMember, null);

            if (!StringUtils.equals(cgmMap.get("status").toString(), "1")) {
                throw new BusinessException("error", "项目成员创建失败!");
            }

        } finally {
            if (lock.isLocked()) {
                lock.unlock();
            }
        }

        return dealAppMember;
    }

    private void sendAppMemberDomainEventToKafka(AppMemberDomainEventTypeEnum type, AppMember dealAppMember,
                                                 AppMember oldMember) {
        if (Objects.isNull(dealAppMember)) {
            return;
        }
        AppMemberDomainEvent appMemberDomainEvent = new AppMemberDomainEvent();
        appMemberDomainEvent.setId(UUID.randomUUID().toString());
        appMemberDomainEvent.setEventType(type.name());
        appMemberDomainEvent.setDomainId(dealAppMember.getId());
        appMemberDomainEvent.setBody(new AppMemberVO(dealAppMember));
        if (!Objects.isNull(oldMember)) {
            appMemberDomainEvent.setOldBody(new AppMemberVO(oldMember));
        }
        appMemberDomainEventProducer.send(appMemberDomainEvent);
    }

    /**
     * 创建应用成员数据
     *
     * @param appId
     * @param kuickUserId
     * @return
     * @throws Exception
     */
    public AppMember createAppMemberData(String appId, String departmentId, int kuickUserId,
                                         String role, String postRole) throws Exception {
        AppMember dealAppMember = dealAppMemberRepository.findOneByAppIdAndKuickUserId(appId, kuickUserId);
        if (dealAppMember == null) {
            dealAppMember = new AppMember();
            dealAppMember.setId(UUID.randomUUID().toString());
        }
        dealAppMember.setAppId(appId);
        dealAppMember.setDepartmentId(departmentId);
        dealAppMember.setKuickUserId(kuickUserId);
        dealAppMember.setRole(role);

        // 获取会议ID
        Integer conferenceId = this.getMemberConferenceId(kuickUserId, appId);
        if (conferenceId != null) {
            dealAppMember.setConferenceId(conferenceId);
        }

        if (StringUtils.equals(postRole, "MarketPost") || StringUtils.equals(postRole, "SalesPost")) {
            dealAppMember.setPostRoles(postRole);
        }

        dealAppMember.setStatus(0);
        dealAppMember.setCreateTime(new Date());
        dealAppMember = this.updateAppMemberConferenceId(dealAppMember);

        String key = getAppMemberRedisKey(appId, kuickUserId);
        redisService.setex(key, 30 * 60, JsonUtil.toJson(dealAppMember));
        return dealAppMember;
    }

    /**
     * 获取会议
     *
     * @param kuickUserId
     * @param appId
     * @return
     */
    public Integer getMemberConferenceId(int kuickUserId, String appId) {
        AppMember dealAppMember = this.getAppMemberByKuickUserId(appId, kuickUserId);
        Integer conferenceId = null;

        if (dealAppMember != null && dealAppMember.getConferenceId() != null) {
            this.dealConferenceSetting(appId, kuickUserId, dealAppMember.getConferenceId());
            conferenceId = dealAppMember.getConferenceId();
        } else {
            DealApp dealApp = dealAppService.getDealAppInfo(appId);

            KuickUser kuickUser = kuickuserUserService.getUserById(kuickUserId);

            String name = dealApp.getName() + "_" + kuickUser.getName();

            String description = dealApp.getDescription();
            if (StringUtils.isBlank(description)) {
                description = name;
            }

            Conference conference = conferenceService.addConference(kuickUserId, name, description);

            conferenceId = conference.getId();
            log.info("getMemberConferenceId.conference -> " + conference);
            this.dealConferenceSetting(appId, kuickUserId, conference.getId());

            conferenceGroupMemberService.createConferenceGroupMember(dealApp.getConferenceGroupId(),
                    conference.getId());

            // dealAppMember = this.updateAppMember(appId, kuickUserId, conference.getId());
            // // todo core
        }
        return conferenceId;
    }

    /**
     * 会议设置
     *
     * @param appId
     * @param kuickUserId
     * @param conferenceId
     */
    public void dealConferenceSetting(String appId, int kuickUserId, int conferenceId) {
        List<AppSetting> list = appSettingService.getSettings(appId);

        String logoURL = null, theme = null, bgURL = null, type = null;

        for (AppSetting item : list) {
            if (item.getKey() == "remote_demo_logo_url") {
                logoURL = item.getValue();
            } else if (item.getKey() == "remote_demo_theme") {
                theme = item.getValue();
            } else if (item.getKey() == "remote_demo_bg_url") {
                bgURL = item.getValue();
            } else if (item.getKey() == "remote_demo_no_audio") {
                type = item.getValue();
            }
        }
        conferenceService.updateConferenceSetting(kuickUserId, conferenceId, logoURL, theme, bgURL, type);
    }

    /**
     * 获取项目
     *
     * @param appId
     * @return
     * @throws Exception
     */
    public DealApp getDealAppByAppId(String appId) throws Exception {
        DealApp dealApp = null;
        String dealAppKey = "dealapp:" + appId;
        String dealAppJson = redisService.get(dealAppKey);

        if (StringUtils.isNotBlank(dealAppJson)) {
            dealApp = JsonUtil.fromJson(dealAppJson, DealApp.class);
        } else {
            dealApp = dealAppService.getDealAppInfo(appId);
            redisService.setex(dealAppKey, 30 * 60, JsonUtil.toJson(dealApp));
        }

        return dealApp;
    }

    /**
     * 获取项目成员
     *
     * @param appId
     * @param kuickUserId
     * @return
     * @throws Exception
     */
    public AppMember getAppMemberByAppIdAndKuickUserId(String appId, int kuickUserId) {
        String appMemberKey = getAppMemberRedisKey(appId, kuickUserId);
        String appMemberJson = redisService.get(appMemberKey);

        AppMember dealAppMember = null;

        if (StringUtils.isNotBlank(appMemberJson)) {
            dealAppMember = JsonUtil.fromJson(appMemberJson, AppMember.class);
        } else {
            dealAppMember = getAppMemberByKuickUserId(appId, kuickUserId);

            if (dealAppMember != null) {
                redisService.setex(appMemberKey, 30 * 60, JsonUtil.toJson(dealAppMember));
            }
        }
        
        return dealAppMember;
    }

    /**
     * 创建项目成员数据
     *
     * @param appId
     * @param kuickUserId
     * @param conferenceId
     * @return
     */
    public AppMember createDealAppMemberData(String appId, Integer kuickUserId, Integer conferenceId) {

        AppMember dealAppMember = new AppMember(appId, kuickUserId, conferenceId);
        dealAppMember.setPostRoles("MarketPost,SalesPost");
        dealAppMember.setRole("Sales");
        dealAppMember = dealAppMemberRepository.saveAndFlush(dealAppMember);

        return dealAppMember;
    }

    /**
     * 2017年12月6日上午10:06:45 panpan
     *
     * @see cn.deal.core.app.service.DealAppMemberService#getAppMemberCount(java.lang.String)
     * TODO 获取项目成员数量
     */
    public int getAppMemberCount(String appId) {
        return dealAppMemberDao.getAppMemberCount(appId);
    }

    /**
     * @param appId
     * @param kuickUserId
     * @return boolean 返回类型
     * @throws @Title: isAppAdmin
     * @Description: TODO(判断是否是项目创建人)
     */
    public boolean isAppOwner(String appId, Integer kuickUserId) {
        DealApp app = dealAppRepository.findOne(appId);

        if (app != null) {
            int catotorId = Integer.valueOf(app.getCreatorId());
            if (catotorId == kuickUserId) {
                return true;
            } else {
                return false;

            }
        } else {
            return false;
        }
    }

    /**
     * @param appId
     * @param kuickUserId
     * @return String 返回类型
     * @throws @Title: getMemberRoles
     * @Description: TODO(获取项目成员的角色)
     */
    public String getMemberRoles(String appId, Integer kuickUserId) {
        AppMember member = this.getAppMemberByKuickUserId(appId, kuickUserId);
        
        String roles = "";
        if (member != null) {
            if (StringUtils.isNotBlank(member.getRole())) {
                roles += member.getRole();
            }

            if (StringUtils.isNotBlank(member.getPostRoles())) {
                if (roles.length() > 0) {
                    roles += "," + member.getPostRoles();
                } else {
                    roles += member.getPostRoles();
                }
            }

            boolean admin = isAppOwner(appId, kuickUserId);
            log.info("getMemberRoles:" + admin);

            if (admin) {
                if (roles.length() > 0) {
                    roles += ",Owner";
                } else {
                    roles += "Owner";
                }

            }
        } else {
            roles = null;
        }
        
        return roles;
    }

    /**
     * @param appId
     * @param kuickUserId
     * @return DealAppMember 返回类型
     * @throws @Title: getAppMemberByKuickUserId
     * @Description: TODO(查询应用成员)
     */
    public AppMember getAppMemberByKuickUserId(String appId, Integer kuickUserId) {
        return dealAppMemberRepository.findByAppIdAndKuickUserId(appId, kuickUserId);
    }

    public AppMemberVO getAppMemberByKuickUserId(String appId, Integer kuickUserId, int withDepartment, int withKuickuser) {
        AppMember appMember = dealAppMemberRepository.findByAppIdAndKuickUserId(appId, kuickUserId);
        log.info("getAppMemberByKuickUserId.appMember: {}", appMember);

        AppMemberVO result = null;
        if (appMember != null) {
            if (withDepartment == AppMember.WithDepartment.YES.getVal() && StringUtils.isNotBlank(appMember.getDepartmentId())) {
                appMember.setDepartment(departmentRepository.findOne(appMember.getDepartmentId()));
            }

            if (withKuickuser == AppMember.WithKuickuser.YES.getVal()) {
                appMember.setUser(kuickuserUserService.getUserById(kuickUserId));
            }

            handleRole(appMember);
            result = new AppMemberVO(appMember);
            result.setIsOwner(dealAppMemberService.isAppOwner(appId, kuickUserId));
        }
        return result;
    }

    public AppMemberVO getAppMemberByKuickUserIdV2(String appId, Integer kuickUserId, int withDepartment, int withKuickuser) {
        AppMember appMember = dealAppMemberRepository.findByAppIdAndKuickUserId(appId, kuickUserId);
        log.info("getAppMemberByKuickUserId.appMember: {}", appMember);

        AppMemberVO result = null;
        if (appMember != null) {
            handleRole(appMember.getAppId(), Collections.singletonList(appMember));

            if (withDepartment == AppMember.WithDepartment.YES.getVal() && StringUtils.isNotBlank(appMember.getDepartmentId())) {
                appMember.setDepartment(departmentRepository.findOne(appMember.getDepartmentId()));
            }

            if (withKuickuser == AppMember.WithKuickuser.YES.getVal()) {
                appMember.setUser(kuickuserUserService.getUserById(kuickUserId));
            }

            result = new AppMemberVO(appMember);
            result.setIsOwner(dealAppMemberService.isAppOwner(appId, kuickUserId));

        }
        return result;
    }

    /**
     * DepartmentAdmin -> Admin
     *
     * @param appMember
     */
    private void handleRole(AppMember appMember) {
        List<AppMemberPermission> amps = appMemberPermissionRepository.findByAppIdAndDomainTypeAndPermAndKuickUserIdIn(
                appMember.getAppId(), Permission.DomainType.DEPARTMENT.getVal(), "ADMIN", Collections.singletonList(appMember.getKuickUserId()));
        if (amps != null && !amps.isEmpty()) {
            appMember.setRole(AppMember.Role.ADMIN.getVal());
        }

        if (StringUtils.contains(appMember.getRole(), AppMember.Role.APP_MEMBER.getVal())) {
            appMember.setRole(StringUtils.replace(appMember.getRole(), AppMember.Role.APP_MEMBER.getVal(), AppMember.Role.SALES.getVal()));
        }
    }

    /**
     * 获取所有项目成员
     *
     * @param appId
     * @return
     */
    public List<AppMember> getAppMembers(String appId) {
        return dealAppMemberDao.getDealAppMembers(appId);
    }

    /**
     * @param member
     * @return DealAppMember 返回类型
     * @throws @Title: updateAppMemberConferenceId
     * @Description: TODO(修改项目成员信息)
     */
    public AppMember updateAppMemberConferenceId(AppMember member) {
        member.setEditTime(new Date());
        redisService.delete(getAppMemberRedisKey(member.getAppId(), member.getKuickUserId()));
        return dealAppMemberRepository.saveAndFlush(member);
    }

    /**
     * @param appId
     * @param kuickUserId
     * @param postRoles
     * @param accessToken
     * @return AppMember 返回类型
     * @throws BusinessException 设定文件
     * @throws @Title:           updateAppMember
     * @Description: TODO(职能角色)
     */
    public AppMemberVO updateAppMember(String appId, Integer kuickUserId, List<String> postRoles, String accessToken)
            throws BusinessException {
        AppMemberVO appMember = null;

        AppMember member = this.getAppMemberByKuickUserId(appId, kuickUserId);
        KuickUser user = kuickuserUserService.getUserById(kuickUserId);
        if (user != null) {

            if (member != null && member.getStatus() == 0) {
                if (postRoles == null || postRoles.size() == 0) {
                    member.setPostRoles("");
                } else {
                    StringBuffer str = new StringBuffer();

                    for (int i = 0; i < postRoles.size(); i++) {
                        str.append(postRoles.get(i) + ",");
                    }

                    String postRole = str.substring(0, str.length() - 1);
                    member.setPostRoles(postRole);
                }

                member = updateAppMemberConferenceId(member);
                member.setUser(user);
                appMember = new AppMemberVO(member);
                appMember.setPostRoles(postRoles);
            } else {
                throw new BusinessException("not_found", "AppMember更新失败");
            }

        } else {
            throw new BusinessException("not_found", "AppMember更新失败,KuickUser 信息没查到");
        }

        return appMember;
    }

    public List<AppMember> getAppMembers(String appId, Integer startIndex, Integer count) {
    	List<AppMember> members = dealAppMemberDao.getDealAppMembers(appId, startIndex, count);
        log.info("getDealAppMembers.members: {}", members);
        
        return members;
	}
    
    public List<AppMember> getDealAppMembers(
            String appId, String departmentIds, List<String> roles, String keyword, String queryType, 
            Integer startIndex, Integer count) {
        log.info("getDealAppMembers.params: {}, {}, {}, {}, {}, {}, {}", appId, departmentIds, roles, keyword, queryType, startIndex, count);

        List<AppMember> members = dealAppMemberDao.getDealAppMembers(appId, departmentIds, roles, keyword, startIndex, count);
        log.info("getDealAppMembers.members: {}", members);

        if (StringUtils.equals(queryType, AppMember.QueryType.ONE.getVal())) {
            handleRole(appId, members);
        }

        return handleKuickUser(members);
    }

    private List<AppMember> handleKuickUser(List<AppMember> members) {
        List<Integer> kuickUserIds = members.stream().map(AppMember::getKuickUserId).collect(Collectors.toList());
        List<KuickUser> kuickUsers = userRepository.findByIds(kuickUserIds, User.IsSimple.NO);
        return members.stream().filter(member -> {
            member.setUser(kuickUsers.stream().filter(kuickUser ->
                    member.getKuickUserId().equals(kuickUser.getId())).findFirst().orElse(null));
            return member.getUser() != null;
        }).collect(Collectors.toList());
    }


    void handleRole(String appId, List<AppMember> members) {
        List<Integer> kuickUserIds = members.stream().map(AppMember::getKuickUserId).collect(Collectors.toList());
        log.info("handleRole.kuickUserIds: {}", kuickUserIds);

        List<AppMemberPermission> amps = appMemberPermissionRepository.findByAppIdAndDomainTypeAndPermAndKuickUserIdIn(
                appId, Permission.DomainType.DEPARTMENT.getVal(), "ADMIN", kuickUserIds);

        List<Integer> adminIds = amps.stream().map(AppMemberPermission::getKuickUserId).collect(Collectors.toList());
        log.info("handleRole.adminIds: {}", adminIds);

        DealApp app = dealAppService.getDealAppInfo(appId);
        log.info("handleRole.app: {}, {}", app.getId(), app.getCreatorId());

        members.forEach(member -> {
            if (adminIds.contains(member.getKuickUserId()) || StringUtils.equals(app.getCreatorId(), String.valueOf(member.getKuickUserId()))) {
                member.setRole(StringUtils.isNotBlank(member.getRole()) ? member.getRole() + "," +
                        AppMember.Role.DEPARTMENT_ADMIN.getVal() : AppMember.Role.DEPARTMENT_ADMIN.getVal());
            }
        });
    }

    /**
     * @param postRoles
     * @return List<String> 返回类型
     * @throws @Title: getPostRoles
     * @Description: TODO(获取只能权限)
     */
    public List<String> getPostRoles(String[] postRoles) {
        List<String> list = new ArrayList<>();
        if (postRoles.length > 0) {
            for (int i = 0; i < postRoles.length; i++) {
                if (StringUtils.isNotBlank(postRoles[i])) {
                    list.add(postRoles[i]);
                }
            }
        }
        return list;
    }

    public boolean isAppMember(String appId, Integer kuickUserId) {
        AppMember member = dealAppMemberRepository.findByAppIdAndKuickUserId(appId, kuickUserId);
        boolean flag;
        if (member == null) {
            flag = false;
        } else {
            flag = true;
        }
        return flag;
    }

    public Map<String, Object> getDealAppMembers(String[] appIds, String[] kuickUserIds) throws Exception {
        Map<String, Object> members = new HashMap<>();
        if (appIds.length != kuickUserIds.length) {
            throw new BusinessException("param_invalid", "appIds参数长度 和kuickUserIds长度不一致");
        }
        if (appIds != null && appIds.length > 0) {
            for (int i = 0; i < appIds.length; i++) {
                String appId = appIds[i];
                Integer kuickUserId = Integer.valueOf(kuickUserIds[i]);
                String appMemberKey = getAppMemberRedisKey(appId, kuickUserId);
                AppMember member = getAppMemberByAppIdAndKuickUserId(appId, kuickUserId);
                log.info("getDealAppMembers  member:" + member);

                if (member != null && member.getStatus() == 1) {
                    member = null;
                }

                members.put(appMemberKey, member);
            }
        }

        log.info("getDealAppMembers  member:" + members);
        return members;
    }

    private String getAppMemberRedisKey(String appId, Integer kuickUserId) {
        return "appMember:" + appId + "_" + kuickUserId;
    }

    /**
     * 查询项目成员
     *
     * @param appIds
     * @param kuickUserIds
     * @return
     */
    public List<AppMember> getDealAppMembers(String appId, String[] kuickUserIds) {
        List<AppMember> members = new ArrayList<>();

        if (kuickUserIds != null && kuickUserIds.length > 0) {
            for (int i = 0; i < kuickUserIds.length; i++) {
                Integer kuickUserId = Integer.valueOf(kuickUserIds[i]);
                AppMember member = getAppMemberByAppIdAndKuickUserId(appId, kuickUserId);
                log.info("getDealAppMembers  member:" + member);

                if (member != null && AppMember.Status.VALID.getValue().equals(member.getStatus().intValue())) {
                    members.add(member);
                }
            }
        }

        log.info("getDealAppMembers:" + members);
        return members;
    }

    /**
     * @return
     * @Description 编辑用户信息
     * @Param [appId, kuickUserId, remarkName]
     * @Return cn.deal.core.app.domain.DealAppMember
     */
    public AppMemberVO editRemarkName(String appId, Integer kuickUserId, String remarkName) {
        AppMember dealAppMember = dealAppMemberRepository.findByAppIdAndKuickUserId(appId, kuickUserId);
        if (dealAppMember == null) {
            throw new BusinessException("not_exists", "用户不存在");
        }
        AppMember oldMember = new AppMember();
        BeanUtils.copyProperties(dealAppMember, oldMember);
        dealAppMember.setRemarkName(remarkName);
        dealAppMemberRepository.saveAndFlush(dealAppMember);
        sendAppMemberDomainEventToKafka(AppMemberDomainEventTypeEnum.update, dealAppMember, oldMember);
        redisService.delete(getAppMemberRedisKey(appId, kuickUserId));
        return new AppMemberVO(dealAppMember);
    }

    @CacheEvict(cacheNames = "appmember", key = "'appMember:appId:'+#p0")
    public boolean remove(String appId, String kid) {
        String url = serviceConfig.getCoreApiBaseUrl() + "api/v1.5/app/{appId}/member/{kid}";
        log.info("delete appMember url:{}", url);
        ResponseEntity<Map> res = restTemplate.exchange(url, HttpMethod.DELETE, null, Map.class, appId, kid);
        log.info("delete app[{}] member[{}] successfully, result: {}", appId, kid, res.getBody());
        if (res.hasBody()) {
            if (Integer.parseInt(res.getBody().get("status").toString()) != 1) {
                throw new BusinessException("error", res.getBody().get("msg").toString());
            } else {
                appMemberPermissionRepository.deleteByAppIdAndKuickUserId(appId, Integer.parseInt(kid));
            }
        }
        return true;
    }

    @CacheEvict(cacheNames = "appmember", key = "'appMember:appId:'+#p0")
    public Object changeRole(String appId, String kuickUserId, String role) {
        String url = serviceConfig.getCoreApiBaseUrl() + "api/v1.0/app/{appId}/member/{kid}/role?role={role}";
        log.info("change appMember role url:{}", url);
        ResponseEntity<Map> res = restTemplate.exchange(url, HttpMethod.PUT, null, Map.class, appId, kuickUserId, role);
        log.info("chanege app[{}] member[{}] role[{}] successfully, result: {}", appId, kuickUserId, role, res.getBody());
        return res.getBody();
    }

    public AppMember findAppMember(String appId, Integer kuickUserId) {
        AppMember appMember = dealAppMemberRepository.findOneByAppIdAndKuickUserId(appId, kuickUserId);
        if (appMember == null) {
            appMember = new AppMember();
        }
        KuickUser kuickUser = kuickuserUserService.getUserById(kuickUserId);
        appMember.setUser(kuickUser);
        return appMember;
    }
}
