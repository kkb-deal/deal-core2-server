package cn.deal.core.customer.repository;

import cn.deal.core.customer.domain.CustomerGroups;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;

@Repository
public interface CustomerGroupRepository extends JpaRepository<CustomerGroups, String> {

    CustomerGroups findFirstByAppId(String appId,Sort sort); //查找最大(Sort)的index值

    CustomerGroups findByAppIdAndId(String appId, String id); //查找对应的记录是否存在

    @Query(value = "SELECT * FROM `customer_group` WHERE appId=?1 order by `index`,createdAt",nativeQuery = true)
    List<CustomerGroups> findListByAppId(String appId); //多条件排序jpa没找到方便的用法
}
