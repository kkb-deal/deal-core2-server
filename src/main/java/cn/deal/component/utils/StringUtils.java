package cn.deal.component.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;

public class StringUtils {
	private static final SnakeCaseStrategy snakeCaseStrategy = new SnakeCaseStrategy();
	
	/** 
	* @Title: listToString 
	* @Description: TODO(list转string) 
	* @param list
	* @return 设定文件 
	* @return String    返回类型 
	* @throws 
	*/
	public static String listToString(List<String> list) {
		if (list == null) {
			return null;
		}
		
		StringBuilder result = new StringBuilder();
		boolean first = true;
		// 第一个前面不拼接","
		
		for (String string : list) {
			
			if (first) {
				first = false;
			} else {
				result.append(",");
			}
			result.append(string);
		}
		
		return result.toString();
	}
	
	
	/** 
	* @Title: stringToList 
	* @Description: TODO(String 转list) 
	* @param strs
	* @return 设定文件 
	* @return List<String>    返回类型 
	* @throws 
	*/
	private List<String> stringToList(String strs) {
		String str[] = strs.split(",");
		return Arrays.asList(str);
	}
	
	public static String join(Collection var0, String var1) {
        StringBuffer var2 = new StringBuffer();

        for(Iterator var3 = var0.iterator(); var3.hasNext(); var2.append((String)var3.next())) {
            if(var2.length() != 0) {
                var2.append(var1);
            }
        }

        return var2.toString();
    }
	
	/**
	 * 转换为驼峰格式
	 * 
	 * @param text
	 * @return
	 */
	public static String toSnakeCase(String text) {
		return snakeCaseStrategy.translate(text);
	}
}
