package cn.deal.component.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

/**
 * 参数辅助工具
 *
 */
public class ParamUtils {

	protected static final HashMap<String, String> EMPTY_MAP = new HashMap<String, String>();

	/**
	 * 将扩展字段解码为Map
	 * 
	 * @param encodedExtsStr
	 * @return
	 */
	public static Map<String, String> decodeBase64JSONAsMap(String encodedExtsStr) {
		if (StringUtils.isNotBlank(encodedExtsStr)) {
			String extsStr = Base64Utils.decodeStr(encodedExtsStr);
			
			if (StringUtils.isNotBlank(extsStr)) {
				return JsonUtil.jsonToMap(extsStr);
			}
		}
		
		return EMPTY_MAP;
	}

	/**
	 * 将字符串数组编码为Base64 Map
	 * 
	 * @param datas
	 * @return
	 */
	public static String encodeMapAsJSONBase64(String[] datas) {
		Map<String, Object> map = MapUtils.from(datas);
		return Base64Utils.encodeStr(JsonUtil.toJson(map));
	}
	
	
	/**
	 * 提取参数
	 * 
	 * @param request
	 * @return
	 */
	public static Map<String, String> extractParams(HttpServletRequest request) {
		Map<String, String> result = new TreeMap<String, String>();
		
		Map<String, String[]> map = request.getParameterMap();
		if (map!=null) {
			for(String key: map.keySet()) {
				result.put(key, String.join(",", map.get(key)));
			}
		}
		
		return result;
	}

	

}
