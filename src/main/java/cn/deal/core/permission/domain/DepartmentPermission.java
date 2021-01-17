package cn.deal.core.permission.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "department_permission")
@JsonIgnoreProperties(ignoreUnknown = true)
@GenericGenerator(name = "dp-uuid", strategy = "uuid")
public class DepartmentPermission {

	public enum Status {

		/**
		 * 可用
		 */
		USED("1"),

		UNUSED("0");

		private String val;

		Status(String val){
			this.val = val;
		}

		public String getVal() {
			return this.val;
		}

	}

	public enum DomainType {

		/**
		 * 部门
		 */
		DEPARTMENT("Department"),

		/**
		 * 前端资源
		 * 
		 */
		RESOURCE("Resource");

		private String val;

		DomainType(String val){
			this.val = val;
		}

		public String getVal() {
			return this.val;
		}

	}

	@Id
	@GeneratedValue(generator = "dp-uuid")
	private String id;

	@Column
	private String appId;

	/**
	 * 业务对象类型，可选值 Sales、Admin
	 */
	@Column
	private String domainType;

	/**
	 * 业务对象ID，Sales: 销售的KuickUserId；Admin: 管理员的KuickUserID
	 */
	@Column
	private String domainId;

	@Column
	private String departmentId;

	/**
	 * 权限；Admin: 管理权限
	 */
	@Column
	private String perm;

	@Column
	private String status;

	@Column
	private Date createdAt;

	@Column
	private Date updatedAt;

	public DepartmentPermission() {
		super();
	}

	public String getDomainType() {
		return domainType;
	}

	public void setDomainType(String domainType) {
		this.domainType = domainType;
	}

	public String getDomainId() {
		return domainId;
	}

	public void setDomainId(String domainId) {
		this.domainId = domainId;
	}

	public String getPerm() {
		return perm;
	}

	public void setPerm(String perm) {
		this.perm = perm;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	@Override
	public String toString() {
		return "DepartmentPermission{" +
				"id='" + id + '\'' +
				", appId='" + appId + '\'' +
				", domainType='" + domainType + '\'' +
				", domainId='" + domainId + '\'' +
				", perm='" + perm + '\'' +
				", departmentId='" + departmentId + '\'' +
				", status='" + status + '\'' +
				", createdAt=" + createdAt +
				", updatedAt=" + updatedAt +
				'}';
	}

}
