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
    public static int SYNC_GROUP_INTERVAL             = 10000; //MILIS
  }

  public static class DB {
    public static       String HOST               = "localhost";
    public static       String PORT               = "8091";
    public static       String USER               = "Administrator";
    public static       String PWD                = "n5t5lnsct";

    // for anti IDLE drop
    public static final String  ID_INCR_KEY     = "HeartBeatOnlineUserID";
    public static final int     ID_INIT         = 100000;
    public static final String  GID_INCR_KEY    = "HeartBeatGroupID";
    public static final int     GID_INIT        = 10000;
  }

  public static class GROUP {
    public static final String  DATE_PATTERN            = "dd/MM/yyyy HH:mm:ss";
    public static String        EVENT_START             = "01/01/2020 23:00:00";
    public static String        EVENT_END               = "31/01/2020 23:00:00";
    public static int           missionStart            = -1;
    public static int           messionEnd              = -1;
    public static int           CREATE_GROUP_TIME_COST  = 5*86400; //5 days
    public static int           GAMESHOW_MISSION_ID     = 1;
    public static int           PRODUCTION_MISSION_ID   = 2;
  }

  public static class USER_GAME_INFO {
    public static final int INIT_TIME_GIFT      = 86400*7;
  }

  public static void serverStartUp() {
    try {
      GROUP.missionStart =
              (int)(Utilities.getMillisFromDateString(GROUP.EVENT_START, GROUP.DATE_PATTERN)/1000);
      GROUP.messionEnd =
              (int)(Utilities.getMillisFromDateString(GROUP.EVENT_END, GROUP.DATE_PATTERN)/1000);
    }
    catch (Exception e) {
      GROUP.missionStart = -1;
      GROUP.messionEnd = -1;
    }
  }
}