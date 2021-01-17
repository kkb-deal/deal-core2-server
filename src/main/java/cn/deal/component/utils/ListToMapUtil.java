package cn.deal.component.utils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;  
import java.util.Map;

/**   
*    
* 项目名称：deal-core-server2   
* 类名称：ListToMapUtil
*/
public class ListToMapUtil {

	 public static <K, V> Map<K, V> listToMap(List<V> list, String keyMethodName,Class<V> c) {  
	        Map<K, V> map = new HashMap<K, V>();  
	        if (list != null) {  
	            try {  
	                Method methodGetKey = c.getMethod(keyMethodName);
	                for (int i = 0; i < list.size(); i++) {
	                    V value = list.get(i);
	                    @SuppressWarnings("unchecked")
	                    K key = (K) methodGetKey.invoke(list.get(i));
	                    map.put(key, value);
	                }
	            } catch (Exception e) {  
	                throw new IllegalArgumentException("field can't match the key!");  
	            }  
	        }  
	  
	        return map;  
	    }  
	 
}
