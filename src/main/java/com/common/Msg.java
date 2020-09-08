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
  public static final int INVALID_GROUP_TYPE = 300;
  public static final int GROUP_PERM                  = 301;
  public static final int DUP_MEMBER                  = 302;
  public static final int GROUP_DELAY                 = 303;
  public static final int NO_GROUP                    = 304;
  public static final int UNKNOWN_GID                 = 305;
  public static final int ALREADY_JOIN                = 306;

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
    msgMap.put(GROUP_NOT_FOUND,             "Khong tim thay cong ty");
    msgMap.put(UNKNOWN_ERR,                 "unknown_err");

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
    msgMap.put(GROUP_PERM,                  "Khong du quyen de thuc hien tinh nang nay");
    msgMap.put(DUP_MEMBER,                  "set_role_fail_dup_memberID");
    msgMap.put(GROUP_DELAY,                 "group_delay");
    msgMap.put(NO_GROUP,                    "user_have_no_group");
    msgMap.put(UNKNOWN_GID,                 "unknown_gid");
    msgMap.put(ALREADY_JOIN,                "user_already_have_group");

  }
}