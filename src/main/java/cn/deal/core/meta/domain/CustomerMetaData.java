package cn.deal.core.meta.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.UUID;


/**
*
* 项目名称：deal-core-server2
* 类名称：CustomerMetaData
* 类描述：客户基本字段类
*/
@Entity
@Builder
@Data
@AllArgsConstructor
@Table(name = "customer_meta_data")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerMetaData {

	public static final Integer TRUE = 1;
	public static final Integer FALSE = 0;

	public enum Type {

		/**
		 * 时间
		 */
		DATE("date");

		private String val;

		public String getVal() {
			return val;
		}

		public void setVal(String val) {
			this.val = val;
		}

		Type(String val) {
			this.val = val;
		}
	}

	@Id
	private String id;

	@Column(name="appId")
	private String appId;

	@Column(name="name")
	private String name;

	@Column(name="`type`")
	private String type;

	@Column(name="title")
	private String title;

	@Column(name="isExt")
	private Boolean isExt;

	@Column(name="`unique`")
	private Boolean unique;

	@Column(name="required")
	private Boolean required;

	@Column(name="visiable")
	private Boolean visiable;

	@Column(name="`index`")
	private Integer index;

	@Column(name="defaultValue")
	private String defaultValue;

	@Column(name="supportFilter")
	private Boolean supportFilter;

	@Column(name="optionValues")
	private String optionValues;

	@Column(name="readonly")
	private Integer readonly;
	/**
	 * 客户管理中，用户字段-微信号做唯一约束
	 */
	@Column(name="uniqueSlot")
	private String uniqueSlot;

    @Column(name="visibleInList")
	private String visibleInList;

    @Column(name="indexInList")
	private Integer indexInList;

    @Column(name="widthInList")
	private String widthInList;

	/**
	 * 创建时间
	 */
	@Column(name = "createdAt")
	private Date createdAt;

	/**
	 * 修改时间
	 */
	@Column(name = "updatedAt")
	private Date updatedAt;


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

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public String getOptionValues() {
		return optionValues;
	}

	public void setOptionValues(String optionValues) {
		this.optionValues = optionValues;
	}

	public Integer getReadonly() {
		return readonly;
	}

	public void setReadonly(Integer readonly) {
		this.readonly = readonly;
	}

	public Boolean getIsExt() {
		return isExt;
	}

	public void setIsExt(Boolean isExt) {
		this.isExt = isExt;
	}

	public Boolean getUnique() {
		return unique;
	}

	public void setUnique(Boolean unique) {
		this.unique = unique;
	}

	public String getUniqueSlot() {
		return uniqueSlot;
	}

	public void setUniqueSlot(String uniqueSlot) {
		this.uniqueSlot = uniqueSlot;
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public Boolean getVisiable() {
		return visiable;
	}

	public void setVisiable(Boolean visiable) {
		this.visiable = visiable;
	}

	public Boolean getSupportFilter() {
		return supportFilter;
	}

	public void setSupportFilter(Boolean supportFilter) {
		this.supportFilter = supportFilter;
	}

    public String getVisibleInList() {
        return visibleInList;
    }

    public void setVisibleInList(String visibleInList) {
        this.visibleInList = visibleInList;
    }

    public Integer getIndexInList() {
        return indexInList;
    }

    public void setIndexInList(Integer indexInList) {
        this.indexInList = indexInList;
    }

    public String getWidthInList() {
        return widthInList;
    }

    public void setWidthInList(String widthInList) {
        this.widthInList = widthInList;
    }

    @Tolerate
    public CustomerMetaData() {
		super();
	}

	public CustomerMetaData(String appId, String name, String type) {
		super();
		this.id = UUID.randomUUID().toString();
		this.createdAt = new Date();

		this.appId = appId;
		this.name = name;
		this.type = type;

		this.title = name;
		this.isExt = false;
		this.unique = false;
		this.required = false;
		this.visiable = true;
		this.index = 0;
		this.defaultValue = null;
		this.supportFilter = true;
		this.optionValues = null;
		this.readonly = 0;
	}

	public CustomerMetaData(String appId, String name, String type, String title, Boolean isExt, Boolean unique,
			Boolean required, Boolean visiable, Integer index, String defaultValue, Boolean supportFilter,
			String optionValues, Integer readonly, String visibleInList, Integer indexInList, String widthInList) {
		super();
		this.id = UUID.randomUUID().toString();
		this.createdAt = new Date();

		this.appId = appId;
		this.name = name;
		this.type = type;

		this.title = title;
		this.isExt = isExt;
		this.unique = unique;
		this.required = required;
		this.visiable = visiable;
		this.index = index;
		this.defaultValue = defaultValue;
		this.supportFilter = supportFilter;
		this.optionValues = optionValues;
		this.readonly = readonly;
		this.visibleInList = visibleInList;
		this.indexInList = indexInList;
		this.widthInList = widthInList;
	}

    @Override
    public String toString() {
        return "CustomerMetaData{" +
                "id='" + id + '\'' +
                ", appId='" + appId + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", isExt=" + isExt +
                ", unique=" + unique +
                ", required=" + required +
                ", visiable=" + visiable +
                ", index=" + index +
                ", defaultValue='" + defaultValue + '\'' +
                ", supportFilter=" + supportFilter +
                ", optionValues='" + optionValues + '\'' +
                ", readonly=" + readonly +
                ", uniqueSlot='" + uniqueSlot + '\'' +
                ", visibleInList='" + visibleInList + '\'' +
                ", indexInList='" + indexInList + '\'' +
                ", widthInList='" + widthInList + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

}
