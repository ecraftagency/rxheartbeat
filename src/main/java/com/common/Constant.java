package com.common;

import com.heartbeat.Passport100D;
import com.heartbeat.scheduler.ExtendEventInfo;
import static com.statics.EventInfo.*;

import java.util.*;

@SuppressWarnings("unused")
public class Constant {
  public static final String        EMPTY_STRING                    = "";
  public static final String        DATE_PATTERN                    = "dd/MM/yyyy HH:mm:ss";
  public static String              TIME_ZONE                       = "Asia/Ho_Chi_Minh";

  public static class GAME_INFO {
    public static int               SERVER_VERSION                  = 140;
    public static int               MIN_AVAILABLE_VERSION           = 140;
    public static final String      OS_ANDROID                      = "android";
    public static final String      DEFAULT_DEVICE_ID_ANDROID       = "6962556a555d60555660593961555d600xff";
    public static String            CH_PLAY_APP_LINK                = "https://play.google.com/store/apps/details?id=com.kooapps.stackybirdandroid&hl=en";
    public static String            APPLE_STORE_APP_LINK            = "https://apps.apple.com/vn/app/apple-store/id375380948?l=vi";
  }

  public static class GAME_FUNCTIONS {
    public static boolean GIFT_CODE = false;
  }

  public static class SERVICE {
    public static String  STD_PROFILE_HOST    = "13.229.140.173";
    public static int     STD_PROFILE_PORT    = 8888;
    public static String  GIFT_SERVICE        = "http://a47233bd069ec42b69f101a5fa681eb6-1872285878.ap-southeast-1.elb.amazonaws.com";
  }

  public static class SYSTEM_INFO {
    public static int               EVENT_LOOP_SLEEP_INV            = 1000;       //millis
    public static String            GATEWAY_EVT_BUS                 = "balancer";
    public static String            PREF_EVT_BUS                    = "pref";
    public static int               GATEWAY_NOTIFY_INTERVAL         = 1000;       //millis
    public static int               STATS_DATA_SYNC_INTERVAL        = 60000;       //millis

    public static int               NODE_HEARTBEAT_INTERVAL         = 5*1000;     //millis
    public static boolean           USE_GLOBAL_FILE_LOG             = true;
    public static boolean           USE_CONSOLE_LOG                 = true;
    public static boolean           USE_POOL_LOG                    = true;
    public static boolean           USE_PAYMENT_LOG                 = true;

    public static long              EB_SEND_TIMEOUT                 = 5000L; //millis
    public static final int         MAX_USER_PER_NODE               = 1000000;
  }

  public static class PASSPORT {
    public static boolean PROD = false;
    public static Passport100D.Env ENV;
    static Passport100D.Env dev;
    static Passport100D.Env prod;

    public static void setEnv(boolean p) {
      PROD = p;
      ENV       = PROD ? prod : dev;
    }

    static {
      dev       = Passport100D.Env.of("https://dev-sdkapi.phoeniz.com/v1", "JKu8xxJR7edfMqUufi1OH2DXxR7qyf6g", "?authorization=%s&timestamp=%d&sign=%s");
      prod      = Passport100D.Env.of("https://sdkapi.phoeniz.com/v1", "JKu8xxJR7edfMqUufi1OH2DXxR7qyf6g", "?authorization=%s&timestamp=%d&sign=%s");
      ENV       = PROD ? prod : dev;
    }
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
    public static final int         SERVER_NOT_FOUND_STATUS_CODE    = -2;
    public static final int         SIGN_WRONG_STATUS_CODE          = -3;
    public static final int         EXPIRE_TIME_STATUS_CODE         = -4;
    public static final int         ROLE_NAME_NOT_EXIST             = -8;
    public static final int         EXCHANGE_FAIL_STATUS_CODE       = -9;
    public static final int         INSUFFICIENT_AMOUNT             = -10;
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
    public static final String      STATS_BUCKET                    = "stats";

  }

