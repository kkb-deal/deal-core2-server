package cn.deal.core.license.domain;

import java.util.Date;
import java.util.UUID;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "app_license")
public class AppLicense {

    public AppLicense() {
        super();
        this.id = UUID.randomUUID().toString();
    }

    @Id
    private String id;

    /**
     * 项目创建人Id
     */
    @Column(name = "appId")
    private String appId;

    /**
     * 签名
     */
    @Column(name = "sign")
    private String sign;

    /**
     * 最大项目成员数
     */
    @Column(name = "maxAppMemberCount")
    private int maxAppMemberCount;

    /**
     * 最大关联公众号数
     */
    @Column(name = "maxOfficialAccountsCount")
    private int maxOfficialAccountsCount;

    /**
     * 最大关联网站数
     */
    @Column(name = "maxWebSiteCount")
    private int maxWebSiteCount;

    /**
     * 最大关联App数量
     */
    @Column(name = "maxAppCount")
    private int maxAppCount;

    /**
     * 最大外呼成员数
     */
    @Column(name = "maxCallerCount")
    private int maxCallerCount;

    /**
     * 最大邮件群发人数
     */
    @Column(name = "maxMailSenderCount")
    private int maxMailSenderCount;

    /**
     * 最大发资料人数
     */
    @Column(name = "maxFileSenderCount")
    private int maxFileSenderCount;

    /**
     * 最大远程演示人数
     */
    @Column(name = "maxDemoMemberCount")
    private int maxDemoMemberCount;

    /**
     * 最大BI用户数
     */
    @Column(name = "maxBIUserCount")
    private int maxBIUserCount;

    /**
     * 最大微信小程序数
     */
    @Column(name = "maxWeixinAppCount")
    private Integer maxWeixinAppCount;

    /**
     * 最大有赞店铺数
     * 
     */
    @Column(name = "maxYouzanShopCount")
    private Integer maxYouzanShopCount;

    /**
     * 留客最大微信号数量
     */
    @Column(name = "maxLiukeWeixinCount")
    private Integer maxLiukeWeixinCount;

    /**
     * 最大留客微信成员数
     */
    @Column(name = "maxLiukeWeixinMemberCount")
    private Integer maxLiukeWeixinMemberCount;

    /**
     * 过期时间
     */
    @Column(name = "expiresTime")
    private String expiresTime;

    /**
     * 版本
     */
    @Column(name = "version")
    private int version;

