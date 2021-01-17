package cn.deal.component.utils;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;

/**
 * IP工具
 *
 */
public class IPUtils {

	/**
	 * 获取公网IP
	 * 
	 * @param req
	 * @return
	 */
	public static String getPublicIp(HttpServletRequest req) {
		String ip = req.getHeader("x-forwarded-for");
	    if(StringUtils.isBlank(ip) || ip.length()==0 || "UNKNOWN"==ip.toUpperCase()){
	        ip = req.getHeader("Proxy-Client-IP");
	    }
	    
	    if(StringUtils.isBlank(ip) || ip.length()==0 || "UNKNOWN"==ip.toUpperCase()){
	        ip = req.getHeader("WL-Proxy-Client-IP");
	    }
	    
	    if(StringUtils.isBlank(ip) || ip.length()==0 || "UNKNOWN"==ip.toUpperCase()){
	        ip = req.getHeader("HTTP_CLIENT_IP");
	    }
	    
	    if(StringUtils.isBlank(ip) || ip.length()==0 || "UNKNOWN"==ip.toUpperCase()){
	        ip = req.getHeader("HTTP_X_FORWARDED_FOR");
	    }
	    
	    if(StringUtils.isBlank(ip) || ip.length()==0 || "UNKNOWN"==ip.toUpperCase()){
	        ip = req.getRemoteAddr();
	    }

	    if (!StringUtils.isBlank(ip)) {
	        String[] tokens = ip.split(",");
	        
	        if (tokens!=null && tokens.length>0) {
	            ip = tokens[0];
	        }
	    }

	    return ip;
	}

}
