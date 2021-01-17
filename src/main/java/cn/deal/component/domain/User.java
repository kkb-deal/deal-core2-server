package cn.deal.component.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * User bean.
 */
@Entity
@Table(name = "user")
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {

	private static final long serialVersionUID = 7370748752003443470L;

	public enum Status {
		/**
		 * 正常
		 */
		OK(1),
		/**
		 * 未激活
		 */
		FAIL(0);
		private int val;

		public int getVal() {
			return val;
		}

		Status(int val) {
			this.val = val;
		}
	}

	public enum IsSimple {
		/**
		 * 是
		 */
		YES(1),

		NO(0);

		private Integer val;

		public Integer getVal() {
			return this.val;
		}

		IsSimple(Integer val) {
			this.val = val;
		}
	}


	@Id
	private Integer id;

	@Column
	private String password;

	@Column
	private Integer emailStatus;

	@Column
	private String phoneNum;

	@Column
	private Integer phoneNumStatus;

	@Column
	private String photoURI;

	@Column
	private String openid;

	@Column
	private Integer sex;

	@Column
	private String city;

	@Column
	private String province;

	@Column
	private String unionid;

	@Column
	private Integer ccNum;

	@Column
	private String position;

	@Column
	private String industry;

	@Column
	private Integer channel;

	@Column
	private String name;

	@Column
	private String email;

	@Column
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	private Date createdAt;

	@Column
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	private Date updatedAt;

	@Column
	private String company;

	@Column
	private Integer status;

	@Column
	private String weixinNickname;

	@Column
	private Integer salesCount;

	@Column
	private Integer personInfoStatus;

	@Column
	private String weixinNum;

	@Column
	private String address;

	@Column
	private String personalitySignature;

	@Column
	private String voiceSignatureUrl;

	@Column
	private String fromClientId;

	@Column
	private String fromClientName;

	@Column
	private String companyTelephone;

	@Column
	private String companyOfficial;

	@Column
	private String companyEmail;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getEmailStatus() {
		return emailStatus;
	}

	public void setEmailStatus(Integer emailStatus) {
		this.emailStatus = emailStatus;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public Integer getPhoneNumStatus() {
		return phoneNumStatus;
	}

	public void setPhoneNumStatus(Integer phoneNumStatus) {
		this.phoneNumStatus = phoneNumStatus;
	}

	public String getPhotoURI() {
		return photoURI;
	}

	public void setPhotoURI(String photoURI) {
		this.photoURI = photoURI;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}

	public Integer getCcNum() {
		return ccNum;
	}

	public void setCcNum(Integer ccNum) {
		this.ccNum = ccNum;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public Integer getChannel() {
		return channel;
	}

	public void setChannel(Integer channel) {
		this.channel = channel;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getWeixinNickname() {
		return weixinNickname;
	}

	public void setWeixinNickname(String weixinNickname) {
		this.weixinNickname = weixinNickname;
	}

	public Integer getSalesCount() {
		return salesCount;
	}

	public void setSalesCount(Integer salesCount) {
		this.salesCount = salesCount;
	}

	public Integer getPersonInfoStatus() {
		return personInfoStatus;
	}

	public void setPersonInfoStatus(Integer personInfoStatus) {
		this.personInfoStatus = personInfoStatus;
	}

	public String getWeixinNum() {
		return weixinNum;
	}

	public void setWeixinNum(String weixinNum) {
		this.weixinNum = weixinNum;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPersonalitySignature() {
		return personalitySignature;
	}

	public void setPersonalitySignature(String personalitySignature) {
		this.personalitySignature = personalitySignature;
	}

	public String getVoiceSignatureUrl() {
		return voiceSignatureUrl;
	}

	public void setVoiceSignatureUrl(String voiceSignatureUrl) {
		this.voiceSignatureUrl = voiceSignatureUrl;
	}

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", password='" + password + '\'' +
				", emailStatus=" + emailStatus +
				", phoneNum='" + phoneNum + '\'' +
				", phoneNumStatus=" + phoneNumStatus +
				", photoURI='" + photoURI + '\'' +
				", openid='" + openid + '\'' +
				", sex=" + sex +
				", city='" + city + '\'' +
				", province='" + province + '\'' +
				", unionid='" + unionid + '\'' +
				", ccNum=" + ccNum +
				", position='" + position + '\'' +
				", industry='" + industry + '\'' +
				", channel=" + channel +
				", name='" + name + '\'' +
				", email='" + email + '\'' +
				", createdAt=" + createdAt +
				", updatedAt=" + updatedAt +
				", company='" + company + '\'' +
				", status=" + status +
				", weixinNickname='" + weixinNickname + '\'' +
				", salesCount=" + salesCount +
				", personInfoStatus=" + personInfoStatus +
				", weixinNum='" + weixinNum + '\'' +
				", address='" + address + '\'' +
				", personalitySignature='" + personalitySignature + '\'' +
				", voiceSignatureUrl='" + voiceSignatureUrl + '\'' +
				", fromClientId='" + fromClientId + '\'' +
				", fromClientName='" + fromClientName + '\'' +
				", companyTelephone='" + companyTelephone + '\'' +
				", companyOfficial='" + companyOfficial + '\'' +
				", companyEmail='" + companyEmail + '\'' +
				'}';
	}
}