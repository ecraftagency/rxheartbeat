package com.transport.model;

import java.util.Map;

public class RollCall {
  public int      nClaimedDays;
  public int      lastDailyClaimTime;
  public boolean  todayClaim;

  public int      currentVipLevel;
  public Map<Integer, Long> vipClaimed;
}