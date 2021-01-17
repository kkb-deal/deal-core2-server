package cn.deal.component;

import cn.deal.component.utils.JsonUtil;
import cn.deal.core.customer.domain.Customer;

import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Component
public class DealCustomerSearchComponent {

    private final Logger logger = LoggerFactory.getLogger(getClass());

//    @Value("${deal.customersearch.baseurl}")
    private String dealCustomerSearchBaseurl;

    @Autowired
    private RestTemplate restTemplate;

    //TODO 完成调用deal-customer-search服务查询customer信息
    public List<Customer> searchCustomer(String appId, String filterId,  int startIndex, int count) {
        String url = dealCustomerSearchBaseurl + "api/v1.0//api/v1.0/app/{app_id}/screening/customers?" +
                "filter_id={filterId}&start_index={startIndex}&count={count}";
        logger.info(" search customer url: {}");
        logger.info(" search customer param, appId: {}, filterId: {}, startIndex: {}, count: {}",
                appId, filterId, startIndex, count);
        String result =
                restTemplate.getForObject(url, String.class, appId, filterId, startIndex, count);
        logger.info(" search customer result: {}", result);
        TypeReference<List<Customer>> type = new TypeReference<List<Customer>>() {};
        List<Customer> customers = JsonUtil.fromJson(result, type);
        return customers;
    }

}
