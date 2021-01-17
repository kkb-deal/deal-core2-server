package cn.deal.core.app.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cn.deal.core.app.domain.DealAppSecret;
import cn.deal.core.app.repository.DealAppSecretRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Joiner;

import cn.deal.component.RedisService;
import cn.deal.component.UserComponent;
import cn.deal.component.config.ServiceConfig;
import cn.deal.component.domain.User;
import cn.deal.component.exception.BusinessException;
import cn.deal.component.kuick.ConferenceGroupMemberService;
import cn.deal.component.kuick.ConferenceGroupService;
import cn.deal.component.kuick.ConferenceService;
import cn.deal.component.kuick.KuickuserUserService;
import cn.deal.component.kuick.domain.Conference;
import cn.deal.component.kuick.domain.ConferenceGroup;
import cn.deal.component.kuick.domain.KuickUser;
import cn.deal.component.utils.JsonUtil;
import cn.deal.core.app.dao.DealAppDao;
import cn.deal.core.app.domain.DealApp;
import cn.deal.core.app.repository.DealAppRepository;
import cn.deal.core.license.domain.License;
import cn.deal.core.license.service.AppLicenseService;
import net.sf.json.JSONObject;


@Service
public class DealAppService {

    private static final Log log = LogFactory.getLog(DealAppService.class);

    @Autowired
    private DealAppRepository dealAppRepository;
    @Autowired
    private DealAppSecretRepository dealAppSecretRepository;

    @Autowired
    private DealAppDao dealAppDao;

    @Autowired
    private DealAppMemberService dealAppMemberService;

    @Autowired(required = true)
    private ConferenceService conferenceService;

    @Autowired
    private ConferenceGroupService conferenceGroupService;

    @Autowired
    private ConferenceGroupMemberService conferenceGroupMemberService;

    @Autowired
    private KuickuserUserService kuickuserUserService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private ServiceConfig config;

    @Autowired
    private  AppLicenseService  appLicenseService;

    @Autowired
    private UserComponent userComponet;

    /**
     * 创建项目数据
     * @param kuickUserId
     * @param name
     * @param description
     * @param conferenceId
     * @param conferenceGroupId
     * @return
     */
    protected DealApp createDealAppData(String kuickUserId, String name, String description, Integer conferenceId,
                                     String conferenceGroupId){
        DealApp dealApp = new DealApp(kuickUserId, name, description, conferenceId, conferenceGroupId);

        // 创建项目时生成 AppSecret
        dealApp.setSecret(UUID.randomUUID().toString());

        dealApp = dealAppRepository.save(dealApp);

        return dealApp;
    }

    /**
     * 创建项目
     * @param kuickUserId
     * @param name
     * @param description
     * @param accessToken
     * @return
     * @throws Exception
     */
    public DealApp createDealApp(String kuickUserId, String name, String description, String accessToken, Integer trialType) throws Exception{
        DealApp dealApp = dealAppRepository.findByCreatorIdAndAppName(kuickUserId, name);
        
        if(dealApp != null){
            log.info("deal_app: " + dealApp.toString());
            throw new BusinessException("app_name_exist", "项目已存在，请更换项目名称");
        } else {
            Integer conferenceId = null;
            Conference conference = conferenceService.createConference(kuickUserId, name, description, accessToken);
            if(conference != null){
                conferenceId = conference.getId();
            } else {
                throw new Exception("创建会议失败");
            }

            String conferenceGroupId = null;
            ConferenceGroup conferenceGroup = conferenceGroupService.createConferenceGroup(kuickUserId, name, null, accessToken);

            if(conferenceGroup != null){
                conferenceGroupId = conferenceGroup.getId();
            } else {
                throw new Exception("创建会议组失败");
            }

            conferenceGroupMemberService.createConferenceGroupMember(conferenceGroupId, String.valueOf(conferenceId), accessToken);
            dealApp = createDealAppData(kuickUserId, name, description, conferenceId, conferenceGroupId);

            String appId = dealApp.getId();

            dealAppMemberService.createDealAppMemberData(appId, Integer.valueOf(kuickUserId), conferenceId);
            String license=config.getKdRsaLicense();

            if(trialType!=null && trialType == 20){
                license=config.getKdRsaLicense20();
            }

            if(trialType!=null && trialType == 30){
                license=config.getKdRsaLicense30();
            }

            appLicenseService.saveApplicense(appId, license);
        }

        return dealApp;
    }