    /**
     * 创建时间
     */
    @Column(name = "createdAt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    /**
     * 修改时间
     */
    @Column(name = "updatedAt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    /**
     * 支持项目类别 all/single
     */
    @Column(name = "app_type")
    private String type;

    /**
     * 账号版本
     */
    @Column(name = "edition")
    private String edition;

    @Column(name = "includeModules")
    private String includeModules;

    @Column(name = "isTrial")
    private Boolean isTrial;



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

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public int getMaxAppMemberCount() {
        return maxAppMemberCount;
    }

    public void setMaxAppMemberCount(int maxAppMemberCount) {
        this.maxAppMemberCount = maxAppMemberCount;
    }

    public int getMaxOfficialAccountsCount() {
        return maxOfficialAccountsCount;
    }

    public void setMaxOfficialAccountsCount(int maxOfficialAccountsCount) {
        this.maxOfficialAccountsCount = maxOfficialAccountsCount;
    }

    public int getMaxWebSiteCount() {
        return maxWebSiteCount;
    }

    public void setMaxWebSiteCount(int maxWebSiteCount) {
        this.maxWebSiteCount = maxWebSiteCount;
    }

    public int getMaxAppCount() {
        return maxAppCount;
    }

    public void setMaxAppCount(int maxAppCount) {
        this.maxAppCount = maxAppCount;
    }

    public int getMaxCallerCount() {
        return maxCallerCount;
    }

    public void setMaxCallerCount(int maxCallerCount) {
        this.maxCallerCount = maxCallerCount;
    }

    public int getMaxMailSenderCount() {
        return maxMailSenderCount;
    }

    public void setMaxMailSenderCount(int maxMailSenderCount) {
        this.maxMailSenderCount = maxMailSenderCount;
    }

    public int getMaxFileSenderCount() {
        return maxFileSenderCount;
    }

    public void setMaxFileSenderCount(int maxFileSenderCount) {
        this.maxFileSenderCount = maxFileSenderCount;
    }

    public int getMaxDemoMemberCount() {
        return maxDemoMemberCount;
    }

    public void setMaxDemoMemberCount(int maxDemoMemberCount) {
        this.maxDemoMemberCount = maxDemoMemberCount;
    }

    public int getMaxBIUserCount() {
        return maxBIUserCount;
    }

    public void setMaxBIUserCount(int maxBIUserCount) {
        this.maxBIUserCount = maxBIUserCount;
    }

    public Integer getMaxWeixinAppCount() {
        return maxWeixinAppCount;
    }

    public void setMaxWeixinAppCount(Integer maxWeixinAppCount) {
        this.maxWeixinAppCount = maxWeixinAppCount;
    }
    
    public Integer getMaxYouzanShopCount() {
		return maxYouzanShopCount;
	}

	public void setMaxYouzanShopCount(Integer maxYouzanShopCount) {
		this.maxYouzanShopCount = maxYouzanShopCount;
	}

	public String getExpiresTime() {
        return expiresTime;
    }

    public void setExpiresTime(String expiresTime) {
        this.expiresTime = expiresTime;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Shanghai")
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Shanghai")
    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getIncludeModules() {
        return includeModules;
    }

    public void setIncludeModules(String includeModules) {
        this.includeModules = includeModules;
    }

    public Boolean getIsTrial() {
        return isTrial;
    }

    public void setIsTrial(Boolean isTrial) {
        this.isTrial = isTrial;
    }

    public Integer getMaxLiukeWeixinCount() {
        return maxLiukeWeixinCount;
    }

    public void setMaxLiukeWeixinCount(Integer maxLiukeWeixinCount) {
        this.maxLiukeWeixinCount = maxLiukeWeixinCount;
    }

    public Integer getMaxLiukeWeixinMemberCount() {
        return maxLiukeWeixinMemberCount;
    }

    public void setMaxLiukeWeixinMemberCount(Integer maxLiukeWeixinMemberCount) {
        this.maxLiukeWeixinMemberCount = maxLiukeWeixinMemberCount;
    }

    @Override
    public String toString() {
        return "AppLicense{" +
                "id='" + id + '\'' +
                ", appId='" + appId + '\'' +
                ", sign='" + sign + '\'' +
                ", maxAppMemberCount=" + maxAppMemberCount +
                ", maxOfficialAccountsCount=" + maxOfficialAccountsCount +
                ", maxWebSiteCount=" + maxWebSiteCount +
                ", maxAppCount=" + maxAppCount +
                ", maxCallerCount=" + maxCallerCount +
                ", maxMailSenderCount=" + maxMailSenderCount +
                ", maxFileSenderCount=" + maxFileSenderCount +
                ", maxDemoMemberCount=" + maxDemoMemberCount +
                ", maxBIUserCount=" + maxBIUserCount +
                ", maxWeixinAppCount=" + maxWeixinAppCount +
                ", maxYouzanShopCount=" + maxYouzanShopCount +
                ", maxLiukeWeixinCount=" + maxLiukeWeixinCount +
                ", maxLiukeWeixinMemberCount=" + maxLiukeWeixinMemberCount +
                ", expiresTime='" + expiresTime + '\'' +
                ", version=" + version +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", type='" + type + '\'' +
                ", edition='" + edition + '\'' +
                ", includeModules='" + includeModules + '\'' +
                ", isTrial=" + isTrial +
                '}';
    }
}
