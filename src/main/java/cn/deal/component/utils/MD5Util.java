package cn.deal.component.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

	public static String getMD5(String content){
		return DigestUtils.md5Hex(content);
	}
}