    public DealApp updateDealApp(String appId, String name, String description) throws Exception{
        DealApp dealApp = dealAppRepository.findByName(name);

        if(dealApp != null && !(dealApp.getId().equals(appId))){
            log.info("deal_app: " + dealApp.toString());
            throw new Exception("项目已存在，请更换项目名称");
        }

        dealApp = dealAppRepository.findOne(appId);

        if(dealApp != null){
            if(StringUtils.isNotBlank(appId)){
                dealApp.setName(name);
            }

            if(StringUtils.isNotBlank(description)){
                dealApp.setDescription(description);
            }

            dealAppRepository.save(dealApp);
            String redisKey = "dealapp:" + appId;
            String dealAppJson = redisService.get(redisKey);

            if(StringUtils.isNotBlank(dealAppJson)){
                JsonUtil util = new JsonUtil();
                String dealAppStr = util.toJson(dealApp);
                redisService.setex(redisKey, 30*60, dealAppStr);
            }

            if(StringUtils.isNotBlank(appId) && null != dealApp.getConferenceId()){
                conferenceService.updateConference(dealApp.getConferenceId(), name, dealApp.getDescription());
            }
        }

        return dealApp;
    }

    /**
     * 获取项目信息
     * @param appId
     * @return
     */
    public DealApp getDealAppInfo(String appId) {
        String redisKey = "dealapp:" + appId;
        String dealAppJson = redisService.get(redisKey);
        DealApp dealApp = null;
        JsonUtil util = new JsonUtil();
        
        try {
			if(StringUtils.isNotBlank(dealAppJson)){
			    log.info("redis_get_data: " + dealAppJson);
			    
			    try {
				    JSONObject jsonObject = JSONObject.fromObject(dealAppJson);
				    dealApp = (DealApp)JSONObject.toBean(jsonObject, DealApp.class);
			    } catch(Exception e) {
			    	log.warn("warn in parse DealApp from json:" + dealAppJson, e);
			    }
			}  
			
			if (dealApp == null){
			    dealApp = dealAppRepository.findOne(appId);
			    if (dealApp == null) {
			        throw new BusinessException("not_found", "not found dealapp with appId:" + appId);
                }

			    String dealAppStr = util.toJson(dealApp);
			    redisService.setex(redisKey, 30*60, dealAppStr);
			}

			if(dealApp != null){
			    log.info("deal_app_info: " + dealApp.toString());
			    
			    if(StringUtils.isBlank(dealApp.getSecret())){
			    	dealApp.setSecret(UUID.randomUUID().toString());
			    	dealAppRepository.saveAndFlush(dealApp);

			    	String dealAppStr = util.toJson(dealApp);
					redisService.setex(redisKey, 30*60, dealAppStr);
			    }
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
        
        return dealApp;
    }

    /**
    * @Title: getKuickUserApps
    * @Description: TODO(我的apps)
    * @param accessToken
    * @param expired
    * @return
    * @throws Exception 设定文件
    * @return List<DealApp>    返回类型
    * @throws
    */
    public List<DealApp> getKuickUserApps(String accessToken, Integer expired) throws Exception {
        User user = userComponet.getUserByToken(accessToken);

        if (user == null)
            throw new Exception("this user is null ");

        String kuickUserId = user.getId() + "";

        List<DealApp> apps = dealAppDao.getKuickUserApps(kuickUserId, null);

        log.info("getKuickUserApps apps: " + apps);

        if (apps == null || apps.size() == 0) {
            DealApp app = this.createDealApp(kuickUserId, "我的名片", "系统默认创建项目", accessToken,10);
            apps.add(app);
        }

        List<DealApp> list = getExpiredList(apps, expired);

        getSortDealApps(list);

        return list;
    }

    public List<DealApp> getAppsByCreatorId(String kuickUserId, String keyword) throws Exception{
        List<DealApp> list = dealAppDao.getKuickUserApps(kuickUserId, keyword);
        this.RecomList(list);

        return list;
    }

	public List<DealApp> getAllDealApps(Integer startIndex, Integer count, String serchName, String settingKey,
			String settingValue) {
		List<DealApp> list = new ArrayList<>();
		 Map<String, String> map = new HashMap<>();

        if (StringUtils.isNotBlank(serchName)) {

            if(validateUUid(serchName)){
                map.put("id", serchName);
            }else{
                map.put("name", serchName);
                map.put("creatorIds", getCreatorIds(serchName, startIndex, count));
            }
        }

		if (StringUtils.isNotBlank(settingKey) || StringUtils.isNotBlank(settingValue)) {
			list = dealAppDao.getAllApps(map, settingKey, settingValue,startIndex, count);
		} else {
			list = dealAppDao.getAllApps(startIndex, count, map);
		}

		this.RecomList(list);

		return list;
	}

	public int getAppCount(String keyword) {
		if(StringUtils.isNotBlank(keyword)){
		    Map<String, String> map = new HashMap<>();

		    if(validateUUid(keyword)){
		        map.put("id", keyword);
		    } else {
		        map.put("name", keyword);
		        map.put("creatorIds", getCreatorIds(keyword,null,null));
		    }

			return dealAppDao.getCount(map);
		} else{
			return new Long(dealAppRepository.count()).intValue();
		}
	}

    public List<DealApp> getExpiredList(List<DealApp> list, Integer expired) {
        List<DealApp> apps = new ArrayList<>();

        if (list != null && list.size() > 0) {

            for (DealApp app : list) {
                try {
                    License license = appLicenseService.getAppLimits(app.getId());

                    if (license != null) {

                        app.setExpired(license.isExpired());
                        app.setExpiresTime(license.getExpiresTime());

                        if (StringUtils.isNotBlank(license.getEdition())) {
                            if (license.getEdition().equals("ultimate")) {
                                app.setAppType(1);
                            } else if (license.getEdition().equals("enterprise")) {
                                app.setAppType(2);
                            } else {
                                app.setAppType(3);
                            }
                        } else {
                            app.setAppType(3);
                        }

                        if (expired != null) {
                            if((expired ==1 && license.isExpired()) || (expired == 0 && !license.isExpired())){
                                apps.add(app);
                            }
                        }

                    }

                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }

            if(expired !=null){
                return apps;
            }else{
                apps = list;
            }

        }

        return apps;
    }

	/**
     * @Title: getSortList
     * @Description: TODO(根据项目license，大客户、企业客户、个人客户)
     * @param list
     * @return 设定文件
     * @return List<DealApp>    返回类型
     * @throws
     */
    public List<DealApp> getSortDealApps(List<DealApp> list){

          Collections.sort(list, new Comparator<DealApp>(){

                 /*
                  * int compare(DealApp o1, DealApp o2) 返回一个基本类型的整型，
                  * 返回负数表示：o1 小于o2，
                  * 返回0 表示：o1和o2相等，
                  * 返回正数表示：o1大于o2。
                  */
                 public int compare(DealApp o1, DealApp o2) {

                     if(o1.getAppType() > o2.getAppType()){
                         return 1;
                     }
                     if(o1.getAppType() == o2.getAppType()){
                         return 0;
                     }
                     return -1;
                 }
             });

          return list;
     }

	private boolean validateUUid (String keyword){
	    boolean isUUid = false;

	    try {
            UUID.fromString(keyword);
            isUUid = true;
        } catch (Exception e) {
            isUUid = false;
        }

	    return isUUid;
	}

    public String getCreatorIds(String keyword, Integer startIndex, Integer count){
         List<User> users = userComponet.getUsersByName(keyword,startIndex,count);
         List<String> catorIds = new ArrayList<>();

         if(users !=null && users.size() >0){
             for(User user: users){
                 catorIds.add(user.getId() + "");
             }
         }

         return Joiner.on(",").join(catorIds);
     }

    private List<DealApp> RecomList(List<DealApp> list) {
        if (list != null && list.size() > 0) {
            for (DealApp app : list) {
                try {
                    KuickUser user = kuickuserUserService.getUserById(Integer.valueOf(app.getCreatorId()));
                    app.setUser(user);

                    License license = appLicenseService.getAppLimits(app.getId());

                    if (license != null) {
                        app.setExpired(license.isExpired());
                        app.setExpiresTime(license.getExpiresTime());

                    }

                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return list;
    }

    public String getSecret(String id){
	     if(StringUtils.isBlank(id)){
	         log.info("getSecret. appid is blank.");
	         return null;
         }
	     DealAppSecret appSecret = dealAppSecretRepository.findOne(id);
	     if(appSecret == null){
	         log.warn("getSecret. No appsecret found by id: " + id);
	         return null;
         }
         return appSecret.getSecret();
    }
}
