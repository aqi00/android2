package com.example.senior.calendar;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.senior.bean.CalendarTransfer;

public class LunarCalendar {
    private int year;
    private int month;
    private int day;
    private String lunarMonth;   //农历的月份
    private boolean leap;
    private int leapMonth = 0;   //闰的是哪个月

    public final static String chineseNumber[] = {"一", "二", "三", "四", "五", "六", "七",
            "八", "九", "十", "十一", "十二"};
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat chineseDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
    private final static long[] lunarInfo = new long[]{0x04bd8, 0x04ae0, 0x0a570,
            0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2,
            0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0,
            0x0ada2, 0x095b0, 0x14977, 0x04970, 0x0a4b0, 0x0b4b5, 0x06a50,
            0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970, 0x06566,
            0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0,
            0x1c8d7, 0x0c950, 0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4,
            0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557, 0x06ca0, 0x0b550,
            0x15355, 0x04da0, 0x0a5d0, 0x14573, 0x052d0, 0x0a9a8, 0x0e950,
            0x06aa0, 0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260,
            0x0f263, 0x0d950, 0x05b57, 0x056a0, 0x096d0, 0x04dd5, 0x04ad0,
            0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b5a0, 0x195a6,
            0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40,
            0x0af46, 0x0ab60, 0x09570, 0x04af5, 0x04970, 0x064b0, 0x074a3,
            0x0ea50, 0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0, 0x0c960,
            0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0,
            0x092d0, 0x0cab5, 0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9,
            0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930, 0x07954, 0x06aa0,
            0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65,
            0x0d530, 0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0, 0x0a4d0,
            0x1d0b6, 0x0d250, 0x0d520, 0x0dd45, 0x0b5a0, 0x056d0, 0x055b2,
            0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0};

    //农历部分假日
    private final static String[] lunarHoliday = new String[]{
            "0101 春节",
            "0115 元宵节",
            "0129 拗九节",
            "0215 花朝节",
            "0408 浴佛节",
            "0505 端午节",
            "0707 七夕节",
            "0715 中元节",
            "0815 中秋节",
            "0909 重阳节",
            "1208 腊八节",
            "1224 小年",
            "1230 除夕"
    };

    //公历部分节假日
    private final static String[] solarHoliday = new String[]{
            "0101 元旦",
            "0214 情人节",
            "0308 妇女节",
            "0312 植树节",
            "0315 消费者日",
            "0401 愚人节",
            "0501 劳动节",
            "0504 青年节",
            "0601 儿童节",
            "0701 建党节",
            "0801 建军节",
            "0903 抗日胜利",
            "0910 教师节",
            "1001 国庆节",
            "1010 双十节",
            "1031 万圣节",
            "1111 光棍节",
            "1224 平安夜",
            "1225 圣诞节",
    };

    //公历部分节假日
    private final static String[] weekHoliday = new String[]{
            "0527 母亲节",
            "0637 父亲节",
            "0744 感恩节",
    };

    // 传回农历年的总天数
    private static int yearDays(int y) {
        int i, sum = 348;
        for (i = 0x8000; i > 0x8; i >>= 1) {
            if ((lunarInfo[y - 1900] & i) != 0)
                sum += 1;
        }
        return (sum + leapDays(y));
    }

    // 传回农历年闰月的天数
    private static int leapDays(int y) {
        if (leapMonth(y) != 0) {
            if ((lunarInfo[y - 1900] & 0x10000) != 0)
                return 30;
            else
                return 29;
        } else
            return 0;
    }

    // 传回农历年闰哪个月 1-12 , 没闰传回 0
    private static int leapMonth(int y) {
        return (int) (lunarInfo[y - 1900] & 0xf);
    }

    // 传回农历年m月的总天数
    private static int monthDays(int y, int m) {
        if ((lunarInfo[y - 1900] & (0x10000 >> m)) == 0)
            return 29;
        else
            return 30;
    }

    // 传回农历年的生肖
    public String animalsYear(int year) {
        final String[] Animals = new String[]{"鼠", "牛", "虎", "兔", "龙", "蛇",
                "马", "羊", "猴", "鸡", "狗", "猪"};
        return Animals[(year - 4) % 12];
    }

