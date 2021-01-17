package cn.deal.core.meta.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.reflect.TypeToken;

import cn.deal.component.RedisService;
import cn.deal.component.config.EnumConfig;
import cn.deal.component.config.RecordProperties;
import cn.deal.component.utils.JsonUtil;
import cn.deal.component.utils.ListToMapUtil;
import cn.deal.core.meta.dao.AppCustomerRecordSettingDao;
import cn.deal.core.meta.domain.AppCustomerRecordSetting;
import cn.deal.core.meta.repository.AppCustomerRecordSettingRepository;
import net.sf.json.JSONArray;

@Service
public class AppCustomerRecordSettingService {
    private static final Log log = LogFactory.getLog(AppCustomerRecordSettingService.class);
    
    @Autowired
    private AppCustomerRecordSettingDao appCustomerRecordSettingDao;
    @Autowired
    private AppCustomerRecordSettingRepository appCustomerRecordSettingRepository;
    @Autowired
    private RedisService redisService;
    @Autowired
    private RecordProperties recordProperties;
    
    /**
     * @throws Exception  
     * @Title: getAppCustomerRecordSettingsByAppId 
     * @Description: TODO(根据调用端返回不同记录项列表) 
     * @param appId
     * @param type
     * @return 设定文件 
     * @return List<AppCustomerRecordSetting>    返回类型 
     * @throws 
     */
     public List<AppCustomerRecordSetting> getAppCustomerRecordSettings(String appId, String type) throws Exception{
         List<AppCustomerRecordSetting> allList = new ArrayList<>();

         if (StringUtils.isNotBlank(type)) {
             List<AppCustomerRecordSetting> list =getList(appId);
             if(type.equals("elanding")){
                 getAllList(appId, list, allList, type);
             }else{
                 allList.addAll(list);
             }
         }else{
             allList = getAppCustomerRecordSettingsByAppId(appId) ;
         }
         
         listAddColor(allList);//色值计算
         getSortList(allList);//index排序
         return allList;
     }
     
    /**
     * @throws Exception  
    * @Title: getAppCustomerRecordSettingsByAppId 
    * @Description: TODO(返回项目下正常、被删除、默认action的数据列表) 
    * @param appId
    * @return 设定文件 
    * @return List<AppCustomerRecordSetting>    返回类型 
    * @throws 
    */
    public List<AppCustomerRecordSetting> getAppCustomerRecordSettingsByAppId(String appId) throws Exception {
        List<AppCustomerRecordSetting> allList = new ArrayList<>();
        String key = "customerRecordTypes:" + appId;
        String listJson = redisService.get(key);
        JsonUtil util = new JsonUtil();
        
        if (StringUtils.isBlank(listJson)) {
            List<AppCustomerRecordSetting> list = appCustomerRecordSettingDao.getAppCustomerRecordSettings(appId, null,
                    null, EnumConfig.Status.NULL);
            getAllList(appId, list, allList, null);
            String json=util.toJson(allList);
            redisService.setex(key, 10, json);// 设置缓存去10秒
        } else {
            Type type = new TypeToken<ArrayList<AppCustomerRecordSetting>>() {
            }.getType();
            List<AppCustomerRecordSetting> list = (List<AppCustomerRecordSetting>) JsonUtil.parseJson(listJson, type);
            getAllList(appId, list, allList, null);
        }
      
        return allList;
    }
    
    /** 
    * @Title: getAllListByType 
    * @Description: TODO(数据库中未被删除的数据列表) 
    * @param appId
    * @return 设定文件 
    * @return List<AppCustomerRecordSetting>    返回类型 
    * @throws 
    */
    public List<AppCustomerRecordSetting> getList(String appId){
        List<AppCustomerRecordSetting> list = appCustomerRecordSettingDao.getAppCustomerRecordSettings(appId, null, null, EnumConfig.Status.VALID_DATA);
        return list;
    }
    
