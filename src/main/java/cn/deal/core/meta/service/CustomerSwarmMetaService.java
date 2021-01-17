package cn.deal.core.meta.service;

import cn.deal.core.meta.domain.CustomerSwarmMeta;
import cn.deal.core.meta.repository.CustomerSwarmMetaRepository;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@CacheConfig(cacheManager = "redis", cacheNames = "customerswarmmeta")
public class CustomerSwarmMetaService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerSwarmMetaService.class);

    @Autowired
    private CustomerSwarmMetaRepository customerSwarmMetaRepository;


    /**
     * 根据appid查询前count条记录
     * @param appId
     * @return list 查询到的客户分群元数据信息
     */
    @Cacheable(cacheNames = "customerswarmmeta", key = "'customerswarmmeta:appid:' + #p0")
    public List<CustomerSwarmMeta> findByAppId(String appId) {
        List<CustomerSwarmMeta> list = customerSwarmMetaRepository.findByAppIdAndStatusTrueAndVisibleTrue(appId);
        logger.info("根据appid:{}获取客户分群元数据信息:{}", appId, new Gson().toJson(list));
        return list;
    }

    @CacheEvict(cacheNames = "customerswarmmeta", key = "'customerswarmmeta:appid:' + #p0")
    public void refreshCustomerSwarmMetaCacheByAppId(String appId) {
        logger.info("根据appid:{}清除客户分群元数据信息", appId);
    }

}
