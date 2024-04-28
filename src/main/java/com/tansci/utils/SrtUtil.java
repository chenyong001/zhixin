package com.tansci.utils;

import java.math.BigInteger;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import lombok.var;

public class SrtUtil {
  static final Instant dotNetEpoch = ZonedDateTime.of(1, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC).toInstant();

  public static Instant InstantFromTicks(BigInteger ticks) {
    // 10,000 ticks per millisecond.
    long ms = Math.floorDiv(ticks.longValue(), 10000L);
    long restTicks = Math.floorMod(ticks.longValue(), 10000L);
    long restNanos = restTicks * 100;
    return dotNetEpoch.plusMillis(ms).plusNanos(restNanos);
  }

  private static String GetTimestamp(Instant startTime, Instant endTime) {
    // SRT format requires ',' as decimal separator rather than '.'.
    var format = "HH:mm:ss,SSS";
    // Set the timezone to UTC so the time is not adjusted for our local time zone.
    var formatter = DateTimeFormatter.ofPattern(format).withZone(ZoneId.from(ZoneOffset.UTC));
    return String.format("%s --> %s", formatter.format(startTime), formatter.format(endTime));
  }

  public static void StringFromCaption(StringBuilder retval, int index, String offset, String duration, String text) {
//    var retval = new StringBuilder();
    var startTime = InstantFromTicks(new BigInteger(offset));
    var resultEnd = InstantFromTicks(new BigInteger(offset).add(new BigInteger(duration)));
    retval.append(String.format("%d%s", index, System.lineSeparator()));
    retval.append(String.format("%s%s", GetTimestamp(startTime, resultEnd), System.lineSeparator()));
    retval.append(String.format("%s%s%s", text, System.lineSeparator(), System.lineSeparator()));
//    return retval.toString();
  }

  public static void main(String[] args) {
//    System.out.println(StringFromCaption(1,"700000","12900000","测试"));
//    System.out.println(StringFromCaption(2,"","","测试"));

    //    var format = "HH:mm:ss,SSS";
//    // Set the timezone to UTC so the time is not adjusted for our local time zone.
//    var formatter = DateTimeFormatter.ofPattern(format).withZone(ZoneId.from(ZoneOffset.UTC));
//    //    now.get
//    String offset = "700000";
//    String duration = "12900000";
//    var startTime = InstantFromTicks(new BigInteger(offset));
//    var resultEnd = InstantFromTicks(new BigInteger(offset).add(new BigInteger(duration)));
//
//    System.out.println(startTime);
//    System.out.println(formatter.format(startTime));
//    System.out.println(formatter.format(resultEnd));
//    System.out.println(String.format("%s%s", GetTimestamp(startTime, resultEnd), System.lineSeparator()));

    //    retval.append(String.format("%s%s%s", caption.text, System.lineSeparator(), System.lineSeparator()));
    //    12900000

    //    var endTime = CaptionHelper.InstantFromTicks(result.getOffset().add(result.getDuration()));
  }

}

