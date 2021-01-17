package cn.deal.core.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cn.deal.core.customer.domain.SalesCustomer;

import java.util.List;

@Repository
public interface SalesCustomerRepository extends JpaRepository<SalesCustomer, String> {

    SalesCustomer findByCustomerId(String customerId);

	void deleteByCustomerId(String customerId);

    SalesCustomer findFirstByAppIdAndCustomerId(String appId, String customerId);

    /**
     * 根据项目id,原销售id,客户id找到销售客户的第一条记录
     * @param appId
     * @param sourceKuickUserId
     * @param customerIds
     * @return
     */
    SalesCustomer findFirstByAppIdAndKuickUserIdAndCustomerId(String appId, String sourceKuickUserId, String customerIds);
}
