package cn.deal.core.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.deal.component.kuick.domain.KuickUser;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import java.util.Date;
import java.util.UUID;


@Entity
@Table(name = "deal_app")
public class DealApp {

    public DealApp(){
        super();
    }

    public DealApp(String creatorId, String name, String description, Integer conferenceId, String conferenceGroupId){
        this.id = UUID.randomUUID().toString();
        this.creatorId = creatorId;
        this.name = name;
        this.description = description;
        this.conferenceId = conferenceId;
        this.conferenceGroupId = conferenceGroupId;
        this.status = 0;
        this.androidPackageName = null;
        this.iosBundleId = null;
        this.iconURL = null;
        this.iconWidth = null;
        this.iconHeight = null;
        this.createTime = new Date();
        this.editTime = new Date();
    }

    @Id
    private String id;

    /**
     * 项目创建人Id
     */
    @Column(name="creatorId")
    private String creatorId;

    /**
     * 项目名称
     */
    @Column(name="name")
    private String name;

    /**
     * 项目类型
     */
    @Column(name="type")
    private String type;

    /**
     * 项目描述
     */
    @Column(name="description")
    private String description;

    /**
     * 项目图标地址
     */
    @Column(name="iconURL")
    private String iconURL;

    /**
     * 图标宽度
     */
    @Column(name="iconWidth")
    private Integer iconWidth;

    /**
     * 图标高度
     */
    @Column(name="iconHeight")
    private Integer iconHeight;

    /**
     * ios包名
     */
    @Column(name="iosBundleId")
    private String iosBundleId;

    /**
     * android包名
     */
    @Column(name="androidPackageName")
    private String androidPackageName;

    /**
     * 会议id
     */
    @Column(name="conferenceId")
    private Integer conferenceId;

    /**
     * 项目密钥
     */
    @Column(name="secret")
    private String secret;

    /**
     * 项目状态
     */
    @Column(name="status")
    private Integer status;

    /**
     * 重定向URL
     */
    @Column(name="redirectUri")
    private String redirectUri;

    /**
     * 会议组Id
     */
    @Column(name="conferenceGroupId")
    private String conferenceGroupId;

    /**
     * 项目创建时间
     */
    @Column(name="createTime")
    private Date createTime;

    /**
     * 项目修改时间
     */
    @Column(name="editTime")
    private Date editTime;
    
    @Transient
    private KuickUser user;
    
    @Transient
    private boolean expired;
    
    
    @Transient
    @JsonIgnore
    private Integer appType;
    
    /**
     * 过期时间
     */
    @Transient
    private Date expiresTime;
    
    public boolean isExpired() {
		return expired;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIconURL() {
		return iconURL;
	}

	public void setIconURL(String iconURL) {
		this.iconURL = iconURL;
	}

	public String getIosBundleId() {
		return iosBundleId;
	}

	public void setIosBundleId(String iosBundleId) {
		this.iosBundleId = iosBundleId;
	}

	public String getAndroidPackageName() {
		return androidPackageName;
	}

	public void setAndroidPackageName(String androidPackageName) {
		this.androidPackageName = androidPackageName;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	public String getConferenceGroupId() {
		return conferenceGroupId;
	}

	public void setConferenceGroupId(String conferenceGroupId) {
		this.conferenceGroupId = conferenceGroupId;
	}

	public Integer getIconWidth() {
		return iconWidth;
	}

	public Integer getIconHeight() {
		return iconHeight;
	}

	public Integer getConferenceId() {
		return conferenceId;
	}

	public Integer getStatus() {
		return status;
	}

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    public Date getEditTime() {
        return editTime;
    }

    public void setEditTime(Date editTime) {
        this.editTime = editTime;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    @Override
    public String toString() {
        return "DealApp{" +
                "id='" + id + '\'' +
                ", creatorId='" + creatorId + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", iconURL='" + iconURL + '\'' +
                ", iconWidth=" + iconWidth +
                ", iconHeight=" + iconHeight +
                ", iosBundleId='" + iosBundleId + '\'' +
                ", androidPackageName='" + androidPackageName + '\'' +
                ", conferenceId=" + conferenceId +
                ", secret='" + secret + '\'' +
                ", status=" + status +
                ", redirectUri='" + redirectUri + '\'' +
                ", conferenceGroupId='" + conferenceGroupId + '\'' +
                ", createTime=" + createTime +
                ", editTime=" + editTime +
                '}';
    }

	public KuickUser getUser() {
		return user;
	}

	public void setUser(KuickUser user) {
		this.user = user;
	}

	public void setIconWidth(Integer iconWidth) {
		this.iconWidth = iconWidth;
	}

	public void setIconHeight(Integer iconHeight) {
		this.iconHeight = iconHeight;
	}

	public void setConferenceId(Integer conferenceId) {
		this.conferenceId = conferenceId;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    public Date getExpiresTime() {
        return expiresTime;
    }

    public void setExpiresTime(Date expiresTime) {
        this.expiresTime = expiresTime;
    }
    
}
