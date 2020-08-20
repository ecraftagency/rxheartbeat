package com.common;

import com.heartbeat.event.ExtEventInfo;
import com.heartbeat.event.ExtIdolEventInfo;
import com.heartbeat.event.ExtRankingInfo;
import static com.statics.IdolEventInfo.*;
import java.util.*;

@SuppressWarnings("unused")
public class Constant {
  public static final String        EMPTY_STRING                    = "";
  public static final int           MINUTE_SECONDS                  = 60;
  public static final int           HOUR_SECONDS                    = 60 * MINUTE_SECONDS;
  public static final int           DAY_SECONDS                     = 24 * HOUR_SECONDS;

  public static class GAME_INFO {
    public static int               SERVER_VERSION                  = 150;
    public static int               MIN_AVAILABLE_VERSION           = 147;
    public static final String      OS_ANDROID                      = "android";
    public static final String      DEFAULT_DEVICE_ID_ANDROID       = "6962556a555d60555660593961555d600xff";
  }

  public static class SYSTEM_INFO {
    public static int               EVENT_LOOP_SLEEP_INV            = 1000;       //millis
    public static String            GATEWAY_EVT_BUS                 = "balancer";
    public static int               GATEWAY_NOTIFY_INTERVAL         = 1000;       //millis
    public static int               NODE_HEARTBEAT_INTERVAL         = 5*1000;     //millis
    public static boolean           USE_SSL                         = true;
    public static boolean           USE_GLOBAL_FILE_LOG             = true;
    public static boolean           USE_CONSOLE_LOG                 = true;
    public static boolean           USE_POOL_LOG                    = true;
    public static long              EB_SEND_TIMEOUT                 = 5000L; //millis
    public static final int         MAX_USER_PER_NODE               = 1000000;
  }

  public static class ONLINE_INFO {
    public static int               ONLINE_RECORD_UPDATE_TIME       = 20; // 20'
    public static int               ONLINE_RECORD_LDB_TIME          = 4; // 20'
    public static int               ONLINE_HEARTBEAT_TIME           = 30; //second
    public static int               ONLINE_HEARTBEAT_CHECK_INTERVAL = 10000; //MILIS
    public static int               SYNC_GROUP_INTERVAL             = 10000; //MILIS
  }

  public static class DB {
    public static String            HOST                            = "localhost";
    public static String            USER                            = "Administrator";
    public static String            PWD                             = "n5t5lnsct";
    public static final String      ID_INCR_KEY                     = "HeartBeatOnlineUserID";
    public static int               ID_INIT                         = 1000000;
    public static final String      GID_INCR_KEY                    = "HeartBeatGroupID";
    public static int               GID_INIT                        = 100000;
    public static final String      BUCKET_PREFIX                   = "vn";
    public static final String      SESSION_BUCKET                  = "sessions";
    public static final String      PERSIST_BUCKET                  = "persist";
    public static final String      INDEX_BUCKET                    = "index";
  }

  public static class SCHEDULE {
    public static String            TIME_ZONE                 = "Asia/Ho_Chi_Minh";
    public static boolean           gameShowOpen              = false;
    public static int               gameShowOneOpenHour       = 12;
    public static int               gameShowTwoOpenHour       = 19;
    public static int               gameShowOneOpenSec        = 0;
    public static int               gameShowOneOpenMin        = 0;
    public static int               gameShowOneCloseHour      = 14;
    public static int               gameShowTwoCloseHour      = 21;
    public static int               gameShowOneCloseSec       = 0;
    public static int               gameShowOneCloseMin       = 0;
  }

  public static class GROUP {
    public static final String      DATE_PATTERN              = "dd/MM/yyyy HH:mm:ss";
    public static String            EVENT_START               = "01/07/2020 23:00:00";
    public static String            EVENT_END                 = "31/08/2020 23:00:00";
    public static int               missionStart              = -1;
    public static int               missionEnd                = -1;
    public static int               CREATE_GROUP_TIME_COST    = 5*86400; //5 days
    public static int               PRODUCTION_MISSION_ID     = 1;
    public static int               GAME_SHOW_MISSION_ID      = 2;
  }

  public static class LEADER_BOARD {
    public static final int         TALENT_LDB_ID             = 0;
    public static final int         FIGHT_LDB_ID              = 1;
    public static final int         LDB_CAPACITY              = 100;

