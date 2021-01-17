package cn.deal.core.meta.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import com.google.gson.reflect.TypeToken;

import cn.deal.component.RedisService;
import cn.deal.component.utils.JsonFileUtil;
import cn.deal.component.utils.JsonUtil;
import cn.deal.component.utils.ListToMapUtil;
import cn.deal.core.meta.dao.AppSettingDao;
import cn.deal.core.meta.domain.AppSetting;
import cn.deal.core.meta.repository.AppSettingRepository;

@Service
public class AppSettingService {
	private static final Log log = LogFactory.getLog(AppSettingService.class);
	
	@Autowired
	private AppSettingRepository appSettingRepository;
	
	@Autowired
	private AppSettingDao appSettingDao;
	
	@Autowired
	private JsonFileUtil jsonFileUtil;
	
	private   List<AppSetting>  appSettings;
	
	@Autowired
	private RedisService redisService;

    @Value(value = "classpath:metas/AppSettingConfig.js")
    private Resource appSettingConfig;
    
    public String getAppSettingConfig() {
        return jsonFileUtil.getJsonFromClasspathFile(appSettingConfig);
    }
    
	@PostConstruct
	public void appSettingConfigInit(){
		Object jsonObject = getAppSettingConfig();
		
		log.info("appSettingConfigInit json==" + jsonObject);
		Type dataType1 = new TypeToken<Map>(){}.getType();
		appSettings=  new ArrayList<>();
		
		Map<String,Map> map=(Map<String,Map>)JsonUtil.parseJson(String.valueOf(jsonObject), dataType1);
		
		for (String key : map.keySet()) {
			AppSetting set = new AppSetting();
			set.setId(null);
			set.setCreatedAt(null);
			set.setKey(key);
			
			Map<String, String> mapN = map.get(key);
			String type=String.valueOf(mapN.get("type"));
			String defaultValue =String.valueOf(mapN.get("defaultValue"));
			set.setType(type);
			if(type.equals("int")){
				
				if(StringUtils.isNotBlank(defaultValue))
					set.setDefaultValue((new Double(defaultValue)).intValue()+"");
				
			}else{
				set.setDefaultValue(defaultValue);
			}
			
			set.setTitle(mapN.get("title"));
			set.setDesc(mapN.get("desc"));
			appSettings.add(set);
		}
		
		log.info("appSettingConfigInit appSettings=="+appSettings);
	}
	
	/** 
	* @Title: getSettings 
	* @Description: TODO(获取项目设置列表) 
	* @param appId
	* @return 设定文件 
	* @return List<AppSetting>    返回类型 
	* @throws 
	*/
	public List<AppSetting> getSettings(String appId) {
		List<AppSetting> appSettingList = appSettings;
		log.info("getSettings result appSettings==" + appSettingList);
		String items = redisService.get("appSetting:" + appId);
		List<AppSetting> list = new ArrayList<>();

		if (StringUtils.isNotBlank(items)) {
			list = getRedisSettings(items);
		} else {
			list = appSettingRepository.findByAppId(appId);
		}

		log.info("getSettings result list==" + list);
		Map<String, AppSetting> mapSetting=ListToMapUtil.listToMap(list, "getKey", AppSetting.class);
		log.info("getSettings result mapSetting==" + mapSetting);
		List<AppSetting> setings = new ArrayList<>();

		for (AppSetting set : appSettingList) {
			AppSetting seting = mapSetting.get(set.getKey());
			
			log.info("getSettings result mapSetting seting==" + seting);
			
			if (seting == null) {
				setings.add(set);
			} else {
				seting.setTitle(set.getTitle());
				seting.setDesc(set.getDesc());
				setings.add(seting);
			}
			
			
		}

		log.info("getSettings result setings==" + setings);
		return setings;

	}
	
