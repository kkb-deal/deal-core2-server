package cn.deal.core.permission.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Resource {

	public enum Type {
		/**
		 * 主菜单
		 */
		MENU("Menu"),

		/**
		 * 子菜单
		 */
		MENU_ITEM("MenuItem");


		private String val;
		public String getVal() {
			return val;
		}
		Type(String val) {
			this.val = val;
		}
	}

	public Resource() {
	}

	@SuppressWarnings("CopyConstructorMissesField")
	public Resource(Resource resource) {
		this.id = resource.getId();
		this.appId = resource.getAppId();
		this.category = resource.getCategory();
		this.type = resource.getType();
		this.name = resource.getName();
		this.description = resource.getDescription();
		this.parentId = resource.getParentId();
		this.createdAt = resource.getCreatedAt();
		this.updatedAt = resource.getUpdatedAt();
	}

	private String id;
	
	/**
	 * 项目id
	 */
	private String appId;
	
	/**
	 * 区域菜单分类
	 */
	private String category;
	
	/**
	 * 类别
	 */
	private String type;

	private String domainType;

	/**
	 * 菜单名称
	 */
	private String name;


	/**
	 * 菜单描述
	 */
	private String description;
	
	
	private String parentId;

	/**
	 * 创建时间
	 */
	private Date createdAt;

	/**
	 * 修改时间
	 */
	private Date updatedAt;

	private List<Resource> menus;
	
	private List<Resource> children;



	private String url;
	private String icon;
	private int index;


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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
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

	public List<Resource> getMenus() {
		return menus;
	}

	public void setMenus(List<Resource> menus) {
		this.menus = menus;
	}

	public List<Resource> getChildren() {
		return children;
	}

	public void setChildren(List<Resource> children) {
		this.children = children;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getDomainType() {
		return domainType;
	}

	public void setDomainType(String domainType) {
		this.domainType = domainType;
	}

	@Override
	public String toString() {
		return "Resource{" +
				"id='" + id + '\'' +
				", appId='" + appId + '\'' +
				", category='" + category + '\'' +
				", type='" + type + '\'' +
				", domainType='" + domainType + '\'' +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", parentId='" + parentId + '\'' +
				", createdAt=" + createdAt +
				", updatedAt=" + updatedAt +
				", menus=" + menus +
				", children=" + children +
				'}';
	}
}