    public static final Map<Integer, String> id2key;
    static {
      id2key = new HashMap<>();
      id2key.put(TALENT_LDB_ID, "talent_ldb");
      id2key.put(FIGHT_LDB_ID,  "fight_ldb");
    }
  }

  /*RECORD*/
  public static class ACHIEVEMENT {
    //todo never change this one
    public static final int         LOGIN_ACHIEVEMENT         = 1;
    public static final int         LEVEL_ACHIEVEMENT         = 2;
    public static final int         TOTAL_TALENT_ACHIEVEMENT  = 3;
    public static final int         IDOL_ACHIEVEMENT          = 4;
    public static final int         FIGHT_ACHIEVEMENT         = 5;

    public static final int         CRT_ACHIEVEMENT           = 6;
    public static final int         VIEW_ACHIEVEMENT          = 7;
    public static final int         FAN_ACHIEVEMENT           = 8;
    public static final int         MEDIA_ACHIEVEMENT         = 9;
    public static final int         RUN_SHOW_ACHIEVEMENT      = 10;
    public static final int         SHOPPING_ACHIEVEMENT      = 11;
    public static final int         TRAVEL_ACHIEVEMENT        = 12;
    public static final int         VIP_ACHIEVEMENT           = 13;
    public static final int         GAME_SHOW_ACHIEVEMENT     = 14;

    public static final int         MEDIA_CONTRACT_USE        = 100*USER_GAME_INFO.MEDIA_CONTRACT_ITEM;
    public static final int         IDOL_LEVEL                = 16;
    public static final int         IDOL_TITLE                = 17;
    public static final int         ROLL_CALL_ACHIEVEMENT     = 18;
    public static final int         GROUP_JOIN                = 19;
    public static final int         STORE_ACHIEVEMENT         = 20;
    public static final int         APT_BUFF_ITEM_ACHIEVEMENT = 67*100;

    public static final int         IDOL_SINGLE_QUERY         = 1;
    public static final int         IDOL_MULTI_QUERY          = 0;
  }

  /*RECORD*/
  public static class DAILY_MISSION {
    public static int               MEDIA_MISSION_TYPE        = 4;
    public static int               GAME_SHOW_MISSION_TYPE    = 5; //phụ bản game show
    public static int               FIGHT_MISSION_TYPE        = 6; //đời sống showbiz, ải
    public static int               TRAVEL_MISSION_TYPE       = 7;
    public static int               IDOL_LV_MISSION_TYPE      = 8;
    public static int               IDOL_APT_MISSION_TYPE     = 9;
    public static int               RUN_SHOW_MISSION_TYPE     = 11;
    public static int               SHOPPING_MISSION_TYPE     = 12;
  }

  /*EVENT*/
  public static class RANK_EVENT {
    public static final int         TOTAL_TALENT_RANK_ID      = 13;
    public static final int         FIGHT_RANK_ID             = 14;
    public static final int         MONEY_SPEND_RANK_ID       = 15;
    public static final int         VIEW_SPEND_RANK_ID        = 16;
    public static final int         FAN_SPEND_RANK_ID         = 17;
    public static final int         LDB_CAPACITY              = 100;
    public static final int         FLUSH_DELAY               = 60*60*6;//second

    public static ExtRankingInfo    rankingInfo;
    static {
      rankingInfo = ExtRankingInfo.of();
    }
  }

  public static class IDOL_EVENT {
    public static final int         BP_EVT_ID                 = 0;
    public static final int         DB_EVT_ID                 = 1;

    public static final Map<Integer, ExtIdolEventInfo> evtMap;
    static {
      evtMap = new HashMap<>();
      ExtIdolEventInfo bpEvt = ExtIdolEventInfo.of(BP_EVT_ID, "5 em hot girl Hàn");
      bpEvt.addIdol(IdolClaimInfo.of(48, 93, 10));
      bpEvt.addIdol(IdolClaimInfo.of(49, 93, 10));
      bpEvt.addIdol(IdolClaimInfo.of(50, 93, 10));
      bpEvt.addIdol(IdolClaimInfo.of(51, 93, 10));
      bpEvt.addIdol(IdolClaimInfo.of(52, 93, 10));

      ExtIdolEventInfo dbEvt = ExtIdolEventInfo.of(DB_EVT_ID, "4 hot boy ngự lâm");
      dbEvt.addIdol(IdolClaimInfo.of(43, 92, 10));
      dbEvt.addIdol(IdolClaimInfo.of(44, 92, 10));
      dbEvt.addIdol(IdolClaimInfo.of(45, 92, 10));
      dbEvt.addIdol(IdolClaimInfo.of(46, 92, 10));
      evtMap.put(BP_EVT_ID, bpEvt);
      evtMap.put(DB_EVT_ID, dbEvt);
    }
  }

