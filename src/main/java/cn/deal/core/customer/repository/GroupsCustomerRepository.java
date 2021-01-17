package cn.deal.core.customer.repository;

import cn.deal.core.customer.domain.GroupsCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface GroupsCustomerRepository extends JpaRepository<GroupsCustomer, String> {

    @Query(value = "select count(c.id) as count from customer c " +
            "left join sales_customer s on c.id=s.customerId " +
            "where s.kuickUserId is not null and c.appId = ?1 and c.groupId=?2 ", nativeQuery = true)
    Integer findCustomerByGroupId(String appId, String groupId); //查询该分组下客户数量

}
