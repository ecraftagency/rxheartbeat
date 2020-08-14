package com.statics;

@SuppressWarnings("unused")
public class EventInfo {
  public static final String  DATE_PATTERN            = "dd/MM/yyyy HH:mm:ss";
  public String   eventName;
  public int      eventId;
  public int      startTime; //second
  public int      endTime; //second
  public int      flushDelay;
  public String   strStart;
  public String   strEnd;
  public boolean  active;
}