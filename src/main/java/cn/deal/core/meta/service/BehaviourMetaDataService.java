package cn.deal.core.meta.service;

import cn.deal.component.utils.JsonFileUtil;
import cn.deal.component.utils.JsonUtil;
import cn.deal.core.meta.dao.BehaviourMetaDataDao;
import cn.deal.core.meta.domain.BehaviourMetaData;
import cn.deal.core.meta.repository.BehaviourMetaDataRepository;

import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class BehaviourMetaDataService {

    private static final Log log = LogFactory.getLog(BehaviourMetaDataService.class);

    @Autowired
    private BehaviourMetaDataRepository behaviourMetaDataRepository;

    @Autowired
    private BehaviourMetaDataDao behaviourMetaDataDao;

    @Autowired
    private BehaviourService behaviourService;

    @Autowired
    private JsonFileUtil jsonFileUtil;

    @Value(value = "classpath:metas/system_behaviour_meta_data.cfg.json")
    private Resource systemBehaviourMetaDataConfig;
    
    
    /**
     * 获取系统行为元数据配置信息
     * @return
     */
    public String getSystemBehaviourMetaDataConfig(){
        return jsonFileUtil.getJsonFromClasspathFile(systemBehaviourMetaDataConfig);
    }
    
    /**
     * 配置行为属性元数据
     * @param appId 项目id
     * @param action 行为Action
     * @param name 行为属性名
     * @param type 属性类型
     * @param title 属性标题
     * @param index 属性索引
     * @param defaultValue 默认值
     * @param optionValues 选项值
     * @param readonly 是否只读
     * @return 行为属性元数据值
     */
    public BehaviourMetaData createOrUpdateBehaviourMetaData(String appId, String action, String name, String type,
                                                             String title, Integer index, String defaultValue,
                                                             String optionValues, Integer readonly) {
        log.info(" appId: " + appId + ", action: " + action + ", name: " + name);
        BehaviourMetaData metaData = behaviourMetaDataRepository.getBehaviourMetaDataByAppIdAndActionAndName(appId,
                action, name);
        if(metaData != null){
            if(StringUtils.isNotBlank(type)){
                metaData.setType(type);
            }
            if(StringUtils.isNotBlank(title)){
                metaData.setTitle(title);
            }
            if(index != null){
                metaData.setIndex(index);
            }
            if(StringUtils.isNotBlank(defaultValue)){
                metaData.setDefaultValue(defaultValue);
            }
            if(StringUtils.isNotBlank(optionValues)){
                metaData.setOptionValues(optionValues);
            }
            if(readonly != null){
                metaData.setReadonly(readonly);
            }
            metaData.setUpdatedAt(new Date());
        } else {
            metaData = new BehaviourMetaData(appId, action, name, type, title, index, defaultValue, optionValues, readonly);
        }
        behaviourMetaDataRepository.saveAndFlush(metaData);
        log.info(" createOrUpdateBehaviourMetaData: " + behaviourMetaDataRepository.toString());
        return metaData;
    }

    public List<BehaviourMetaData> getBehaviourMetaDataList(String appId, String action, Integer startIndex, Integer count) {
        List<BehaviourMetaData> behaviourMetaDatas = null;
        boolean isSystemAction = behaviourService.isSystemAction(action);
        if(isSystemAction){
            behaviourMetaDatas = getSystemBehaviourMetaData(action);
        } else {
            behaviourMetaDatas = behaviourMetaDataDao.getBehaviourMetaDataList(appId, action, startIndex, count);
        }
        return behaviourMetaDatas;
    }

    public List<BehaviourMetaData> getSystemBehaviourMetaData(String action){
        String systemBehaviourMetaConfig = getSystemBehaviourMetaDataConfig();
        JSONArray jsonArray = new JSONArray(systemBehaviourMetaConfig);
        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject jsonObj = jsonArray.getJSONObject(i);
            log.info(" behaviourMetaDataJsonObj: " + jsonObj.toString());
            if(jsonObj.has("action")){
                String behaviourAction = jsonObj.getString("action");
                if(StringUtils.equals(behaviourAction, action)){
                    String metaDataJson = jsonObj.getJSONArray("metaData").toString();
                    Type dataType = new TypeToken<ArrayList<BehaviourMetaData>>(){}.getType();
                    return (ArrayList<BehaviourMetaData>) JsonUtil.parseJson(metaDataJson, dataType);
                }
            }
        }
        return null;
    }
}
