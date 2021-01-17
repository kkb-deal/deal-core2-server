package cn.deal.core.customerswarm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.beanutils.BeanUtils;

import javax.persistence.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 客户分群
 */
@Data
@NoArgsConstructor
@ToString
@Entity
@Table(name = "customer_swarm")
public class CustomerSwarm {

	@Id
	private String id;
	@Column(name = "appId")
	private String appId;
	@Column(name = "kuickUserId")
	private String kuickUserId;
	@Column(name = "name")
	private String name;
	@Column(name = "photoUrl")
	private String photoUrl;
	@Column(name = "comment")
	private String comment;
	@Column(name = "status")
    /**
     * 状态
     * 1：正常
     * 0：删除
     */
	private Integer status;
	@Column(name = "type")
	private Integer type;
	@Column(name = "filterId")
	private String filterId;
    @Column(name = "ext_01")
    private String ext01;
    @Column(name = "ext_02")
    private String ext02;
    @Column(name = "ext_03")
    private String ext03;
    @Column(name = "ext_04")
    private String ext04;
    @Column(name = "ext_05")
    private String ext05;
    @Column(name = "createdAt")
    private Date createdAt;
    @Column(name = "updatedAt")
    private Date updatedAt;
	@PreUpdate
	public void onUpdate(){
		this.updatedAt = new Date();
	}

    /**
     * 共享状态
     * 1：共享给别人
     * 2：别人共享给自己的
     */
	@Transient
	private Integer shareType;
    /**
     * 如果是共享给别人的，需要统计共享给了几个人
     */
	@Transient
	private Integer shareCount;
    /**
     * 如果是别人共享的，该值为共享人用户名
     */
	@Transient
	private String userName;
	@Transient
	private Integer customerCount;
    /**
     * 如果有共享给别人，该字段值不为空
     * APP(1):共享给整个项目
     * APP_MEMBER(0)：共享给项目成员
     */
	@Transient
	private Integer targetType;
	
	public CustomerSwarm(String id, String appId, String kuickUserId, String name, String photoUrl, String comment,
			Integer status, Date createdAt, Date updatedAt, Integer type, String filterId) {
		super();
		this.id = id;
		this.appId = appId;
		this.kuickUserId = kuickUserId;
		this.name = name;
		this.photoUrl = photoUrl;
		this.comment = comment;
		this.status = status;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.type = type;
		this.filterId = filterId;
	}
	public CustomerSwarm(String appId, String kuickUserId, String name, String photoUrl, String comment, Integer type, String filterId) {
		super();
		Date cur = new Date();
		this.id = UUID.randomUUID().toString();
		this.appId = appId;
		this.kuickUserId = kuickUserId;
		this.name = name;
		this.photoUrl = photoUrl;
		this.comment = comment;
		this.status = 1;
		this.createdAt = cur;
		this.updatedAt = cur;
		this.type = type;
		this.filterId = filterId;
	}
	public enum Status{
        /**
         * 标记删除
         */
		DELETED(0),
		UNDELETE(1);
		
		private int val;
		private Status(int val){
			this.val = val;
		}
		public int getVal(){
			return val;
		}
	}

	public enum ShareType{
        /**
         * 别人共享给自己
         */
		TO_ME(2),
        /**
         * 共享给别人的
         */
		TO_OTHERS(1);
		
		private int val;
		private ShareType(int val){
			this.val = val;
		}
		public int getVal(){
			return val;
		}
	}

    /**
     * 分群类型
     */
    public enum Type {
        /**
         * 普通分群
         */
        BASE(1),
        /**
         * 虚拟分群
         */
        VIRTUAL(2);
        private int val;
        public int getVal() {
            return val;
        }
        private Type(int val) {
            this.val = val;
        }
    }
    public void setBySlot(String slot, String val){
        if(slot == null || "".equals(slot.trim())){
            return;
        }

        switch (slot) {
            case "ext_01":
                this.setExt01(val);
                return;
            case "ext_02":
                this.setExt02(val);
                return;
            case "ext_03":
                this.setExt03(val);
                return;
            case "ext_04":
                this.setExt04(val);
                return;
            case "ext_05":
                this.setExt05(val);
                return;
            default:
                return;
        }
    }
    public String getBySlot(String slot){
        if(slot == null || "".equals(slot.trim())){
            return null;
        }

        switch (slot) {
            case "ext_01":
                return this.getExt01();
            case "ext_02":
                return this.getExt02();
            case "ext_03":
                return this.getExt03();
            case "ext_04":
                return this.getExt04();
            case "ext_05":
                return this.getExt05();
            default:
                return null;
        }
    }
    public Map toMap() {
        Map<String, String> map = new HashMap<>();
        try {
            map =  BeanUtils.describe(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
       return map;
    }
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    public Date getCreatedAt() {
        return createdAt;
    }
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    public Date getUpdatedAt() {
        return updatedAt;
    }
}
