package com.heartbeat.effect;

import com.heartbeat.model.Session;

import java.util.List;

public class EffectManager implements EffectHandler{
  private static EffectManager instance = new EffectManager();
  private EffectHandler userEffectHandler;
  private EffectHandler idolEffectHandler;
  private EffectHandler dropEffectHandler;
  private EffectHandler inventoryEffectHandler;

  public static EffectManager inst() {
    return instance;
  }

  private EffectManager() {
    userEffectHandler         = UserEffectHandler.inst();
    idolEffectHandler         = IdolEffectHandlerV2.inst();
    inventoryEffectHandler    = InventoryEffectHandler.inst();
    dropEffectHandler         = new DropEffectHandler();
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
    return EffectHandler.UNKNOWN_FORMAT_TYPE;
  }

  private void userEffectPostProcessing(Session session) {
    //correct data (due to reflection cant control @all)
    if (session.userGameInfo.currMedia > session.userGameInfo.maxMedia)
      session.userGameInfo.currMedia = session.userGameInfo.maxMedia;
  }
}
