package cn.deal.core.license.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = {"license"})
public class License {

    public static final String DEFAULT_VERSION = "personal";

    /**
     * 最大项目成员数
     */
    private int maxAppMemberCount = 0;

    /**
     * 最大关联公众号数
     */
    private int maxOfficialAccountsCount = 0;

    /**
     * 最大关联网站数
     */
    private int maxWebSiteCount = 0;

    /**
     * 最大关联App数量
     */
    private int maxAppCount = 0;

    /**
     * 最大外呼成员数
     */
    private int maxCallerCount = 0;

    /**
     * 最大邮件群发人数
     */
    private int maxSenderCount = 0;

    /**
     * 最大发资料人数
     */
    private int maxFileSenderCount = 0;

    /**
     * 最大远程演示人数
     */
    private int maxDemoMemberCount = 0;

    /**
     * 最大BI用户数
     */
    private int maxBIUserCount = 0;
    
    /**
     * 最大微信小程序数
     * 
     */
    private int maxWeixinAppCount;
    
    /**
     * 最大有赞店铺数
     */
    private int maxYouzanShopCount = 0;

    /**
     * 留客最大微信号数量
     */
    private int maxLiukeWeixinCount = 1;

    /**
     * 最大留客微信成员数
     */
    private int maxLiukeWeixinMemberCount = 10;

    /**
     * 过期时间
     */
    private Date expiresTime;

    /**
     * 系统当前时间
     */
    private Date currentTime;

    //是否过期
    private boolean expired = true;

    private AppLicense appLicense;

    /**
     * personal
     * personal_juke
     * enterprise
     * enterprise_juke
     * ultimate
     * ultimate_juke
     **/
    private String edition; 
    private String includeModules;
    private Boolean isTrial;


    public License() {
        super();
    }


    public License(AppLicense appLicense, Date expiresTime, boolean expired) {
    	this.appLicense = appLicense;
    	
        this.maxAppMemberCount = appLicense.getMaxAppMemberCount();
        this.maxAppCount = appLicense.getMaxAppCount();
        this.maxBIUserCount = appLicense.getMaxBIUserCount();
        this.maxCallerCount = appLicense.getMaxCallerCount();
        this.maxDemoMemberCount = appLicense.getMaxDemoMemberCount();
        this.maxFileSenderCount = appLicense.getMaxFileSenderCount();
        this.maxOfficialAccountsCount = appLicense.getMaxOfficialAccountsCount();
        this.maxSenderCount = appLicense.getMaxMailSenderCount();
        this.maxWebSiteCount = appLicense.getMaxWebSiteCount();

        if (appLicense.getMaxLiukeWeixinMemberCount() != null) {
            this.maxLiukeWeixinMemberCount = appLicense.getMaxLiukeWeixinMemberCount();
        }

        if (appLicense.getMaxWeixinAppCount() != null) {
            this.maxWeixinAppCount = appLicense.getMaxWeixinAppCount();
        }

        if (appLicense.getMaxYouzanShopCount() != null) {
            this.maxYouzanShopCount = appLicense.getMaxYouzanShopCount();
        }

        if (appLicense.getMaxLiukeWeixinCount() != null) {
            this.maxLiukeWeixinCount= appLicense.getMaxLiukeWeixinCount();
        }

        this.edition = appLicense.getEdition();
        this.expiresTime = expiresTime;
        this.expired = expired;
        
        this.includeModules = appLicense.getIncludeModules();
        this.isTrial = appLicense.getIsTrial();
    }


    public int getMaxAppMemberCount() {
        return maxAppMemberCount;
    }


    public void setMaxAppMemberCount(int maxAppMemberCount) {
        this.maxAppMemberCount = maxAppMemberCount;
        if (appLicense != null) {
            maxAppMemberCount = appLicense.getMaxAppMemberCount();
        }
    }


    public int getMaxOfficialAccountsCount() {
        return maxOfficialAccountsCount;
    }


    public void setMaxOfficialAccountsCount(int maxOfficialAccountsCount) {
        this.maxOfficialAccountsCount = maxOfficialAccountsCount;

        if (appLicense != null) {
            this.maxOfficialAccountsCount = appLicense.getMaxOfficialAccountsCount();
        }
    }


    public int getMaxWebSiteCount() {
        return maxWebSiteCount;
    }


    public void setMaxWebSiteCount(int maxWebSiteCount) {
        this.maxWebSiteCount = maxWebSiteCount;

        if (appLicense != null) {
            this.maxWebSiteCount = appLicense.getMaxWebSiteCount();
        }
    }


    public int getMaxAppCount() {
        return maxAppCount;
    }


    public void setMaxAppCount(int maxAppCount) {
        this.maxAppCount = maxAppCount;

        if (appLicense != null) {
            this.maxAppCount = appLicense.getMaxAppCount();
        }
    }


