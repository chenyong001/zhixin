package com.tansci.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
  public static final String FORMAT_YYYY = "yyyy";
  public static final String FORMAT_YYYY_MM = "yyyy-MM";
  public static final String FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
  public static final String FORMAT_YYYY_MM_DD_HH = "yyyy-MM-dd HH";
  public static final String FORMAT_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
  public static final String FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
  public static final String FORMAT_YYYYMMDD = "yyyyMMdd";
  public static final String FORMAT_YYYYMM = "yyyyMM";
  public static final String FORMAT_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
  public static final String FORMAT_YYYYMMDDTHHMMSS = "yyyy-MM-dd'T'HH:mm:ss";
  public static final String FORMAT_HH_MM_SS_SSS = "HH:mm:ss,SSS";

  public static final DateTimeFormatter FORMATTER_YYYY = DateTimeFormatter.ofPattern("yyyy");
  public static final DateTimeFormatter FORMATTER_YYYYMM = DateTimeFormatter.ofPattern("yyyyMM");
  public static final DateTimeFormatter FORMATTER_YYYY_MM = DateTimeFormatter.ofPattern("yyyy-MM");
  public static final DateTimeFormatter FORMATTER_YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");
  public static final DateTimeFormatter FORMATTER_YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  public static final DateTimeFormatter FORMATTER_YYYY_MM_DD_HH_MM_SS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public DateUtil() {
  }

  public static Date str2Date(String strDate, String format) {
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    try {
      return sdf.parse(strDate);
    } catch (ParseException var4) {
      var4.printStackTrace();
      return null;
    }
  }

  public static String date2Str(Date date, String format) {
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    return sdf.format(date);
  }

  public static LocalDateTime date2LocalDateTime(Date date) {
    Instant instant = date.toInstant();
    return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
  }

  public static LocalDate date2LocalDate(Date date) {
    Instant instant = date.toInstant();
    return instant.atZone(ZoneId.systemDefault()).toLocalDate();
  }

  public static int localDateSubtraction(LocalDate begin, LocalDate end) {
    Period between = Period.between(begin, end);
    return Integer.parseInt(String.valueOf(between.getDays()));
  }

  public static Date localDateTime2Date(LocalDateTime time) {
    return Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
  }

  public static Date localDate2Date(LocalDate date) {
    return Date.from(LocalDateTime.of(date, LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant());
  }

  public static String localDateTime2String(LocalDateTime time, DateTimeFormatter formatter) {
    return formatter.format(time);
  }

  public static LocalDateTime string2LocalDateTime(String time, DateTimeFormatter formatter) {
    return LocalDateTime.parse(time, formatter);
  }

  public static String localDate2String(LocalDate date, DateTimeFormatter formatter) {
    return date.format(formatter);
  }

  public static LocalDate string2LocalDate(String time, DateTimeFormatter formatter) {
    return LocalDate.parse(time, formatter);
  }

  public static DateTimeFormatter getFormatter(String format) {
    return DateTimeFormatter.ofPattern(format);
  }
}

