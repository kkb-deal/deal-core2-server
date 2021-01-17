package cn.deal.core.customer.domain;

import cn.deal.component.kuick.domain.KuickUser;
import com.alibaba.druid.util.StringUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.beanutils.BeanUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;


@Data
@ToString
@Entity
@Table(name = "customer")
@JsonIgnoreProperties(ignoreUnknown=true)
public class Customer implements Serializable, Comparable<Customer>{

	private static final long serialVersionUID = 661883670338076022L;
	public static final String DEFAULT_HEAD_URL = "https://img-prod.kuick.cn/user/header/guest.png";

    /**
     * 客户来源字段
     */
	public static final String[] SOURCE_PROPS = new String[]{"source", "from", "createWay", "promoterId", "platform", "fromProvince", "fromCity", "searchKeyword", "fromContentTitle", "fromContentLink",
            "buyedKeyword", "utmMedium", "utmCampaign", "utmContent"};

    /**
     * 客户id
     */
    @Id
    private String id;
    /**
     * 项目id
     */
    @Column
    private String appId;
    /**
     * 姓名
     */
    @Column
    private String name;
    /**
     * 职位
     */
    @Column
    private String title;
    /**
     * 邮箱
     */
    @Column
    private String email;
    /**
     * 手机号
     */
    @Column
    private String phone;
    /**
     * 公司
     */
    @Column
    private String company;
    /**
     *
     */
    @Column
    private String headportraitUrl;
    /**
     * 客户状态： 0：标记删除状态 1:正常状态 2:被合并
     */
    @Column
    private Integer status;
    /**
     * 被合并到的客户ID
     * TODO 名字修改为 mergeCustomerId
     */
    @Column
    private String mergedCustomerId;
    /**
     * 创建时间
     */
    @Column
    private Date createdAt;
    /**
     * 编辑时间
     */
    @Column
    private Date updatedAt;
    /**
     * 分组id
     */
    @Column
    private String groupId;
    /**
     *  性别 0：女，1：男，2：未知
     */
    @Column
    private Integer sex;
    /**
     * 公司详细地址
     */
    @Column
    private String address;
    /**
     * 年龄段
     */
    @Column
    private Integer ageState;
    /**
     * 座机号
     */
    @Column
    private String fixedPhone;
    /**
     * 公司省份
     */
    @Column
    private String province;
    /**
     * 公司城市
     */
    @Column
    private String city;
    /**
     * 公司县区
     */
    @Column
    private String county;
    /**
     * 线索来源【下拉选项】
     */
    @Column
    private String leadSource;
    /**
     * 客户等级
     */
    @Column
    private Integer grade;
    /**
     * 所属行业
     */
    @Column
    private String industry;
    /**
     * 意向度
     */
    @Column
    private Integer intentionality;
    /**
     * 来源
     */
    @Column
    private String source;
    /**
     * utm来源
     */
    @Column(name = "`from`")
    private String from;
    /**
     * 获取方式
     */
    @Column
    private String getWay;
    /**
     * 推广人id
     */
    @Column
    private String promoterId;
    /**
     * 来源内容标题
     */
    @Column
    private String fromContentTitle;
    /**
     * 来源内容链接
     */
    @Column
    private String fromContentLink;
    /**
     * 搜索的关键词
     */
    @Column
    private String searchKeyword;
    /**
     * utm关键词
     */
    @Column
    private String buyedKeyword;
    /**
     * 平台
     */
    @Column
    private String platform;
    /**
     * 创建方式
     */
    @Column
    private Integer createWay;
    /**
     * 区域省份
     */
    @Column
    private String fromProvince;
    /**
     * 区域城市
     */
    @Column
    private String fromCity;
    /**
     * utm媒介
     */
    @Column
    private String utmMedium;
    /**
     * utm活动
     */
    @Column
    private String utmCampaign;
    /**
     * utm内容
     */
    @Column
    private String utmContent;
    @Column
    private Integer isOfficialAccountFans = 0;
    @Column
    private String phoneProvince;
    @Column
    private String phoneCity;
    @Column
    private String phoneISP;
    /**
     * 是否合并，0：未合并，1：已合并
     */
    @Transient
    private Integer whetherMerge;
    /**
     * 是否为新客户，0：否，1：是
     */
    @Transient
    private Integer isNew;
    /**
     * 客户所属人
     */
    @Transient
    @JsonProperty
    private String kuickUserId;
    /**
     * 客户所属分组名
     */
    @Transient
    private String groupName;
    /**
     * 客户最新动态数量
     */
    @Transient
    private Integer newCount;

