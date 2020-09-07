package com.common;

import java.util.HashMap;
import java.util.Map;

public class Msg {
  public static Map<Integer, String> msgMap;

  //common
  public static final int LEVEL_LIMIT           = 0;
  public static final int INSUFFICIENT_TIME     = 1;
  public static final int DTO_DATA_NOT_FOUND    = 2;
  public static final int VIP_LEVEL_LIMIT       = 3;
  public static final int INVALID_DISPLAY_NAME  = 4;
  public static final int DISPLAY_NAME_EXIST    = 5;

  //profile
  public static final int SHOP_DATA_NOT_AVAIL     = 100;
  public static final int SHOP_ITEM_DAILY_LIMIT   = 200;
  public static final int USER_MAX_LEVEL          = 300;
  public static final int USER_EXP_INSUFFICIENT   = 400;

  static {
    msgMap = new HashMap<>();

    //profile/shop
    msgMap.put(LEVEL_LIMIT,             "Bạn không chưa đủ level để sử dụng tính năng này");
    msgMap.put(INSUFFICIENT_TIME,       "Thời gian còn lại không đủ");
    msgMap.put(DTO_DATA_NOT_FOUND,      "dto_data_not_found");
    msgMap.put(SHOP_DATA_NOT_AVAIL,     "shop_item_not_avail");
    msgMap.put(SHOP_ITEM_DAILY_LIMIT,   "Vat Pham Da Dat Gioi Han Mua Trong Ngay");
    msgMap.put(VIP_LEVEL_LIMIT,         "Chưa đủ cấp VIP");

    //profile/processUserLevelUp
    msgMap.put(USER_MAX_LEVEL,          "Da Dat Hang Sao Toi Da");
    msgMap.put(USER_EXP_INSUFFICIENT,   "Khong du Chinh Tich");
    msgMap.put(INVALID_DISPLAY_NAME,    "Ten Nhan Vat Khong Hop Le");
    //profile/processUpdateUserInfo
    //profile//updateDisplayName
    msgMap.put(DISPLAY_NAME_EXIST,      "Ten Nhan Vat Da Ton Tai");

  }
}