    public int getMaxCallerCount() {
        return maxCallerCount;
    }


    public void setMaxCallerCount(int maxCallerCount) {
        this.maxCallerCount = maxCallerCount;

        if (appLicense != null) {
            this.maxCallerCount = appLicense.getMaxCallerCount();
        }
    }


    public int getMaxSenderCount() {
        return maxSenderCount;
    }


    public void setMaxSenderCount(int maxSenderCount) {
        this.maxSenderCount = maxSenderCount;

        if (appLicense != null) {
            this.maxSenderCount = appLicense.getMaxMailSenderCount();
        }
    }


    public int getMaxWeixinAppCount() {
        return maxWeixinAppCount;
    }


    public void setMaxWeixinAppCount(int maxWeixinAppCount) {
        this.maxWeixinAppCount = maxWeixinAppCount;

        if (appLicense != null) {
            this.maxWeixinAppCount = appLicense.getMaxWeixinAppCount();
        }
    }

   
    public int getMaxYouzanShopCount() {
		return maxYouzanShopCount;
	}


	public void setMaxYouzanShopCount(int maxYouzanShopCount) {
		this.maxYouzanShopCount = maxYouzanShopCount;
		
		if (appLicense != null) {
            this.maxYouzanShopCount = appLicense.getMaxYouzanShopCount();
        }
	}


	public int getMaxFileSenderCount() {
        return maxFileSenderCount;
    }


    public void setMaxFileSenderCount(int maxFileSenderCount) {
        this.maxFileSenderCount = maxFileSenderCount;

        if (appLicense != null) {
            this.maxFileSenderCount = appLicense.getMaxFileSenderCount();
        }
    }


    public int getMaxDemoMemberCount() {
        return maxDemoMemberCount;
    }


    public void setMaxDemoMemberCount(int maxDemoMemberCount) {
        this.maxDemoMemberCount = maxDemoMemberCount;

        if (appLicense != null) {
            this.maxDemoMemberCount = appLicense.getMaxDemoMemberCount();
        }
    }


    public int getMaxBIUserCount() {
        return maxBIUserCount;
    }


    public void setMaxBIUserCount(int maxBIUserCount) {
        this.maxBIUserCount = maxBIUserCount;

        if (appLicense != null) {
            this.maxBIUserCount = appLicense.getMaxBIUserCount();
        }
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    public Date getExpiresTime() {
        return expiresTime;
    }


    public void setExpiresTime(Date expiresTime) {
        this.expiresTime = expiresTime;

    }


    public boolean isExpired() {
        return expired;
    }


    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    public Date getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(Date currentTime) {
        this.currentTime = currentTime;
    }

    public String getIncludeModules() {
        return includeModules;
    }

    public void setIncludeModules(String includeModules) {
        this.includeModules = includeModules;
        if (appLicense != null) {
            this.includeModules = appLicense.getIncludeModules();
        }
    }

    public Boolean getIsTrial() {
        return isTrial;
    }

    public void setIsTrial(Boolean isTrial) {
        this.isTrial = isTrial;
        if (appLicense != null) {
            this.isTrial = appLicense.getIsTrial();
        }
    }

    public int getMaxLiukeWeixinCount() {
        return maxLiukeWeixinCount;
    }

    public void setMaxLiukeWeixinCount(int maxLiukeWeixinCount) {
        this.maxLiukeWeixinCount = maxLiukeWeixinCount;
    }

    public int getMaxLiukeWeixinMemberCount() {
        return maxLiukeWeixinMemberCount;
    }

    public void setMaxLiukeWeixinMemberCount(int maxLiukeWeixinMemberCount) {
        this.maxLiukeWeixinMemberCount = maxLiukeWeixinMemberCount;
    }

    @Override
    public String toString() {
        return "License{" +
                "maxAppMemberCount=" + maxAppMemberCount +
                ", maxOfficialAccountsCount=" + maxOfficialAccountsCount +
                ", maxWebSiteCount=" + maxWebSiteCount +
                ", maxAppCount=" + maxAppCount +
                ", maxCallerCount=" + maxCallerCount +
                ", maxSenderCount=" + maxSenderCount +
                ", maxFileSenderCount=" + maxFileSenderCount +
                ", maxDemoMemberCount=" + maxDemoMemberCount +
                ", maxBIUserCount=" + maxBIUserCount +
                ", maxWeixinAppCount=" + maxWeixinAppCount +
                ", maxLiukeWeixinCount=" + maxLiukeWeixinCount +
                ", maxLiukeWeixinMemberCount=" + maxLiukeWeixinMemberCount +
                ", expiresTime=" + expiresTime +
                ", currentTime=" + currentTime +
                ", expired=" + expired +
                ", appLicense=" + appLicense +
                ", edition='" + edition + '\'' +
                ", includeModules='" + includeModules + '\'' +
                ", isTrial=" + isTrial +
                '}';
    }
}