    @Transient
    private Date assignMemberTime;

    @Transient
    private KuickUser kuickUser;

    /**
     * 扩展字段
     */
    @Transient
    private Map<String, String> extensions;
    /**
     * 合并时，关联的被合并客户IDs, 用于取消合并
     */
    @Transient
    private String mergedCustomerIds;
    /**
     * 合并时，关联的DealUserIDs, 用于取消合并
     */
    @Transient
    private String linkDealUserIds;

    private String uniqueKey1;
    private String uniqueKey2;
    private String uniqueKey3;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getHeadportraitUrl() {
        return headportraitUrl;
    }

    public void setHeadportraitUrl(String headportraitUrl) {
        this.headportraitUrl = headportraitUrl;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMergedCustomerId() {
        return mergedCustomerId;
    }

    public void setMergedCustomerId(String mergedCustomerId) {
        this.mergedCustomerId = mergedCustomerId;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getAgeState() {
        return ageState;
    }

    public void setAgeState(Integer ageState) {
        this.ageState = ageState;
    }

    public String getFixedPhone() {
        return fixedPhone;
    }

    public void setFixedPhone(String fixedPhone) {
        this.fixedPhone = fixedPhone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getLeadSource() {
        return leadSource;
    }

    public void setLeadSource(String leadSource) {
        this.leadSource = leadSource;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public Integer getIntentionality() {
        return intentionality;
    }

    public void setIntentionality(Integer intentionality) {
        this.intentionality = intentionality;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getGetWay() {
        return getWay;
    }

    public void setGetWay(String getWay) {
        this.getWay = getWay;
    }

    public String getPromoterId() {
        return promoterId;
    }

    public void setPromoterId(String promoterId) {
        this.promoterId = promoterId;
    }

    public String getFromContentTitle() {
        return fromContentTitle;
    }

    public void setFromContentTitle(String fromContentTitle) {
        this.fromContentTitle = fromContentTitle;
    }

    public String getFromContentLink() {
        return fromContentLink;
    }

    public void setFromContentLink(String fromContentLink) {
        this.fromContentLink = fromContentLink;
    }

    public String getSearchKeyword() {
        return searchKeyword;
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }

    public String getBuyedKeyword() {
        return buyedKeyword;
    }

    public void setBuyedKeyword(String buyedKeyword) {
        this.buyedKeyword = buyedKeyword;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Integer getCreateWay() {
        return createWay;
    }

    public void setCreateWay(Integer createWay) {
        this.createWay = createWay;
    }

    public String getFromProvince() {
        return fromProvince;
    }

    public void setFromProvince(String fromProvince) {
        this.fromProvince = fromProvince;
    }

    public String getFromCity() {
        return fromCity;
    }

    public void setFromCity(String fromCity) {
        this.fromCity = fromCity;
    }

    public String getUtmMedium() {
        return utmMedium;
    }

    public void setUtmMedium(String utmMedium) {
        this.utmMedium = utmMedium;
    }

    public String getUtmCampaign() {
        return utmCampaign;
    }

    public void setUtmCampaign(String utmCampaign) {
        this.utmCampaign = utmCampaign;
    }

    public String getUtmContent() {
        return utmContent;
    }

    public void setUtmContent(String utmContent) {
        this.utmContent = utmContent;
    }
    
    public Integer getIsOfficialAccountFans() {
		return isOfficialAccountFans;
	}

	public void setIsOfficialAccountFans(Integer isOfficialAccountFans) {
		this.isOfficialAccountFans = isOfficialAccountFans;
	}

	public Integer getWhetherMerge() {
        return whetherMerge;
    }

    public void setWhetherMerge(Integer whetherMerge) {
        this.whetherMerge = whetherMerge;
    }

    public Integer getIsNew() {
        return isNew;
    }

    public void setIsNew(Integer isNew) {
        this.isNew = isNew;
    }

    public String getKuickUserId() {
        return kuickUserId;
    }

    public void setKuickUserId(String kuickUserId) {
        this.kuickUserId = kuickUserId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getNewCount() {
        return newCount;
    }

    public void setNewCount(Integer newCount) {
        this.newCount = newCount;
    }

	public String getPhoneProvince() {
		return phoneProvince;
	}

	public void setPhoneProvince(String phoneProvince) {
		this.phoneProvince = phoneProvince;
	}

	public String getPhoneCity() {
		return phoneCity;
	}

	public void setPhoneCity(String phoneCity) {
		this.phoneCity = phoneCity;
	}

	public String getPhoneISP() {
		return phoneISP;
	}

	public void setPhoneISP(String phoneISP) {
		this.phoneISP = phoneISP;
	}

	public Map<String, String> getExtensions() {
		return extensions;
	}

	public void setExtensions(Map<String, String> extensions) {
		this.extensions = extensions;
	}

	//-----------------------------------------------------
	
	public String getMergedCustomerIds() {
		return mergedCustomerIds;
	}

	public void setMergedCustomerIds(String mergedCustomerIds) {
		this.mergedCustomerIds = mergedCustomerIds;
	}

	public String getLinkDealUserIds() {
		return linkDealUserIds;
	}

	public void setLinkDealUserIds(String linkDealUserIds) {
		this.linkDealUserIds = linkDealUserIds;
	}

    public KuickUser getKuickUser() {
        return kuickUser;
    }

    public void setKuickUser(KuickUser kuickUser) {
        this.kuickUser = kuickUser;
    }

    public String getUniqueKey1() {
        return uniqueKey1;
    }

    public void setUniqueKey1(String uniqueKey1) {
        this.uniqueKey1 = uniqueKey1;
    }

    public String getUniqueKey2() {
        return uniqueKey2;
    }

    public void setUniqueKey2(String uniqueKey2) {
        this.uniqueKey2 = uniqueKey2;
    }

    public String getUniqueKey3() {
        return uniqueKey3;
    }

    public void setUniqueKey3(String uniqueKey3) {
        this.uniqueKey3 = uniqueKey3;
    }

    /**
	 * 是否为新客户
	 * 
	 * @return
	 */
	public boolean isNew() {
		return this.id==null;
	}
	/**
	 * 获取属性值
	 * 
	 * @param propName
	 * @return
	 */
	public String getProperty(String propName) {
		String oldValue = null;
		
		if (this.extensions!=null) {
			oldValue = this.extensions.get(propName);
		}
		
		if (StringUtils.isEmpty(oldValue)) {
			try {
				oldValue = BeanUtils.getProperty(this, propName);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				oldValue = null;
			}
		}
		
		return oldValue;
	}
	/**
	 * 设置客户属性
	 * 
	 * @param propName
	 * @param value
	 */
	public void setProperty(String propName, String value) {
		Field field = null;
		
		try {
			field = this.getClass().getDeclaredField(propName);
		} catch (NoSuchFieldException | SecurityException e) {
			field = null;
		}
		
		if (field!=null) {
			// 基础属性
			try {
				BeanUtils.setProperty(this, propName, value);
			} catch (IllegalAccessException | InvocationTargetException e) {
				
			}
		} else {
			// 扩展属性
			if (this.extensions==null) {
				this.extensions = new TreeMap<String, String>();
			}
			
			this.extensions.put(propName, value);
		}
	}

    /**
     * 来源属性判断
     *
     * @param propName
     * @return
     */
	public static boolean isSourceProperty(String propName) {
        for(int i=0; i<SOURCE_PROPS.length; i++) {
            if (SOURCE_PROPS[i].equals(propName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取唯一值
     * db value = propername + ":" + value
     * @param propName
     * @return
     */
	public String getUniqueValue(String propName) {
	    return propName + ":" + this.getProperty(propName);
    }
	/**
	 * 属性值是否发现变更
	 * 
	 * @param propName
	 * @param value
	 * @return
	 */
	public boolean isChanged(String propName, String value) {
		String oldValue = getProperty(propName);
		return !StringUtils.equals(oldValue, value);
	}

    public int compareTo(Customer o) {
//	    按照创建时间先后排序
        return (int) (this.getCreatedAt().getTime() - o.getCreatedAt().getTime());
    }

    /**
     * 状态
     */
    public enum Status {
        /**
         * 禁用
         */
        DISABLE(0),

        /**
         * 正常
         */
        NORMAL(1),

        /**
         * 被合并
         */
        MERGED(2),

        /**
         * 标记删除
         */
        DELETED(3);

        private int value;

        Status(int value){
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    /**
     * 创建方式
     *
     */
    public enum CreateWay {
        /**
         * 手动创建
         */
        SINGLE_CREATE(6),

        /**
         * 批量创建
         */
        BATCH_IMPORT(7);

        private int value;

        CreateWay(int value){
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    public Date getAssignMemberTime() {
        return assignMemberTime;
    }

    public void setAssignMemberTime(Date assignMemberTime) {
        this.assignMemberTime = assignMemberTime;
    }
}
