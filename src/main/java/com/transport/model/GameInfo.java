package com.transport.model;

public class GameInfo {   //todo almost field is use combine reflection, so don't refactor without double check!!!!
  public int      gender          = -1;
  public int      avatar          = -1;
  public String   displayName     = "";
  public long     money           = 2000;
  public long     view            = 2000;
  public long     fan             = 2000;
  public long     talent          = 0;
  public int      time            = 0;
  public int      titleId         = 1;
  public long     exp             = 0;
  public int      vipExp          = 0;

  //media data
  public int      currMedia       = 3;
  public int      maxMedia        = 3;
  public int      lastMediaClaim  = 0;
  public int      nextQuestion    = 1;

  //mission data
  public int      crazyDegree     = 0;
}