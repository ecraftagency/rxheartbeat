package com.heartbeat.common;

public class Constant {
  public static final String  EMPTY_STRING    = "";
  public static final int     MINUTE_SECONDS  = 60;
  public static final int     HOUR_SECONDS    = 60 * MINUTE_SECONDS;
  public static final int     DAY_SECONDS     = 24 * HOUR_SECONDS;

  public static class SCHEDULE {
    public static boolean gameShowOpen            = false;
    public static int     gameShowOneOpenHour     = 12;
    public static int     gameShowOneCloseHour    = 14;
    public static int     gameShowTwoOpenHour     = 19;
    public static int     gameShowTwoCloseHour    = 21;
  }

  public static class GAME_INFO {
    public static int SERVER_VERSION = 150;
    public static int MIN_AVAILABLE_VERSION = 147;
    public static final int OS_SRC_KNOWN = 0;
    public static final String OS_IOS = "ios";
    public static final String OS_ANDROID = "android";
    public static final String OS_WINDOWS = "windows";
    public static final String DEFAULT_DEVICE_ID_ANDROID = "6962556a555d60555660593961555d600xff";
    public static final String ANDROID_URL = "http://helloandroid.com";
    public static final String IOS_URL = "http://helloios.com";
  }

  public static class ONLINE_INFO {
    public static int ONLINE_RECORD_UPDATE_TIME       = 20; // 20'
    public static int ONLINE_HEARTBEAT_TIME           = 30; //second
    public static int ONLINE_HEARTBEAT_CHECK_INTERVAL = 10000; //MILIS
  }

  public static class DB {
    public static       String HOST               = "18.140.197.136";
    public static       String PORT               = "8091";
    public static       String USER               = "Administrator";
    public static       String PWD                = "n5t5lnsct";

    // for anti IDLE drop
    public static final int COUCHBASE_CHECK_INTERVAL = 29 * 60 * 1000; // 29' in ms
    public static final String COUCHBASE_CHECK_KEY  = "heartbeatIdleCount";
    public static final String INCR_KEY             = "HeartBeatOnlineUserID";
  }
}