    // 传入月日，传回干支, 0=甲子
    private static String cyclicalm(int num) {
        final String[] Gan = new String[]{"甲", "乙", "丙", "丁", "戊", "己", "庚",
                "辛", "壬", "癸"};
        final String[] Zhi = new String[]{"子", "丑", "寅", "卯", "辰", "巳", "午",
                "未", "申", "酉", "戌", "亥"};
        return (Gan[num % 10] + Zhi[num % 12]);
    }

    // 传入年份，传回干支, 0=甲子
    private String cyclical(int year) {
        int num = year - 1900 + 36;
        return (cyclicalm(num));
    }

    public static String getChinaDayString(int day) {
        String chineseTen[] = {"初", "十", "廿", "卅"};
        int n = day % 10 == 0 ? 9 : day % 10 - 1;
        if (day > 30)
            return "";
        if (day == 10)
            return "初十";
        else
            return chineseTen[day / 10] + chineseNumber[n];
    }

    /**
     * 传出y年m月d日对应的农历. yearCyl3:农历年与1864的相差数 ? monCyl4:从1900年1月31日以来,闰月数
     * dayCyl5:与1900年1月31日相差的天数,再加40 ?
     * <p>
     * isday: 这个参数为false---日期为节假日时，阴历日期就返回节假日 ，true---不管日期是否为节假日依然返回这天对应的阴历日期
     */
    @SuppressLint("DefaultLocale")
    public CalendarTransfer getLunarDate(int year_log, int month_log, int day_log, boolean isday) {
        int yearCyl, monCyl, dayCyl;
        String nowadays;
        Date baseDate = null;
        Date nowaday = null;
        try {
            baseDate = chineseDateFormat.parse("1900年1月31日");
        } catch (Exception e) {
            e.printStackTrace();
        }

        nowadays = year_log + "年" + month_log + "月" + day_log + "日";
        try {
            nowaday = chineseDateFormat.parse(nowadays);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 求出和1900年1月31日相差的天数
        int offset = (int) ((nowaday.getTime() - baseDate.getTime()) / 86400000L);
        dayCyl = offset + 40;
        monCyl = 14;

        // 用offset减去每农历年的天数，计算当天是农历第几天
        // iYear最终结果是农历的年份，offset是当年的第几天
        int iYear, daysOfYear = 0;
        for (iYear = 1900; iYear < 10000 && offset > 0; iYear++) {
            daysOfYear = yearDays(iYear);
            offset -= daysOfYear;
            monCyl += 12;
        }
        if (offset < 0) {
            offset += daysOfYear;
            iYear--;
            monCyl -= 12;
        }
        // 农历年份
        year = iYear;
        setYear(year);  //设置公历对应的农历年份

        yearCyl = iYear - 1864;
        leapMonth = leapMonth(iYear); // 闰哪个月,1-12
        leap = false;

        // 用当年的天数offset,逐个减去每月（农历）的天数，求出当天是本月的第几天
        int iMonth, daysOfMonth = 0;
        for (iMonth = 1; iMonth < 13 && offset > 0; iMonth++) {
            // 闰月
            if (leapMonth > 0 && iMonth == (leapMonth + 1) && !leap) {
                --iMonth;
                leap = true;
                daysOfMonth = leapDays(year);
            } else {
                daysOfMonth = monthDays(year, iMonth);
            }

            offset -= daysOfMonth;
            // 解除闰月
            if (leap && iMonth == (leapMonth + 1))
                leap = false;
            if (!leap)
                monCyl++;
        }
        int daysOfThisMonth;
        if (leapMonth > 0 && iMonth == (leapMonth + 1) && !leap) {
            daysOfThisMonth = leapDays(year);
        } else {
            daysOfThisMonth = monthDays(year, iMonth);
        }
        // offset为0时，并且刚才计算的月份是闰月，要校正
        if (offset == 0 && leapMonth > 0 && iMonth == leapMonth + 1) {
            if (leap) {
                leap = false;
            } else {
                leap = true;
                --iMonth;
                --monCyl;
            }
        }
        // offset小于0时，也要校正
        if (offset < 0) {
            offset += daysOfMonth;
            --iMonth;
            --monCyl;
        }
        month = iMonth;
        setLunarMonth(chineseNumber[month - 1] + "月");  //设置对应的阴历月份
        day = offset + 1;

        CalendarTransfer trans = new CalendarTransfer();
        trans.lunar_year = year;
        trans.lunar_month = month;
        trans.lunar_day = day;
        if (day == 1) {
            trans.day_name = chineseNumber[month - 1] + "月";
            if (daysOfThisMonth < 30) {
                trans.day_name = trans.day_name + "小";
            }
            if (leap) {
                trans.day_name = "闰" + trans.day_name;
            }
        } else {
            trans.day_name = getChinaDayString(day);
        }
        return trans;
    }

    private static String[] termDays = new String[24];

    @SuppressLint("DefaultLocale")
    public CalendarTransfer getSubDate(CalendarTransfer lastTrans,
                                       int year_log, int month_log, int day_log, int weekday, boolean isday) {
        CalendarTransfer trans;
        String day_result = "";
        String day_name;
        if (lastTrans.lunar_month == 0 || lastTrans.lunar_day == 0
                || lastTrans.lunar_day == 29 || lastTrans.lunar_day == 30) {
            trans = getLunarDate(year_log, month_log, day_log, isday);
            day_name = trans.day_name;
        } else {
            trans = lastTrans;
            trans.lunar_day++;
            day_name = getChinaDayString(trans.lunar_day);
        }
        trans.solar_year = year_log;
        trans.solar_month = month_log;
        trans.solar_day = day_log;
        if (!isday) {
            //如果日期为节假日则阴历日期则返回节假日
            //setLeapMonth(leapMonth);
            for (int i = 0; i < solarHoliday.length; i++) {
                //返回公历节假日名称
                String sd = solarHoliday[i].split(" ")[0];  //节假日的日期
                String sdv = solarHoliday[i].split(" ")[1]; //节假日的名称
                String smd = String.format("%02d%02d", month_log, day_log);
                if (sd.equals(smd)) {
                    day_result = getLinkName(day_result, sdv);
                    break;
                }
            }

            for (int i = 0; i < weekHoliday.length; i++) {
                int weeknum = (day_log - 1) / 7;
                if (day_log - weeknum * 7 > 0) {
                    weeknum++;
                }
                //返回公历节假日名称
                String wd = weekHoliday[i].split(" ")[0];  //节假日的日期
                String wdv = weekHoliday[i].split(" ")[1]; //节假日的名称
                String wmd = String.format("%02d%d%d", month_log, weeknum, weekday);
                if (wd.equals(wmd)) {
                    day_result = getLinkName(day_result, wdv);
                    break;
                }
            }

            for (int i = 0; i < lunarHoliday.length; i++) {
                //返回农历节假日名称
                String ld = lunarHoliday[i].split(" ")[0];   //节假日的日期
                String ldv = lunarHoliday[i].split(" ")[1];  //节假日的名称
                String lmd = String.format("%02d%02d", trans.lunar_month, trans.lunar_day);
                if (ld.equals(lmd)) {
                    day_result = getLinkName(day_result, ldv);
                    break;
                }
            }

            if (termDays[0] == null || termDays[0].length() <= 0) {
                termDays = (new SolarTerm()).getSolarDays(year_log);
            }
            for (int i = 0; i < termDays.length; i++) {
                //返回农历二十四节气名称
                String td = termDays[i].split(" ")[0];   //二十四节气的日期
                String tdv = termDays[i].split(" ")[1];  //二十四节气的名称
                String tmd = String.format("%02d%02d", month_log, day_log);
                if (td.equals(tmd)) {
                    day_result = getLinkName(day_result, tdv);
                    break;
                }
            }
        }
        if (day_result.length() > 0) {
            trans.day_name = day_result;
        } else {
            trans.day_name = day_name;
        }
        return trans;
    }

    private String getLinkName(String str, String appendStr) {
        String fullStr = str;
        if (fullStr.length() == 0) {
            fullStr = appendStr;
        } else {
            fullStr = fullStr + "/" + appendStr;
        }
        return fullStr;
    }

    public String toString() {
        if (chineseNumber[month - 1].equals("一") && getChinaDayString(day).equals("初一"))
            return "农历" + year + "年";
        else if (getChinaDayString(day).equals("初一"))
            return chineseNumber[month - 1] + "月";
        else
            return getChinaDayString(day);
    }

    public int getLeapMonth() {
        return leapMonth;
    }

    public void setLeapMonth(int leapMonth) {
        this.leapMonth = leapMonth;
    }

    // 得到当前日期对应的阴历月份
    public String getLunarMonth() {
        return lunarMonth;
    }

    public void setLunarMonth(String lunarMonth) {
        this.lunarMonth = lunarMonth;
    }

    // 得到当前年对应的农历年份
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
