package com.transport.model;

import java.util.Deque;
import java.util.Map;

public class Inbox {
  public long                   lastMailCheckTime;
  public Map<Long, Long>        claimedMsg;
  public Deque<MailObj>         privateMails;
  public Map<Long, Long>        claimedPrivateMsg;
}