    /** 
    * @Title: getAllList 
    * @Description: TODO(不同情况封装数据) 
    * @param appId
    * @param list
    * @param allList
    * @param type
    * @return 设定文件 
    * @return List<AppCustomerRecordSetting>    返回类型 
    * @throws 
    */
    private List<AppCustomerRecordSetting> getAllList(String appId, List<AppCustomerRecordSetting> list, List<AppCustomerRecordSetting> allList, String type){
        Map<String, AppCustomerRecordSetting> map = null;

        if (list != null && list.size() > 0) {
            allList.addAll(list);
            map = ListToMapUtil.listToMap(list, "getAction", AppCustomerRecordSetting.class);
        }
        
        this.getAppCustomerRecordSettingByAction(appId, map, allList, EnumConfig.Actions.VISIT_RECORD, EnumConfig.ActionNames.VISIT_RECORD_NAME, -1, type);
        this.getAppCustomerRecordSettingByAction(appId, map, allList, EnumConfig.Actions.ATTEND_MEETING_RECORD, EnumConfig.ActionNames.ATTEND_MEETING_RECORD_NAME, -2, type);
        this.getAppCustomerRecordSettingByAction(appId, map, allList, EnumConfig.Actions.DEAL_RECORD, EnumConfig.ActionNames.DEAL_RECORD_NAME, -3, type);
        this.getAppCustomerRecordSettingByAction(appId, map, allList, EnumConfig.Actions.CUSTOMER_RECORD, EnumConfig.ActionNames.CUSTOMER_RECORD_NAME, -4, type);
        this.getAppCustomerRecordSettingByAction(appId, map, allList, EnumConfig.Actions.CHAT_RECORD, EnumConfig.ActionNames.CHAT_RECORD_NAME, -5, type);
        return allList;
    }
    
    /** 
     * @Title: getSortList 
     * @Description: TODO(客户基本字段按index排序) 
     * @param list
     * @return 设定文件 
     * @return List<CustomerMeta>    返回类型 
     * @throws 
     */
     public List<AppCustomerRecordSetting> getSortList(List<AppCustomerRecordSetting> list){
         
          Collections.sort(list, new Comparator<AppCustomerRecordSetting>(){  
               
                 /*  
                  * int compare(CustomerMeta o1, CustomerMeta o2) 返回一个基本类型的整型，  
                  * 返回负数表示：o1 小于o2，  
                  * 返回0 表示：o1和o2相等，  
                  * 返回正数表示：o1大于o2。  
                  */  
                 public int compare(AppCustomerRecordSetting o1, AppCustomerRecordSetting o2) {  
                   
                     if(o1.getIndex() > o2.getIndex()){  
                         return 1;  
                     }  
                     if(o1.getIndex() == o2.getIndex()){  
                         return 0;  
                     }  
                     return -1;  
                 }  
             }); 
          
          return list;
     }
    
    /** 
    * @Title: listAddColor 
    * @Description: TODO(返回的每种类型添加 color 字段) 
    * @param list
    * @return 设定文件 
    * @return List<AppCustomerRecordSetting>    返回类型 
    * @throws 
    */
    private List<AppCustomerRecordSetting> listAddColor(List<AppCustomerRecordSetting> list){
        String[] colors = recordProperties.getColor().split(",");
        if (list != null && list.size() > 0) {
            for(AppCustomerRecordSetting record : list){
                String action = record.getAction();
                if(action.equals(EnumConfig.Actions.CHAT_RECORD))
                    record.setStatus(0);
                    
                int keyHash = Math.abs(action.hashCode());
                int index = keyHash % colors.length;
                record.setColor(colors[index]);
                record.setStatus(record.getStatus());
            }
        }
        return list;
    }
    
    /** 
    * @Title: getAppCustomerRecordSettingByAction 
    * @Description: TODO(根据参数过滤默认action是否应该被覆盖加载) 
    * @param appId
    * @param map
    * @param allList
    * @param action
    * @param actionName
    * @param index
    * @return 设定文件 
    * @return List<AppCustomerRecordSetting>    返回类型 
    * @throws 
    */
    private List<AppCustomerRecordSetting> getAppCustomerRecordSettingByAction(String appId,
            Map<String, AppCustomerRecordSetting> map, List<AppCustomerRecordSetting> allList, String action,
            String actionName, int index,String type) {
        boolean isDelete = false;
        if(StringUtils.isNotBlank(type) && type.equals("elanding")){
            List<AppCustomerRecordSetting> deleteList = appCustomerRecordSettingDao.getAppCustomerRecordSettings(appId, null, null,0); 
            Map<String, AppCustomerRecordSetting> delteMap = ListToMapUtil.listToMap(deleteList, "getAction",AppCustomerRecordSetting.class);
            
            if((delteMap == null || delteMap.size() == 0 || delteMap.get(action) == null)){
                isDelete = true;
            }
            
        }
        
        if (map == null || map.size() == 0 || map.get(action) == null) {
            if (StringUtils.isBlank(type)
                    ||isDelete) {
                AppCustomerRecordSetting acrs = new AppCustomerRecordSetting(appId, action, index, actionName);
                
                if(action.equals(EnumConfig.Actions.CHAT_RECORD)){
                    acrs.setStatus(0);
                }else{
                    acrs.setStatus(1);
                }
                acrs.setId("");
                allList.add(acrs);
            }
        }
        
        return allList;
    }
    
