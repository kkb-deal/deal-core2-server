package cn.deal.core.app.resource.vo;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.deal.component.kuick.domain.KuickUser;
import cn.deal.core.app.domain.AppMember;

public class AppMemberVO {

    private String id;

    /**
     * 项目id
     */
    private String appId;

    /**
     * kuick用户id
     */
    private Integer kuickUserId;
    
    private String role; 
    
    private boolean isOwner;

    private boolean owner;
    
    /**
     * 会议id
     */
    private Integer conferenceId;

    /**
     * 成员状态，0：正常、1：移除
     */
    private Integer status;

    private Date createTime;

    private Date editTime;
    
    private List<String> postRoles; 
    
    private KuickUser user;
    
    private AppMember dealAppMember;

    private String remarkName;
    
	public AppMemberVO() {
		super();
	}

	public AppMemberVO(AppMember dealAppMember) {
        BeanUtils.copyProperties(dealAppMember, this, "postRoles");
        
        if (StringUtils.isNotBlank(dealAppMember.getPostRoles())) {
            this.postRoles = Arrays.asList(dealAppMember.getPostRoles().split(","));
        }
        
        this.dealAppMember = dealAppMember;
	}

	public List<String> getPostRoles() {
		return postRoles;
	}

	public void setPostRoles(List<String> postRoles) {
		this.postRoles = postRoles;
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

    public Integer getKuickUserId() {
        return kuickUserId;
    }

    public void setKuickUserId(Integer kuickUserId) {
        this.kuickUserId = kuickUserId;
    }

    public Integer getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(Integer conferenceId) {
        this.conferenceId = conferenceId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    
    public KuickUser getUser() {
		return user;
	}

	public void setUser(KuickUser user) {
		this.user = user;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean isIsOwner() {
		return isOwner;
	}

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }

    public void setIsOwner(boolean isOwner) {
		this.isOwner = isOwner;
	}

    public String getRemarkName() {
        return remarkName;
    }

    public void setRemarkName(String remarkName) {
        this.remarkName = remarkName;
    }

    @Override
    public String toString() {
        return "AppMemberVO{" +
                "id='" + id + '\'' +
                ", appId='" + appId + '\'' +
                ", kuickUserId=" + kuickUserId +
                ", role='" + role + '\'' +
                ", isOwner=" + isOwner +
                ", owner=" + owner +
                ", conferenceId=" + conferenceId +
                ", status=" + status +
                ", createTime=" + createTime +
                ", editTime=" + editTime +
                ", postRoles=" + postRoles +
                ", user=" + user +
                ", dealAppMember=" + dealAppMember +
                ", remarkName='" + remarkName + '\'' +
                '}';
    }
}
