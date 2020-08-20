package com.gmtool.model;

import java.util.HashMap;
import java.util.Map;
import static com.common.Constant.*;

@SuppressWarnings("unused")
public class Event {
  public static Map<Integer, String> id2Name;

  int    id;
  String eventName;
  String startDate;
  String endDate;
  String active;

  public static Event of(int id, String name, String start, String end, String active) {
    Event e = new Event();
    e.id = id;
    e.active = active;
    e.startDate = start;
    e.endDate = end;
    e.eventName = name;
    return e;
  }

  public int getId() {
    return id;
  }

  public String getEventName() {
    return eventName;
  }

  public String getActive() {
    return active;
  }

  public String getEndDate() {
    return endDate;
  }

  public String getStartDate() {
    return startDate;
  }

  static {
    id2Name = new HashMap<>();
    id2Name.put(USER_EVENT.APT_BUFF_USE_EVT_ID,   "Sử dụng cuộn cường hóa");
    id2Name.put(USER_EVENT.VIEW_PROD_EVT_ID,      "Cày View");
    id2Name.put(USER_EVENT.VIEW_SPEND_EVT_ID,     "Tiêu hao view");
    id2Name.put(USER_EVENT.CRT_PROD_EVT_ID,       "Sáng tác");
    id2Name.put(USER_EVENT.FAN_PROD_EVT_ID,       "Fan metting");
    id2Name.put(USER_EVENT.FAN_SPEND_EVT_ID,      "Tiêu hao fan");
    id2Name.put(USER_EVENT.GAME_SHOW_EVT_ID,      "Phụ bản game ");
    id2Name.put(USER_EVENT.MONEY_SPEND_EVT_ID,    "Tiêu hao ");
    id2Name.put(USER_EVENT.TIME_SPEND_EVT_ID,     "Tiêu hao time");
    id2Name.put(USER_EVENT.TOTAL_TALENT_EVT_ID,   "Tăng tổng tài năng");
  }
}