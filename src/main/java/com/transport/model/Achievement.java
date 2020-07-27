package com.transport.model;

import java.util.List;
import java.util.Map;

public class Achievement {
  public Map<Integer, List<Long>> claimedAchievement;
  public Map<Integer, Long>       records;

  public void recordClaim(int achievementType, int milestone) {
    try {
      List<Long> subClaim = claimedAchievement.get(achievementType);
      if (subClaim == null)
        return;
      int idx = milestone/64;
      long shift = milestone%64;
      Long segment = subClaim.get(idx);
      segment |= (1L<<shift);
      subClaim.set(idx, segment);
    }
    catch (Exception e) {
      //
    }
  }

  public boolean checkClaim(int achievementType, int milestone) {
    try {
      List<Long> subClaim = claimedAchievement.get(achievementType);
      if (subClaim == null)
        return false;

      int idx = milestone/64;
      long shift = milestone%64;
      Long segment = subClaim.get(idx);
      long mask = 1L<<shift;
      return ((segment&mask) > 0);
    }
    catch (Exception e) {
      return false;
    }
  }
}