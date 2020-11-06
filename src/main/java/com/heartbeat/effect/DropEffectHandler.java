package com.heartbeat.effect;

import com.heartbeat.model.Session;
import com.statics.DropData;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DropEffectHandler implements EffectHandler{
  @Override
  public String handleEffect(ExtArgs extArgs, Session session, final List<Integer> effectFormat) {
    int dropId = effectFormat.get(PARAM1);
    DropData.Drop drop = DropData.dropMap.get(dropId);

    if (drop == null)
      return "invalid_drop_id";
    if (drop.type == 1) { //pick 1 of them
      int rand = ThreadLocalRandom.current().nextInt(1, drop.totalRate + 1);
      DropData.Pack selectedPack = null;
      int acc = 0;
      for (DropData.Pack pack : drop.rewardPacks) {
        acc += pack.rate;
        if (acc >= rand) {
          selectedPack = pack;
          break;
        }
      }
      if (selectedPack == null)
        return EffectHandler.UNKNOWN_DROP_PACK;
      return EffectManager.inst().handleEffect(extArgs, session, selectedPack.format);
    }
    else if (drop.type == 2) {
      for (DropData.Pack pack : drop.rewardPacks) {
        int rand = ThreadLocalRandom.current().nextInt(1, 101);
        if (rand <= pack.rate)
          EffectManager.inst().handleEffect(extArgs, session, pack.format);
      }
      return "ok";
    }
    return "invalid_drop";
  }
}
