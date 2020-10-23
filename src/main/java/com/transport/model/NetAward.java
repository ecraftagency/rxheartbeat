package com.transport.model;

public class NetAward {
  public int    id;
  public String titleName;
  public String username;
  public String manifesto;

  public long   totalCrt;
  public long   totalPerf;
  public long   totalAttr;
  public int    curFightId;
  public int    userTitleId;
  public int    vipLevel;
  public int    avatar;
  public int    gender;
  public long   exp;
  public int    addedTime;

  public static NetAward of(int id, String titleName, String username, String manifesto) {
    NetAward netAward = new NetAward();
    netAward.id        = id;
    netAward.titleName = titleName;
    netAward.username  = username;
    netAward.manifesto = manifesto;
    netAward.addedTime = (int)(System.currentTimeMillis()/1000);
    return netAward;
  }
}