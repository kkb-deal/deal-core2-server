package cn.deal.component.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;


public class FilterUtils {

    public static class DateType {
        public static final String TODAY = "today";
        public static final String YESTERDAY = "yesterday";
        public static final String THIS_WEEK = "this_week";
        public static final String LAST_WEEK = "last_week";
        public static final String THIS_MONTH = "this_month";
        public static final String LAST_MONTH = "last_month";
    }

    public static List<String> getTimeRange(int type, String range){
        List<String> ranges = new ArrayList<String>();
        if(type == 0){
            if(DateType.TODAY.equals(range)){
                getTodayTimeRange(ranges);
            } else if(DateType.YESTERDAY.equals(range)){
                getYesterdayTimeRange(ranges);
            } else if(DateType.THIS_WEEK.equals(range)){
                getThisWeekTimeRange(ranges);
            } else if(DateType.LAST_WEEK.equals(range)){
                getLastWeekTimeRange(ranges);
            } else if(DateType.THIS_MONTH.equals(range)){
                getThisMonthTimeRange(ranges);
            } else if(DateType.LAST_MONTH.equals(range)){
                getLastMonthTimeRange(ranges);
            }
        } else if(type == 1){
            String[] rangeTimes = range.split("~");
            if(rangeTimes != null && rangeTimes.length == 2){
                ranges.add(rangeTimes[0]);
                ranges.add(rangeTimes[1]);
            }
        }
        return ranges;
    }

    private static void getTodayTimeRange(List<String> ranges) {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.HOUR_OF_DAY , 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        String startTime = DateUtils.format(startCalendar.getTime());
        ranges.add(startTime);
        String endTime = DateUtils.format(new Date());
        ranges.add(endTime);
    }

    private static void getYesterdayTimeRange(List<String> ranges) {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.add(Calendar.DAY_OF_MONTH, -1);
        startCalendar.set(Calendar.HOUR_OF_DAY , 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        String startTime = DateUtils.format(startCalendar.getTime());
        ranges.add(startTime);
        startCalendar.set(Calendar.HOUR_OF_DAY , 23);
        startCalendar.set(Calendar.MINUTE, 59);
        startCalendar.set(Calendar.SECOND, 59);
        String endTime = DateUtils.format(startCalendar.getTime());
        ranges.add(endTime);
    }

    private static void getThisWeekTimeRange(List<String> ranges) {
        Calendar nowCalendar = Calendar.getInstance();
        Integer dayOfWeek = nowCalendar.get(Calendar.DAY_OF_WEEK);
        Integer dayOfYear = nowCalendar.get(Calendar.DAY_OF_YEAR);
        if(dayOfWeek == 1){
            dayOfWeek = 8;
        }
        nowCalendar.set(Calendar.DAY_OF_YEAR, dayOfYear - dayOfWeek + 2);
        nowCalendar.set(Calendar.HOUR_OF_DAY , 0);
        nowCalendar.set(Calendar.MINUTE, 0);
        nowCalendar.set(Calendar.SECOND, 0);
        String startTime = DateUtils.format(nowCalendar.getTime());
        ranges.add(startTime);

        String endTime = DateUtils.format(new Date());
        ranges.add(endTime);
    }

    private static void getLastWeekTimeRange(List<String> ranges) {
        Calendar startCalendar = Calendar.getInstance();
        Integer dayOfWeek = startCalendar.get(Calendar.DAY_OF_WEEK);
        Integer dayOfYear = startCalendar.get(Calendar.DAY_OF_YEAR);
        if(dayOfWeek == 1){
            dayOfWeek = 8;
        }
        startCalendar.set(Calendar.DAY_OF_YEAR, dayOfYear - dayOfWeek + 1);
        startCalendar.set(Calendar.HOUR_OF_DAY , 23);
        startCalendar.set(Calendar.MINUTE, 59);
        startCalendar.set(Calendar.SECOND, 59);
        String endTime = DateUtils.format(startCalendar.getTime());

        startCalendar.set(Calendar.DAY_OF_YEAR, dayOfYear - dayOfWeek - 5);
        startCalendar.set(Calendar.HOUR_OF_DAY , 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        String startTime = DateUtils.format(startCalendar.getTime());
        ranges.add(startTime);
        ranges.add(endTime);
    }

    private static void getThisMonthTimeRange(List<String> ranges) {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.DAY_OF_MONTH, 1);
        startCalendar.set(Calendar.HOUR_OF_DAY , 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        String startTime = DateUtils.format(startCalendar.getTime());
        ranges.add(startTime);
        String endTime = DateUtils.format(new Date());
        ranges.add(endTime);
    }

    private static void getLastMonthTimeRange(List<String> ranges) {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.add(Calendar.MONTH, -1);
        startCalendar.set(Calendar.DAY_OF_MONTH, 1);
        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        String startTime = DateUtils.format(startCalendar.getTime());
        ranges.add(startTime);

        startCalendar.set(Calendar.DAY_OF_MONTH, getLastMonthDayCount());
        startCalendar.set(Calendar.HOUR_OF_DAY , 23);
        startCalendar.set(Calendar.MINUTE, 59);
        startCalendar.set(Calendar.SECOND, 59);
        String endTime = DateUtils.format(startCalendar.getTime());
        ranges.add(endTime);
    }

    public static int getLastMonthDayCount() {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.add(Calendar.MONTH, -1);
        startCalendar.set(Calendar.DATE, 1);
        startCalendar.roll(Calendar.DATE, -1);
        int maxDate = startCalendar.get(Calendar.DATE);
        return maxDate;
    }

    /*public static void main(String[] args){
        System.out.println("today: " + getTimeRange(0, "today").toString());
        System.out.println("yesterday: " + getTimeRange(0, "yesterday").toString());
        System.out.println("thisWeek: " + getTimeRange(0, "this_week").toString());
        System.out.println("lastWeek: " + getTimeRange(0, "last_week").toString());
        System.out.println("thisMonth: " + getTimeRange(0, "this_month").toString());
        System.out.println("lastMonth: " + getTimeRange(0, "last_month").toString());
        System.out.println("timeRange: " + getTimeRange(1, "2017-12-20 12:32:20~2017-12-26 17:53:20").toString());
    }*/
}