    /** 
    * @Title: isReName 
    * @Description: TODO(校验数据库再同一个项目下是否重复名称和动作) 
    * @param appId
    * @param name
    * @param action
    * @return 设定文件 
    * @return boolean    返回类型 
    * @throws 
    */
    private boolean isReName(String appId, String name, String action) {
        boolean isReName = false;
        List<AppCustomerRecordSetting> list = appCustomerRecordSettingDao.getAppCustomerRecordSettings(appId, name, action ,EnumConfig.Status.NULL);

        if (list != null && list.size() > 0) {
            isReName = true;
        }

        return isReName;
    }
    
    public  AppCustomerRecordSetting saveCustomerRecordSetting (String appId, String name, String action, Integer index,
            String description) throws Exception {
        boolean isReName = isReName(appId, name, action);
        AppCustomerRecordSetting appCustomerRecordSetting = new AppCustomerRecordSetting();

        if (isReName) {
            throw new Exception("appCustomerRecordSetting is already existed.");
        } else {
            appCustomerRecordSetting.setAppId(appId);
            appCustomerRecordSetting.setName(name);
            appCustomerRecordSetting.setAction(action);
            appCustomerRecordSetting.setStatus(EnumConfig.Status.VALID_DATA);
            
            if (index == null || index == 0) {
                index = appCustomerRecordSettingDao.getMaxIndex(appId);
                log.info("---saveCustomerRecordSetting-index-"+index);
            }
            
            index = index == null ? 0 : index;
            appCustomerRecordSetting.setIndex(index);

            if (StringUtils.isNotBlank(description)) {
                appCustomerRecordSetting.setDescription(description);
            }
            
            log.info("---saveCustomerRecordSetting-appCustomerRecordSetting-"+appCustomerRecordSetting);
            
            appCustomerRecordSetting = appCustomerRecordSettingRepository.saveAndFlush(appCustomerRecordSetting);
        }

        return appCustomerRecordSetting;
    }
    
    /**
     * @throws Exception  
    * @Title: updateCustomerRecordSetting 
    * @Description: TODO(修改记录项) 
    * @param appId
    * @param name
    * @param recordId
    * @param index
    * @param description
    * @return 设定文件 
    * @return AppCustomerRecordSetting    返回类型 
    * @throws 
    */
    public  AppCustomerRecordSetting updateCustomerRecordSetting (String appId, String name, String recordId, String index,
            String description) throws Exception {
        AppCustomerRecordSetting appCustomerRecordSetting = appCustomerRecordSettingRepository.findOne(recordId);

        if (appCustomerRecordSetting != null && appCustomerRecordSetting.getAppId().equals(appId)) {
            if (StringUtils.isNotBlank(name)) {
                boolean isReName = isReName(appId, name, null);

                if (isReName && !appCustomerRecordSetting.getName().equals(name)) {
                    throw new Exception("appCustomerRecordSetting is already existed.");
                }

                appCustomerRecordSetting.setName(name);
            }

            if (StringUtils.isNotBlank(index)) {
                appCustomerRecordSetting.setIndex(Integer.valueOf(index));
            }

            if (StringUtils.isNotBlank(description)) {
                appCustomerRecordSetting.setDescription(description);
            }
            
            appCustomerRecordSetting.setStatus(EnumConfig.Status.VALID_DATA);
            appCustomerRecordSetting = appCustomerRecordSettingRepository.saveAndFlush(appCustomerRecordSetting);
        } else {
            throw new Exception("appCustomerRecordSetting error data.");
        }

        return appCustomerRecordSetting;

    }
    
    /** 
    * @Title: deleteCustomerRecordSetting 
    * @Description: TODO(删除) 
    * @param appId
    * @param id
    * @return 设定文件 
    * @return boolean    返回类型 
    * @throws 
    */
    public boolean deleteCustomerRecordSetting(String appId, String id) {
        AppCustomerRecordSetting appCustomerRecordSetting = appCustomerRecordSettingRepository.findOne(id);
        boolean isDelete = false;
        
        if(appCustomerRecordSetting != null && appCustomerRecordSetting.getAppId().equals(appId)) {
            try {
                appCustomerRecordSetting.setStatus(0);
                appCustomerRecordSettingRepository.save(appCustomerRecordSetting);
                isDelete = true;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        
        return isDelete;
    }
}
