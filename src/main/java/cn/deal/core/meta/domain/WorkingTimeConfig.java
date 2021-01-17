package cn.deal.core.meta.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.UUID;


@Table(name="working_time_config")
@Entity
public class WorkingTimeConfig {

    public WorkingTimeConfig(){
        super();
    }

    public WorkingTimeConfig(String appId, String type, String values, String remark){
        this.id = UUID.randomUUID().toString();
        this.appId = appId;
        this.type = type;
        this.values = values;
        this.remark = remark;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    @Id
    private String id;

    @Column(name="appId")
    private String appId;

    @Column(name="`type`")
    private String type;

    @Column(name="`values`")
    private String values;

    @Column(name="remark")
    private String remark;

    @Column(name="createdAt")
    private Date createdAt;

    @Column(name="updatedAt")
    private Date updatedAt;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "WorkingTimeConfig{" +
                "id='" + id + '\'' +
                ", appId='" + appId + '\'' +
                ", type='" + type + '\'' +
                ", values='" + values + '\'' +
                ", remark='" + remark + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
