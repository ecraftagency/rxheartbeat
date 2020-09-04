package com.transport.model;

public class Title {
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

  public static Title of(String id, String titleName, String username, String manifesto) {
    Title title = new Title();
    title.id        = id;
    title.titleName = titleName;
    title.username  = username;
    title.manifesto = manifesto;
    return title;
  }
}
