package cn.deal.core.customer.service.impl;

import cn.deal.core.app.domain.DealApp;
import cn.deal.core.app.service.DealAppService;
import cn.deal.core.customer.domain.CustomerGroups;
import cn.deal.core.customer.repository.CustomerGroupRepository;
import cn.deal.core.customer.repository.GroupsCustomerRepository;
import cn.deal.core.customer.service.CustomerGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class CustomerGroupImpl implements CustomerGroupService {

    private Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Autowired
    private CustomerGroupRepository customerGroupRepository;
    @Autowired
    private GroupsCustomerRepository customerRepository;
    @Autowired
    private DealAppService dealAppService;

    @Override
    public List<CustomerGroups> getCustomerGroups(String appId) {
        List<CustomerGroups> customerGroups = customerGroupRepository.findListByAppId(appId);
        return customerGroups;
    }

    @Override
    public CustomerGroups addCustomerGroups(String appId, String name, Integer index) {

        DealApp dealApp = dealAppService.getDealAppInfo(appId);
        if (dealApp == null) {
            return null;
        }
        if (index == 0) {
            Sort sort = new Sort(Sort.Direction.DESC, "index");
            CustomerGroups customerGroups = customerGroupRepository.findFirstByAppId(appId, sort);
            if (customerGroups != null && customerGroups.getIndex() != null) {
                index = customerGroups.getIndex() + 1; //索引最大值+1
            }else {
                index = 0;
            }
        }

        CustomerGroups customerGroups = new CustomerGroups(appId, name, index);
        return customerGroupRepository.save(customerGroups);
    }

    @Override
    public int deleteCustomerGroups(String appId, String groupId) {
        CustomerGroups customerGroups = customerGroupRepository.findByAppIdAndId(appId, groupId);
        if (customerGroups == null) {
            logger.info("该记录不存在,不能删除");
            return 0;
        }

        Integer customerCount = customerRepository.findCustomerByGroupId(appId,groupId);

        if (customerCount > 0) {
            return 2;
        }

        try {
            customerGroupRepository.delete(groupId);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public CustomerGroups updateCustomerGroups(String appId, String groupId, String name, Integer index) {
        CustomerGroups customerGroups = customerGroupRepository.findByAppIdAndId(appId, groupId);
        if (customerGroups == null) {
            logger.info("该记录不存在,不能修改");
            return null;
        }

        Date updateTime = new Date();

        customerGroups.setUpdatedAt(updateTime);
        if (name != null) {
            customerGroups.setName(name);
        }
        if (index != null) {
            customerGroups.setIndex(index);
        }
        return customerGroupRepository.save(customerGroups);
    }

    @Override
    @Transactional
    public boolean exchangeCustomerGroups(String appId, String groupId, String groupId2) throws Exception{
        CustomerGroups customerGroup1 = customerGroupRepository.findByAppIdAndId(appId, groupId);
        CustomerGroups customerGroup2 = customerGroupRepository.findByAppIdAndId(appId, groupId2);
        if (customerGroup1 == null || customerGroup2 == null) {
            logger.info("该记录不存在,不能修改");
            return false;
        }
        Date updateTime = new Date();
        customerGroup1.setUpdatedAt(updateTime);
        customerGroup2.setUpdatedAt(updateTime);
        Integer exchangeIndex = customerGroup1.getIndex();
        customerGroup1.setIndex(customerGroup2.getIndex());
        customerGroup2.setIndex(exchangeIndex);

        customerGroupRepository.save(customerGroup1);
        logger.info("分组1修改成功");
        customerGroupRepository.save(customerGroup2);
        logger.info("分组2修改成功");
        return true;
    }
}
