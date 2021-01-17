package cn.deal.component.kuick.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class KuickUser extends BaseUser{

	private int id;

	private String password;

	private int emailStatus;

	private String phoneNum;

	private int phoneNumStatus;

	private String photoURI;

	private String openid;

	private int sex;

	private String city;

	private String province;

	private String unionid;

	private int ccNum;

	private String position;

	private String industry;

	private int channel;

	private String conferenceId;

	private String accessToken;

	private String createdAt;

	private String updatedAt;

	public KuickUser() {
		super();
	}

	public KuickUser(int id, String photoURI, String name) {
		super(name);
		this.id = id;
		this.photoURI = photoURI;
	}

	@Deprecated
	public String getUserName() {
		return getName();
	}

	@Deprecated
	public Integer getUserId() {
		return getId();
	}

	@Deprecated
	public String getUserPhotoURL() {
		return photoURI;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getConferenceId() {
		return conferenceId;
	}

	public void setConferenceId(String conferenceId) {
		this.conferenceId = conferenceId;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getEmailStatus() {
		return emailStatus;
	}

	public void setEmailStatus(int emailStatus) {
		this.emailStatus = emailStatus;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		setPhone(phoneNum);
		this.phoneNum = phoneNum;
	}

	public int getPhoneNumStatus() {
		return phoneNumStatus;
	}

	public void setPhoneNumStatus(int phoneNumStatus) {
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

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
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

	public int getCcNum() {
		return ccNum;
	}

	public void setCcNum(int ccNum) {
		this.ccNum = ccNum;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		setTitle(position);
		this.position = position;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "KuickUser [id=" + id + ", password=" + password + ", emailStatus=" + emailStatus + ", phoneNum="
				+ phoneNum + ", phoneNumStatus=" + phoneNumStatus + ", photoURI=" + photoURI + ", openid=" + openid
				+ ", sex=" + sex + ", city=" + city + ", province=" + province + ", unionid=" + unionid + ", ccNum="
				+ ccNum + ", position=" + position + ", industry=" + industry + ", channel=" + channel
				+ ", conferenceId=" + conferenceId + ", accessToken=" + accessToken + ", createdAt=" + createdAt
				+ ", updatedAt=" + updatedAt + "]";
	}
}
