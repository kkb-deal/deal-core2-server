package cn.deal.core.meta.domain;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "app_setting")
public class AppSetting {

	@Id
	private String id;
	
	/**
	 * 项目id
	 */
	@Column(name="appId")
	private String appId;
	
	@Column(name="`key`")
	private String key;
	
	@Column(name="`type`")
	private String type;
	
	@Column(name="`value`")
	private String value;
	
	@Column(name="defaultValue")
	private String defaultValue;
	
	@Transient
	private String title;
	
	@Transient
	private String desc;
	
	/**
	 * 创建时间
	 */
	@Column(name = "createdAt")
	private Date createdAt;

	/**
	 * 修改时间
	 */
	@Column(name = "updatedAt")
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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
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

	public AppSetting() {
		super();
		this.id = UUID.randomUUID().toString();
		this.createdAt=new Date();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public AppSetting( String appId, String key,String value,String type,String defaultValue,String title) {
		super();
		this.id = UUID.randomUUID().toString();
		this.createdAt=new Date();
		this.appId = appId;
		this.key = key;
		this.type = type;
		this.value = value;
		this.defaultValue = defaultValue;
		this.title = title;
	}

	@Override
	public String toString() {
		return "AppSetting [id=" + id + ", appId=" + appId + ", key=" + key + ", type=" + type + ", value=" + value
				+ ", defaultValue=" + defaultValue + ", title=" + title + ", desc=" + desc + ", createdAt=" + createdAt
				+ ", updatedAt=" + updatedAt + "]";
	}
	
}
