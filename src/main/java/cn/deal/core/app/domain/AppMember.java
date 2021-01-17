package cn.deal.core.app.domain;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.deal.component.kuick.domain.KuickUser;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang.StringUtils;


@Entity
@Table(name = "app_member")
@JsonIgnoreProperties(ignoreUnknown=true)
public class AppMember {

    public enum Status {
        /**
         * 正常
         */
        VALID(0),
        INVALID(1);

        private Integer value;

        Status(int value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }
    }

    public enum Role {
        /**
         * 部门管理员
         */
        DEPARTMENT_ADMIN("DepartmentAdmin"),
        ADMIN("Admin"),
        SALES("Sales"),
        APP_MEMBER("AppMember"),
        MASTER("Master");


        private String val;
        public String getVal() {
            return val;
        }
        Role(String val) {
            this.val = val;
        }
    }

    public enum PostRole {
        MARKET_POST("MarketPost"),
        SALES_POST("SalesPost");

        private String val;
        public String getVal() {
            return val;
        }
        PostRole(String val) {
            this.val = val;
        }
    }


    public enum QueryType {
        /**
         * DEFAULT
         */
        ZERO("0"),

        ONE("1");


        private String val;
        public String getVal() {
            return val;
        }
        QueryType(String val) {
            this.val = val;
        }
    }

    /**
     * 是否查询部门信息
     */
    public enum WithDepartment {
        /**
         * 是
         */
        YES(1),
        NO(0);
        private int val;
        public int getVal() {
            return val;
        }
        WithDepartment(int val) {
            this.val = val;
        }
    }

    /**
     * 是否查询用户
     */
    public enum WithKuickuser{
        /**
         * 是
         */
        YES(1),
        /**
         * 否
         */
        NO(0);
        private int val;
        public int getVal() {
            return val;
        }
        WithKuickuser(int val) {
            this.val = val;
        }
    }

    @Id
    private String id;

    /**
     * 项目id
     */
    @Column(name="appId")
    private String appId;

    /**
     * kuick用户id
     */
    @Column(name="kuickUserId")
    private Integer kuickUserId;

    @Column
    private String departmentId;

    @Column
    private String remarkName;

    /**
     * 会议id
     */
    @Column(name="conferenceId")
    private Integer conferenceId;

    /**
     * 成员状态，0：正常、1：移除
     */
    @Column(name="status")
    private Integer status;

    @Column(name="createTime")
    private Date createTime;

    @Column(name="editTime")
    private Date editTime;
    
    @Column(name="postRoles")
    private String postRoles;
    
    @Column(name="role")
    private String role;
    
    @Transient
    private KuickUser user;

    @Transient
    private Department department;

    @PrePersist
    public void initId() {
        this.id = UUID.randomUUID().toString();
    }
    
	public AppMember() {
		super();
	}

	public AppMember(String appId, Integer kuickUserId) {
		super();
		this.appId = appId;
		this.kuickUserId = kuickUserId;
	}

    public AppMember(String appId, Integer kuickUserId, String postRoles) {
        super();
        this.appId = appId;
        this.kuickUserId = kuickUserId;
        this.postRoles = postRoles;
    }

	public AppMember(String appId, Integer kuickUserId, Integer conferenceId){
        this.id = UUID.randomUUID().toString();
        this.appId = appId;
        this.kuickUserId = kuickUserId;
        this.conferenceId = conferenceId;
        this.status = 0;
        this.createTime = new Date();
        this.editTime = new Date();
    }
	
	public String getPostRoles() {
		return postRoles;
	}

	public void setPostRoles(String postRoles) {
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

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getRemarkName() {
        return remarkName;
    }

    public void setRemarkName(String remarkName) {
        this.remarkName = remarkName;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    /**
     * 是否为销售岗位
     *
     * @return
     */
    public boolean checkSalesPost() {
        if (StringUtils.isNotBlank(this.postRoles)) {
            return this.postRoles.contains(PostRole.SALES_POST.getVal());
        }

        return false;
    }

    public String getStringKuickUserId() {
        return String.valueOf(this.kuickUserId);
    }

    @Override
    public String toString() {
        return "DealAppMember{" +
                "id='" + id + '\'' +
                ", appId='" + appId + '\'' +
                ", kuickUserId=" + kuickUserId +
                ", departmentId='" + departmentId + '\'' +
                ", remarkName='" + remarkName + '\'' +
                ", conferenceId=" + conferenceId +
                ", status=" + status +
                ", createTime=" + createTime +
                ", editTime=" + editTime +
                ", postRoles='" + postRoles + '\'' +
                ", role='" + role + '\'' +
                ", user=" + user +
                ", department=" + department +
                '}';
    }

}
