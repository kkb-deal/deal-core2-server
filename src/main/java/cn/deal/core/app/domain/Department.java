package cn.deal.core.app.domain;

import org.hibernate.annotations.GenericGenerator;

import cn.deal.core.app.resource.vo.AppMemberVO;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName Department
 * @Description TODO
 **/
@Entity(name = "department")
@GenericGenerator(name = "dpt-uuid", strategy = "uuid")
public class Department implements Serializable {

    /**
     * 是否递归查询子部门
     */
    public enum Cascade {

        /**
         * 是
         */
        YES(1),

        NO(0);

        private int val;

        public int getVal() {
            return val;
        }

        Cascade(int val) {
            this.val = val;
        }
    }

    /**
     * 是否查询部门成员
     */
    public enum WithMembers {

        /**
         * 是
         */
        YES(1),

        NO(0);

        private int val;

        public int getVal() {
            return val;
        }

        WithMembers(int val) {
            this.val = val;
        }
    }

    @Id
    @GeneratedValue(generator = "dpt-uuid")
    private String id;

    private String appId;

    private String name;

    private String parentId;

    private Date createdAt;

    private Date updatedAt;

    @Transient
    private List<Department> children;

    @Transient
    private List<AppMemberVO> members;

    @Transient
    private boolean hasChild;

    public static Department.DepartmentBuilder builder() {
        return new Department.DepartmentBuilder();
    }

    public String getId() {
        return this.id;
    }

    public String getAppId() {
        return this.appId;
    }

    public String getName() {
        return this.name;
    }

    public String getParentId() {
        return this.parentId;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public Date getUpdatedAt() {
        return this.updatedAt;
    }

    public List<Department> getChildren() {
        return this.children;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setChildren(List<Department> children) {
        this.children = children;
    }

    public List<AppMemberVO> getMembers() {
        return members;
    }

    public void setMembers(List<AppMemberVO> members) {
        this.members = members;
    }

    public boolean isHasChild() {
        return hasChild;
    }

    public void setHasChild(boolean hasChild) {
        this.hasChild = hasChild;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Department;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Department that = (Department) o;
        return getId().equals(that.getId()) &&
                getAppId().equals(that.getAppId()) &&
                getName().equals(that.getName()) &&
                getParentId().equals(that.getParentId()) &&
                getCreatedAt().equals(that.getCreatedAt()) &&
                getUpdatedAt().equals(that.getUpdatedAt()) &&
                getChildren().equals(that.getChildren());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getAppId(), getName(), getParentId(), getCreatedAt(), getUpdatedAt(), getChildren());
    }

    @Override
    public String toString() {
        return "Department(id=" + this.getId() + ", appId=" + this.getAppId() + ", name=" + this.getName() + ", parentId=" + this.getParentId() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ", children=" + this.getChildren() + ")";
    }

    public Department() {
    }

    public Department(String id, String appId, String name, String parentId, Date createdAt, Date updatedAt, List<Department> children) {
        this.id = id;
        this.appId = appId;
        this.name = name;
        this.parentId = parentId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.children = children;
    }

    public static class DepartmentBuilder {
        private String id;
        private String appId;
        private String name;
        private String parentId;
        private Date createdAt;
        private Date updatedAt;
        private List<Department> children;

        DepartmentBuilder() {
        }

        public Department.DepartmentBuilder id(String id) {
            this.id = id;
            return this;
        }

        public Department.DepartmentBuilder appId(String appId) {
            this.appId = appId;
            return this;
        }

        public Department.DepartmentBuilder name(String name) {
            this.name = name;
            return this;
        }

        public Department.DepartmentBuilder parentId(String parentId) {
            this.parentId = parentId;
            return this;
        }

        public Department.DepartmentBuilder createdAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Department.DepartmentBuilder updatedAt(Date updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Department.DepartmentBuilder children(List<Department> children) {
            this.children = children;
            return this;
        }

        public Department build() {
            return new Department(this.id, this.appId, this.name, this.parentId, this.createdAt, this.updatedAt, this.children);
        }

        @Override
        public String toString() {
            return "Department.DepartmentBuilder(id=" + this.id + ", appId=" + this.appId + ", name=" + this.name + ", parentId=" + this.parentId + ", createdAt=" + this.createdAt + ", updatedAt=" + this.updatedAt + ", children=" + this.children + ")";
        }
    }
}
