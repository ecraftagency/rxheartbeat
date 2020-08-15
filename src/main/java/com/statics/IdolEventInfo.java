package com.statics;

import java.util.Map;

public class IdolEventInfo extends EventInfo{
  public static class IdolClaimInfo {
    public int idolId;
    public int requireItem;
    public int amount;
    public static IdolClaimInfo of(int idolId, int itemId, int amount) {
      IdolClaimInfo icp   = new IdolClaimInfo();
      icp.idolId          = idolId;
      icp.requireItem     = itemId;
      icp.amount          = amount;
      return icp;
    }
  }

  public Map<Integer, IdolClaimInfo> idolList;
}