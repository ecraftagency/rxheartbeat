package com.common;

import java.util.HashMap;
import java.util.Map;

public class Msg {
  public static Map<Integer, String> map;

  //common
  public static final int LEVEL_LIMIT                 = 0;
  public static final int INSUFFICIENT_TIME           = 1;
  public static final int DTO_DATA_NOT_FOUND          = 2;
  public static final int VIP_LEVEL_LIMIT             = 3;
  public static final int INVALID_DISPLAY_NAME        = 4;
  public static final int DISPLAY_NAME_EXIST          = 5;
  public static final int ALREADY_CLAIM               = 6;
  public static final int BLANK_REWARD                = 7;
  public static final int UNKNOWN_MILESTONE           = 8;
  public static final int RECORD_NOT_FOUND            = 9;
  public static final int INSUFFICIENT_CLAIM          = 10;
  public static final int CAS_EXPIRE                  = 11;
  public static final int TIMEOUT_CLAIM               = 12;
  public static final int EVENT_NOT_FOUND             = 13;
  public static final int IDOL_NOT_FOUND              = 14;
  public static final int INSUFFICIENT_ITEM           = 15;
  public static final int TIME_LIMIT                  = 16;
  public static final int GROUP_NOT_FOUND             = 17;
  public static final int UNKNOWN_ERR                 = 18;
  public static final int MALFORM_ARGS                = 19;
  public static final int REWARD_FORMAT_INVALID       = 20;

  //profile
  public static final int SHOP_DATA_NOT_AVAIL         = 100;
  public static final int SHOP_ITEM_DAILY_LIMIT       = 101;
  public static final int USER_MAX_LEVEL              = 102;
  public static final int USER_EXP_INSUFFICIENT       = 103;
  public static final int MEDIA_TIME_OUT              = 104;
  public static final int INSUFFICIENT_CRAZY_DEGREE   = 105;
  public static final int USER_INFO_EXIST             = 106;


  //fight
  public static final int GAME_SHOW_TIMEOUT           = 200;
  public static final int MAX_FIGHT                   = 201;
  public static final int FIGHT_LOSS                  = 202;
  public static final int IDOL_ALREADY_FOUGHT         = 203;
  public static final int IDOL_ALREADY_RESTORE        = 204;
  public static final int GAME_SHOW_MAX               = 205;
  public static final int RUN_SHOW_MAX                = 206;
  public static final int MAX_SHOPPING                = 207;

  //group
  public static final int INVALID_GROUP_TYPE          = 300;
  public static final int GROUP_PERM                  = 301;
  public static final int DUP_MEMBER                  = 302;
  public static final int GROUP_DELAY                 = 303;
  public static final int NO_GROUP                    = 304;
  public static final int UNKNOWN_GID                 = 305;
  public static final int ALREADY_JOIN                = 306;
  public static final int OWNER_KICK                  = 307;
  public static final int OWNER_LEAVE                 = 308;
  public static final int SELF_APPROVE                = 309;
  public static final int GROUP_FULL_SEAT             = 310;
  public static final int GROUP_JOIN_PENDING          = 311;
  public static final int MEMBER_NOT_FOUND            = 312;
  public static final int MISSION_NOT_FOUND           = 313;

  //idol
  public static final int IDOL_NOT_EXIST              = 400;
  public static final int IDOL_MAX_LEVEL              = 401;
  public static final int IDOL_LV_UP_INSUFFICIENT     = 402;
  public static final int IDOL_HONOR_MAX_LEVEL        = 403;
  public static final int INSUFFICIENT_APT_EXP        = 404;
  public static final int APT_LIMIT                   = 405;
  public static final int HALO_NOT_EXIST              = 408;
  public static final int HALO_PREFIX_NOT_MATCH       = 409;
  public static final int HALO_DATA_INVALID           = 410;
  public static final int HALO_LEVEL_UP_FAIL          = 411;
  public static final int HALO_LEVEL_MAX              = 412;

  //inbox
  public static final int MSG_REWARD_CLAIM_ALREADY    = 500;
  public static final int MSG_NOT_EXIST               = 501;
  public static final int MSG_REWARD_INVALID          = 502;

  //inventory
  public static final int INSUFFICIENT_MATERIAL       = 600;
  public static final int MERGE_DAILY_LIMIT           = 601;

