package cn.deal.component.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * 项目名称：deal-core-server2 类名称：DateUtils 类描述：
 */
public class DateUtils {

    public static final String[] pattens = new String[]{
        "yyyy-MM-dd HH:mm:ss.SSS",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd HH:mm",
        "yyyy-MM-dd",
        "yyyy-M-dd"
    };

    /** 
    * @Title: compareDate 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param nowTime
    * @param expireTime
    * @return 设定文件 
    * @return boolean    返回类型 
    * @throws 
    */
    public static boolean getExpired(Date nowTime, Date expireTime) {

        if (nowTime.getTime() > expireTime.getTime()) {

            return true;
        } else if (nowTime.getTime() < expireTime.getTime()) {
            return false;
        } else {// 相等
            return true;
        }
    }

    /**
     * 日期转换成字符串
     * @param date
     * @param patten
     * @return
     */
    public static String format(Date date, String patten){
        SimpleDateFormat sdf = new SimpleDateFormat(patten);
        return sdf.format(date);
    }

    /**
     * 日期转换成字符串,默认格式：yyyy-MM-dd HH:mm:ss
     * @param date
     * @return
     */
    public static String format(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat(pattens[1]);
        return sdf.format(date);
    }

    public static Date parseDate(String dateString) {
        for (int i=0; i< pattens.length; i++ ) {
            DateFormat df = new SimpleDateFormat(pattens[i]);
            Date date = parseDate(dateString, df);
            if (date != null)  {
                return date;
            }
        }
        return null;
    }

    public static Date parseDate(String dateStr, DateFormat df)  {
        try {
            if (dateStr == null || dateStr.trim().length() == 0) {
                return null;
            }
            return df.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }
    
    /** 
    * @Title: isDate 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param dateString
    * @return 设定文件 
    * @return boolean    返回类型 
    * @throws 
    */
    public static boolean isDate(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");

        // SimpleDateFormat format=new SimpleDateFormat("yyyyMM");
        // 设置日期转化成功标识
        boolean dateflag = true;
        // 这里要捕获一下异常信息
        try {
            Date date = format.parse(dateString);
        } catch (ParseException e) {
            dateflag = false;
        } finally {
            // 成功：true ;失败:false
            System.out.println("日期是否满足要求" + dateflag);
        }
        return dateflag;
    }
    
    public static Date getDate(int count, String type, Date createAt) {
        Calendar calendar = Calendar.getInstance();
         calendar.setTime(createAt);
         
        if (type.equals("D")) {
            calendar.add(Calendar.DAY_OF_MONTH, count);
        }

        if (type.equals("M")) {
            calendar.add(Calendar.MONTH, count);
        }
        
        if (type.equals("Y")) {
            calendar.add(Calendar.YEAR, count);
        }

        calendar.set(Calendar.HOUR_OF_DAY, createAt.getHours());  
         calendar.set(Calendar.MINUTE, createAt.getMinutes());  
         calendar.set(Calendar.SECOND, createAt.getSeconds());  
         calendar.set(Calendar.MILLISECOND, createAt.getTimezoneOffset());
         
        System.out.println("增加月份后的日期："+calendar.getTime());
        return calendar.getTime();
    }
    
}
