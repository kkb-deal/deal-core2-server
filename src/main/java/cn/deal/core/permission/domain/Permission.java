package cn.deal.core.permission.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
*
* 项目名称：deal-core-server2
* 类名称：Permission
*/
public class Permission {


	public enum DomainType {

		/**
		 * 部门
		 */
		DEPARTMENT("Department"),

		RESOURCE("Resource");

		private String val;

		DomainType(String val){
			this.val = val;
		}

		public String getVal() {
			return this.val;
		}

	}

	public enum Perm {

		/**
		 * 管理员
		 */
		ADMIN("ADMIN");

		private String val;

		Perm(String val){
			this.val = val;
		}

		public String getVal() {
			return this.val;
		}

	}

	/**
	 * 业务对象类型，可选值 Sales、Admin
	 */
	private String domainType;

	/**
	 * 业务对象ID，Sales: 销售的KuickUserId；Admin: 管理员的KuickUserID
	 */
	private String domainId;

	private String domainName;

	/**
	 * 权限；Admin: 管理权限
	 */
	private String perm;

	private Date createdAt;

	public Permission(AppMemberPermission appMemberPermission) {
		this.domainId=appMemberPermission.getDomainId();
		this.domainType=appMemberPermission.getDomainType();
		this.domainName=appMemberPermission.getUserName();
		this.perm=appMemberPermission.getPerm();
		this.createdAt=appMemberPermission.getCreatedAt();
	}

	public Permission() {
		super();
	}

	public Permission(String domainId, String domainType) {
		this.domainId = domainId;
		this.domainType = domainType;
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

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	@Override
	public String toString() {
		return "RolePermission{" +
				"domainType='" + domainType + '\'' +
				", domainId='" + domainId + '\'' +
				", domainName='" + domainName + '\'' +
				", perm='" + perm + '\'' +
				", createdAt=" + createdAt +
				'}';
	}

}
