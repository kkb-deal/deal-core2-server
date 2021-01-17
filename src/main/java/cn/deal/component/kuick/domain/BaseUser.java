package cn.deal.component.kuick.domain;

/**
 * 用户基本信息
 */
public class BaseUser {

	private String name;  // 姓名

	private String title;// 职称

	private String email; // 邮箱

	private String phone; // 手机号

	private String company;// 公司
	
	private Integer status; // 状态, 1:正常状态，0：标记删除状态

	public BaseUser() {
	}

	public BaseUser(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "BaseUser [name=" + name + ", title=" + title + ", email=" + email + ", phone=" + phone + ", company="
				+ company + ", status=" + status + "]";
	}
	
}
