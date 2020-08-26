package com.statics;

@SuppressWarnings("unused")
public class EventInfo {
  public static int FLUSH_DELAY = 60*60*24;
  public String     eventName;
  public int        eventId;
  public int        startTime; //second
  public int        endTime; //second
  public int        flushDelay;
  public boolean    active;

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