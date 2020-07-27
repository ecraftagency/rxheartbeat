package com.transport.model;

import java.util.List;
import java.util.Map;

public class Achievement {
  public List<Long>             claimedAchievement;
  public Map<Integer, Integer>  records;

  public void recordClaim(int milestone) {
    try {
      int idx = milestone/64;
      int shift = milestone%64;
      Long segment = claimedAchievement.get(idx);
      segment |= (1<<shift);
      claimedAchievement.set(idx, segment);
    }
    catch (Exception e) {
      //
    }
  }

  public boolean checkClaim(int milestone) {
    try {
      int idx = milestone/64;
      int shift = milestone%64;
      Long segment = claimedAchievement.get(idx);
      long mask = 1<<shift;
      return ((segment&mask) > 0);
    }
    catch (Exception e) {
      return false;
    }
  }
}