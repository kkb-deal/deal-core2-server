package cn.deal.core.customer.service;

import java.util.List;

import cn.deal.core.customer.domain.CustomerGroups;


public interface CustomerGroupService {
    List<CustomerGroups> getCustomerGroups(String appId);

    CustomerGroups addCustomerGroups(String appId,String name,Integer index);

    int deleteCustomerGroups(String appId,String groupId);

    CustomerGroups updateCustomerGroups(String appId, String groupId, String name, Integer index);

    boolean exchangeCustomerGroups(String appId, String groupId, String groupId2) throws Exception;
}
