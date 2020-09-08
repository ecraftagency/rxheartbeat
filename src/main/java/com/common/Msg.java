package com.common;

import java.util.HashMap;
import java.util.Map;

public class Msg {
  public static Map<Integer, String> msgMap;

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
  public static final int USER_INFO_EXIST             = 106;
  public static final int MEDIA_TIME_OUT              = 104;
  public static final int INSUFFICIENT_CRAZY_DEGREE   = 105;


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
  public static final int ADD_APT_INVALID_STEP        = 406;
  public static final int INVALID_SPECIALITY          = 407;
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
    msgMap = new HashMap<>();

    //common
    msgMap.put(LEVEL_LIMIT,                 "Bạn không chưa đủ hang sao để sử dụng tính năng này");
    msgMap.put(INSUFFICIENT_TIME,           "Khong du thoi gian");
    msgMap.put(DTO_DATA_NOT_FOUND,          "dto_data_not_found");  /**/
    msgMap.put(VIP_LEVEL_LIMIT,             "Chưa đủ cấp VIP");
    msgMap.put(INVALID_DISPLAY_NAME,        "Ten Nhan Vat Khong Hop Le");
    msgMap.put(DISPLAY_NAME_EXIST,          "Ten Nhan Vat Da Ton Tai");
    msgMap.put(ALREADY_CLAIM,               "already_claim"); /**/
    msgMap.put(BLANK_REWARD,                "blank_reward"); /**/
    msgMap.put(UNKNOWN_MILESTONE,           "unknown_milestone");
    msgMap.put(RECORD_NOT_FOUND,            "record_not_found");
    msgMap.put(INSUFFICIENT_CLAIM,          "insufficient_claim");
    msgMap.put(CAS_EXPIRE,                  "cas_expire");
    msgMap.put(TIMEOUT_CLAIM,               "timeout_claim");
    msgMap.put(EVENT_NOT_FOUND,             "event_not_found");
    msgMap.put(IDOL_NOT_FOUND,              "idol_not_found");
    msgMap.put(INSUFFICIENT_ITEM,           "event_not_found");
    msgMap.put(TIME_LIMIT,                  "Ban ko du time de su dung tinh nang nay");
    msgMap.put(GROUP_NOT_FOUND,             "group_not_found");
    msgMap.put(UNKNOWN_ERR,                 "unknown_err");
    msgMap.put(MALFORM_ARGS,                "malform_args");
    msgMap.put(REWARD_FORMAT_INVALID,       "reward_format_invalid");

    //Profile Controller
    msgMap.put(SHOP_DATA_NOT_AVAIL,         "shop_item_not_avail"); /**/
    msgMap.put(SHOP_ITEM_DAILY_LIMIT,       "Vat Pham Da Dat Gioi Han Mua Trong Ngay");
    msgMap.put(USER_MAX_LEVEL,              "Da Dat Hang Sao Toi Da");
    msgMap.put(USER_EXP_INSUFFICIENT,       "Khong du Chinh Tich");
    msgMap.put(USER_INFO_EXIST,             "user_info_exist");
    msgMap.put(MEDIA_TIME_OUT,              "Ban can thoi gian hoi phuc truyen thong");
    msgMap.put(INSUFFICIENT_CRAZY_DEGREE,   "Khong Du DO Soi Noi");

    //Achievement Controller
    //DailyMission  Controller
    //Event Controller
    //Fight Controller
    msgMap.put(GAME_SHOW_TIMEOUT,           "Chua den gio mo phu ban");
    msgMap.put(MAX_FIGHT,                   "Ban da hoan thanh tat ca cac ai");
    msgMap.put(FIGHT_LOSS,                  "That bai");
    msgMap.put(IDOL_ALREADY_FOUGHT,         "Idol da xuat chien");
    msgMap.put(IDOL_ALREADY_RESTORE,        "Idol da tai xuat chien");
    msgMap.put(GAME_SHOW_MAX,               "Ban da hoan thanh ta ca phu ban");
    msgMap.put(RUN_SHOW_MAX,                "Ban da chay tat ca cac show");
    msgMap.put(MAX_SHOPPING,                "Ban da hoan thanh tat ca cac luot shopping");

