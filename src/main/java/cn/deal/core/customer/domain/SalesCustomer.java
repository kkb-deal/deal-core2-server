package cn.deal.core.customer.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sales_customer")
public class SalesCustomer implements Serializable {

    private static final long serialVersionUID = 2651727413537915036L;
    
    @Id
    private String id;
    private String appId;
    private String kuickUserId;
    private String customerId;
    private Date createdAt;
    private Date updatedAt;
    private Boolean whetherMerge;
    private Boolean isNew;
    private Integer newCount;

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

    public String getKuickUserId() {
        return kuickUserId;
    }

    public void setKuickUserId(String kuickUserId) {
        this.kuickUserId = kuickUserId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getWhetherMerge() {
        return whetherMerge;
    }

    public void setWhetherMerge(Boolean whetherMerge) {
        this.whetherMerge = whetherMerge;
    }

    public Boolean getIsNew() {
        return isNew;
    }

    public void setIsNew(Boolean isNew) {
        this.isNew = isNew;
    }

    public Integer getNewCount() {
        return newCount;
    }

    public void setNewCount(Integer newCount) {
        this.newCount = newCount;
    }

    @Override
    public String toString() {
        return "SalesCustomer [id=" + id + ", appId=" + appId + ", kuickUserId=" + kuickUserId + ", customerId="
                + customerId + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", whetherMerge="
                + whetherMerge + ", isNew=" + isNew + ", newCount=" + newCount + "]";
    }

}
