package com.heartbeat.common;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

@SuppressWarnings("unused")
public class Utilities {
  public static Gson gson = new GsonBuilder().create();

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

    if (y2 > y1)
      return 1;

    return Math.abs(d2 - d1);
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

  public static int getRandomNumberInRange(int min, int max) {
    if (min >= max) {
      throw new IllegalArgumentException("max must be greater than min");
    }

    Random r = new Random();
    return r.nextInt((max - min) + 1) + min;
  }

  public static long getMillisFromDateString(String dateString, String pattern) throws Exception {
    Date date = new SimpleDateFormat(pattern).parse(dateString);
    return date.getTime();
  }
}