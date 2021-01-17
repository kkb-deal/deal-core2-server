package cn.deal.component.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

public class KuickUserDomainEventBody implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private Integer id;

	private String name;//用户姓名

	private String email;//用户邮箱

	private Integer emailStatus;//用户邮箱验证状态 0-未验证，1-已验证

	private String phoneNum;//用户手机号

	private Integer phoneNumStatus;//用户手机验证状态 0-未验证，1-已验证

	private String photoURI;//用户头像

	private String openid;//用户openid

	private Integer sex;//用户性别 1-男性，2-女性

	private String city;

	private String province;

	private String company;//用户公司

	private Integer ccNum;//cc号

	private Integer status;//用户状态，0-新注册，1-已激活，2-已停用

	private String position;//用户职位

	private String industry;//用户所属行业

	private Integer channel;//用户注册渠道

	private String weixinNickname;//微信昵称

	private Integer salesCount;//销售人数

	private Integer personInfoStatus;//是否完善个人信息，1-已经完善，0-未完善，默认为0

	
	private Date createdAt;

	private Date updatedAt;
	
	private String weixinNum;
	
	private String address;
	
	private String personalitySignature;
	
	private String voiceSignatureUrl;
	
	private String fromClientId;
	
	private String fromClientName;
	
	private Integer unionidStatus;
	
	private String companyTelephone;
	
	private String companyOfficial;
	
	private String companyEmail;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public Integer getCcNum() {
		return ccNum;
	}

	public void setCcNum(Integer ccNum) {
		this.ccNum = ccNum;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
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

	public String getFromClientId() {
		return fromClientId;
	}

	public void setFromClientId(String fromClientId) {
		this.fromClientId = fromClientId;
	}

	public String getFromClientName() {
		return fromClientName;
	}

	public void setFromClientName(String fromClientName) {
		this.fromClientName = fromClientName;
	}

	public Integer getUnionidStatus() {
		return unionidStatus;
	}

	public void setUnionidStatus(Integer unionidStatus) {
		this.unionidStatus = unionidStatus;
	}

	public String getCompanyTelephone() {
        return companyTelephone;
    }

    public void setCompanyTelephone(String companyTelephone) {
        this.companyTelephone = companyTelephone;
    }

    public String getCompanyOfficial() {
        return companyOfficial;
    }

    public void setCompanyOfficial(String companyOfficial) {
        this.companyOfficial = companyOfficial;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    @Override
	public String toString() {
		return "KuickUserDomainEventBody [id=" + id + ", name=" + name + ", email=" + email
				+ ", emailStatus=" + emailStatus + ", phoneNum=" + phoneNum + ", phoneNumStatus=" + phoneNumStatus
				+ ", photoURI=" + photoURI + ", openid=" + openid + ", sex=" + sex + ", city=" + city + ", province="
				+ province + ", company=" + company + ", ccNum=" + ccNum + ", status=" + status
				+ ", position=" + position + ", industry=" + industry + ", channel=" + channel + ", weixinNickname="
				+ weixinNickname + ", salesCount=" + salesCount + ", personInfoStatus=" + personInfoStatus
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", weixinNum=" + weixinNum + ", address="
				+ address + ", personalitySignature=" + personalitySignature + ", voiceSignatureUrl="
				+ voiceSignatureUrl + ", fromClientId=" + fromClientId + ", fromClientName=" + fromClientName
				+ ", unionidStatus=" + unionidStatus+ ", companyTelephone=" + companyTelephone+ ", companyOfficial=" + companyOfficial
				+ ", companyEmail=" + companyEmail+ "]";
	}
	
}
