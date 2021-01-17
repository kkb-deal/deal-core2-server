package cn.deal.core.dealuser.domain;

import cn.deal.core.customer.domain.Customer;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;


@Entity
@Table(name = "deal_user")
@JsonIgnoreProperties(ignoreUnknown=true)
public class DealUser {
	@Id
	private String id;// 记录唯一标识：使用uuid
	@Column
	private String appId; // 应用id
	@Column
	private String appUserId; // 应用中用户ID，由第三方提供，用来唯一标识一个用户
	@Column
	private String password; // 密码
	@Column
	private String phoneNum;// 用户手机号
	@Column
	private String photoURL; // 用户头像
	@Column
	private String photoWidth; // 用户头像
	@Column
	private String photoHeight; // 用户头像
	@Column
	private String unionid;// 用户unionid
	@Column
	private String openid;// 用户openid
	@Column
	private String deviceId; // 用户deviceId
	@Column
	private String deviceId2; // 用户deviceId
	@Column
	private String fromType;// 用户fromType
	@Column
	private int isNamed = 0; // 是否实名，0：非实名，1：实名
	@Column
	private String name;  // 姓名
	@Column
	private String title;// 职称
	@Column
	private String email; // 邮箱
	
	@Column(name = "phoneNum",insertable=false,updatable=false)
	private String phone; // 手机号
	
	@Column
	private String company;// 公司
	@Column
	private int status; // 状态, 1:正常状态，0：标记删除状态
	@Column
	private Date lastLoginTime;
	@Column
	private Date createTime;
	@Column
	private Date editTime;
	@Column
	private String fromInfo;// 用户
	@Column
	private String utmContent;// 用户
	@Column
	private String utmSource;// 用户
	@Column
	private String utmCampaign;// 用户
	@Column
	private String utmMedium;// 用户
	
	@Column
	private String utmTerm;// 用户
	@Column
	private String searchedKeyword;// 用户
	@Column
	private String fromProvince;// 用户
	@Column
	private String fromCity;// 用户

	@Transient
	private Customer customer;

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
	public String getAppUserId() {
		return appUserId;
	}
	public void setAppUserId(String appUserId) {
		this.appUserId = appUserId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public String getPhotoURL() {
		return photoURL;
	}
	public void setPhotoURL(String photoURL) {
		this.photoURL = photoURL;
	}
	public String getPhotoWidth() {
		return photoWidth;
	}
	public void setPhotoWidth(String photoWidth) {
		this.photoWidth = photoWidth;
	}
	public String getPhotoHeight() {
		return photoHeight;
	}
	public void setPhotoHeight(String photoHeight) {
		this.photoHeight = photoHeight;
	}
	public String getUnionid() {
		return unionid;
	}
	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getDeviceId2() {
		return deviceId2;
	}
	public void setDeviceId2(String deviceId2) {
		this.deviceId2 = deviceId2;
	}
	public String getFromType() {
		return fromType;
	}
	public void setFromType(String fromType) {
		this.fromType = fromType;
	}
	public int getIsNamed() {
		return isNamed;
	}
	public void setIsNamed(int isNamed) {
		this.isNamed = isNamed;
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
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Date getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getEditTime() {
		return editTime;
	}
	public void setEditTime(Date editTime) {
		this.editTime = editTime;
	}
	public String getFromInfo() {
		return fromInfo;
	}
	public void setFromInfo(String fromInfo) {
		this.fromInfo = fromInfo;
	}
	public String getUtmContent() {
		return utmContent;
	}
	public void setUtmContent(String utmContent) {
		this.utmContent = utmContent;
	}
	public String getUtmSource() {
		return utmSource;
	}
	public void setUtmSource(String utmSource) {
		this.utmSource = utmSource;
	}
	public String getUtmCampaign() {
		return utmCampaign;
	}
	public void setUtmCampaign(String utmCampaign) {
		this.utmCampaign = utmCampaign;
	}
	public String getUtmMedium() {
		return utmMedium;
	}
	public void setUtmMedium(String utmMedium) {
		this.utmMedium = utmMedium;
	}
	public String getUtmTerm() {
		return utmTerm;
	}
	public void setUtmTerm(String utmTerm) {
		this.utmTerm = utmTerm;
	}
	public String getSearchedKeyword() {
		return searchedKeyword;
	}
	public void setSearchedKeyword(String searchedKeyword) {
		this.searchedKeyword = searchedKeyword;
	}
	public String getFromProvince() {
		return fromProvince;
	}
	public void setFromProvince(String fromProvince) {
		this.fromProvince = fromProvince;
	}
	public String getFromCity() {
		return fromCity;
	}
	public void setFromCity(String fromCity) {
		this.fromCity = fromCity;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	//------------------------------------------------------

	/**
	 * 是否实名化
	 * 
	 * @return
	 */
	public boolean isNamed() {
		return this.isNamed==1;
	}
	
	/**
	 * 设置是否实名
	 * 
	 * @param named
	 */
	public void setNamed(boolean named) {
		this.isNamed = named ? 1 : 0;
	}
	
	@Override
	public String toString() {
		return "DealUser [id=" + id + ", appId=" + appId + ", appUserId=" + appUserId + ", password=" + password
				+ ", phoneNum=" + phoneNum + ", photoURL=" + photoURL + ", photoWidth=" + photoWidth + ", photoHeight="
				+ photoHeight + ", unionid=" + unionid + ", openid=" + openid + ", deviceId=" + deviceId
				+ ", deviceId2=" + deviceId2 + ", fromType=" + fromType + ", isNamed=" + isNamed + ", name=" + name
				+ ", title=" + title + ", email=" + email + ", phone=" + phone + ", company=" + company + ", status="
				+ status + ", lastLoginTime=" + lastLoginTime + ", createTime=" + createTime + ", editTime=" + editTime
				+ ", fromInfo=" + fromInfo + ", utmContent=" + utmContent + ", utmSource=" + utmSource
				+ ", utmCampaign=" + utmCampaign + ", utmMedium=" + utmMedium + ", utmTerm=" + utmTerm
				+ ", searchedKeyword=" + searchedKeyword + ", fromProvince=" + fromProvince + ", fromCity=" + fromCity
				+ ", customer=" + customer + "]";
	}
}
