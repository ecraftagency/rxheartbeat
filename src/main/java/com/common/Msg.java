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
  public static final int INVALID_GROUP_NAME          = 314;
  public static final int INVALID_EXT_INFORM          = 315;
  public static final int INVALID_INT_INFORM          = 316;
  public static final int PREV_CLAIM                  = 317;

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
  public static final int ZERO_MERGE_COUNT            = 602;

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

  //netAward
  public static final int AWARD_TITLE_INVALID         = 1200;


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
    map.put(LEVEL_LIMIT,                 "Bạn chưa đủ hạng sao để sử dụng tính năng này");
    map.put(INSUFFICIENT_TIME,           "Bạn không đủ TIME");
    map.put(DTO_DATA_NOT_FOUND,          "Không tìm thấy dữ liệu, vui lòng thử lại");  /**/
    map.put(VIP_LEVEL_LIMIT,             "Bạn chưa đạt cấp VIP");
    map.put(INVALID_DISPLAY_NAME,        "Tên Nhân Vật Không Hợp Lệ");
    map.put(DISPLAY_NAME_EXIST,          "Tên Nhân Vật Đã Tồn Tại");
    map.put(ALREADY_CLAIM,               "Bạn đã nhận phần thưởng này"); /**/
    map.put(BLANK_REWARD,                "Dữ liệu phần thưởng không hợp lệ"); /**/
    map.put(UNKNOWN_MILESTONE,           "Dữ liệu thành tích không hợp lệ");
    map.put(RECORD_NOT_FOUND,            "Không tìm thấy dữ liệu, vui lòng thử lại");
    map.put(INSUFFICIENT_CLAIM,          "Bạn chưa đạt yêu cầu nhận thưởng");
    map.put(CAS_EXPIRE,                  "cas_expire");
    map.put(TIMEOUT_CLAIM,               "Thời gian nhận thưởng đã hết");
    map.put(EVENT_NOT_FOUND,             "Không tìm thấy dữ liệu sự kiện");
    map.put(IDOL_NOT_FOUND,              "Không tìm thấy dữ liệu Idol");
    map.put(INSUFFICIENT_ITEM,           "Bạn không có đủ vật phẩm yêu cầu");
    map.put(TIME_LIMIT,                  "Bạn đã hết TIME, không thể nhận thưởng");
    map.put(GROUP_NOT_FOUND,             "Không tìm thấy dữ liệu công ty");
    map.put(UNKNOWN_ERR,                 "Mạng không ổn định, vui lòng thử lại");
    map.put(MALFORM_ARGS,                "Mạng không ổn định, vui lòng thử lại");
    map.put(REWARD_FORMAT_INVALID,       "Dữ liệu phần thưởng không hợp lệ");

    //Profile Controller
    map.put(SHOP_DATA_NOT_AVAIL,         "Không tìm thấy dữ liệu SHOP"); /**/
    map.put(SHOP_ITEM_DAILY_LIMIT,       "Lần giới hạn mua đã hết");
    map.put(USER_MAX_LEVEL,              "Đã đạt hạng sao tối đa");
    map.put(USER_EXP_INSUFFICIENT,       "Bạn không có đủ danh tiếng");
    map.put(MEDIA_TIME_OUT,              "Bạn cần chờ thời gian hồi phục");
    map.put(INSUFFICIENT_CRAZY_DEGREE,   "Không đủ độ sôi nổi");
    map.put(USER_INFO_EXIST,             "Không tìm thấy dữ liệu nhân vật");

    //Achievement Controller
    //DailyMission  Controller
    //Event Controller
    //Fight Controller
    map.put(GAME_SHOW_TIMEOUT,           "Phụ bản Gameshow chưa mở, vui lòng thử lại sau");
    map.put(MAX_FIGHT,                   "Bạn đã hoàn thành tất cả các ải");
    map.put(FIGHT_LOSS,                  "Khiêu chiến thất bại");
    map.put(IDOL_ALREADY_FOUGHT,         "Idol đã xuất chiến");
    map.put(IDOL_ALREADY_RESTORE,        "Idol đã hồi phục lượt xuất chiến");
    map.put(GAME_SHOW_MAX,               "Bạn đã hoàn thành tất cả phụ bản");
    map.put(RUN_SHOW_MAX,                "Bạn đã hoàn thành tất cả lượt chạy show trong ngày");
    map.put(MAX_SHOPPING,                "Bạn đã hoàn thành tất cả lượt shopping trong ngày");

    //Group Controller
    map.put(INVALID_GROUP_TYPE,          "Dữ liệu công ty không hợp lệ");
    map.put(GROUP_PERM,                  "Chức vụ không phù hợp để thực hiện thao tác này");
    map.put(DUP_MEMBER,                  "Không thể thay đổi chức vụ bản thân");
    map.put(GROUP_DELAY,                 "Bạn vừa rời khỏi công ty, vui lòng chờ 24h");
    map.put(NO_GROUP,                    "Bạn chưa tham gia công ty");
    map.put(UNKNOWN_GID,                 "Không tìm thấy dữ liệu công ty");
    map.put(ALREADY_JOIN,                "Bạn đã tham gia công ty");
    map.put(OWNER_KICK,                  "Không thể tự kick bản thân");
    map.put(OWNER_LEAVE,                 "Bạn đang giữ chức vụ giám đốc, không thể rời công ty");
    map.put(SELF_APPROVE,                "Chức vụ không phù hợp để duyệt");
    map.put(GROUP_FULL_SEAT,             "Công ty đã đủ thành viên");
    map.put(GROUP_JOIN_PENDING,          "Đang chờ duyệt");
    map.put(MEMBER_NOT_FOUND,            "Không tìm thấy dữ liệu thành viên");
    map.put(MISSION_NOT_FOUND,           "Không tìm thấy dữ liệu nhiệm vụ công ty");
    map.put(INVALID_GROUP_NAME,          "Tên công ty không hợp lệ");
    map.put(INVALID_EXT_INFORM,          "Nội dung đối ngoại không hợp lệ");
    map.put(INVALID_INT_INFORM,          "Nội dung đối nội không hợp lệ");
    map.put(PREV_CLAIM,                  "Bạn phải hoàn thành/ nhận phần thưởng của NV trước");
    //Idol Controller
    map.put(IDOL_NOT_EXIST,              "Idol không tồn tại");
    map.put(IDOL_MAX_LEVEL,              "Idol đã đạt cấp tối đa");
    map.put(IDOL_LV_UP_INSUFFICIENT,     "Cát xê không đủ để tăng cấp Idol");
    map.put(IDOL_HONOR_MAX_LEVEL,        "Idol đã đạt cấp danh hiệu tối đa");
    map.put(INSUFFICIENT_APT_EXP,        "EXP tố chất không đủ");
    map.put(APT_LIMIT,                   "Tố chất của Idol đã đạt giới hạn, sau khi đề cử tăng giới hạn");
    map.put(HALO_NOT_EXIST,              "Vòng sáng không tồn tại");
    map.put(HALO_PREFIX_NOT_MATCH,       "Thông tin không hợp lệ, vui lòng thử lại");
    map.put(HALO_DATA_INVALID,           "Dữ liệu vòng sáng không hợp lệ");
    map.put(HALO_LEVEL_UP_FAIL,          "Tăng cấp vòng sáng thất bại");
    map.put(HALO_LEVEL_MAX,              "Vòng sáng đã đạt cấp tối đa");

    //Inbox Controller
    map.put(MSG_REWARD_CLAIM_ALREADY,    "Bạn đã nhận vật phẩm đính kèm");
    map.put(MSG_NOT_EXIST,               "Thư không tồn tại");
    map.put(MSG_REWARD_INVALID,          "Dữ liệu phần thưởng không hợp lệ");

    //Item Controller
    map.put(INSUFFICIENT_MATERIAL,       "Không đủ nguyên liệu để ghép");
    map.put(MERGE_DAILY_LIMIT,           "Số lần ghép hàng ngày đã đạt giới hạn");
    map.put(ZERO_MERGE_COUNT,            "Số lượng ghép phải từ 1 trở lên");

    //Mission Controller
    map.put(MISSION_NOT_ACCOMPLISH,      "Nhiệm vụ chưa hoàn thành");

    //Production Controller
    map.put(CLAIM_PRODUCT_TIME_OUT,      "Không đủ số lần kinh doanh");

    //Rollcall Controller
    map.put(GIFT_CARD_EXPIRE,            "Thẻ tháng đã hết hạn");
    map.put(VIP_GIFT_CLAIM_FAIL,         "Nhận thất bại, vui lòng thử lại");

    //Travel Controller
    map.put(CLAIM_TRAVEL_INSUFFICIENT,   "Không đủ số lượt đu đưa");
    map.put(CLAIM_TRAVEL_MISS,           "claim_travel_miss");
    map.put(MAX_TRAVEL_ADD,              "Số lượt mua hàng ngày đã đạt giới hạn, tăng cấp VIP có thể tăng giới hạn");
    map.put(INSUFFICIENT_VIEW,           "Lượng view không đủ");

    //Ranking Controller
    map.put(INVALID_RANK,                "Không nằm trong BXH");
    map.put(UNKNOWN_RANK_TYPE,           "Dữ liệu xếp hạng không hợp lệ");
    map.put(RANKING_NOT_ACTIVE,          "Không trong thời gian sự kiện");

    //NetAward Controller
    map.put(AWARD_TITLE_INVALID,          "Tuyên ngôn không hợp lệ");
  }
}