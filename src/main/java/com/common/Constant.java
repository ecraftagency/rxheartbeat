package com.common;

import com.heartbeat.event.ExtEventInfo;
import com.heartbeat.event.ExtIdolEventInfo;
import com.heartbeat.event.ExtRankingInfo;
import static com.statics.IdolEventInfo.*;
import java.util.*;

public class Constant {
  public static final String        EMPTY_STRING                    = "";
  public static final String        DATE_PATTERN                    = "dd/MM/yyyy HH:mm:ss";
  public static String              TIME_ZONE                       = "Asia/Ho_Chi_Minh";

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
    public static boolean           USE_GLOBAL_FILE_LOG             = true;
    public static boolean           USE_CONSOLE_LOG                 = true;
    public static boolean           USE_POOL_LOG                    = true;
    public static boolean           USE_PAYMENT_LOG                 = true;

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

  public static class PAYMENT {
    public static final String      SECRET                          = "HVzl34e6vwspyTUAUMc8sutz/IaT";
    public static final int         NOT_FOUND_STATUS_CODE           = 2;
    public static final int         SIGN_WRONG_STATUS_CODE          = 3;
    public static final int         EXPIRE_TIME_STATUS_CODE         = 4;
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
    public static final int         MEDIA_MISSION_TYPE        = 4;
    public static final int         GAME_SHOW_MISSION_TYPE    = 5; //phụ bản game show
    public static final int         FIGHT_MISSION_TYPE        = 6; //đời sống showbiz, ải
    public static final int         TRAVEL_MISSION_TYPE       = 7;
    public static final int         IDOL_LV_MISSION_TYPE      = 8;
    public static final int         IDOL_APT_MISSION_TYPE     = 9;
    public static final int         RUN_SHOW_MISSION_TYPE     = 11;
    public static final int         SHOPPING_MISSION_TYPE     = 12;
  }

  /*EVENT*/
  public static class RANK_EVENT {
    public static final int         TOTAL_TALENT_RANK_ID      = 13;
    public static final int         FIGHT_RANK_ID             = 14;
    public static final int         MONEY_SPEND_RANK_ID       = 15;
    public static final int         VIEW_SPEND_RANK_ID        = 16;
    public static final int         FAN_SPEND_RANK_ID         = 17;
    public static final int         LDB_CAPACITY              = 100;
    public static final Map<Integer, ExtRankingInfo> evtMap;
    static {
      evtMap      = new HashMap<>();
      evtMap.put(VIEW_SPEND_RANK_ID,    ExtRankingInfo.of(VIEW_SPEND_RANK_ID));
      evtMap.put(FAN_SPEND_RANK_ID,     ExtRankingInfo.of(FAN_SPEND_RANK_ID));
      evtMap.put(TOTAL_TALENT_RANK_ID,  ExtRankingInfo.of(TOTAL_TALENT_RANK_ID));
      evtMap.put(FIGHT_RANK_ID,         ExtRankingInfo.of(FIGHT_RANK_ID));
      evtMap.put(MONEY_SPEND_RANK_ID,   ExtRankingInfo.of(MONEY_SPEND_RANK_ID));
    }
  }

  public static class IDOL_EVENT {
    public static final int         BP_EVT_ID                 = 0;
    public static final int         DB_EVT_ID                 = 1;

    public static final Map<Integer, ExtIdolEventInfo> evtMap;
    static {
      evtMap = new HashMap<>();
      ExtIdolEventInfo bpEvt = ExtIdolEventInfo.of(BP_EVT_ID);
      bpEvt.addIdol(IdolClaimInfo.of(48, 93, 10));
      bpEvt.addIdol(IdolClaimInfo.of(49, 93, 10));
      bpEvt.addIdol(IdolClaimInfo.of(50, 93, 10));
      bpEvt.addIdol(IdolClaimInfo.of(51, 93, 10));
      bpEvt.addIdol(IdolClaimInfo.of(52, 93, 10));

      ExtIdolEventInfo dbEvt = ExtIdolEventInfo.of(DB_EVT_ID);
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
    public static int               INIT_TIME_GIFT            = 86400*7; //milis
    public static int               TIME_ACTIVE_LEVEL         = 2;
    public static final int         MAX_AVATAR                = 10;
    public static final int         MAX_GENDER                = 2;
    public static final int         MEDIA_INTERVAL            = 1198; //unit second 20'
    public static final int         MEDIA_CONTRACT_ITEM       = 2;    //hợp đồng truyền thông
    public static int               INIT_MONEY                = 20000;
    public static int               INIT_VIEW                 = 20000;
    public static int               INIT_FAN                  = 20000;
  }

