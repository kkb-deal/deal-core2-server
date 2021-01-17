package cn.deal.core.meta.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.UUID;


@Entity
@Table(name = "behaviour_meta_data")
public class BehaviourMetaData {

    private static final Integer DEFAULT_READONLY = 0;

    public BehaviourMetaData(){
        super();
    }

    public BehaviourMetaData(String appId, String action, String name, String type, String title, Integer index,
                             String defaultValue, String optionValues, Integer readonly){
        this.id = UUID.randomUUID().toString();
        this.appId = appId;
        this.action = action;
        this.name = name;
        this.type = type;
        this.title = title;
        this.index = index;
        this.defaultValue = defaultValue;
        this.optionValues = optionValues;
        if(readonly == null){
            readonly = DEFAULT_READONLY;
        }
        this.readonly = readonly;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    @Id
    private String id;

    @Column
    private String appId;

    @Column
    private String action;

    @Column(name = "`name`")
    private String name;

    @Column
    private String type;

    @Column
    private String title;

    @Column(name = "`index`")
    private Integer index;

    @Column
    private String defaultValue;

    @Column
    private String optionValues;

    @Column
    private Integer readonly;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getOptionValues() {
        return optionValues;
    }

    public void setOptionValues(String optionValues) {
        this.optionValues = optionValues;
    }

    public Integer getReadonly() {
        return readonly;
    }

    public void setReadonly(Integer readonly) {
        this.readonly = readonly;
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
        return "BehaviourMetaData{" +
                "id='" + id + '\'' +
                ", appId='" + appId + '\'' +
                ", action='" + action + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", index=" + index +
                ", defaultValue='" + defaultValue + '\'' +
                ", optionValues='" + optionValues + '\'' +
                ", readonly=" + readonly +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
