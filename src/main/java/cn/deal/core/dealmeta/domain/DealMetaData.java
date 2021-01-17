package cn.deal.core.dealmeta.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @ClassName DealMetadata
 */
@Entity
@Table(name = "deal_meta_data")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DealMetaData {

    @Id
    private String id;

    @Column
    private String appId;

    @Column(name="names")
    private String name;

    @Column
    private String type;
    
    @Column
    private String title;

    @Column
    private String visiable;

    @Column
    private String defaultValue;

    @Column
    private String optionValues;

    @Column(name="indexs")
    private String index;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd hh-mm-ss")
    private Date createdAt;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd hh-mm-ss")
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

    public String getVisiable() {
        return visiable;
    }

    public void setVisiable(String visiable) {
        this.visiable = visiable;
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

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
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

    @Override
    public String toString() {
        return "DealMetaData{" +
                "id='" + id + '\'' +
                ", appId='" + appId + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", visiable='" + visiable + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                ", optionValues='" + optionValues + '\'' +
                ", index='" + index + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
