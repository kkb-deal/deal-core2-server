package cn.deal.core.permission.domain;

import java.util.Arrays;
import java.util.List;

/**
 * 许可版本模块
 */
public class Edition extends Module {

	public static final long serialVersionUID = 1462939739921526482L;
	
	/**
	 * 旗舰版/大客户版本
	 */
	public static final String ULTIMAT = "ultimate";
	
	/**
	 * 企业版/快推9800元套餐付费版本
	 */
	public static final String ENTERPRISE = "enterprise";
	
	/**
	 * 个人版/快推2980元套餐付费版本
	 */
	public static final String PERSONAL = "personal";
	
	/**
	 * 快推2980元套餐付费版本
	 */
	public static final String JUKE = "juke";
	
	/**
	 * 企业留客版/快推9800元套餐付费留客版本
	 */
	public static final String ENTERPRISE_JUKE = "enterprise-juke";
	
	/**
	 * 个人留客版/快推2980元套餐付费留客版本
	 */
	public static final String PERSONAL_JUKE = "personal-juke";
	
	/**
	 * 大客户版本删减-有赞白石版本
	 * 
	 */
	public static final String YOUZAN_BAISHI = "youzan-baishi";
	
	/**
	 * 系统支持的所有版本
	 */
	public static List<String> SUPPORT_EDITIONS = Arrays.asList(
			ULTIMAT, 
    		ENTERPRISE, 
    		PERSONAL,
    		JUKE,
    		ENTERPRISE_JUKE,
    		PERSONAL_JUKE,
    		YOUZAN_BAISHI
    	);
   
	/**
	 * 可见的版本
	 */
    public static List<String> VISIABLE_EDITIONS = Arrays.asList(
    		ULTIMAT, 
    		ENTERPRISE, 
    		PERSONAL,
    		JUKE,
    		YOUZAN_BAISHI
    	);
    
    /**
     * 可选模块
     */
	private List<Module> optionals;

    public List<Module> getOptionals() {
        return optionals;
    }

    public void setOptionals(List<Module> optionals) {
        this.optionals = optionals;
    }

	@Override
	public String toString() {
		return "Edition [optionals=" + optionals + "]";
	}
}
