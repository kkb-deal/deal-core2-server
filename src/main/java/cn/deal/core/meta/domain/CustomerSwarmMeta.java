package cn.deal.core.meta.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Tolerate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;


/**
*
* 项目名称：deal-core2-server
* 类名称：CustomerSwarmMeta
* 类描述：客户分群元数据信息
*/
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer_swarm_meta")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerSwarmMeta implements Serializable {

	private static final long serialVersionUID = 5305269240237050476L;

	@Id
	private String id;

	/**
	 * 项目id
	 */
	@Column(name="app_id")
	private String appId;

	/**
	 * 元数据列名
	 */
	@Column(name="name")
	private String name;

	/**
	 * 元数据列名
	 */
	@Column(name="`type`")
	private String type;

	/**
	 * 元数据字段类型
	 */
	@Column(name="title")
	private String title;

	/**
	 * 是否可见:1:可见 0:不可见
	 */
	@Column(name="visible")
	private Boolean visible;

	/**
	 * 排序用
	 */
	@Column(name="`index`")
	private Integer index;

	/**
	 * 状态 1:启用 0:禁用
	 */
	@Column(name="status")
	private Boolean status;

	/**
	 * 备注
	 */
	@Column(name="description")
	private String description;

	/**
	 * 插槽，即所属的预置字段名称
	 */
	@Column(name="slot")
	private String slot;

	/**
	 * 创建时间
	 */
	@Column(name = "created")
	private Date created;

	/**
	 * 最后修改时间
	 */
	@Column(name = "last_modified")
	private Date lastModified;


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

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSlot() {
		return slot;
	}

	public void setSlot(String slot) {
		this.slot = slot;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	@Override
	public String toString() {
		return "CustomerSwarmMeta{" +
				"id='" + id + '\'' +
				", appId='" + appId + '\'' +
				", name='" + name + '\'' +
				", type='" + type + '\'' +
				", title='" + title + '\'' +
				", visible=" + visible +
				", index=" + index +
				", status=" + status +
				", description='" + description + '\'' +
				", slot='" + slot + '\'' +
				", created=" + created +
				", lastModified=" + lastModified +
				'}';
	}
}
