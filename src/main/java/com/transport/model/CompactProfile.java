package com.transport.model;

import com.statics.FightData;
import java.util.List;

public class CompactProfile {
  public String   groupName;
  public String   displayName;
  public int      userId;
  public int      titleId;
  public long     totalCrt;
  public long     totalPerf;
  public long     totalAttr;
  public int      vipExp;
  public int      avatar;
  public int      gender;
  public long     exp;
  public FightData.FightLV curFightLV;
  public List<Integer> awards;
  public int      defaultCustom;
}