	/** 
	* @Title: getRedisSettings 
	* @Description: TODO(判断redis里面是否包含数据库查询的数据) 
	* @param items
	* @return 设定文件 
	* @return List<AppSetting>    返回类型 
	* @throws 
	*/
	private List<AppSetting> getRedisSettings(String items){
		List<AppSetting> list = new ArrayList<>();
		Type dataType = new TypeToken<Map<String, AppSetting>>() {}.getType();
		Map<String, AppSetting> map = (Map<String, AppSetting>) JsonUtil.parseJson(String.valueOf(items), dataType);
		
		for (String k : map.keySet()) {
			log.info("map.get(k):" + map.get(k));
			list.add((AppSetting) map.get(k));
		}
		
		return list;
	}
	/** 
	* @Title: saveAppSetting 
	* @Description: TODO(保存) 
	* @param appSetting
	* @return 设定文件 
	* @return AppSetting    返回类型 
	* @throws 
	*/
	private AppSetting saveAppSetting(AppSetting appSetting){
		return appSettingRepository.saveAndFlush(appSetting);
	}
	
	/**
	 * @throws Exception  
	* @Title: updateAppSetting 
	* @Description: TODO(修改项目设置) 
	* @param id
	* @param key
	* @param type
	* @param value
	* @param defaultValue
	* @return 设定文件 
	* @return AppSetting    返回类型 
	* @throws 
	*/
	public AppSetting updateAppSetting(String appId,String key, String value) throws Exception {
		AppSetting appSetting = appSettingDao.getAppSettingByAppIdAndKey(appId,key);
		Map<String, AppSetting> mapSetting=ListToMapUtil.listToMap(appSettings, "getKey", AppSetting.class);
		
		if (appSetting != null) {
			appSetting.setValue(value);
		}else{
			AppSetting appSet=mapSetting.get(key);
			appSetting=new AppSetting(appId, key, value, appSet.getType(), appSet.getDefaultValue(), appSet.getTitle());
		}
		
		appSetting=saveAppSetting(appSetting);
		updateRedis(appId,appSetting);
		return appSetting;
	}
	
	/**
	 * @throws Exception  
	* @Title: updateRedis 
	* @Description: TODO(更新redis) 
	* @param appId
	* @param appSetting 设定文件 
	* @return void    返回类型 
	* @throws 
	*/
	private void updateRedis(String appId, AppSetting appSetting) throws Exception {
		String items = redisService.get("appSetting:" + appId);
		log.info("itme:"+items);
		Map<String, AppSetting> mapSetting = new HashMap<>();
		JsonUtil util=new JsonUtil();
		
		//key存在情况
		if (StringUtils.isNotBlank(items)) {
			Type dataType = new TypeToken<Map<String, AppSetting>>() {}.getType();
			mapSetting = (Map<String, AppSetting>) JsonUtil.parseJson(String.valueOf(items), dataType);
			log.info("updateRedis result map:"+mapSetting);
			
			mapSetting.put(appSetting.getKey(), appSetting);
			log.info("updateRedis result map:"+mapSetting);
		}else{//key不存在情况
			List<AppSetting> list = appSettingRepository.findByAppId(appId);
			
			if(list!=null && list.size()>0){
				mapSetting=ListToMapUtil.listToMap(list, "getKey", AppSetting.class);
			}
		}
		
		String json=util.toJson(mapSetting);
		log.info("redis fuwu   appSetting:" + appId + ":" + json);
		redisService.setex("appSetting:" + appId, 0, json);
	}

	/** 
	* @Title: getSetting 
	* @Description: TODO(获取应用设置项的值) 
	* @param appId
	* @param settingItemName
	* @return 设定文件 
	* @return AppSetting    返回类型 
	* @throws 
	*/
	public AppSetting getSetting(String appId, String  settingItemName){
		String items = redisService.get("appSetting:" + appId);
		Map<String, AppSetting> mapSetting = new HashMap<>();
		AppSetting appSetting=null;
		
		//key存在情况
		if (StringUtils.isNotBlank(items)) {
			Type dataType = new TypeToken<Map<String, AppSetting>>() {}.getType();
			mapSetting = (Map<String, AppSetting>) JsonUtil.parseJson(String.valueOf(items), dataType);
			appSetting=mapSetting.get(settingItemName);
		}
		
		if(appSetting==null){
			appSetting=appSettingDao.getAppSettingByAppIdAndKey(appId,settingItemName);
		}
		
		return appSetting;
	}
	
}
