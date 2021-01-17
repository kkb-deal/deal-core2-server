package cn.deal.core.customer.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "customer_group")
public class CustomerGroups { //对应customer_group表的实体类

    @Id
    private String id;

    @Column
    private String appId;

    @Column(name = "`name`")
    private String name;

    @Column(name = "`index`")
    private Integer index;  //排序索引

    @Column
    private Date createdAt;

    @Column
    private Date updatedAt;

    public CustomerGroups() {
    }

    public CustomerGroups(String id) {
        this.id = id;
        
        this.appId = null;
        this.name = null;
        this.index = 0;
        
        Date now = new Date();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public CustomerGroups(String appId, String name, Integer index) {
        this.id = UUID.randomUUID().toString();
        
        this.appId = appId;
        this.name = name;
        this.index = index;
        
        Date now = new Date();
        this.createdAt = now;
        this.updatedAt = now;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
