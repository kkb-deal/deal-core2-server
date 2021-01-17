package cn.deal.component;

import cn.deal.component.domain.filter.CustomerFilter;
import cn.deal.component.utils.JsonUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;


@Component
public class CustomerFilterComponent {

    private Logger logger = LoggerFactory.getLogger(CustomerFilterComponent.class);

    @Value("${deal.filter.server.inner.url}")
    private String dealFilterServerInnerURL;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 根据filterId获取筛选条件
     * @param appId
     * @param filterId
     * @return
     * @throws IOException
     */
    public CustomerFilter getCustomerFilterByFilterId(String appId, String filterId) throws IOException {
        String url = dealFilterServerInnerURL + "api/v1.0/app/" + appId + "/filter/" + filterId;
        logger.info("getFilterByFilterIdApiURL：{}", url);
        String result = null;
        try{
            result = restTemplate.getForObject(url, String.class);
            logger.info("getFilterByFilterIdResultFailed: {}", result);
        } catch (Exception e){
            logger.error("getFilterByFilterIdResultFailed: {}", e.getMessage());
            throw new RuntimeException("getCustomerFilterByFilterId failed");
        }
        if (StringUtils.isBlank(result)){
            throw new RuntimeException("can't get filter data");
        }
        CustomerFilter customerFilter = JsonUtil.fromJson(result, CustomerFilter.class);
        return customerFilter;
    }
}
