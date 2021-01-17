package cn.deal.core.customerswarm.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 分群的共享信息
 */
@Entity
@Table(name="swarm_share")
public class SwarmShare {
	
	@Id
	private String id;
	
	@Column(name="appId")
	private String appId;
	
	@Column(name="swarmId")
	private String swarmId;
	
	@Column(name="targetType")
	private Integer targetType;//分享类型： 2-分享给整个项目，1-分享给某个项目成员
	
	@Column(name="targetId")
	private String targetId;//如果targetType为2，该值为appId;  如果targetType为1，该值为kuickUserId
	
	@Column(name="createdAt")
	private Date createdAt;
	
	@Transient
	private String shareSourceName;//共享人
	@Transient
	private String shareSourcePhoto;
	@Transient
	private String kuickUserId;//共享人的kuickuserid
	
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
	public String getSwarmId() {
		return swarmId;
	}
	public void setSwarmId(String swarmId) {
		this.swarmId = swarmId;
	}
	public Integer getTargetType() {
		return targetType;
	}
	public void setTargetType(Integer targetType) {
		this.targetType = targetType;
	}
	public String getTargetId() {
		return targetId;
	}
	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	public String getShareSourceName() {
		return shareSourceName;
	}
	public void setShareSourceName(String shareSourceName) {
		this.shareSourceName = shareSourceName;
	}
	public String getShareSourcePhoto() {
		return shareSourcePhoto;
	}
	public void setShareSourcePhoto(String shareSourcePhoto) {
		this.shareSourcePhoto = shareSourcePhoto;
	}
	public String getKuickUserId() {
		return kuickUserId;
	}
	public void setKuickUserId(String kuickUserId) {
		this.kuickUserId = kuickUserId;
	}

	public static enum TargetType{
		
		APP(1),
		APP_MEMBER(0);
		
		private int val;
		private TargetType(int val){
			this.val = val;
		}
		public int getVal(){
			return val;
		}
	}
	
}
