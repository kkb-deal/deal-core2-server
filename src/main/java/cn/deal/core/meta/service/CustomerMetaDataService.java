package cn.deal.core.meta.service;

import cn.deal.component.exception.BusinessException;
import cn.deal.component.utils.JsonFileUtil;
import cn.deal.component.utils.JsonUtil;
import cn.deal.component.utils.ListToMapUtil;
import cn.deal.component.utils.ParamValidateUtil;
import cn.deal.core.license.domain.License;
import cn.deal.core.license.service.AppLicenseService;
import cn.deal.core.meta.domain.CustomerMetaData;
import cn.deal.core.meta.repository.CutomerMetaDataRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CustomerMetaDataService {
    private static final Logger log = LoggerFactory.getLogger(CustomerMetaDataService.class);

    private static final String PERSONAL = "personal";

    private static final String ULTIMATE = "ultimate";

    private static final String ENTERPRISE = "enterprise";
    private static final String JUKE = "juke";

    @Value(value = "classpath:metas/AppCustomerConfig.personal.js")
    private Resource personalAppCustomerConfig;

    @Value(value = "classpath:metas/AppCustomerConfig.enterprise.js")
    private Resource enterpriseAppCustomerConfig;

    @Value(value = "classpath:metas/AppCustomerConfig.ultimate.js")
    private Resource appCustomerConfig;
    
    
    @Autowired
    private JsonFileUtil jsonFileUtil;
    @Autowired
    private CutomerMetaDataRepository customerMetaDataRepository;

    private ConcurrentHashMap<String, List<CustomerMetaData>> customerMetaDataMap = new ConcurrentHashMap<>();

    @Autowired
    private AppLicenseService appLicenseService;

   
    public String getAppCustomerConfig() {
        return jsonFileUtil.getJsonFromClasspathFile(appCustomerConfig);
    }

    public String getPersonalAppCustomerConfig() {
        return jsonFileUtil.getJsonFromClasspathFile(personalAppCustomerConfig);
    }


    public String getEnterpriseAppCustomerConfig() {
        return jsonFileUtil.getJsonFromClasspathFile(enterpriseAppCustomerConfig);
    }
    
    
    /**
     * 添加扩展字段客户元数据
     * 
     * @param appId
     * @param name
     * @param type
     * @param unique
     * @param required
     * @param visiable
     * @param index
     * @param supportFilter
     * @param readonly
     * @param defaultValue
     * @param optionValues
     * @param title
     * @return
     * @throws Exception
     */
    @CacheEvict(cacheNames = "customermeta", key = "'customermeta:appid:'+#p0")
    public CustomerMetaData addExtensionCustomerMeta(String appId, String name, String type, boolean unique, boolean required, boolean visiable, Integer index,
            boolean supportFilter, Integer readonly, String defaultValue, String optionValues, String visibleInList, Integer indexInList, String widthInList, String title) throws Exception {
        String  editon = getEditionLicenseByAppId(appId);
        String key = convertEdition2CustomerMetaDataMapKey(editon);
        List<CustomerMetaData> datas = customerMetaDataMap.get(key);

        //如果没有传index的值，需要计算
        if (null == index){
            index = customerMetaDataRepository.findAppMaxIndex(appId);
            for (CustomerMetaData metaData : datas){
                if (null == index){
                    index = metaData.getIndex();
                } else {
                    index = Math.max(index, metaData.getIndex());
                }
            }
            index++;
        }

        //检查name是否重复
        CustomerMetaData metaDataByName = customerMetaDataRepository.findByAppIdAndName(appId, name);
        if (null != metaDataByName){
            throw new BusinessException("name_exists", "该名称已经存在");
        }
        for (CustomerMetaData metaData : datas){
            if (null != metaData.getName() && metaData.getName().equals(name)){
                throw new BusinessException("name_exists", "该名称已经存在");
            }
        }

        CustomerMetaData metaData = new CustomerMetaData(appId, name, type, title, true, unique,
                required, visiable, index, defaultValue, supportFilter, optionValues, readonly, visibleInList, indexInList, widthInList);
        metaData = customerMetaDataRepository.saveAndFlush(metaData);
        return metaData;
    }
    
    /**
     * 删除客户元数据
     * 
     * @param customerMetaId
     * @return
     */
    @CacheEvict(cacheNames = "customermeta", key = "'customermeta:appid:'+#p0")
    public boolean deleteExtensionCustomerMeta(String appId, String customerMetaId) {
        log.info("deleteExtensionCustomerMeta.appId: " + appId);
        CustomerMetaData metaData = customerMetaDataRepository.findOne(customerMetaId);
        return deleteCustomerMeta(customerMetaId, metaData);
    }

    @CacheEvict(cacheNames = "customermeta", key = "'customermeta:appid:'+#p0")
	public boolean deleteCustomerMeta(String appId, CustomerMetaData metaData) {
		if (null != metaData && metaData.getIsExt()){
            customerMetaDataRepository.delete(metaData.getId());
            return true;
        } else {
            throw new BusinessException("not_ext","不是扩展字段，不能删除");
        }
	}
    
    @PostConstruct
    public void customerMetaDatasInit() {

        String appCustomerConfig = getAppCustomerConfig();
        log.info("customerMetaDatasInit json:" + appCustomerConfig);
        List<CustomerMetaData> ultimateCustomerMetaDataList = loadCustomerMetaData(appCustomerConfig);
        ParamValidateUtil.validateCollection(ultimateCustomerMetaDataList, "初始化旗舰版客户字段失败:");
        customerMetaDataMap.put("ultimate", ultimateCustomerMetaDataList);

        String enterpriseAppCustomerConfig = getEnterpriseAppCustomerConfig();
        log.info("customerMetaDatasInit json:" + appCustomerConfig);
        List<CustomerMetaData> enterpriseCustomerMetaDataList = loadCustomerMetaData(enterpriseAppCustomerConfig);
        ParamValidateUtil.validateCollection(enterpriseCustomerMetaDataList, "9800客户字段失败:");
        customerMetaDataMap.put("enterprise", enterpriseCustomerMetaDataList);

        String personalCustomerConfig = getPersonalAppCustomerConfig();
        List<CustomerMetaData> personalCustomerMetaData = loadCustomerMetaData(personalCustomerConfig);
        ParamValidateUtil.validateCollection(personalCustomerMetaData, "初始化个人版客户字段失败:");
        customerMetaDataMap.put("personal", personalCustomerMetaData);
    }

    @SuppressWarnings("unchecked")
	private List<CustomerMetaData> loadCustomerMetaData(String customerMetaDataStr) {
        Type dataType = new TypeToken<List<CustomerMetaData>>() {}.getType();
        return (List<CustomerMetaData>) JsonUtil.parseJson(customerMetaDataStr, dataType);
    }

    /**
     * 获取客户元数据
     * 
     * @param appId
     * @return
     * @throws Exception
     */
    @Cacheable(cacheNames = "customermeta", key = "'customermeta:appid:'+#p0")
    public List<CustomerMetaData> getCustomerMetas(String appId) {
        log.info("根据appid获取客户元数据信息:{}", appId);
        List<CustomerMetaData> list = customerMetaDataRepository.getCustomerMetaDataByAppId(appId);
        String edition = getEditionLicenseByAppId(appId);
        String key = convertEdition2CustomerMetaDataMapKey(edition);
        log.info("service层查询获取元数据信息:{}", new Gson().toJson(list));

        List<CustomerMetaData> datas = customerMetaDataMap.get(key);
        List<CustomerMetaData> metas = new ArrayList<>();
        log.info("getCustomerMetaDatas datas:" + datas);
        Map<String, CustomerMetaData> map = ListToMapUtil.listToMap(list, "getName", CustomerMetaData.class);

        for (CustomerMetaData meta : datas) {
            if (list != null && list.size() > 0) {

                CustomerMetaData data = map.get(meta.getName());

                if (data == null) {
                    metas.add(meta);
                } else {
                    map.remove(meta.getName());
                    metas.add(data);
                }
            } else {
                metas = datas;
                break;
            }
        }

        for(Map.Entry<String, CustomerMetaData> entry : map.entrySet()){
            metas.add(entry.getValue());
        }

        log.info("getCustomerMetaDatas metas:" + metas);
        return metas;
    }

    private String getEditionLicenseByAppId(String appId) {
        License license = appLicenseService.getAppLimits(appId);
        String editon = "personal";
        
        if(license !=null ){
            editon = license.getEdition();
        }
        
        return editon;
    }

    public CustomerMetaData getCustomerMetaDataByAppIdAndName(String appId, String name) {
        List<CustomerMetaData> customerMetaDatas = customerMetaDataRepository.getCustomerMetaDataByNameAndAppId(name, appId);
        return DataAccessUtils.singleResult(customerMetaDatas);
    }

    @CacheEvict(cacheNames = "customermeta", key = "'customermeta:appid:'+#p0")
    public CustomerMetaData update(String appId, String name, String isExt, String unique, String required, String visiable,
                                   String index, String supportFilter, String readonly, String optionValues,
                                   String visibleInList, Integer indexInList, String widthInList, String title) throws Exception {

        List<CustomerMetaData> customermes = customerMetaDataRepository.getCustomerMetaDataByNameAndAppId(name, appId);
        CustomerMetaData customerMetaData;
        
        if (customermes != null && customermes.size() > 0) {
            customerMetaData = customermes.get(0);
            customerMetaData.setIndex(Integer.valueOf(index));
        } else {
           String edtion = getEditionLicenseByAppId(appId);
            String key = convertEdition2CustomerMetaDataMapKey( edtion );
            List<CustomerMetaData> list = customerMetaDataMap.get(key);
            
            Map<String, CustomerMetaData> map = ListToMapUtil.listToMap(list, "getName", CustomerMetaData.class);
            CustomerMetaData data = map.get(name);
            log.info("update ListToMapUtil  data result :" + data);
            
            customerMetaData = new CustomerMetaData(appId, name, data.getType(), data.getTitle(), data.getIsExt(),
                    data.getUnique(), data.getRequired(), data.getVisiable(), Integer.valueOf(index), data.getDefaultValue(),
                    data.getSupportFilter(), data.getOptionValues(), data.getReadonly(), data.getVisibleInList(), data.getIndexInList(), data.getWidthInList());
            
        }
        customerMetaData.setIndex(Integer.valueOf(index));

        if (StringUtils.isNotBlank(isExt)) {
            customerMetaData.setIsExt(Boolean.valueOf(isExt));
        }

        if (StringUtils.isNotBlank(unique)) {
            customerMetaData.setUnique(Boolean.valueOf(unique));
        }

        if (StringUtils.isNotBlank(required)) {
            customerMetaData.setRequired(Boolean.valueOf(required));
        }

        if (StringUtils.isNotBlank(visiable)) {
            customerMetaData.setVisiable(Boolean.valueOf(visiable));
        }

        if (StringUtils.isNotBlank(supportFilter)) {
            customerMetaData.setSupportFilter(Boolean.valueOf(supportFilter));
        }

        if (StringUtils.isNotBlank(readonly)) {
            customerMetaData.setReadonly(Integer.valueOf(readonly));
        }

        if (StringUtils.isNotBlank(optionValues)) {
            customerMetaData.setOptionValues(optionValues);
        }

        if (StringUtils.isNotBlank(title)) {
            customerMetaData.setTitle(title);
        }

        if (StringUtils.isNotBlank(visibleInList)) {
            customerMetaData.setVisibleInList(visibleInList);
        }

        if (indexInList != null) {
            customerMetaData.setIndexInList(indexInList);
        }

        if (StringUtils.isNotBlank(widthInList)) {
            customerMetaData.setWidthInList(widthInList);
        }

        return customerMetaDataRepository.saveAndFlush(customerMetaData);
    }

    private String convertEdition2CustomerMetaDataMapKey(String edition) {
        String key = PERSONAL;
        
        if (edition.indexOf(ULTIMATE) !=-1) {
            key = ULTIMATE;
        }else if (edition.indexOf(ENTERPRISE) !=-1){
            key = ENTERPRISE;
        }else if (edition.equals(JUKE)){
            key = ULTIMATE;
        }else if (PERSONAL.indexOf(edition) !=-1){
            key = PERSONAL;
        }else{
            key = PERSONAL;
        }
        
        return key;
    }
}
