package cn.deal.component.kuick;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;

import cn.deal.component.config.ServiceConfig;
import cn.deal.component.domain.ResponseWrapper;
import cn.deal.component.kuick.domain.KuickUser;
import cn.deal.component.utils.JsonUtil;


@Service
@CacheConfig(cacheNames = "kuickuser", cacheManager = "redis")
public class KuickuserUserService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private ServiceConfig serviceConfig;

	public KuickUser getUserById(String kuickUserId,String accessToken) {
		String url = serviceConfig.getKuickApiBaseUrl()+ "user/getUserInfoByUserId?user_id={kuickUserId}&access_token="+accessToken;
		log.info("kuick user url: " + url);

		KuickUser user = null;
		String jsonStr = restTemplate.getForObject(url, String.class, kuickUserId);
		
		if (StringUtils.isNotBlank(jsonStr)) {
			log.info("kuick user: " + jsonStr);
			JSONObject jsonObj = new JSONObject(jsonStr);
			user = (KuickUser) JsonUtil.JSONReturnObj(jsonObj, KuickUser.class);
		}
		
		return user;
	}

	@Cacheable(cacheNames = "kuickuser", key = "'kuickuser:getUserById:' + #p0")
	public KuickUser getUserById(int kuickUserId) {
		String url = serviceConfig.getKuickUserApiBaseUrl() + "api/v1.0/user/getUserInfoByUserId?user_id={kuickUserId}";
		
		KuickUser user = null;
		String jsonStr = restTemplate.getForObject(url, String.class, kuickUserId);
		
		if (StringUtils.isNotBlank(jsonStr)) {
			log.info("kuick user: " + jsonStr);
			JSONObject jsonObj = new JSONObject(jsonStr);
			user = (KuickUser) JsonUtil.JSONReturnObj(jsonObj, KuickUser.class);
		}
		
		return user;
	}

	@Cacheable(cacheNames = "kuickuser", key = "'kuickuser:getUserById:' + #p0")
	public KuickUser getUserById(String kuickUserId) {
		String url = serviceConfig.getKuickUserApiBaseUrl() + "api/v1.0/user/getUserInfoByUserId?user_id={kuickUserId}";
		log.info("getUserById.url: " + url + ", kuickUserId: " + kuickUserId);

		KuickUser user = null;
		String jsonStr = restTemplate.getForObject(url, String.class, kuickUserId);

		if (StringUtils.isNotBlank(jsonStr)) {
			log.info("kuick user: " + jsonStr);
			JSONObject jsonObj = new JSONObject(jsonStr);
			String userJson = JsonUtil.getReturnJson(jsonObj);
			TypeReference<KuickUser> type = new TypeReference<KuickUser>() {};
			user = JsonUtil.fromJson(userJson, type);
		}

		return user;
	}


	public List<KuickUser> getUsersByIds(List<String> kuickUserIds,int isSimple) {
		String url = serviceConfig.getKuickUserApiBaseUrl() + "api/v1.1/users?user_ids={userIds}&is_simple={isSimple}";
		log.info("getUsersByIds.url: {}, {}, {}", url, kuickUserIds, isSimple);
		
		List<KuickUser> users = new ArrayList<>();
		if (kuickUserIds != null && kuickUserIds.size()>0) {
			String userIds = cn.deal.component.utils.StringUtils.listToString(kuickUserIds);
			
			String jsonStr = restTemplate.getForObject(url, String.class, userIds, isSimple);
			
			if (StringUtils.isNotBlank(jsonStr)) {
				log.info("kuick user: " + jsonStr);
				ResponseWrapper<List<KuickUser>> response = JsonUtil.jsonToObject(jsonStr, new TypeReference<ResponseWrapper<List<KuickUser>>>() {});
				users = response.getData();
			}
		}
		
		return users;
	}
}
