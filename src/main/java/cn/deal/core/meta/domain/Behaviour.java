package cn.deal.core.meta.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;


@Entity
@Table(name = "behaviour")
public class Behaviour {

    public enum Type {

        /**
         * 系统行为
         */
        SYSTEM(1),
        /**
         * 自定义行为
         */
        CUSTOM(2);

        private Integer val;

        private Type(Integer val){
            this.val = val;
        }

        public Integer getVal() {
            return this.val;
        }
    }

    public Behaviour(){
        super();
    }

    public Behaviour(String appId, Integer type, String action, String name, String description){
        this.id = UUID.randomUUID().toString();
        this.appId = appId;
        this.type = type;
        this.action = action;
        this.name = name;
        this.description = description;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    @Id
    private String id;

    @Column
    private String appId;

    @Column
    private Integer type;

    @Column
    private String action;

    @Column(name = "`name`")
    private String name;

    @Column
    private String description;

    @Transient
    private Integer metaCount;

    @Column
    private Date createdAt;

    @Column
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
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

    public Integer getMetaCount() {
        return metaCount;
    }

    public void setMetaCount(Integer metaCount) {
        this.metaCount = metaCount;
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

    @Override
    public String toString() {
        return "Behaviour{" +
                "id='" + id + '\'' +
                ", appId='" + appId + '\'' +
                ", type=" + type +
                ", action='" + action + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
