package cn.deal.core.app.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name="invite")
@GenericGenerator(name = "invite-uuid", strategy = "uuid")
public class Invite implements Serializable {

	private static final long serialVersionUID = -7467445201989945822L;

	@Id
    @GeneratedValue(generator = "invite-uuid")
    private String id;

    @Column
    private String code;

    @Column
    private String appId;

    private String inviterId;

    private String departmentId;

    private String roles;

    private String postRoles;

    @Column
    private Date expireTime;

    @Column
    private Date createdAt;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Date getExpireTime() {
        return this.expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Invite{id='" + this.id + '\'' + ", code='" + this.code + '\'' + ", appId='" + this.appId + '\'' + ", expireTime=" + this.expireTime + ", createdAt=" + this.createdAt + '}';
    }

    public static Invite.InviteBuilder builder() {
        return new Invite.InviteBuilder();
    }

    public String getInviterId() {
        return this.inviterId;
    }

    public String getDepartmentId() {
        return this.departmentId;
    }

    public String getRoles() {
        return this.roles;
    }

    public String getPostRoles() {
        return this.postRoles;
    }

    public void setInviterId(String inviterId) {
        this.inviterId = inviterId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public void setPostRoles(String postRoles) {
        this.postRoles = postRoles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Invite invite = (Invite) o;
        return Objects.equals(getId(), invite.getId()) &&
                Objects.equals(getCode(), invite.getCode()) &&
                Objects.equals(getAppId(), invite.getAppId()) &&
                Objects.equals(getInviterId(), invite.getInviterId()) &&
                Objects.equals(getDepartmentId(), invite.getDepartmentId()) &&
                Objects.equals(getRoles(), invite.getRoles()) &&
                Objects.equals(getPostRoles(), invite.getPostRoles()) &&
                Objects.equals(getExpireTime(), invite.getExpireTime()) &&
                Objects.equals(getCreatedAt(), invite.getCreatedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCode(), getAppId(), getInviterId(), getDepartmentId(), getRoles(), getPostRoles(), getExpireTime(), getCreatedAt());
    }

    public Invite() {
    }

    public Invite(String id, String code, String appId, String inviterId, String departmentId, String roles, String postRoles, Date expireTime, Date createdAt) {
        this.id = id;
        this.code = code;
        this.appId = appId;
        this.inviterId = inviterId;
        this.departmentId = departmentId;
        this.roles = roles;
        this.postRoles = postRoles;
        this.expireTime = expireTime;
        this.createdAt = createdAt;
    }

    public static class InviteBuilder {
        private String id;
        private String code;
        private String appId;
        private String inviterId;
        private String departmentId;
        private String roles;
        private String postRoles;
        private Date expireTime;
        private Date createdAt;

        InviteBuilder() {
        }

        public Invite.InviteBuilder id(String id) {
            this.id = id;
            return this;
        }

        public Invite.InviteBuilder code(String code) {
            this.code = code;
            return this;
        }

        public Invite.InviteBuilder appId(String appId) {
            this.appId = appId;
            return this;
        }

        public Invite.InviteBuilder inviterId(String inviterId) {
            this.inviterId = inviterId;
            return this;
        }

        public Invite.InviteBuilder departmentId(String departmentId) {
            this.departmentId = departmentId;
            return this;
        }

        public Invite.InviteBuilder roles(String roles) {
            this.roles = roles;
            return this;
        }

        public Invite.InviteBuilder postRoles(String postRoles) {
            this.postRoles = postRoles;
            return this;
        }

        public Invite.InviteBuilder expireTime(Date expireTime) {
            this.expireTime = expireTime;
            return this;
        }

        public Invite.InviteBuilder createdAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Invite build() {
            return new Invite(this.id, this.code, this.appId, this.inviterId, this.departmentId, this.roles, this.postRoles, this.expireTime, this.createdAt);
        }

        @Override
        public String toString() {
            return "InviteBuilder{" +
                    "id='" + id + '\'' +
                    ", code='" + code + '\'' +
                    ", appId='" + appId + '\'' +
                    ", inviterId='" + inviterId + '\'' +
                    ", departmentId='" + departmentId + '\'' +
                    ", roles='" + roles + '\'' +
                    ", postRoles='" + postRoles + '\'' +
                    ", expireTime=" + expireTime +
                    ", createdAt=" + createdAt +
                    '}';
        }
    }

}

