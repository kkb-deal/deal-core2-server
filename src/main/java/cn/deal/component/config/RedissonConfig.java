package cn.deal.component.config;

import java.util.concurrent.locks.ReadWriteLock;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.deal.component.utils.ReadWriteLockProvider;

@Configuration
public class RedissonConfig {

    @Value("${redisson.redis.host}")
    private String host;
    
    @Value("${redisson.redis.port}")
    private int port;
    
    @Value("${redisson.redis.password}")
    private String password;
    
    @Value("${redisson.redis.database}")
    private int database;
    
    @Value("${redisson.redis.pool.max-idle}")
    private int poolMaxIdle;
    
    @Value("${redisson.redis.pool.min-idle}")
    private int poolMinIdle;
    
    @Value("${redisson.redis.pool.max-wait}")
    private long poolMaxWait;
    
    @Value("${redisson.redis.pool.max-active}")
    private int poolMaxActive;
    
    @Bean
    public RedissonClient buildRedission(){
    	Config config = new Config();
		config.useSingleServer()
			  .setAddress("redis://" + host + ":" + port)
			  .setPassword(password)
			  .setDatabase(database)
			  .setConnectionPoolSize(poolMaxIdle)
			  .setConnectionMinimumIdleSize(poolMinIdle);
		
		return Redisson.create(config);
    }
    
    @Bean
    public ReadWriteLockProvider getRedissonLock(RedissonClient redissonClient) {
    	return new ReadWriteLockProvider() {
			@Override
			public ReadWriteLock getReadWriteLock(String key) {
				return redissonClient.getReadWriteLock(key);
			}
    		
    	};
    }
}
