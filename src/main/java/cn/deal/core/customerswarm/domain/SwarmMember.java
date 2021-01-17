package cn.deal.core.customerswarm.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 分群的成员
 */
@Entity
@Table(name="swarm_member")
public class SwarmMember {
	
	@Id
	private String id;
	
	@Column(name="appId")
	private String appId;
	
	@Column(name="swarmId")
	private String swarmId;

	@Column(name="customerId")
	private String customerId;
	
	@Column(name="createdAt")
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
	public String getSwarmId() {
		return swarmId;
	}
	public void setSwarmId(String swarmId) {
		this.swarmId = swarmId;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

}
