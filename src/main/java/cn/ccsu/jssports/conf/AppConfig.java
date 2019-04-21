/*
 * Created by Long Duping
 * Date 2019-03-24 17:04
 */
package cn.ccsu.jssports.conf;

import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AppConfig {

    /**
     * 最少签到步数
     */
    private static int minStep = 10000;
    /**
     * 活动开始日期
     */
    private static String activityBeginDate = "2019-04-16 00:00:00";
    /**
     * 活动持续天数
     */
    private static int activityDayCount = 11;

    /**
     * 缓存的活动开始日期和结束日期，避免重复计算
     */
    private static Calendar bd = null;
    private static Calendar ed = null;

    public static void config(int step, String beginDate, int dayCount) {
        minStep = step;
        activityBeginDate = beginDate;
        activityDayCount = dayCount;
    }

    public static int minStep() {
        return minStep;
    }

    public static int activityDayCount() {
        return activityDayCount;
    }

    public static String activityBeginDate() {
        return activityBeginDate;
    }

    public static Calendar beginDate() {
        if (bd != null) {
            return bd;
        }
        Calendar bd = Calendar.getInstance();
        bd.setTime(parseDate(activityBeginDate));
        return bd;
    }

    public static Calendar endDate() {
        if (ed != null) {
            return ed;
        }
        Calendar ed = Calendar.getInstance();
        ed.setTime(parseDate(activityBeginDate));
        ed.add(Calendar.DAY_OF_MONTH, AppConfig.activityDayCount);
        return ed;
    }

    private static Date parseDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d = null;
        try {
            d = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }
}
