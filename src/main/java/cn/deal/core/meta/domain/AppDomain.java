package cn.deal.core.meta.domain;

import org.hibernate.annotations.GenericGenerator;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="app_domain")
@Entity
@GenericGenerator(name = "system_uuid", strategy = "uuid")
public class AppDomain {
	@Id
    @GeneratedValue(generator = "system_uuid")
	private String id;
	
	private String appId;
	private String domain;
	private Integer valid;
	private String validResult;
	private String failCode;
	private String officialAccountId;
	private Integer status;
	private Date createdAt;
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
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public Integer getValid() {
		return valid;
	}
	public void setValid(Integer valid) {
		this.valid = valid;
	}
	public String getValidResult() {
		return validResult;
	}
	public void setValidResult(String validResult) {
		this.validResult = validResult;
	}
	public String getFailCode() {
		return failCode;
	}
	public void setFailCode(String failCode) {
		this.failCode = failCode;
	}
	public String getOfficialAccountId() {
		return officialAccountId;
	}
	public void setOfficialAccountId(String officialAccountId) {
		this.officialAccountId = officialAccountId;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
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
		return "AppDomain [id=" + id + ", appId=" + appId + ", domain="
				+ domain + ", valid=" + valid + ", validResult=" + validResult
				+ ", failCode=" + failCode + ", officialAccountId="
				+ officialAccountId + ", status=" + status + ", createdAt="
				+ createdAt + ", updatedAt=" + updatedAt + "]";
	}
}
