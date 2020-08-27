package com.transport.model;

import java.util.Map;

public class Inbox {
  public long                   lastMailCheckTime;
  public Map<Long, Long>        claimedMsg;
  public Map<Long, MailObj>     privateMail;
}