package cn.deal.component;

import cn.deal.component.domain.Response;
import cn.deal.component.domain.ResponseWrapper;
import cn.deal.component.domain.User;
import cn.deal.component.kuick.domain.MemberImportVO;
import cn.deal.component.kuick.domain.ResponseVO;
import cn.deal.component.utils.JsonUtil;
import cn.deal.component.utils.MD5Util;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.reflect.TypeToken;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


@Component
public class UserComponent{

    private Logger log = LoggerFactory.getLogger(UserComponent.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${kuick.api.baseurl}")
    private String userServiceApiBaseurl;

    @Value("${kuickuser.api.baseurl}")
    private String kuickUserApiBaseurl;

    @Value("${oauth2.clientId}")
    private String clientId;

    @Value("${oauth2.clientSecret}")
    private String clientSecret;

    @SuppressWarnings("unchecked")
    public User getUserById(int id) {
        String url = kuickUserApiBaseurl + "api/v1.0/user/getUserInfoByUserId?user_id={user_id}";
        log.info("getUserById.url: {}", url);
        String json = restTemplate.getForObject(url, String.class, id);
        log.info("getUserById.json: {}", json);
        Type type = new TypeToken<ResponseWrapper<User>>() {
        }.getType();
        ResponseWrapper<User> result = (ResponseWrapper<User>) JsonUtil.parseJson(json, type);
        log.info("getUserById.result: {}", result);
        return result.getData();
    }

    /** 
    * @Title: getUserByToken 
    * @Description: TODO(通过token获取用户) 
    * @param accessToken
    * @return 设定文件 
    * @return User    返回类型 
    * @throws 
    */
    public User getUserByToken( String accessToken) {
        String url = "user/getUserInfoByAccessToken?access_token={accessToken}";
        if(StringUtils.isBlank(accessToken)){
            return null;
        }
        
        User bean = null;
        StringBuilder stringBuilder = new StringBuilder(userServiceApiBaseurl).append(url);
        RestTemplate restTemplate = new RestTemplate();
        String jsonStr = restTemplate.getForObject(stringBuilder.toString(), String.class, accessToken);
        if(StringUtils.isNotBlank(jsonStr)){
            JSONObject jsonObj = JSONObject.fromObject(jsonStr);
            if(jsonObj!=null){
                bean = JSONReturnObj(jsonObj);
            }
        }
        return bean;
    }

    private static User JSONReturnObj(JSONObject res) {
        User bean = null;
        if (res.has("status")) {
            int status = res.getInt("status");
            if (status == 1 && res.has("data")) {
                JSONObject obj1 = res.getJSONObject("data");
                if (obj1 != null) {
                    bean= new User();
                    bean.setName(obj1.getString("name"));
                    bean.setId(obj1.getInt("id"));
                }
            }
        }
        return bean;
    }
    
    /** 
    * @Title: getUsersByName 
    * @Description: TODO(根据名称查询账号列表) 
    * @param name
    * @return 设定文件 
    * @return List<User>    返回类型 
    * @throws 
    */
    public List<User> getUsersByName(String name,Integer startIndex, Integer count){
        String url = kuickUserApiBaseurl+"api/v1.1/users?keyword=" + name;
        log.info("getUsersByName url ="+url);
        
        if(startIndex !=null || count != null ){
            url +="&start_index="+ startIndex +"&count="+count;
        }
        
        
        RestTemplate restTemplate = new RestTemplate();
        String jsonStr = restTemplate.getForObject(url,String.class);
        log.info("getUsersByName jsonStr ="+jsonStr);
        Type type = new TypeToken<ArrayList<User>>() {}.getType();
        
        List<User> users =new ArrayList<>();
        
        if (StringUtils.isNotBlank(jsonStr)) {
            JSONObject jsonObj = JSONObject.fromObject(jsonStr);
            if (jsonObj != null) {
                if (jsonObj.has("status")) {
                    int status = jsonObj.getInt("status");
                    if (status == 1 && jsonObj.has("data")) {
                        users = (List<User>) cn.deal.component.utils.JsonUtil.parseJson(jsonObj.getString("data"), type);
                    }
                }
            }}
        
        log.info("getUsersByName users ="+users);
        return users;
    }


    public List<User> getUsersByIds(List<String> kuickUserIds, int isSimple) {
        String url = kuickUserApiBaseurl + "api/v1.1/users?user_ids={userIds}&is_simple={isSimple}";

        List<User> users = new ArrayList<>();
        if (kuickUserIds != null && kuickUserIds.size()>0) {
            String userIds = cn.deal.component.utils.StringUtils.listToString(kuickUserIds);

            String jsonStr = restTemplate.getForObject(url, String.class, userIds, isSimple);

            if (StringUtils.isNotBlank(jsonStr)) {
                log.info("kuick user: " + jsonStr);
                ResponseWrapper<List<User>> response = JsonUtil.jsonToObject(jsonStr, new TypeReference<ResponseWrapper<List<User>>>() {});
                users = response.getData();
            }
        }

        return users;
    }

    public User createUser(String name, String phone, String email) {
        String url = kuickUserApiBaseurl + "api/v1.0/users";
        log.info("createUser.params: {}, {}", phone, email);

        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, Object> data = new LinkedMultiValueMap<>();
        data.add("nickname", name);
        data.add("phoneNum", phone);
        data.add("email", email);
        data.add("clientId", clientId);
        data.add("phoneSign", buildPhoneSign(phone));
        data.add("channel", 10);
        data.add("unionid", "skip_weixin_bind");
        data.add("password", "kkb" + StringUtils.substring(phone, phone.length() - 6));

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(data, headers);
        String result = restTemplate.postForObject(url, entity, String.class);
        log.info("createUser.result: {}", result);

        TypeReference<ResponseVO<User>> type = new TypeReference<ResponseVO<User>>() {};
        ResponseVO<User> response = JsonUtil.fromJson(result, type);
        Assert.notNull(response, "response is null");
        Assert.isTrue(response.getStatus() == ResponseVO.Status.OK.getVal(), "error in create user - error");

        return response.getData();
    }

    private String buildPhoneSign(String phone) {
        return MD5Util.getMD5(phone + ":" + clientSecret);
    }

    public String bindEmail(User user, String email) {
        String url = kuickUserApiBaseurl + "api/v1.0/user/bindEmail";
        log.info("bindEmail.url: {}", url, email);

        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, Object> data = new LinkedMultiValueMap<>();
        data.add("user_id", user.getId());
        data.add("email", email);

        return restTemplate.postForObject(url, new HttpEntity<>(data, headers), String.class);
    }

    public String editUser(User user, String name) {
        String url = kuickUserApiBaseurl + "api/v1.0/user/update";
        log.info("editUser.url: {}", url, name);

        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, Object> data = new LinkedMultiValueMap<>();
        data.add("user_id", user.getId());
        data.add("name", name);
        user.setName(name);

        return restTemplate.postForObject(url, new HttpEntity<>(data, headers), String.class);
    }

    public String updateStatus(User user) {
        String url = kuickUserApiBaseurl + "api/v1.0/user/" + user.getId();
        log.info("updateStatus.url: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> data = new LinkedMultiValueMap<>();
        data.add("status", String.valueOf(User.Status.OK.getVal()));
        user.setStatus(User.Status.OK.getVal());
        HttpEntity<MultiValueMap<String, Object>> requestEntity  = new HttpEntity<>(data, headers);

        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);
        return result != null ? result.getBody() : null;
    }

    /**
     * 用手机号查询用户,查不到就创建新的
     * @param memberImportVO
     * @return
     */
    public User findOrCreateUser(MemberImportVO memberImportVO) {
        String url = kuickUserApiBaseurl + "api/v1.0/user/findByUserName?user_name={userName}";
        log.info("findOrCreateUser.url: {}, {}", url, memberImportVO);

        String result = restTemplate.getForObject(url, String.class, memberImportVO.getPhone());
        log.info("findOrCreateUser.result: {}", result);
        Assert.notNull(result, "result is null");

        TypeReference<Response<User>> type = new TypeReference<Response<User>>() {};
        Response<User> response = JsonUtil.fromJson(result, type);
        Assert.notNull(response, "response is null");

        if (response.getStatus() == Response.Status.OK.getVal()) {
            return response.getData();
        } else {
            return createUser(memberImportVO.getName(), memberImportVO.getPhone(), memberImportVO.getEmail());
        }
    }

}
