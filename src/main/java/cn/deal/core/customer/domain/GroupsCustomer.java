package cn.deal.core.customer.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "customer")
public class GroupsCustomer { //查询分组下是否包含客户的实体类
    /**
     * 客户id
     */
    @Id
    private String id;

    /**
     * 分组id
     */
    @Column
    private String groupId;
}