  public static class USER_IDOL {
    public static final int               CREATIVITY              = 2; //trí lực
    public static final int               PERFORMANCE             = 3; //chính trị
    public static final int               ATTRACTIVE              = 4; //mị lực
    public static final int               GROUP_HALO              = 1;
    public static final int               PERSONAL_HALO           = 0;

    public static final int               EXP_UP_STEP             = 1;

    public static final int               CRT_UP_ITEM             = 67;
    public static final int               PERF_UP_ITEM            = 68;
    public static final int               ATTR_UP_ITEM            = 69;
    public static List<Integer>           APT_UP_RATE             = new ArrayList<>();
    public static final List<Integer>     DEFAULT_IDOLS;

    public static final int               RAMPAGE_BUFF_PERCENT    = 15;
    public static final int               RAMPAGE_BUFF_LV_CNT     = 2;

    public static List<Integer>           EXCLUDE_RAMPAGE_LEVEL;
    public static final int               MAX_RAMPAGE_ALLOW_LV    = 197;

    public static final int               APT_UP_COST               = 1;
    public static int                     APT_EXP_COST_PER_UPGRADE  = 200;
    public static int                     INIT_APT_EXP              = 2000;

    static {
      APT_UP_RATE.add(0);
      APT_UP_RATE.add(100);
      APT_UP_RATE.add(0);
      APT_UP_RATE.add(30);
      APT_UP_RATE.add(0);
      APT_UP_RATE.add(23);
      DEFAULT_IDOLS = Arrays.asList(1,2,3,4);
      EXCLUDE_RAMPAGE_LEVEL = Arrays.asList(98,99,100,148,149,150);
    }
  }

  @SuppressWarnings("unused")
  public static class USER_PAYMENT {
    public static final int               PAYMENT_CHANNEL_UNKNOWN     = -1;
    public static final int               PAYMENT_CHANNEL_NONE        = 0;
    public static final int               PAYMENT_CHANNEL_GOOGLE_IAP  = 1;
    public static final int               PAYMENT_CHANNEL_APPLE_IAP   = 2;
    public static final int               PAYMENT_CHANNEL_100D        = 2;
  }

  public static class UNLOCK_FUNCTION {
    public static int TIME_UNLOCK_LEVEL           = 2;
    public static int GAME_SHOW_UNLOCK_LEVEL      = 2;
    public static int SHOP_UNLOCK_LEVEL           = 2;
    public static int TRAVEL_UNLOCK_LEVEL         = 3;
    public static int GROUP_UNLOCK_LEVEL          = 4;
    public static int FRIEND_QR_UNLOCK_LEVEL      = 5;
    public static int SHOPPING_UNLOCK_LEVEL       = 6;
    public static int SKIP_FIGHT_UNLOCK_LEVEL     = 6;
    public static int FAST_SHOPPING_UNLOCK_LEVEL  = 7;
    public static int RUN_SHOW_UNLOCK_LEVEL       = 7;
    public static int FAST_RUN_SHOW_UNLOCK_LEVEL  = 7;
  }

  public static class EFFECT_RESULT {
    public static final int         ITEM_EFFECT_RESULT        = 1000; // item [1000, id, count]
    public static final int         IDOL_EFFECT_RESULT        = 2000; // idol [2000, field, change]
    public static final int         RAMPAGE_EFFECT_RESULT     = 3000; // rampage [3000, count, 0];
  }

  public static class TITLE {
    public static int               EXPIRY                    = 60*24*30; //minutes
  }

  public static void serverStartUp() {
    try {
      GROUP.missionStart =
              (int)(Utilities.getMillisFromDateString(GROUP.EVENT_START, DATE_PATTERN)/1000);
      GROUP.missionEnd =
              (int)(Utilities.getMillisFromDateString(GROUP.EVENT_END, DATE_PATTERN)/1000);
    }
    catch (Exception e) {
      GROUP.missionStart = -1;
      GROUP.missionEnd = -1;
    }
  }
}