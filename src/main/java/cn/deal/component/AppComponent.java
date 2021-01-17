package cn.deal.component;

import cn.deal.component.domain.App;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class AppComponent {
    private static final String APP_SERVICE_URL = "app/{appId}?access_token={accessToken}";
    @Value("${deal.api.baseurl}")
    private String dealApiApiBaseurl;

    public App getAppById(String id, String accessToken) {
        App bean = null;
        if(StringUtils.isBlank(id)){
            return null;
        }
        if(StringUtils.isBlank(accessToken)){
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder(dealApiApiBaseurl).append(APP_SERVICE_URL);
        RestTemplate restTemplate = new RestTemplate();
        String jsonStr = restTemplate.getForObject(stringBuilder.toString(), String.class, id, accessToken);
        if(StringUtils.isNotBlank(jsonStr)){
            JSONObject jsonObj = JSONObject.fromObject(jsonStr);
            if(jsonObj!=null){
                bean = JSONReturnObj(jsonObj);
            }
        }
        return bean;
    }

    private static App JSONReturnObj(JSONObject res) {
        App bean = null;
        if (res.has("status")) {
            int status = res.getInt("status");
            if (status == 1 && res.has("data")) {
                JSONObject obj1 = res.getJSONObject("data");
                if (obj1 != null) {
                    bean = new App();
                    bean.setName(obj1.getString("name"));
                    bean.setId(obj1.getString("id"));
                }
            }
        }
        return bean;
    }

}
