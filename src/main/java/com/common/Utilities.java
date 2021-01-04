package com.common;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

@SuppressWarnings("unused")
public class Utilities {
  public static Gson gson = new GsonBuilder().create();
  static HashMap<String, SimpleDateFormat> dateFormat = new HashMap<>();

  public static int dayDiff(int t1, int t2) {
    return dayDiff(((long)t1)*1000, ((long)t2)*1000);
  }

  public static long certainSecond(int hour, int minute, int second) {
    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DATE);
    calendar.set(year, month, day, hour, 0, 0);
    return calendar.getTimeInMillis();
  }

  public static int dayDiff(long t1, long t2) {
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(t1);
    int d1 = cal.get(Calendar.DAY_OF_YEAR);
    int y1 = cal.get(Calendar.YEAR);
    cal.setTimeInMillis(t2);
    int d2 = cal.get(Calendar.DAY_OF_YEAR);
    int y2 = cal.get(Calendar.YEAR);

    return Math.abs(d2 - d1 + 365*(y2 - y1));
  }

  public static String md5Encode(String string) throws Exception {
    if(string != null) {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(string.getBytes());
      byte[] data = md.digest();
      StringBuilder sb = GlobalVariable.stringBuilder.get();
      for(byte b : data)
        sb.append(String.format("%02x", b&0xff));
      return sb.toString();
    }
    return null;
  }

  public static String sha256Hash(String string) throws Exception {
    if (string != null) {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      md.update(string.getBytes(StandardCharsets.UTF_8));
      byte[] hash = md.digest();
      StringBuilder sb = GlobalVariable.stringBuilder.get();
      for(byte b : hash)
        sb.append(String.format("%02x", b&0xff));
      return sb.toString();
    }
    return "";
  }

  public static boolean isValidString(String... strings) {
    for(String str : strings)
      if(str == null || str.isEmpty())
        return false;
    return true;
  }

  public static long getMillisFromDateString(String dateString, String pattern) throws Exception {
    Date date = new SimpleDateFormat(pattern).parse(dateString);
    return date.getTime();
  }

  public static int hourOfMs(long milliSecond) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(milliSecond);
    return calendar.get(Calendar.HOUR_OF_DAY);
  }

  public static String formatTime(long milliSecond, String strFormat) {
    SimpleDateFormat format = dateFormat.get(strFormat);
    if(format == null) {
      format = new SimpleDateFormat(strFormat);
      dateFormat.put(strFormat, format);
    }
    return format.format(new Date(milliSecond));
  }

  public static int startOfDay(long milliSecond) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(milliSecond);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return (int)(calendar.getTimeInMillis()/1000);
  }
}