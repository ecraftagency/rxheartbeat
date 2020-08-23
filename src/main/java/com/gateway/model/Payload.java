package com.gateway.model;

public class Payload {
  public String    id100D;
  public int       sessionId;
  public int       nodeId;
  public String    orderId;
  public String    itemId;
  public long      money;
  public long      gold;
  public long      time;

  private Payload() {

  }

  public static Payload of(String id100D, int sessionId, int nodeId, String orderId, String itemId, long money, long gold, long time) {
    Payload payload   = new Payload();
    payload.id100D    = id100D;
    payload.sessionId = sessionId;
    payload.nodeId    = nodeId;
    payload.orderId   = orderId;
    payload.itemId    = itemId;
    payload.money     = money;
    payload.gold      = gold;
    payload.time      = time;
    return payload;
  }
}