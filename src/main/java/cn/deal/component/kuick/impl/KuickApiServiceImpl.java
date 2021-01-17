package cn.deal.component.kuick.impl;

import cn.deal.component.RedisService;
import cn.deal.component.config.ServiceConfig;
import cn.deal.component.kuick.KuickApiService;
import cn.deal.component.utils.JsonUtil;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class KuickApiServiceImpl implements KuickApiService {

    private static final Logger log = Logger.getLogger(KuickApiServiceImpl.class);

    @Autowired
    private ServiceConfig serviceConfig;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Map clientToken() throws Exception {
        String key = "kuick_client_credentials:" + serviceConfig.getClientId() + ":token";
        String value = redisService.get(key);

        Map map = null;

        if (StringUtils.isNotBlank(value)) {
            map = JsonUtil.fromJson(value, Map.class);
        } else {
            String url = serviceConfig.getAccessTokenURL();
            log.info("url -> " + url);

            MultiValueMap<String, Object> param = new LinkedMultiValueMap<String, Object>();
            param.add("clientId", serviceConfig.getClientId());
            param.add("clientSecret", serviceConfig.getClientSecret());
            param.add("grant_type", serviceConfig.getGrantTypeClientCredentials());

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");
            httpHeaders.add("Authorization", new String("Basic " + Base64.encodeBase64String((serviceConfig.getClientId() + ':' + serviceConfig.getClientSecret()).getBytes())));

            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<MultiValueMap<String, Object>>(param, httpHeaders);

            map = restTemplate.postForObject(url, entity, Map.class);

            int expire = Integer.parseInt(map.get("expires_in").toString()) / 2 / 1000;
            redisService.setex(key, expire, JsonUtil.toJson(map));
        }

        log.info("clientToken.map -> " + map);
        return map;
    }

}
