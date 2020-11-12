package com.heartbeat.effect;

import com.common.Constant;
import com.common.Msg;
import com.heartbeat.model.Session;
import com.statics.PickData;
import com.transport.EffectResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PickEffectHandler implements EffectHandler{
  public PickEffectHandler() {
  }

  @Override
  public String handleEffect(ExtArgs extArgs, Session session, List<Integer> effectFormat) {
    int pickId   = effectFormat.get(PARAM1);
    int amount   = effectFormat.get(PARAM2);
    int chosen   = extArgs.intParam;

    PickData.PickDto dto = PickData.pickMap.get(pickId);
    if (dto == null || dto.items == null || dto.items.size() == 0)
      return Msg.map.getOrDefault(Msg.DTO_DATA_NOT_FOUND, "dto_data_not_found");

    if (!dto.items.contains(chosen))
      return Msg.map.getOrDefault(Msg.MALFORM_ARGS, "malform_args");

    session.userInventory.addItem(session, chosen, amount);
    session.effectResults.add(EffectResult.of(Constant.EFFECT_RESULT.ITEM_EFFECT_RESULT,chosen, amount));
    return "ok";
  }
}
