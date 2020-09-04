package com.transport.model;

import java.util.Map;

public class RollCall {
  public static class GiftInfo {
    public int      giftType;
    public int      boughtTime;
    public int      remainDay;
    public int      lastClaimTime;
    public boolean  todayClaim;
  }

  public int                nClaimedDays;
  public int                lastDailyClaimTime;
  public boolean            todayClaim;

  public int                currentVipLevel;
  public Map<Integer, Long> vipClaimed;

  public Map<Integer, GiftInfo> giftCards; //key:giftType

  public boolean            isPaidUser; //this mean this user already have at lease 1 success payment
}