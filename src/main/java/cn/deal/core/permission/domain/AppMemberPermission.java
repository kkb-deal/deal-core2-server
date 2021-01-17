package cn.deal.core.permission.domain;

import java.util.Date;
import java.util.UUID;
import javax.persistence.*;

import cn.deal.core.app.domain.Department;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import org.hibernate.annotations.GenericGenerator;

/**
 * 项目名称：deal-core-server2
 * 类名称：AppMemberPermission
 */
@Entity
@Builder
@Data
@AllArgsConstructor
@Table(name = "app_member_permission")
@GenericGenerator(name = "amp-uuid", strategy = "uuid")
public class AppMemberPermission {

    public AppMemberPermission(Department department, Integer kuickUserId) {
        this.appId = department.getAppId();
        this.kuickUserId = kuickUserId;
        this.domainType = Permission.DomainType.DEPARTMENT.getVal();
        this.domainId = department.getId();
        this.perm = Perm.ADMIN.getVal();
    }

    public enum WithKuickuser {
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

    public enum Perm {
        /**
         * 管理员
         */
        ADMIN("ADMIN");


        private String val;

        public String getVal() {
            return val;
        }

        Perm(String val) {
            this.val = val;
        }
    }

    @Id
    @GeneratedValue(generator = "amp-uuid")
    private String id;

    /**
     * 项目id
     */
    @Column(name = "appId")
    private String appId;

    /**
     * kuick用户id
     */
    @Column(name = "kuickUserId")
    private Integer kuickUserId;

    /**
     * 业务对象类型，可选值 Sales、Admin
     */
    @Column(name = "domainType")
    private String domainType;

    /**
     * 业务对象ID，Sales: 销售的KuickUserId；Admin: 管理员的KuickUserID
     */
    @Column(name = "domainId")
    private String domainId;

    /**
     * 权限；Admin: 管理权限
     */
    @Column(name = "perm")
    private String perm;

    @Column(name = "createdAt")
    private Date createdAt;

    @Transient
    private String userName;

    @Override
    public String toString() {
        return "AppMemberPermission [id=" + id + ", appId=" + appId + ", kuickUserId=" + kuickUserId + ", domainType="
                + domainType + ", domainId=" + domainId + ", perm=" + perm + ", createdAt=" + createdAt + "]";
    }

    @Tolerate
    public AppMemberPermission() {
        super();
        this.id = UUID.randomUUID().toString();
        this.createdAt = new Date();
    }

    @PrePersist
    private void persist() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = new Date();
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

    public void setDomainType(String domainType) {
        this.domainType = domainType;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public void setPerm(String perm) {
        this.perm = perm;
    }

    public String getDomainType() {
        return domainType;
    }

    public String getDomainId() {
        return domainId;
    }

    public String getPerm() {
        return perm;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


}
