package com.transport.model;

import com.statics.Common;

public class LDBObj implements Comparable<LDBObj>, Common.hasKey<Integer> {
  public int    id;
  public String displayName;
  public int    titleId;
  public long   score;
  public int    defaultCustom;

  public static LDBObj of (int id, String dn, int tId, long score, int defaultCustom) {
    LDBObj ldbObj         = new LDBObj();
    ldbObj.id             = id;
    ldbObj.displayName    = dn;
    ldbObj.titleId        = tId;
    ldbObj.score          = score;
    ldbObj.defaultCustom  = defaultCustom;
    return ldbObj;
  }

  @Override
  public Integer mapKey() {
    return id;
  }

  @Override
  public int compareTo(LDBObj o) {
    if (score > o.score)
      return 1;
    else if (score < o.score)
      return -1;
    return 0;
  }
}