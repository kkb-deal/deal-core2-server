package cn.deal.component.utils;

import org.springframework.stereotype.Component;

/**
 * 路径处理工具类
 */
@Component
public class PathUtil {
	public static final String SEPARATOR = "/";
	
	/**
	 * 适配基路径， 如果基路径不是以/结尾则添加
	 * @param url
	 * @return
	 */
	public String urlAdapter(String url) {
		
		if(url != null && !"".equals(url)) {
			String lastChar = url.substring(url.lastIndexOf(SEPARATOR));
			if(SEPARATOR.equals(lastChar)) {
				return url;
			}else {
				return url + SEPARATOR;
			}
		}
		return url;
	}
}