    //Group Controller
    msgMap.put(INVALID_GROUP_TYPE,          "invalid_group_type");
    msgMap.put(GROUP_PERM,                  "fail_permission");
    msgMap.put(DUP_MEMBER,                  "set_role_fail_dup_memberID");
    msgMap.put(GROUP_DELAY,                 "group_delay");
    msgMap.put(NO_GROUP,                    "user_have_no_group");
    msgMap.put(UNKNOWN_GID,                 "unknown_gid");
    msgMap.put(ALREADY_JOIN,                "user_already_have_group");
    msgMap.put(OWNER_KICK,                  "fail_cant_kick_owner");
    msgMap.put(OWNER_LEAVE,                 "leave_group_fail_admin");
    msgMap.put(SELF_APPROVE,                "can_not_self_approve");
    msgMap.put(GROUP_FULL_SEAT,             "group_full_seat");
    msgMap.put(GROUP_JOIN_PENDING,          "group_join_pending");
    msgMap.put(MEMBER_NOT_FOUND,            "member_not_found");
    msgMap.put(MISSION_NOT_FOUND,           "mission_not_found");

    //Idol Controller
    msgMap.put(IDOL_NOT_EXIST,              "idol_not_exist");
    msgMap.put(IDOL_MAX_LEVEL,              "idol_max_level");
    msgMap.put(IDOL_LV_UP_INSUFFICIENT,     "idol_insufficient_exp");
    msgMap.put(IDOL_HONOR_MAX_LEVEL,        "idol_honor_max_level");
    msgMap.put(INSUFFICIENT_APT_EXP,        "insufficient_apt_exp");
    msgMap.put(APT_LIMIT,                   "idol_apt_limit");
    msgMap.put(ADD_APT_INVALID_STEP,        "add_apt_invalid_step");
    msgMap.put(INVALID_SPECIALITY,          "invalid_speciality");
    msgMap.put(HALO_NOT_EXIST,              "halo_not_exist");
    msgMap.put(HALO_PREFIX_NOT_MATCH,       "prefix_not_match");
    msgMap.put(HALO_DATA_INVALID,           "halo_data_invalid");
    msgMap.put(HALO_LEVEL_UP_FAIL,          "halo_level_up_fail");

    //Inbox Controller
    msgMap.put(MSG_REWARD_CLAIM_ALREADY,    "msg_reward_already_claim");
    msgMap.put(MSG_NOT_EXIST,               "msg_not_exist");
    msgMap.put(MSG_REWARD_INVALID,          "msg_reward_invalid");

    //Item Controller
    msgMap.put(MERGE_DAILY_LIMIT,           "merge_daily_limit");
    msgMap.put(INSUFFICIENT_MATERIAL,       "insufficient_material");

    //Mission Controller
    msgMap.put(MISSION_NOT_ACCOMPLISH,      "mission_not_accomplish");

    //Production Controller
    msgMap.put(CLAIM_PRODUCT_TIME_OUT,      "claim_product_timeout");

    //Rollcall Controller
    msgMap.put(GIFT_CARD_EXPIRE,            "gift_card_expire");
    msgMap.put(VIP_GIFT_CLAIM_FAIL,         "vip_gift_claim_fail");

    //Travel Controller
    msgMap.put(CLAIM_TRAVEL_INSUFFICIENT,   "claim_travel_insufficient_count");
    msgMap.put(CLAIM_TRAVEL_MISS,           "claim_travel_miss");
    msgMap.put(MAX_TRAVEL_ADD,              "max_travel_add");
    msgMap.put(INSUFFICIENT_VIEW,           "insufficient_view");

    //Ranking Controller
    msgMap.put(INVALID_RANK,                "invalid_rank");
    msgMap.put(UNKNOWN_RANK_TYPE,           "unknown_rank_type");
    msgMap.put(RANKING_NOT_ACTIVE,          "ranking_not_active");
  }
}