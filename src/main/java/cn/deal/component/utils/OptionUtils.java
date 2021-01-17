package cn.deal.component.utils;

import java.util.Map;
import org.apache.commons.lang.StringUtils;

public class OptionUtils {

	/**
	 * 获取字符串
	 * 
	 * @param opts
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	public static String getString(Map<String, String> opts, String key, String defaultVal) {
		String value = opts.get(key);
		
		if (StringUtils.isBlank(value)) {
			value = defaultVal;
		}
		
		return value;
	}
	
	/**
	 * 选项是否相等
	 * 
	 * @param opts
	 * @param key
	 * @param inputValue
	 * @return
	 */
	public static boolean equals(Map<String, Object> opts, String key, String inputValue) {
		String value = (String)opts.get(key);
		
		if (StringUtils.isBlank(value)) {
			return false;
		}
		
		return StringUtils.equals(value, inputValue);
	}

	/**
	 * 是否不为空
	 * 
	 * @param opts
	 * @param key
	 * @return
	 */
	public static boolean isNotBlank(Map<String, Object> opts, String key) {
		String value = (String)opts.get(key);
		return StringUtils.isNotBlank(value);
	}
}
