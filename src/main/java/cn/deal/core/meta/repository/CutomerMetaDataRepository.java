package cn.deal.core.meta.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cn.deal.core.meta.domain.CustomerMetaData;

public interface CutomerMetaDataRepository extends JpaRepository<CustomerMetaData, String> {

    List<CustomerMetaData> getCustomerMetaDataByNameAndAppId(String name, String appId);
    List<CustomerMetaData> getCustomerMetaDataByAppId(String appId);

    CustomerMetaData findByAppIdAndName(String appId, String name);

    @Query(value = "select max(`index`) from customer_meta_data where appId=:appId", nativeQuery = true)
    Integer findAppMaxIndex(@Param("appId") String appId);
}
