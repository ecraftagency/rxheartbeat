package com.statics;

import com.heartbeat.common.Utilities;

public class EventInfo {
  public static final String  DATE_PATTERN            = "dd/MM/yyyy HH:mm:ss";
  public int      eventId;
  public int      startTime; //second
  public int      endTime; //second
  public String   strStart;
  public String   strEnd;
  public boolean  active;

  public static EventInfo of(int id, String start, String end, boolean active) {
    EventInfo ei  = new EventInfo();
    ei.eventId    = id;
    ei.strStart   = start;
    ei.strEnd     = end;
    ei.active     = active;

    try {
      ei.startTime  = (int)(Utilities.getMillisFromDateString(ei.strStart, DATE_PATTERN)/1000);
      ei.endTime    = (int)(Utilities.getMillisFromDateString(ei.strEnd, DATE_PATTERN)/1000);

      int second    = (int)(System.currentTimeMillis()/1000);
      if (second >= ei.startTime)
        throw new IllegalArgumentException("event time < current time");

      if (ei.endTime - ei.startTime <= 0)
        throw new IllegalArgumentException("end time < start time");
    }
    catch (Exception e) {
      ei.startTime  = -1;
      ei.endTime    = -1;
    }
    return ei;
  }

  public void updateEventTime(String startDate, String endDate) {
    strStart  = startDate;
    strEnd    = endDate;

    try {
      startTime  = (int)(Utilities.getMillisFromDateString(strStart, DATE_PATTERN)/1000);
      endTime    = (int)(Utilities.getMillisFromDateString(strEnd, DATE_PATTERN)/1000);

      int second    = (int)(System.currentTimeMillis()/1000);
      if (second >= startTime)
        throw new IllegalArgumentException("event time < current time");

      if (endTime - startTime <= 0)
        throw new IllegalArgumentException();
    }
    catch (Exception e) {
      startTime  = -1;
      endTime    = -1;
    }
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}