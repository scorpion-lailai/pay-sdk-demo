package com.baidu.base.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
  static Logger log = LoggerFactory.getLogger(DateUtil.class);
  static String datePattern_show = "yyyy-MM-dd";
  static String datePattern_all = "yyyy.MM.dd";
  static String datePattern_month = "yyyy.MM";
  static String datePattern_year = "yyyy";
  static String timeSecPattern = "yyyy-MM-dd HH:mm:ss";
  static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  public static final int DAY = 1000 * 60 * 60 * 24;

  /**
   * 获取时间差，结果 = [时间2]-[时间1]
   *
   * @param date1 第一个时间
   * @param date2 第二个时间
   * @return unit对应的结果
   */
  public static int getTimeInterval(Date date1, Date date2) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date1);
    long time1 = cal.getTimeInMillis();
    cal.setTime(date2);
    long time2 = cal.getTimeInMillis();
    long between_days = (time2 - time1) / DAY;
    return Integer.parseInt(String.valueOf(between_days));
  }

  // 将日期转为[yyyy-MM-dd]格式
  public static String formatDate(Date date) {
    SimpleDateFormat sdf = new SimpleDateFormat(datePattern_show);
    String strDate = "";
    if (date != null) {
      strDate = sdf.format(date);
    }
    return strDate;
  }

  // 报告日期转换
  public static String formatDateByReport(Date date) {
    String strDate;
    SimpleDateFormat sdf;
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    // 获取天，如果天数为1 ，则取月
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    if (day == 1) {
      // 获取月，如果为1月，则获取年
      int month = calendar.get(Calendar.MONTH);
      if (month == 0) {
        sdf = new SimpleDateFormat(datePattern_year);
      } else {
        sdf = new SimpleDateFormat(datePattern_month);
      }
    } else {
      sdf = new SimpleDateFormat(datePattern_all);
    }
    strDate = sdf.format(date);
    return strDate;
  }
  // 将日期转为[yyyy-MM-dd HH:mm:ss]格式
  public static String formatDateTimeSec(Date time) {
    SimpleDateFormat sdf = new SimpleDateFormat(timeSecPattern);
    String strTime = "";

    if (time != null) {
      strTime = sdf.format(time);
    }
    return strTime;
  }

  public static String formatCSTTime(String date, String format) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
    Date d = sdf.parse(date);
    return DateUtil.getDateFormat(d, format);
  }

  private static String getDateFormat(Date date, String dateFormatType) {
    SimpleDateFormat format = new SimpleDateFormat(dateFormatType);
    return format.format(date);
  }

  // 将[yyyy-MM-dd HH:mm:ss]格式转为Date
  public static Date parseDateTimeSec(String strDate) {
    Date date = null;
    if (StringUtils.isBlank(strDate)) {
      return null;
    }
    try {
      SimpleDateFormat sdf = new SimpleDateFormat(timeSecPattern);
      date = sdf.parse(strDate);
    } catch (Exception e) {
      log.error("字符串转日期出错", e);
    }

    return date;
  }

  public static Date getSDay(Date date, Integer day) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.DAY_OF_MONTH, +day);
    date = calendar.getTime();
    return date;
  }

  /**
   * 获取指定[时间段]之后的时间
   *
   * @param date 时间
   * @param field Calendar的field字段
   * @param amount 时间数量
   */
  public static Date getAfterDate(Date date, int field, Integer amount) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.add(field, amount);
    return c.getTime();
  }

  public static Boolean compareDate(String reportEndTime) {
    if (StringUtils.isBlank(reportEndTime)) {
      return false;
    }
    LocalDateTime endTimeFormat = LocalDateTime.parse(reportEndTime, dtf);
    return !endTimeFormat.isBefore(LocalDateTime.now()); // 已经结束
  }

  /** 提取日期中的年 */
  public static Integer getYear(Date date) {
    Calendar cld = Calendar.getInstance();
    cld.setTime(date);
    return cld.get(Calendar.YEAR);
  }

  public static String getDatePoor(Date endDate, Date nowDate) {

    long nd = 1000 * 24 * 60 * 60;
    long nh = 1000 * 60 * 60;
    long nm = 1000 * 60;

    // long ns = 1000;
    // 获得两个时间的毫秒时间差异
    long diff = endDate.getTime() - nowDate.getTime();
    // 计算差多少天
    long day = diff / nd;
    // 计算差多少小时
    long hour = diff % nd / nh;
    // 计算差多少分钟
    long min = diff % nd % nh / nm;
    // 计算差多少秒//输出结果
    // long sec = diff % nd % nh % nm / ns;
    return String.valueOf(day);
  }
}
