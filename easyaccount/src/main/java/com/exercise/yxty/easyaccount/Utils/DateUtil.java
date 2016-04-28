package com.exercise.yxty.easyaccount.Utils;

import java.util.Calendar;

/**
 * Created by Administrator on 2016/4/13.
 */
public class DateUtil {

    public static String getThisWeek(Calendar calendar,int[] timeArea) {
        //增加此行，将初始化转到工具类内部执行
        if (calendar.get(Calendar.SECOND) != 0) {
            initCalendarToSecond(calendar);
        }

        int gapFromMonday = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        if (gapFromMonday < 0) {
            gapFromMonday = 6;
        }
        calendar.add(Calendar.DAY_OF_MONTH, -gapFromMonday);    //  得到周初的日期
        String start = getDateString(calendar);
        timeArea[0] = (int) (calendar.getTimeInMillis() / 1000);

        calendar.add(Calendar.DAY_OF_MONTH, 7); //  得到周末日期
        calendar.add(Calendar.SECOND, -1);
        String end = getDateString(calendar);
        timeArea[1] = (int) (calendar.getTimeInMillis() / 1000);
        //恢复时分秒为0，方便以后使用
        initCalendarToSecond(calendar);
        return start + "-" + end;
    }

    public static String getThisMonth(Calendar calendar,int[] timeArea) {
        //增加此行，将初始化转到工具类内部执行
        if (calendar.get(Calendar.SECOND) != 0) {
            initCalendarToSecond(calendar);
        }
        calendar.set(Calendar.DAY_OF_MONTH, 1);     //得到当月1日
        String start = getDateString(calendar);
        timeArea[0] = (int) (calendar.getTimeInMillis() / 1000);

        calendar.add(Calendar.MONTH, 1);        //得到下月1日
        calendar.add(Calendar.SECOND,-1);
        timeArea[1] = (int) (calendar.getTimeInMillis() / 1000);
        String end = getDateString(calendar);

//        Log.i("test", "now:" + calendar.get(Calendar.YEAR) + "," + calendar.get(Calendar.MONTH) + "," + calendar.get(Calendar.DAY_OF_MONTH));
        //恢复时分秒为0，方便以后使用
        initCalendarToSecond(calendar);
        return start + "-" + end;
    }

    //返回日期格式为04.1-04.31
    public static String getThisMonthSimple(Calendar calendar,int[] timeArea) {
        //增加此行，将初始化转到工具类内部执行
        if (calendar.get(Calendar.SECOND) != 0) {
            initCalendarToSecond(calendar);
        }

        calendar.set(Calendar.DAY_OF_MONTH, 1);     //得到当月1日
        String start = getDateStringSimple(calendar);
        timeArea[0] = (int) (calendar.getTimeInMillis() / 1000);

        calendar.add(Calendar.MONTH, 1);        //得到下月1日
        calendar.add(Calendar.SECOND,-1);
        timeArea[1] = (int) (calendar.getTimeInMillis() / 1000);
        String end = getDateStringSimple(calendar);

//        Log.i("test", "now:" + calendar.get(Calendar.YEAR) + "," + calendar.get(Calendar.MONTH) + "," + calendar.get(Calendar.DAY_OF_MONTH));
        //恢复时分秒为0，方便以后使用
        initCalendarToSecond(calendar);
        return start + "-" + end;
    }

    public static String getThisMonthSimple(Calendar calendar) {
        //增加此行，将初始化转到工具类内部执行
        if (calendar.get(Calendar.SECOND) != 0) {
            initCalendarToSecond(calendar);
        }

        calendar.set(Calendar.DAY_OF_MONTH, 1);     //得到当月1日
        String start = getDateStringSimple(calendar);

        calendar.add(Calendar.MONTH, 1);        //得到下月1日
        calendar.add(Calendar.SECOND, -1);
        String end = getDateStringSimple(calendar);

//        Log.i("test", "now:" + calendar.get(Calendar.YEAR) + "," + calendar.get(Calendar.MONTH) + "," + calendar.get(Calendar.DAY_OF_MONTH));
        //恢复时分秒为0，方便以后使用
        initCalendarToSecond(calendar);
        return start + "-" + end;
    }

    public static void getThisMonthNoReturn(Calendar calendar,int[] timeArea) {
        //增加此行，将初始化转到工具类内部执行
        if (calendar.get(Calendar.SECOND) != 0) {
            initCalendarToSecond(calendar);
        }

        calendar.set(Calendar.DAY_OF_MONTH, 1);     //得到当月1日
        timeArea[0] = (int) (calendar.getTimeInMillis() / 1000);

        calendar.add(Calendar.MONTH, 1);        //得到下月1日
        calendar.add(Calendar.SECOND,-1);
        timeArea[1] = (int) (calendar.getTimeInMillis() / 1000);

//        Log.i("test", "now:" + calendar.get(Calendar.YEAR) + "," + calendar.get(Calendar.MONTH) + "," + calendar.get(Calendar.DAY_OF_MONTH));
        //恢复时分秒为0，方便以后使用
        initCalendarToSecond(calendar);
    }
    public static void getThisYear(Calendar calendar, int[] timeArea) {
        //增加此行，将初始化转到工具类内部执行
        if (calendar.get(Calendar.SECOND) != 0) {
            initCalendarToSecond(calendar);
        }
        calendar.set(Calendar.MONTH, 0);    //得到当年1月
        calendar.set(Calendar.DAY_OF_MONTH, 1);     //得到当月1日
        timeArea[0] = (int) (calendar.getTimeInMillis() / 1000);

        calendar.add(Calendar.YEAR, 1);        //得到下一年1日
        calendar.add(Calendar.SECOND, -1);
        timeArea[1] = (int) (calendar.getTimeInMillis() / 1000);
        //恢复时分秒为0，方便以后使用
        initCalendarToSecond(calendar);
    }

    public static String getDateString(Calendar calendar) {
        return formatString(calendar.get(Calendar.MONTH)+1) + "月" +
                formatString(calendar.get(Calendar.DAY_OF_MONTH)) + "日";
    }

    public static String getDateStringSimple(Calendar calendar) {
        return formatString(calendar.get(Calendar.MONTH)+1) + "." +
                formatString(calendar.get(Calendar.DAY_OF_MONTH));
    }

    public static String formatString(int date) {
        return date < 10 ? "0" + date : date + "";
    }

    public static void getToday(Calendar calendar, int[] dayArea) {
        //增加此行，将初始化转到工具类内部执行
        if (calendar.get(Calendar.SECOND) != 0) {
            initCalendarToSecond(calendar);
        }
        dayArea[0] = (int) (calendar.getTimeInMillis() / 1000);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.SECOND, -1);
        dayArea[1] = (int) (calendar.getTimeInMillis() / 1000);
        //恢复时分秒为0，方便以后使用
        initCalendarToSecond(calendar);
    }

    public static String getDayFromInt(int dayOfWeek) {
        switch (dayOfWeek) {
            case 1:
                return "周日";
            case 2:
                return "周一";
            case 3:
                return "周二";
            case 4:
                return "周三";
            case 5:
                return "周四";
            case 6:
                return "周五";
            case 7:
                return "周六";
            default:
                return "";
        }

    }

    public static void initCalendarToSecond(Calendar calendar) {
        //24小时制用HOUR_OF_DAY
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set((Calendar.MINUTE), 0);
        calendar.set(Calendar.SECOND, 0);
    }

}
