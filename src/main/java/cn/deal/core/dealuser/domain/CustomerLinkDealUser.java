package cn.deal.core.dealuser.domain;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "customer_link_deal_user")
public class CustomerLinkDealUser {
	@Id
	private String id;// 记录唯一标识：使用uuid
	@Column
	private String customerId;
	@Column
	private String dealUserId;
	
	/**
	 * 创建时间
	 */
	@Column
	private Date createdAt;

	/**
	 * 修改时间
	 */
	@Column
	private Date updatedAt;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getDealUserId() {
		return dealUserId;
	}

	public void setDealUserId(String dealUserId) {
		this.dealUserId = dealUserId;
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
		return "CustomerLinkDealUser [id=" + id + ", customerId=" + customerId + ", dealUserId=" + dealUserId
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}

	public CustomerLinkDealUser() {
		super();
	}
	
	
}
