package cn.deal.core.meta.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import cn.deal.component.utils.JsonFileUtil;
import cn.deal.component.utils.JsonUtil;
import cn.deal.core.meta.dao.BehaviourDao;
import cn.deal.core.meta.dao.BehaviourMetaDataDao;
import cn.deal.core.meta.domain.Behaviour;
import cn.deal.core.meta.repository.BehaviourRepository;


@Service
public class BehaviourService {

    private static final Log log = LogFactory.getLog(BehaviourService.class);

    @Autowired
    private BehaviourRepository behaviourRepository;

    @Autowired
    private BehaviourDao behaviourDao;

    @Autowired
    private BehaviourMetaDataDao behaviourMetaDataDao;

    @Autowired
    private JsonFileUtil jsonFileUtil;

    @Value(value = "classpath:metas/system_behaviour.cfg.json")
    private Resource systemBehaviourConfig;

    /**
     * 获取系统行为配置信息
     * @return
     */
    public String getSystemBehaviourConfig(){
        return jsonFileUtil.getJsonFromClasspathFile(systemBehaviourConfig);
    }


    
    /**
     * 创建或修改行为信息
     * @param appId 项目id
     * @param action 行为Action
     * @param type 行为类型，1：系统行为，2：自定义行为
     * @param name 行为名称
     * @param description 行为描述
     * @return 行为信息
     */
    public Behaviour createOrUpdateBehaviour(String appId, String action, Integer type, String name, String description) {

        if(type == null){
            type = Behaviour.Type.CUSTOM.getVal();
        }
        log.info(" type: " + type + ", name: " + name + ", description: " + description);
        Behaviour behaviour = behaviourRepository.getBehaviourByAppIdAndTypeAndAction(appId, type, action);
        if(behaviour != null){
            if(StringUtils.isNotBlank(name)){
                behaviour.setName(name);
            }
            if(StringUtils.isNotBlank(description)){
                behaviour.setDescription(description);
            }
        } else {
            behaviour = new Behaviour(appId, type, action, name, description);
        }
        behaviourRepository.saveAndFlush(behaviour);

        return behaviour;
    }

    /**
     * 获取项目配置的行为信息
     * @param appId 项目id
     * @param type 行为类型，1：系统行为，2：自定义行为
     * @param startIndex 开始索引
     * @param count 记录条数
     * @return 行为信息列表
     */
    public List<Behaviour> getBehavioursByType(String appId, Integer type, Integer withMetaCount, Integer startIndex,
                                               Integer count) {

        List<Behaviour> behaviours = new ArrayList<>();
        if(type != null || Behaviour.Type.SYSTEM.getVal().equals(type) || type < 0){
            behaviours.addAll(getSystemBehaviours());
        }

        if(type == null || type < 0 || Behaviour.Type.CUSTOM.equals(type)){
            type = Behaviour.Type.CUSTOM.getVal();
            List<Behaviour> customerBes = behaviourDao.getBehavioursByType(appId, type, startIndex, count);
            behaviours.addAll(customerBes);
        }
        if(withMetaCount != null && withMetaCount == 1){
            for(int i = 0; i < behaviours.size(); i++){
                Behaviour behaviour = behaviours.get(i);
                String action = behaviour.getAction();
                int metaCount = behaviourMetaDataDao.getConfigMetaDataCount(appId, action);
                behaviour.setMetaCount(metaCount);
            }
        }
        return behaviours;
    }

    private List<Behaviour> getSystemBehaviours(){
        String systemBehaviourConfig = getSystemBehaviourConfig();
        Type dataType = new TypeToken<ArrayList<Behaviour>>(){}.getType();
        return (ArrayList<Behaviour>) JsonUtil.parseJson(systemBehaviourConfig, dataType);
    }

    public boolean isSystemAction(String action){
        boolean isSystemAction = false;
        List<Behaviour> behaviours = getSystemBehaviours();
        for(int i = 0; i < behaviours.size(); i++){
            Behaviour behaviour = behaviours.get(i);
            if(StringUtils.equals(behaviour.getAction(), action)){
                isSystemAction = true;
                break;
            }
        }
        return isSystemAction;
    }
}
