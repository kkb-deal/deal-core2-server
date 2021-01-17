package cn.deal.component;

import cn.deal.component.kuick.domain.KuickUser;
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
public class AdminSalesComponent {

    private Logger logger = LoggerFactory.getLogger(AdminSalesComponent.class);

    @Value("${deal.api.baseurl}")
    private String dealApiBaseURL;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 获取用户所管理的销售列表
     * @param appId
     * @param kuickUserId
     * @return
     */
    public List<KuickUser> getAdminSales(String appId, String kuickUserId){
        List<KuickUser> kuickUsers = new ArrayList<>();
        try {
            String url = dealApiBaseURL + "app/" + appId + "/" + kuickUserId + "/admin-sales";
            logger.info("getAdminSalesApiURL: " + url);
            String result = restTemplate.getForObject(url, String.class);
            logger.info("getAdminSalesApiResult: " + result);
            if(StringUtils.isNotBlank(result)){
                JSONObject res = new JSONObject(result);
                Type type = new TypeToken<ArrayList<KuickUser>>(){}.getType();
                if (res.has("status")) {
                    int status = res.getInt("status");
                    if (status == 1 && res.has("data")) {
                        String dataResult = res.get("data").toString();
                        kuickUsers = (List<KuickUser>) JsonUtil.parseJson(dataResult, type);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return kuickUsers;
    }
}