  public static class SCHEDULE {
    public static boolean           gameShowOpen              = false;
    public static final int         gameShowOneOpenHour       = 12;
    public static final int         gameShowTwoOpenHour       = 19;
    public static final int         gameShowOneOpenSec        = 0;
    public static final int         gameShowOneOpenMin        = 0;
    public static final int         gameShowOneCloseHour      = 14;
    public static final int         gameShowTwoCloseHour      = 21;
    public static final int         gameShowOneCloseSec       = 0;
    public static final int         gameShowOneCloseMin       = 0;
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
    public static final Map<Integer, ExtendEventInfo> evtMap;
    public static final Set<Integer>                  eventSet;
    static {
      eventSet    = new HashSet<>();
      evtMap      = new HashMap<>();
      evtMap.put(VIEW_SPEND_RANK_ID,    ExtendEventInfo.of(VIEW_SPEND_RANK_ID, 1));
      evtMap.put(FAN_SPEND_RANK_ID,     ExtendEventInfo.of(FAN_SPEND_RANK_ID, 1));
      evtMap.put(TOTAL_TALENT_RANK_ID,  ExtendEventInfo.of(TOTAL_TALENT_RANK_ID, 1));
      evtMap.put(FIGHT_RANK_ID,         ExtendEventInfo.of(FIGHT_RANK_ID, 1));
      evtMap.put(MONEY_SPEND_RANK_ID,   ExtendEventInfo.of(MONEY_SPEND_RANK_ID, 1));

      eventSet.add(VIEW_SPEND_RANK_ID);
      eventSet.add(FAN_SPEND_RANK_ID);
      eventSet.add(TOTAL_TALENT_RANK_ID);
      eventSet.add(FIGHT_RANK_ID);
      eventSet.add(MONEY_SPEND_RANK_ID);
    }
  }

  public static class GROUP_EVENT {
    public static final int         GE_PROD_EVT_ID            = 1;
    public static final int         GE_GS_EVT_ID              = 2;
    public static final int         GE_CRZ_DEGREE_EVT_ID      = 3;
    public static final int         GE_MONTHLY_GC_EVT_ID      = 4;

    public static final Map<Integer, ExtendEventInfo> evtMap;

    static {
      evtMap = new HashMap<>();
      evtMap.put(GE_PROD_EVT_ID,        ExtendEventInfo.of(GE_PROD_EVT_ID, 1));
      evtMap.put(GE_GS_EVT_ID,          ExtendEventInfo.of(GE_GS_EVT_ID, 1));
      evtMap.put(GE_CRZ_DEGREE_EVT_ID,  ExtendEventInfo.of(GE_CRZ_DEGREE_EVT_ID, 1));
      evtMap.put(GE_MONTHLY_GC_EVT_ID,  ExtendEventInfo.of(GE_MONTHLY_GC_EVT_ID, 1));
    }
  }

  public static class IDOL_EVENT {
    public static final int         BP_EVT_ID                 = 0;
    public static final int         DB_EVT_ID                 = 1;
    public static final int         NT_EVT_ID                 = 2;

    public static final Map<Integer, ExtendEventInfo> evtMap;
    static {
      evtMap = new HashMap<>();
      ExtendEventInfo blackPinkEvt  = ExtendEventInfo.of(BP_EVT_ID, 1);
      blackPinkEvt.eventName        = "Blak Pink";

      ExtendEventInfo banDamEvt     = ExtendEventInfo.of(DB_EVT_ID, 1);
      banDamEvt.eventName           = "Ban Dam";

      ExtendEventInfo ngocTrinhEvt = ExtendEventInfo.of(NT_EVT_ID, 1);
      banDamEvt.eventName           = "Ban Dam";

      evtMap.put(BP_EVT_ID, blackPinkEvt);
      evtMap.put(DB_EVT_ID, banDamEvt);
      evtMap.put(NT_EVT_ID, ngocTrinhEvt);

      banDamEvt.addIdol(IdolClaimInfo.of(48, 93, 10));
      banDamEvt.addIdol(IdolClaimInfo.of(49, 93, 10));
      banDamEvt.addIdol(IdolClaimInfo.of(50, 93, 10));
      banDamEvt.addIdol(IdolClaimInfo.of(51, 93, 10));
      banDamEvt.addIdol(IdolClaimInfo.of(52, 93, 10));

      blackPinkEvt.addIdol(IdolClaimInfo.of(43, 92, 10));
      blackPinkEvt.addIdol(IdolClaimInfo.of(44, 92, 10));
      blackPinkEvt.addIdol(IdolClaimInfo.of(45, 92, 10));
      blackPinkEvt.addIdol(IdolClaimInfo.of(46, 92, 10));

      ngocTrinhEvt.addIdol(IdolClaimInfo.of(53, 140, 10));
    }
  }

  public static class COMMON_EVENT {
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
    public static final int         VIP_INCR_EVT_ID           = 14;

