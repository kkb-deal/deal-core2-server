package cn.deal.core.app.resource.vo;

import cn.deal.core.app.domain.DealApp;

/**   
*    
* 项目名称：deal-core-server2   
* 类名称：App   
* 类描述：
*/
public class AppVO {
	private String appId;
	private String name;
	private String description;
	private DealApp dealApp;

	public AppVO(DealApp dealApp) {
		super();
		this.appId = dealApp.getId();
		this.name = dealApp.getName();
		this.description = dealApp.getDescription();
		this.dealApp = dealApp;
	}

	public String getAppId() {
		return appId;

	}

	public AppVO() {
		super();
	}

	public void setAppId(String appId) {
		this.appId = appId;
		if (dealApp != null) {
			this.appId = dealApp.getId();
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		if (dealApp != null) {
			this.name = dealApp.getName();
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
		if (dealApp != null) {
			this.description = dealApp.getDescription();
		}
	}

}