  //mission
  public static final int MISSION_NOT_ACCOMPLISH      = 700;

  //userProduction
  public static final int CLAIM_PRODUCT_TIME_OUT      = 800;

  //userRollcall
  public static final int GIFT_CARD_EXPIRE            = 900;
  public static final int VIP_GIFT_CLAIM_FAIL         = 901;

  //userTravel
  public static final int CLAIM_TRAVEL_INSUFFICIENT   = 1000;
  public static final int CLAIM_TRAVEL_MISS           = 1001;
  public static final int MAX_TRAVEL_ADD              = 1002;
  public static final int INSUFFICIENT_VIEW           = 1003;

  //user Ranking
  public static final int INVALID_RANK                = 1100;
  public static final int UNKNOWN_RANK_TYPE           = 1101;
  public static final int RANKING_NOT_ACTIVE          = 1102;


  /*
  - Chia ra error chung(xuất hiện nhiều lần) / riêng(chỉ 1 lần cho bối cảnh nào đó).
    giá trị default sẽ là String lỗi cũ để làm tail
  - Trong quá trình chạy nếu 1 tính năng báo lỗi chung nhưng bối cảnh cần text riêng thì đưa
    từ common qua specific
  - Một số lỗi hệ thống tức là nếu code ko lỗi || data ok || gamer chơi bt sẽ ko bh xảy ra
    thì vẫn text lỗi bt. Nếu quá trình chơi 1 cách bt mà nó vẫn nhảy lỗi thì sẽ đưa từ
    hệ thống sang lỗi bối cảnh và sẽ có text.
  - Với các lỗi bối cảnh như ko đủ, ko chưa claim dc, quá giờ..mà bên ops chơi cảm thấy
    ko ok thì báo và change.
  - Mọi thứ ok hết thì ra file + có thể chia lang sau này
   */


