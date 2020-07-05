package com.transport;

public class EffectResult {
  public int objId;
  public int propertyId;
  public long change;
  public static EffectResult of(int objId, int propertyId, long change) {
    EffectResult fr = new EffectResult();
    fr.objId = objId;
    fr.propertyId = propertyId;
    fr.change = change;
    return fr;
  }
}