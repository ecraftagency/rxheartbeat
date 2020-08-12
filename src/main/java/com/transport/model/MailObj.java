package com.transport.model;

import java.util.List;

public class MailObj implements Comparable<ScoreObj>{
  public static final int MSG_TYPE_PUBLIC   = 0;
  public static final int MSG_TYPE_PRIVATE  = 1;

  public long                 id;
  public String               title;
  public String msg;
  public int msgType;
  public List<List<Integer>>  rewards;

  @Override
  public int compareTo(ScoreObj o) {
    if (id > o.id)
      return 1;
    else if (id < o.id)
      return -1;
    return 0;
  }

  public static MailObj of(String title, String message, List<List<Integer>> rewards, int msgType) {
    MailObj mo  = new MailObj();
    mo.id       = System.currentTimeMillis();
    mo.title    = title;
    mo.msg      = message;
    mo.rewards  = rewards;
    mo.msgType  = msgType;
    return mo;
  }
}