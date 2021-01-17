package cn.deal.core.tag.domain;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name="deal_user_tag")
public class DealUserTag {

	public DealUserTag() {
		super();
	}
	
	public DealUserTag(String id, String appId, String dealUserId, String tag,
			Date createdAt) {
		super();
		this.id = id;
		this.appId = appId;
		this.dealUserId = dealUserId;
		this.tag = tag;
		this.createdAt = createdAt;
	}

	@Id
	private String id;
	
	@Column
	private String appId;
	
	@Column
	private String dealUserId;
	
	@Column
	private String tag;

	@Column
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

	public String getDealUserId() {
		return dealUserId;
	}

	public void setDealUserId(String dealUserId) {
		this.dealUserId = dealUserId;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
}
