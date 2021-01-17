package cn.deal.component.kuick.domain;

import java.util.Date;


public class Conference {

    private Integer id;

    /**
     * 会议创建人
     */
    private Integer creatorId;

    /**
     * 会议创建人姓名
     */
    private String creatorName;

    /**
     * 会议创建人头像
     */
    private String creatorPhotoURL;

    /**
     * 会议名称
     */
    private String name;

    /**
     * 会议内容
     */
    private String content;

    /**
     * 会议状态  0： 未开始， 1：已开始，2：开始过，已结束
     */
    private Integer status;

    /**
     * 是否为默认项目， 是：1， 否：null
     */
    private Integer isDefault;

    /**
     * LOGO地址
     */
    private String logoURL;

    /**
     * 主题
     */
    private String theme;

    /**
     * 背景图URL
     */
    private String bgURL;

    /**
     * 类型，0: 不加载语音的会议；1：正常会议
     */
    private Integer type;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 修改时间
     */
    private Date updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorPhotoURL() {
        return creatorPhotoURL;
    }

    public void setCreatorPhotoURL(String creatorPhotoURL) {
        this.creatorPhotoURL = creatorPhotoURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public String getLogoURL() {
        return logoURL;
    }

    public void setLogoURL(String logoURL) {
        this.logoURL = logoURL;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getBgURL() {
        return bgURL;
    }

    public void setBgURL(String bgURL) {
        this.bgURL = bgURL;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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
        return "Conference{" +
                "id=" + id +
                ", creatorId=" + creatorId +
                ", creatorName='" + creatorName + '\'' +
                ", creatorPhotoURL='" + creatorPhotoURL + '\'' +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", isDefault=" + isDefault +
                ", logoURL='" + logoURL + '\'' +
                ", theme='" + theme + '\'' +
                ", bgURL='" + bgURL + '\'' +
                ", type=" + type +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
