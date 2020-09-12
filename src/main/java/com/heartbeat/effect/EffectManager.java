package com.heartbeat.effect;

import com.common.Constant;
import com.heartbeat.model.Session;
import com.heartbeat.model.data.UserIdol;
import com.transport.EffectResult;
import com.transport.model.Idols;

import java.util.List;

public class EffectManager implements EffectHandler{
  private static EffectManager instance = new EffectManager();
  private EffectHandler userEffectHandler;
  private EffectHandler idolEffectHandler;
  private EffectHandler dropEffectHandler;
  private EffectHandler inventoryEffectHandler;
  private EffectHandler titleEffectHandler;
  private EffectHandler flipEffectHandler;

  public static EffectManager inst() {
    return instance;
  }

  private EffectManager() {
    userEffectHandler         = UserEffectHandler.inst();
    idolEffectHandler         = IdolEffectHandlerV2.inst();
    inventoryEffectHandler    = InventoryEffectHandler.inst();
    dropEffectHandler         = new DropEffectHandler();
    titleEffectHandler        = new NetAwardEffectHandler();
    flipEffectHandler         = new PickEffectHandler();
  }

  @Override
  public String handleEffect(ExtArgs extArgs, Session session, List<Integer> effectFormat) {
    if (effectFormat == null || effectFormat.size() != 4)
      return EffectHandler.UNKNOWN_FORMAT_TYPE;
    int type = effectFormat.get(0);
    if (type >= 1 && type <= 20) { //user
      String res =  userEffectHandler.handleEffect(extArgs, session, effectFormat);
      userEffectPostProcessing(session);
      return res;
    }
    else if (type >= 21 && type <= 40) { //idol
      return idolEffectHandler.handleEffect(extArgs, session, effectFormat);
    }
    else if (type == 100) { //inventory
      return inventoryEffectHandler.handleEffect(extArgs, session, effectFormat);
    }
    else if (type == 102){
      return dropEffectHandler.handleEffect(extArgs, session, effectFormat);
    }
    else if (type == 103) {
      return titleEffectHandler.handleEffect(extArgs, session, effectFormat);
    }
    else if(type == 104) {
      int idolId = effectFormat.get(1);
      Idols.Idol idol = UserIdol.buildIdol(idolId);
      if (session.userIdol.addIdol(idol)) {
        session.effectResults.add(EffectResult.of(Constant.EFFECT_RESULT.IDOL_EFFECT_RESULT, idolId, 0));
        session.userAchievement.addAchieveRecord(Constant.ACHIEVEMENT.IDOL_ACHIEVEMENT, 1);
        return "ok";
      }
      return "effect_add_idol_fail";
    }
    else if(type == 105) {
      return flipEffectHandler.handleEffect(extArgs, session, effectFormat);
    }
    return EffectHandler.UNKNOWN_FORMAT_TYPE;
  }

  private void userEffectPostProcessing(Session session) {
    //correct data (due to reflection cant control @all)
    if (session.userGameInfo.currMedia > session.userGameInfo.maxMedia)
      session.userGameInfo.currMedia = session.userGameInfo.maxMedia;
  }
}
