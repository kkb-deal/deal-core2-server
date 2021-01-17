package cn.deal.core.meta.domain;

import javax.persistence.*;
import java.util.Date;

@Table(name="extension_point")
@Entity
public class AppExtensionPoint {

	public enum Platform {

		/**
		 * WEB端
		 */
		WEB("pc_web");

		private String val;

		public String val() {
			return this.val;
		}

		Platform(String val) {
			this.val = val;
		}
	}

	@Id
	private String id;
	
	@Column(name="`app_id`")
	private String appId;
	
	@Column(name="`platform`")
	private String platform;

	@Column(name = "module")
	private String module;
	
	@Column(name="`type`")
	private String type;
	
	@Column(name="`name`")
	private String name;

	@Column(name = "config")
	private String config;

	@Column(name="`created_at`")
	private Date createdAt;
	
	@Column(name="`updated_at`")
	private Date updatedAt;

	@PrePersist
	public void onCreate(){
		this.createdAt = new Date();
	}
	
	@PreUpdate
	public void onUpdate(){
		this.updatedAt = new Date();
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

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	@Override
	public String toString() {
		return "AppExtensionPoint{" +
				"id='" + id + '\'' +
				", appId='" + appId + '\'' +
				", platform='" + platform + '\'' +
				", module='" + module + '\'' +
				", type='" + type + '\'' +
				", name='" + name + '\'' +
				", config='" + config + '\'' +
				", createdAt=" + createdAt +
				", updatedAt=" + updatedAt +
				'}';
	}
}
