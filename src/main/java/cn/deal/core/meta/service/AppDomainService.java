package cn.deal.core.meta.service;

import cn.deal.component.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import cn.deal.component.RedisService;
import cn.deal.core.meta.domain.AppDomain;
import cn.deal.core.meta.repository.AppDomainRepository;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;


@Service
public class AppDomainService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AppDomainRepository appDomainRepository;

    @Autowired
    private RedisService redisService;

    @Value("${deal.domain}")
    private String KUICK_DOMAIN;

    @Cacheable(cacheNames = "appdomain", key = "'appid:'+#p0")
    public AppDomain getAppDomainByAppId(String appId) throws Exception {
        return appDomainRepository.getAppDomainByAppId(appId);
		/*String key = "appdomain:appid:" + appId;
		String appDomain = redisService.get(key);
		
		JsonUtil util = new JsonUtil();
		
		logger.info("read appDomain from cache, with key:{}, and value:{}", key, appDomain);
		
		if (StringUtils.isBlank(appDomain)) {
			AppDomain domain = appDomainRepository.getAppDomainByAppId(appId);
			if (domain != null) {
				redisService.setex(key, 3600 * 24, util.toJson(domain));
			} else {
				redisService.setex(key, 3600 * 24, "none");
			}
			
			return domain;
		} else {
			if ("none".equals(appDomain)) {
				return null;
			} else {
				return util.fromJson(appDomain, AppDomain.class);
			}
		}*/
    }

    /**
     * @param appId
     * @param domain
     * @return
     */
    @CachePut(cacheNames = "appdomain", key = "'appid:'+#appId")
    public AppDomain addAppDomainByAppId(String appId, String domain) {
        AppDomain appDomain = appDomainRepository.getAppDomainByAppId(appId);
        if (appDomain != null && appDomain.getStatus() == 1) {
            throw new BusinessException("has_been_exists", "域名已配置并启用");
        }
        if (appDomain == null) {
            appDomain = new AppDomain();
            appDomain.setCreatedAt(new Date());
        }
        appDomain.setAppId(appId);
        appDomain.setDomain(domain);
        appDomain.setValid(0);
        appDomain.setValidResult("");
        appDomain.setFailCode("");
        appDomain.setStatus(0);
        return appDomainRepository.save(appDomain);
    }

    /**
     * @param appId
     * @return
     */
    @CachePut(cacheNames = "appdomain", key = "'appid:'+#appId")
    public AppDomain checkAppDomainByAppId(String appId) {
        AppDomain appDomain = appDomainRepository.getAppDomainByAppId(appId);
        if (appDomain == null) {
            throw new BusinessException("not_config", "未配置域名");
        }
        boolean equals;
        try {
            String hostId = InetAddress.getByName(KUICK_DOMAIN).getHostAddress();
            String appHostId = InetAddress.getByName(appDomain.getDomain()).getHostAddress();
            equals = hostId.equals(appHostId);
        } catch (UnknownHostException e) {
            logger.error("domain convert IP occur exception", e);
            throw new BusinessException("invalide_domain", "无效的域名");
        }
        appDomain.setValid(equals ? 1 : 0);
        appDomain.setValidResult(equals ? "域名校验通过！" : "域名校验不匹配");
        appDomain.setFailCode(equals ? "" : "not_match");
        return appDomainRepository.save(appDomain);
    }

    /**
     * @param appId
     * @param status
     * @return
     */
    @CacheEvict(cacheNames = "appdomain", key = "'appid:'+#appId")
    public AppDomain changeStatusByAppId(String appId, int status) {
        AppDomain appDomain;
        if (status == 1) {
            appDomain = checkAppDomainByAppId(appId);
            if (appDomain.getValid() == 0) {
                throw new BusinessException(appDomain.getFailCode(), appDomain.getValidResult());
            }
        } else {
            appDomain = appDomainRepository.getAppDomainByAppId(appId);
        }
        appDomain.setStatus(status);
        return appDomainRepository.save(appDomain);
    }
}

