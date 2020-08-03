package com.heartbeat.common;

public class Constant {
  public static final String  EMPTY_STRING    = "";
  public static final int     MINUTE_SECONDS  = 60;
  public static final int     HOUR_SECONDS    = 60 * MINUTE_SECONDS;
  public static final int     DAY_SECONDS     = 24 * HOUR_SECONDS;

  public static class SCHEDULE {
    public static final String  TIME_ZONE               = "Asia/Ho_Chi_Minh";
    public static boolean       gameShowOpen            = false;
    public static int           gameShowOneOpenHour     = 12;
    public static int           gameShowTwoOpenHour     = 19;
    public static int           gameShowOneOpenSec      = 0;
    public static int           gameShowOneOpenMin      = 0;
    public static int           gameShowOneCloseHour    = 14;
    public static int           gameShowTwoCloseHour    = 21;
    public static int           gameShowOneCloseSec     = 0;
    public static int           gameShowOneCloseMin     = 0;
  }

  public static class GAME_INFO {
    public static int           SERVER_VERSION            = 150;
    public static int           MIN_AVAILABLE_VERSION     = 147;
    public static final int     OS_SRC_KNOWN              = 0;
    public static final String  OS_IOS                    = "ios";
    public static final String  OS_ANDROID                = "android";
    public static final String  OS_WINDOWS                = "windows";
    public static final String  DEFAULT_DEVICE_ID_ANDROID = "6962556a555d60555660593961555d600xff";
    public static final String  ANDROID_URL               = "http://helloandroid.com";
    public static final String  IOS_URL                   = "http://helloios.com";
  }

  public static class SYSTEM_INFO {
    public static boolean USE_POOL_MSG                    = false;
    public static int     EVENT_LOOP_SLEEP_INV            = 1000; //millis
  }

  public static class ONLINE_INFO {
    public static int ONLINE_RECORD_UPDATE_TIME       = 20; // 20'
    public static int ONLINE_HEARTBEAT_TIME           = 30; //second
    public static int ONLINE_HEARTBEAT_CHECK_INTERVAL = 10000; //MILIS
    public static int SYNC_GROUP_INTERVAL             = 10000; //MILIS
  }

  public static class DB {
    public static       String HOST               = "localhost";
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
    public static String        EVENT_START             = "01/07/2020 23:00:00";
    public static String        EVENT_END               = "31/08/2020 23:00:00";
    public static int           missionStart            = -1;
    public static int           messionEnd              = -1;
    public static int           CREATE_GROUP_TIME_COST  = 5*86400; //5 days
    public static int           PRODUCTION_MISSION_ID   = 1;
    public static int           GAMESHOW_MISSION_ID     = 2;
  }

  public static class DAILY_MISSION {
    public static int MEDIA_MISSION_TYPE        = 4;
    public static int GAME_SHOW_MISSION_TYPE    = 5; //phụ bản game show
    public static int FIGHT_MISSION_TYPE        = 6; //đời sống showbiz, ải
    public static int TRAVEL_MISSION_TYPE       = 7;
    public static int IDOL_LV_MISSION_TYPE      = 8;
    public static int IDOL_APT_MISSION_TYPE     = 9;
    public static int RUN_SHOW_MISSION_TYPE     = 11;
    public static int SHOPPING_MISSION_TYPE     = 12;
  }

  public static class ACHIEVEMENT {
    //todo never change this one
    public static final int LOGIN_ACHIEVEMENT         = 1;
    public static final int LEVEL_ACHIEVEMENT         = 2;
    public static final int TOTAL_TALENT_ACHIEVEMENT  = 3;
    public static final int IDOL_ACHIEVEMENT          = 4;
    public static final int FIGHT_ACHIEVEMENT         = 5;

    public static final int CRT_ACHIEVEMENT           = 6;
    public static final int VIEW_ACHIEVEMENT          = 7;
    public static final int FAN_ACHIEVEMENT           = 8;
    public static final int MEDIA_ACHIEVEMENT         = 9;
    public static final int RUNSHOW_ACHIEVEMENT       = 10;
    public static final int SHOPPING_ACHIEVEMENT      = 11;
    public static final int TRAVEL_ACHIEVEMENT        = 12;
    public static final int VIP_ACHIEVEMENT           = 13;
    public static final int GAMESHOW_ACHIEVEMENT      = 14;

    public static final int IDOL_LEVEL                = 16;
    public static final int IDOL_TITLE                = 17;
    public static final int IDOL_SINGLE_QUERY         = 1;
    public static final int IDOL_MULTI_QUERY          = 0;
    public static final int GROUP_JOIN                = 19;
    public static final int STORE_ACHIEVEMENT         = 20;
    public static final int TIME_SPENT_ACHIEVEMENT    = 21;
    public static final int APT_BUFF_ITEM_ACHIEVEMENT = 67*100;

  }

  public static class USER_GAME_INFO {
    public static final int INIT_TIME_GIFT            = 86400*7; //milis
    public static final int INIT_TIME_GIFT_LV         = 2;
  }

  public static class USER_ROLL_CALL {
    public static final int WEEKLY_GIFT_TYPE          = 1;
    public static final int MONTHLY_GIFT_TYPE         = 2;
    public static final int YEARLY_GIFT_TYPE          = 3;
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