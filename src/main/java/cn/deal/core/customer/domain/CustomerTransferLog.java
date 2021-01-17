package cn.deal.core.customer.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "customer_transfer_log")
public class CustomerTransferLog {
    @Id
    private String id;
    private String appId;
    private String kuickUserId;
    private String kuickUserName;
    private String targetKuickUserId;
    private String customerId;
    @Column(name = "`when`")
    private Date when;
    private Date createdAt;

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

    public String getKuickUserName() {
        return kuickUserName;
    }

    public void setKuickUserName(String kuickUserName) {
        this.kuickUserName = kuickUserName;
    }

    public String getTargetKuickUserId() {
        return targetKuickUserId;
    }

    public void setTargetKuickUserId(String targetKuickUserId) {
        this.targetKuickUserId = targetKuickUserId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Date getWhen() {
        return when;
    }

    public void setWhen(Date when) {
        this.when = when;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "CustomerTransferLog{" +
                "id='" + id + '\'' +
                ", appId='" + appId + '\'' +
                ", kuickUserId='" + kuickUserId + '\'' +
                ", kuickUserName='" + kuickUserName + '\'' +
                ", targetKuickUserId='" + targetKuickUserId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", when=" + when +
                ", createdAt=" + createdAt +
                '}';
    }
}
