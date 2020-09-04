package com.transport.model;

public class NetAward {
  public String id;
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

  public static NetAward of(String id, String titleName, String username, String manifesto) {
    NetAward netAward = new NetAward();
    netAward.id        = id;
    netAward.titleName = titleName;
    netAward.username  = username;
    netAward.manifesto = manifesto;
    return netAward;
  }
}
