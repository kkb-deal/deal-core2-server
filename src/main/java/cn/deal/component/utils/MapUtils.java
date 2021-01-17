package cn.deal.component.utils;

import java.util.Map;
import java.util.TreeMap;

import io.netty.util.internal.StringUtil;

/**
 * Map工具
 *
 */
public class MapUtils {

	/**
	 * 将字符串数据转换为Map
	 * 
	 * @param datas
	 * @return
	 */
	public static Map<String, Object> from(Object[] datas) {
		Map<String, Object> map = new TreeMap<String, Object>();
		
		AssertUtils.notNull(datas, "datas不能为空！");
		AssertUtils.assertTrue(datas.length%2==0, "数据的长度必须为偶数");
		
		if (datas!=null) {
			for(int i=0; i<datas.length; i+=2) {
				map.put(String.valueOf(datas[i]), datas[i+1]);
			}
		}
		
		return map;
	}

	/**
	 * Map中对象是否匹配
	 * 
	 * @param map
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean equals(Map<String, Object> map, String key, String value) {
		String val = (String)map.get(key);
		return org.apache.commons.lang3.StringUtils.equals(val, value);
	}

}
