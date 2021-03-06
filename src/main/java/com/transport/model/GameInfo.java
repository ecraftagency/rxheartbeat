package com.transport.model;

import java.util.Map;

public class GameInfo {   //todo almost field is use combine reflection, so don't refactor without double check!!!!
  public int      gender          = -1;
  public int      avatar          = -1;
  public String   displayName     = "";

  public long     money           = 2000;
  public long     view            = 2000;
  public long     fan             = 2000;
  public long     talent          = 0;
  public long     time            = 0;
  public long     exp             = 0;
  public long     crazyDegree     = 0;

  public int      titleId         = 1;
  public int      vipExp          = 0;

  //media data
  public long     currMedia       = 3;
  public long     maxMedia        = 3;
  public int      lastMediaClaim  = 0;
  public int      nextQuestion    = 1;
  public boolean  timeChange      = false;
  public long     totalCrt        = 0;
  public long     totalPerf       = 0;
  public long     totalAttr       = 0;

  //shopping data
  public Map<Integer, Integer> shopping;
  //mission data
  public Map<Integer, Boolean> crazyRewardClaim;

  public long     netPoint        = 0;
  public int      defaultCustom   = 0;
  public int      tutorStep       = 0;
}