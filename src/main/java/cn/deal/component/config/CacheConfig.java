package cn.deal.component.config;

import com.google.common.collect.Maps;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

@EnableCaching
@Configuration
public class CacheConfig {

    @Bean("local")
    public CacheManager localCacheManager() {
        EhCacheManagerFactoryBean bean = new EhCacheManagerFactoryBean();

        bean.setConfigLocation(new ClassPathResource("ehcache.xml"));
        bean.setShared(true);

        return new EhCacheCacheManager(bean.getObject());
    }

    @Primary
    @Bean("redis")
    public CacheManager redisCacheManager(RedisTemplate<String, Object> redisTemplate) {
        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
        // 默认8秒过期
        cacheManager.setDefaultExpiration(8);
        Map<String, Long> expire = Maps.newHashMap();
        expire.put("license", 60L * 60); // 1小时过期
        expire.put("appdomain", 60L * 60 * 24); // 1天过期
        expire.put("department", 60L * 60); // 1小时过期
        expire.put("appmember", 60L * 60); // 1小时过期
        expire.put("customermeta", 60L * 60 * 24); //1天过期
        expire.put("dealuser", 60L * 60); //1天过期
        expire.put("kuickuser", 60L * 60 * 24); //1天过期

        cacheManager.setExpires(expire);
        return cacheManager;
    }

}