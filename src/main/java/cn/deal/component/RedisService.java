package cn.deal.component;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@ConfigurationProperties(prefix = "redis")
public class RedisService {

	@Autowired
	private StringRedisTemplate redisTemplate;
	
	/** 
	* @Title: setex 
	* @Description: TODO(设置缓存) 
	* @param key
	* @param seconds
	* @param value 设定文件 
	* @return void    返回类型 
	* @throws 
	*/
	public void setex(String key, int seconds,String value){
		if (seconds > 0) {
			redisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
		} else {
			this.delete(key);
		}
	}

	/**
	 * 配置过期
	 * 
	 * @param key
	 * @param sec
	 */
	public void expire(String key, int sec) {
		redisTemplate.expire(key, sec, TimeUnit.SECONDS);
	}

	/** 
	* @Title: get 
	* @Description: TODO(获取) 
	* @param key
	* @return 设定文件 
	* @return String    返回类型 
	* @throws 
	*/
	public String get(String key){
		return redisTemplate.opsForValue().get(key);
	}
	
	/** 
	* @Title: delete 
	* @Description: TODO(删除) 
	* @param key 设定文件 
	* @return void    返回类型 
	* @throws 
	*/
	public void delete(String key){
		redisTemplate.delete(key);
	}
	
	/** 
	 * @Title: exists 
	 * @Description: TODO(key是否存在) 
	 * @param key 设定文件 
	 * @return void    返回类型 
	 * @throws 
	 */
	public boolean exists(String key){
		return redisTemplate.getExpire(key) > 0;
	}
	
	/** 
	 * @Title: incr 
	 * @Description: TODO(增加) 
	 * @param key 设定文件 
	 * @param increment 增加的值
	 * @return void    返回类型 
	 * @throws 
	 */
	public long incr(String key, long increment){
		return redisTemplate.opsForValue().increment(key, increment);
	}
	
	/** 
	 * @Title: decr 
	 * @Description: TODO(增加) 
	 * @param key 设定文件 
	 * @param increment 增加的值
	 * @return void    返回类型 
	 * @throws 
	 */
	public long decr(String key, long increment){
		return redisTemplate.opsForValue().increment(key, 0 - increment);
	}
	
}