  static {
    map = new HashMap<>();

    //common
    map.put(LEVEL_LIMIT,                 "Bạn không chưa đủ hang sao để sử dụng tính năng này");
    map.put(INSUFFICIENT_TIME,           "Khong du thoi gian");
    map.put(DTO_DATA_NOT_FOUND,          "dto_data_not_found");  /**/
    map.put(VIP_LEVEL_LIMIT,             "Chưa đủ cấp VIP");
    map.put(INVALID_DISPLAY_NAME,        "Ten Nhan Vat Khong Hop Le");
    map.put(DISPLAY_NAME_EXIST,          "Ten Nhan Vat Da Ton Tai");
    map.put(ALREADY_CLAIM,               "already_claim"); /**/
    map.put(BLANK_REWARD,                "blank_reward"); /**/
    map.put(UNKNOWN_MILESTONE,           "unknown_milestone");
    map.put(RECORD_NOT_FOUND,            "record_not_found");
    map.put(INSUFFICIENT_CLAIM,          "insufficient_claim");
    map.put(CAS_EXPIRE,                  "cas_expire");
    map.put(TIMEOUT_CLAIM,               "timeout_claim");
    map.put(EVENT_NOT_FOUND,             "event_not_found");
    map.put(IDOL_NOT_FOUND,              "idol_not_found");
    map.put(INSUFFICIENT_ITEM,           "insufficient_item");
    map.put(TIME_LIMIT,                  "Ban ko du time de su dung tinh nang nay");
    map.put(GROUP_NOT_FOUND,             "group_not_found");
    map.put(UNKNOWN_ERR,                 "unknown_err");
    map.put(MALFORM_ARGS,                "malform_args");
    map.put(REWARD_FORMAT_INVALID,       "reward_format_invalid");

    //Profile Controller
    map.put(SHOP_DATA_NOT_AVAIL,         "shop_item_not_avail"); /**/
    map.put(SHOP_ITEM_DAILY_LIMIT,       "Vat Pham Da Dat Gioi Han Mua Trong Ngay");
    map.put(USER_MAX_LEVEL,              "Da Dat Hang Sao Toi Da");
    map.put(USER_EXP_INSUFFICIENT,       "Khong du Chinh Tich");
    map.put(MEDIA_TIME_OUT,              "Ban can thoi gian hoi phuc truyen thong");
    map.put(INSUFFICIENT_CRAZY_DEGREE,   "Khong Du DO Soi Noi");
    map.put(USER_INFO_EXIST,             "user_info_exist");

    //Achievement Controller
    //DailyMission  Controller
    //Event Controller
    //Fight Controller
    map.put(GAME_SHOW_TIMEOUT,           "Chua den gio mo phu ban");
    map.put(MAX_FIGHT,                   "Ban da hoan thanh tat ca cac ai");
    map.put(FIGHT_LOSS,                  "That bai");
    map.put(IDOL_ALREADY_FOUGHT,         "Idol da xuat chien");
    map.put(IDOL_ALREADY_RESTORE,        "Idol da tai xuat chien");
    map.put(GAME_SHOW_MAX,               "Ban da hoan thanh ta ca phu ban");
    map.put(RUN_SHOW_MAX,                "Ban da chay tat ca cac show");
    map.put(MAX_SHOPPING,                "Ban da hoan thanh tat ca cac luot shopping");

    //Group Controller
    map.put(INVALID_GROUP_TYPE,          "invalid_group_type");
    map.put(GROUP_PERM,                  "fail_permission");
    map.put(DUP_MEMBER,                  "set_role_fail_dup_memberID");
    map.put(GROUP_DELAY,                 "group_delay");
    map.put(NO_GROUP,                    "user_have_no_group");
    map.put(UNKNOWN_GID,                 "unknown_gid");
    map.put(ALREADY_JOIN,                "user_already_have_group");
    map.put(OWNER_KICK,                  "fail_cant_kick_owner");
    map.put(OWNER_LEAVE,                 "leave_group_fail_admin");
    map.put(SELF_APPROVE,                "can_not_self_approve");
    map.put(GROUP_FULL_SEAT,             "group_full_seat");
    map.put(GROUP_JOIN_PENDING,          "group_join_pending");
    map.put(MEMBER_NOT_FOUND,            "member_not_found");
    map.put(MISSION_NOT_FOUND,           "mission_not_found");

    //Idol Controller
    map.put(IDOL_NOT_EXIST,              "idol_not_exist");
    map.put(IDOL_MAX_LEVEL,              "idol_max_level");
    map.put(IDOL_LV_UP_INSUFFICIENT,     "idol_lv_up_insufficient_exp");
    map.put(IDOL_HONOR_MAX_LEVEL,        "idol_honor_max_level");
    map.put(INSUFFICIENT_APT_EXP,        "insufficient_apt_exp");
    map.put(APT_LIMIT,                   "idol_apt_limit");
    map.put(HALO_NOT_EXIST,              "halo_not_exist");
    map.put(HALO_PREFIX_NOT_MATCH,       "prefix_not_match");
    map.put(HALO_DATA_INVALID,           "halo_data_invalid");
    map.put(HALO_LEVEL_UP_FAIL,          "halo_level_up_fail");
    map.put(HALO_LEVEL_MAX,              "halo_level_max");

    //Inbox Controller
    map.put(MSG_REWARD_CLAIM_ALREADY,    "msg_reward_already_claim");
    map.put(MSG_NOT_EXIST,               "msg_not_exist");
    map.put(MSG_REWARD_INVALID,          "msg_reward_invalid");

    //Item Controller
    map.put(INSUFFICIENT_MATERIAL,       "insufficient_material");
    map.put(MERGE_DAILY_LIMIT,           "merge_daily_limit");

    //Mission Controller
    map.put(MISSION_NOT_ACCOMPLISH,      "mission_not_accomplish");

    //Production Controller
    map.put(CLAIM_PRODUCT_TIME_OUT,      "claim_product_timeout");

    //Rollcall Controller
    map.put(GIFT_CARD_EXPIRE,            "gift_card_expire");
    map.put(VIP_GIFT_CLAIM_FAIL,         "vip_gift_claim_fail");

    //Travel Controller
    map.put(CLAIM_TRAVEL_INSUFFICIENT,   "claim_travel_insufficient_count");
    map.put(CLAIM_TRAVEL_MISS,           "claim_travel_miss");
    map.put(MAX_TRAVEL_ADD,              "max_travel_add");
    map.put(INSUFFICIENT_VIEW,           "insufficient_view");

    //Ranking Controller
    map.put(INVALID_RANK,                "invalid_rank");
    map.put(UNKNOWN_RANK_TYPE,           "unknown_rank_type");
    map.put(RANKING_NOT_ACTIVE,          "ranking_not_active");
  }
}