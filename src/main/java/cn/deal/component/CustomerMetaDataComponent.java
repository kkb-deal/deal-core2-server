package cn.deal.component;

import cn.deal.component.domain.CustomerMetaDataItem;
import cn.deal.component.utils.JsonUtil;

import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


@Component
public class CustomerMetaDataComponent {

    private Logger logger = LoggerFactory.getLogger(CustomerMetaDataComponent.class);

    @Value("${deal.api.baseurl}")
    private String dealApiBaseURL;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 获取客户元数据列表
     * @param appId
     * @return
     */
    public List<CustomerMetaDataItem> getCustomerMetaDataItems(String appId){
        List<CustomerMetaDataItem> customerMetaDataList = new ArrayList<CustomerMetaDataItem>();
        try{
            String apiURL = dealApiBaseURL + "metadata/customer?app_id=" + appId;
            logger.info("getCustomerMetaDataApiURL: " + apiURL);
            String result = restTemplate.getForObject(apiURL, String.class);
            logger.warn("getCustomerMetaDataApiResult: " + result);

            if(StringUtils.isNotBlank(result)){
                JSONObject res = new JSONObject(result);
                Type type = new TypeToken<ArrayList<CustomerMetaDataItem>>(){}.getType();
                if (res.has("status")) {
                    int status = res.getInt("status");
                    if (status == 1 && res.has("data")) {
                        String dataResult = res.get("data").toString();
                        customerMetaDataList = (List<CustomerMetaDataItem>)JsonUtil.parseJson(dataResult, type);
                    }
                }
            }
        } catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        return customerMetaDataList;
    }
}