  public static class USER_EVENT {
    public static final int         TIME_SPEND_EVT_ID         = 21;
    public static final int         APT_BUFF_USE_EVT_ID       = 6700;
    public static final int         MONEY_SPEND_EVT_ID        = 6;
    public static final int         VIEW_SPEND_EVT_ID         = 7;
    public static final int         FAN_SPEND_EVT_ID          = 8;
    public static final int         CRT_PROD_EVT_ID           = 9;
    public static final int         VIEW_PROD_EVT_ID          = 10;
    public static final int         FAN_PROD_EVT_ID           = 11;
    public static final int         GAME_SHOW_EVT_ID          = 12;
    public static final int         TOTAL_TALENT_EVT_ID       = 13;

    public static final Map<Integer, ExtEventInfo> evtMap;
    static {
      evtMap = new HashMap<>();
      evtMap.put(TIME_SPEND_EVT_ID,   ExtEventInfo.of(TIME_SPEND_EVT_ID));
      evtMap.put(APT_BUFF_USE_EVT_ID, ExtEventInfo.of(APT_BUFF_USE_EVT_ID));
      evtMap.put(MONEY_SPEND_EVT_ID,  ExtEventInfo.of(MONEY_SPEND_EVT_ID));
      evtMap.put(VIEW_SPEND_EVT_ID,   ExtEventInfo.of(VIEW_SPEND_EVT_ID));
      evtMap.put(FAN_SPEND_EVT_ID,    ExtEventInfo.of(FAN_SPEND_EVT_ID));
      evtMap.put(CRT_PROD_EVT_ID,     ExtEventInfo.of(CRT_PROD_EVT_ID));
      evtMap.put(VIEW_PROD_EVT_ID,    ExtEventInfo.of(VIEW_PROD_EVT_ID));
      evtMap.put(FAN_PROD_EVT_ID,     ExtEventInfo.of(FAN_PROD_EVT_ID));
      evtMap.put(TOTAL_TALENT_EVT_ID, ExtEventInfo.of(TOTAL_TALENT_EVT_ID));
      evtMap.put(GAME_SHOW_EVT_ID,    ExtEventInfo.of(GAME_SHOW_EVT_ID));
    }
  }

  public static class USER_GAME_INFO {
    public static final int         INIT_TIME_GIFT            = 86400*7; //milis
    public static final int         TIME_ACTIVE_LEVEL         = 2;
    public static final int         MAX_AVATAR                = 10;
    public static final int         MAX_GENDER                = 2;
    public static final int         MEDIA_INTERVAL            = 1198; //unit second 20'
    public static final int         MEDIA_CONTRACT_ITEM       = 2;    //hợp đồng truyền thông
  }

  public static class EFFECT_RESULT {
    public static final int         ITEM_EFFECT_RESULT        = 1000; // item [1000, id, count]
    public static final int         IDOL_EFFECT_RESULT        = 2000; // idol [2000, field, change]
    public static final int         RAMPAGE_EFFECT_RESULT     = 3000; // rampage [3000, count, 0];
  }

  public static class TITLE {
    public static final int         EXPIRY                    = 60*24*30; //minutes
  }

  public static void serverStartUp() {
    try {
      GROUP.missionStart =
              (int)(Utilities.getMillisFromDateString(GROUP.EVENT_START, GROUP.DATE_PATTERN)/1000);
      GROUP.missionEnd =
              (int)(Utilities.getMillisFromDateString(GROUP.EVENT_END, GROUP.DATE_PATTERN)/1000);
    }
    catch (Exception e) {
      GROUP.missionStart = -1;
      GROUP.missionEnd = -1;
    }
  }
}