package cn.deal.core.meta.domain;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity(name="app_customer_record_setting")
public class AppCustomerRecordSetting {
    @Id
    private String id;
    @Column
    private String appId;
    @Column(name = "`action`")
    private String action;
    @Column(name = "`index`")
    private Integer index;
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private Date createdAt;
    @Transient
    private String color;
    @Column
    private Integer status;//默认1,被删除0

    public AppCustomerRecordSetting() {
        super();
        this.id = UUID.randomUUID().toString();
        this.createdAt = new Date();
        
    }

    public AppCustomerRecordSetting(String appId, String action, Integer index, String name) {
        super();
        this.appId = appId;
        this.action = action;
        this.index = index;
        this.name = name;
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AppCustomerRecordSetting [id=" + id + ", appId=" + appId + ", action=" + action + ", index=" + index
                + ", name=" + name + ", description=" + description + ", createdAt=" + createdAt + ", color=" + color
                + ", status=" + status + "]";
    }

}
