package cn.deal.component;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;


@Component
public class AccessTokenComponent {

    private static final Logger logger = LoggerFactory.getLogger(AccessTokenComponent.class);

    @Value("${oauth2.clientId}")
    private String authClientId;

    @Value("${oauth2.clientSecret}")
    private String authClientSecret;

    @Value("${passport.api.baseurl}")
    private String passportBaseURL;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * @Title: getKuickCode
     * @Description: TODO(授权获取code)
     * @param kuickUserId
     * @return 设定文件
     * @return String    返回类型
     * @throws
     */
    public String getKuickCode(String kuickUserId) {
        String url = passportBaseURL + "kuickuser/oauth2/inner_authorization?client_id="
                + authClientId + "&response_type=code";

        logger.info("getKuickCodeApiURL: " + url);
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<String, Object>();
        formData.add("user_id", kuickUserId);
        formData.add("decision", 1);
        logger.info("getKuickCodeApiParams: " + formData,toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<MultiValueMap<String, Object>>(formData,
                headers);
        String jsonStr = restTemplate.postForObject(url, entity, String.class, formData);
        String code = "";
        logger.info("getKuickCodeApiResult: " + jsonStr);

        if (StringUtils.isNotEmpty(jsonStr)) {
            JSONObject jsonObj = new JSONObject(jsonStr);
            if (jsonObj.get("code") != null){
                String codeJ = jsonObj.get("code").toString();
                JSONObject json = new JSONObject(codeJ);
                if(json.has("code")){
                    code=json.get("code").toString();
                }
            }
        }
        logger.info("getKuickCode: " + code);
        return code;

    }

    public String getKuickLoginToken(String code) {
        String accessToken = "";
        try {
            String url = passportBaseURL + "kuickuser/oauth2/access_token";
            logger.info("getKuickLoginURL: " + url);
            String data = authClientId + ':' + authClientSecret;
            MultiValueMap<String, Object> formData = new LinkedMultiValueMap<String, Object>();
            formData.add("grant_type", "authorization_code");
            formData.add("code", code);
            logger.info("getKuickLoginParams: " + formData.toSingleValueMap());
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Basic " + Base64.getEncoder().encodeToString(data.getBytes("utf-8")));
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<MultiValueMap<String, Object>>(formData, headers);
            String jsonStr = restTemplate.postForObject(url, entity, String.class);

            logger.info("getKuickTokenResult: " + jsonStr);
            if (StringUtils.isNotEmpty(jsonStr)) {
                JSONObject jsonObj = new JSONObject(jsonStr);
                if(jsonObj.get("access_token") != null) {
                    accessToken = jsonObj.get("access_token").toString();
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return accessToken;
    }

    public String getAccessTokenByKuickUserId(String kuickUserId){
        String code = this.getKuickCode(kuickUserId);
        String accessToken = this.getKuickLoginToken(code);
        return accessToken;
    }
}
