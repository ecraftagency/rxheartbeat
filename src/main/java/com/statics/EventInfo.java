package com.statics;

import java.util.Map;

@SuppressWarnings("unused")
public class EventInfo {
  public static int FLUSH_DELAY = 60*60*24;
  public String     eventName;
  public int        eventId;
  public int        startTime; //second
  public int        endTime; //second
  public int        flushDelay;
  public boolean    active;

  //extra composite
  public Map<Integer, IdolClaimInfo> idolList;

  public static class IdolClaimInfo {
    public int idolId;
    public int requireItem;
    public int amount;
    public static IdolClaimInfo of(int idolId, int itemId, int amount) {
      IdolClaimInfo icp   = new IdolClaimInfo();
      icp.idolId          = idolId;
      icp.requireItem     = itemId;
      icp.amount          = amount;
      return icp;
    }
  }

  public int getEndTime() {
    return endTime;
  }

  public int getEventId() {
    return eventId;
  }

  public int getFlushDelay() {
    return flushDelay;
  }

  public int getStartTime() {
    return startTime;
  }

  public String getEventName() {
    return eventName;
  }

  public boolean isActive() {
    return active;
  }
}