    public static final Map<Integer, ExtendEventInfo> evtMap;
    public static final Set<Integer>                  eventSet;
    static {
      eventSet = new HashSet<>();
      eventSet.add(TIME_SPEND_EVT_ID);
      eventSet.add(APT_BUFF_USE_EVT_ID);
      eventSet.add(MONEY_SPEND_EVT_ID);
      eventSet.add(VIEW_SPEND_EVT_ID);
      eventSet.add(FAN_SPEND_EVT_ID);
      eventSet.add(CRT_PROD_EVT_ID);
      eventSet.add(VIEW_PROD_EVT_ID);
      eventSet.add(FAN_PROD_EVT_ID);
      eventSet.add(GAME_SHOW_EVT_ID);
      eventSet.add(TOTAL_TALENT_EVT_ID);
      eventSet.add(VIP_INCR_EVT_ID);

      evtMap = new HashMap<>();
      evtMap.put(TIME_SPEND_EVT_ID,   ExtendEventInfo.of(TIME_SPEND_EVT_ID, 1));
      evtMap.put(APT_BUFF_USE_EVT_ID, ExtendEventInfo.of(APT_BUFF_USE_EVT_ID, 1));
      evtMap.put(MONEY_SPEND_EVT_ID,  ExtendEventInfo.of(MONEY_SPEND_EVT_ID, 1));
      evtMap.put(VIEW_SPEND_EVT_ID,   ExtendEventInfo.of(VIEW_SPEND_EVT_ID, 1));
      evtMap.put(FAN_SPEND_EVT_ID,    ExtendEventInfo.of(FAN_SPEND_EVT_ID, 1));
      evtMap.put(CRT_PROD_EVT_ID,     ExtendEventInfo.of(CRT_PROD_EVT_ID, 1));
      evtMap.put(VIEW_PROD_EVT_ID,    ExtendEventInfo.of(VIEW_PROD_EVT_ID, 1));
      evtMap.put(FAN_PROD_EVT_ID,     ExtendEventInfo.of(FAN_PROD_EVT_ID, 1));
      evtMap.put(TOTAL_TALENT_EVT_ID, ExtendEventInfo.of(TOTAL_TALENT_EVT_ID, 1));
      evtMap.put(GAME_SHOW_EVT_ID,    ExtendEventInfo.of(GAME_SHOW_EVT_ID, 1));
      evtMap.put(VIP_INCR_EVT_ID,     ExtendEventInfo.of(VIP_INCR_EVT_ID, 1));
    }
  }

  public static class USER_GAME_INFO {
    public static int               INIT_TIME_GIFT                = 86400*7; //milis
    public static int               TIME_ACTIVE_LEVEL             = 2;
    public static final int         MAX_AVATAR                    = 10;
    public static final int         MAX_GENDER                    = 2;
    public static final int         MEDIA_INTERVAL                = 1198; //unit second 20'
    public static final int         MEDIA_CONTRACT_ITEM           = 2;    //hợp đồng truyền thông
    public static int               INIT_MONEY                    = 500000;
    public static int               INIT_VIEW                     = 500000;
    public static int               INIT_FAN                      = 500000;
  }

  public static class USER_GROUP {
    public static int               CREATE_GROUP_TIME_COST    = 5*86400; //5 days
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
    public static int                     INIT_APT_EXP              = 200;

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
    public static int TRAVEL_UNLOCK_LEVEL         = 2;
    public static int GROUP_UNLOCK_LEVEL          = 2;
    public static int FRIEND_QR_UNLOCK_LEVEL      = 2;
    public static int SHOPPING_UNLOCK_LEVEL       = 2;
    public static int SKIP_FIGHT_UNLOCK_LEVEL     = 5;
    public static int FAST_SHOPPING_UNLOCK_LEVEL  = 5;
    public static int RUN_SHOW_UNLOCK_LEVEL       = 2;
    public static int FAST_RUN_SHOW_UNLOCK_LEVEL  = 5;
  }

  public static class EFFECT_RESULT {
    public static final int         ITEM_EFFECT_RESULT        = 1000; // item [1000, id, count]
    public static final int         IDOL_EFFECT_RESULT        = 2000; // idol [2000, field, change]
    public static final int         RAMPAGE_EFFECT_RESULT     = 3000; // rampage [3000, count, 0];
  }

  public static class NET_AWARD {
    public static int               EXPIRY                    = 60*24*30; //minutes
  }
}