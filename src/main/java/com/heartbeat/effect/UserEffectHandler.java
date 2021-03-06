package com.heartbeat.effect;

import com.common.Constant;
import com.common.LOG;
import com.heartbeat.model.GroupPool;
import com.heartbeat.model.Session;
import com.heartbeat.model.data.UserGameInfo;
import com.heartbeat.model.data.UserGroup;
import com.statics.HeadData;
import com.transport.EffectResult;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class UserEffectHandler implements EffectHandler{
  private static final int LINEAR_INCREASE                = 1;
  private static final int PRODUCTION_RATE_INCREASE_1000  = 2;
  private static final int BOUND_INCREASE                 = 3;
  private static final int STEP_INCREASE                  = 4;
  private static final int RATE_INCREASE                  = 5;
  private static final int PRODUCTION_RATE_INCREASE_100   = 6;
  private static final int PRODUCTION_STEP_INCREASE       = 7;
  private static final int ID_CHANGE                      = 8;
  private static final int SPECIAL_AVATAR_CHANGE          = 9;
  private static final int LINEAR_EXT_INCREASE            = 10; // x = ax + b
  private static final int EXPONENT_INCREASE              = 11; // x = ax + b
  private static final int PRODUCTION_INCREASE            = 12;

  private static final int MONEY_PROPERTY       = 1;
  private static final int VIEW_PROPERTY        = 2;
  private static final int FAN_PROPERTY         = 3;
  private static final int CURR_MEDIA_PROPERTY  = 4;
  private static final int TIME_PROPERTY        = 5;
  private static final int EXP_PROPERTY         = 6;
  private static final int CRAZY_PROPERTY       = 7;
  private static final int NET_POINT_PROPERTY   = 8;

  private static final int AVATAR               = 2;
  private static final int DISPLAY_NAME         = 1;

  private static Map<Integer, String> format2Field;

  static {
    format2Field = new HashMap<>();
    format2Field.put(MONEY_PROPERTY,            "money");
    format2Field.put(VIEW_PROPERTY,             "view");
    format2Field.put(FAN_PROPERTY,              "fan");
    format2Field.put(CURR_MEDIA_PROPERTY,       "currMedia");
    format2Field.put(TIME_PROPERTY,             "time");
    format2Field.put(EXP_PROPERTY,              "exp");
    format2Field.put(CRAZY_PROPERTY,            "crazyDegree");
    format2Field.put(NET_POINT_PROPERTY,        "netPoint");
  }

  private static UserEffectHandler instance = new UserEffectHandler();
  private Map<Integer, EffectHandler> subHandlers;


  private Field getUserInfoField(String fieldName) {
    try {
      return UserGameInfo.class.getField(fieldName);
    }
    catch (Exception e) {
      LOG.globalException("node", "userEffectHandler", e);
      return null;
    }
  }

  private UserEffectHandler() {
    subHandlers = new HashMap<>();

    subHandlers.put(LINEAR_INCREASE, (extArgs , session, eff) -> {
      int propertyId    = eff.get(EffectHandler.PARAM1);
      int amount        = eff.get(EffectHandler.PARAM2);
      String result     = EffectHandler.UNKNOWN_PROPERTY;
      Field field       = getUserInfoField(format2Field.get(propertyId));

      if (field != null) {
        try {
          long oldValue = field.getLong(session.userGameInfo);
          long newValue = oldValue + amount;
          if (newValue < 0)
            newValue = 0;
          field.setLong(session.userGameInfo, newValue);
          session.effectResults.add(EffectResult.of(0,propertyId, amount));
          result = EffectHandler.SUCCESS;

        } catch (IllegalAccessException e) {
          LOG.globalException("node", "userEffectHandler:linearIncrease", e);
          result     = EffectHandler.UNKNOWN_PROPERTY;
        }
      }

      return result;
    });

    subHandlers.put(PRODUCTION_RATE_INCREASE_1000, (extArgs, session, eff) -> {
      int propertyId    = eff.get(EffectHandler.PARAM1);
      int rate          = eff.get(EffectHandler.PARAM2);
      float percent     = rate/1000.0f;

      switch (propertyId) {
        case MONEY_PROPERTY:
          long additionalMoney = (int)(session.userIdol.totalCrt()*percent);
          //session.userGameInfo.money += additionalMoney;
          session.userGameInfo.incrMoney(additionalMoney);
          session.effectResults.add(EffectResult.of(0,propertyId, additionalMoney));
          return EffectHandler.SUCCESS;

        case VIEW_PROPERTY:
          long additionalView = (int)(session.userIdol.totalPerf()*percent);
          //session.userGameInfo.view += additionalView;
          session.userGameInfo.incrView(additionalView);
          session.effectResults.add(EffectResult.of(0,propertyId, additionalView));
          return EffectHandler.SUCCESS;

        case FAN_PROPERTY:
          long additionalFan = (int)(session.userIdol.totalAttr()*percent);
          //session.userGameInfo.fan += additionalFan;
          session.userGameInfo.incrFan(additionalFan);
          session.effectResults.add(EffectResult.of(0,propertyId, additionalFan));
          return EffectHandler.SUCCESS;

        default:
          return EffectHandler.UNKNOWN_PROPERTY;
      }
    });

    subHandlers.put(BOUND_INCREASE, (extArgs, session, eff) -> {
      int propertyId    = eff.get(EffectHandler.PARAM1);
      int lowBound      = eff.get(EffectHandler.PARAM2);
      int upBound       = eff.get(EffectHandler.PARAM3);

      String result     = EffectHandler.UNKNOWN_PROPERTY;
      Field field       = getUserInfoField(format2Field.get(propertyId));

      if (field != null) {
        try {
          long    oldVale         = field.getLong(session.userGameInfo);
          long    rangeIncrement  = ThreadLocalRandom.current().nextLong(lowBound, upBound);
          long    newValue        = oldVale + rangeIncrement;
          if (newValue < 0)
            newValue = 0;
          field.setLong(session.userGameInfo, newValue);
          session.effectResults.add(EffectResult.of(0,propertyId, rangeIncrement));

          result = EffectHandler.SUCCESS;
        } catch (IllegalAccessException e) {
          LOG.globalException("node", "userEffectHandler:boundIncrease", e);
          result     = EffectHandler.UNKNOWN_PROPERTY;
        }
      }
      return result;
    });

    subHandlers.put(STEP_INCREASE, (extArgs, session, eff) -> {
      int propertyId    = eff.get(EffectHandler.PARAM1);
      int step          = eff.get(EffectHandler.PARAM2);
      int level         = session.userGameInfo.titleId;
      String result     = EffectHandler.UNKNOWN_PROPERTY;
      Field field       = getUserInfoField(format2Field.get(propertyId));

      if (field != null) {
        try {
          long    oldVale         = field.getLong(session.userGameInfo);
          long    increment       = step*level;
          long    newValue        = increment + oldVale;
          if (newValue < 0)
            newValue = 0;
          field.setLong(session.userGameInfo, newValue);
          session.effectResults.add(EffectResult.of(0,propertyId, increment));
          result = EffectHandler.SUCCESS;

        } catch (Exception e) {
          LOG.globalException("node", "userEffectHandler:stepIncrease",e);
          result     = EffectHandler.UNKNOWN_PROPERTY;
        }
      }
      return result;
    });

    subHandlers.put(RATE_INCREASE, (extArgs, session, eff) -> {
      int propertyId    = eff.get(EffectHandler.PARAM1);
      int amount        = eff.get(EffectHandler.PARAM2);
      int rate          = eff.get(EffectHandler.PARAM3);
      String result     = EffectHandler.UNKNOWN_PROPERTY;
      Field field       = getUserInfoField(format2Field.get(propertyId));

      if (field != null) {
        try {
          long    oldVale         = field.getLong(session.userGameInfo);
          float   _rate           = rate/100f;
          float   rand            = ThreadLocalRandom.current().nextFloat();
          if (_rate > rand) {
            long newValue         = oldVale + amount;
            if (newValue < 0)
              newValue = 0;
            field.setLong(session.userGameInfo, newValue);
            session.effectResults.add(EffectResult.of(0,propertyId, amount));
            result = EffectHandler.SUCCESS;
          }

        } catch (IllegalAccessException e) {
          LOG.globalException("node", "userEffectHandler:rateIncrease", e);
          result     = EffectHandler.UNKNOWN_PROPERTY;
        }
      }
      return result;
    });

    subHandlers.put(PRODUCTION_RATE_INCREASE_100, (objId, session, eff) -> {
      int propertyId    = eff.get(EffectHandler.PARAM1);
      int rate          = eff.get(EffectHandler.PARAM2);
      float percent     = rate/100.0f;

      switch (propertyId) {
        case MONEY_PROPERTY:
          long additionalMoney = (int)(session.userIdol.totalCrt()*percent);
          //session.userGameInfo.money += additionalMoney;
          session.userGameInfo.incrMoney(additionalMoney);
          session.effectResults.add(EffectResult.of(0,propertyId, additionalMoney));
          return EffectHandler.SUCCESS;

        case VIEW_PROPERTY:
          long additionalView = (int)(session.userIdol.totalPerf()*percent);
          //session.userGameInfo.view += additionalView;
          session.userGameInfo.incrView(additionalView);
          session.effectResults.add(EffectResult.of(0,propertyId, additionalView));
          return EffectHandler.SUCCESS;

        case FAN_PROPERTY:
          long additionalFan = (int)(session.userIdol.totalAttr()*percent);
          //session.userGameInfo.fan += additionalFan;
          session.userGameInfo.incrFan(additionalFan);
          session.effectResults.add(EffectResult.of(0,propertyId, additionalFan));
          return EffectHandler.SUCCESS;
        default:
          return EffectHandler.UNKNOWN_PROPERTY;
      }
    });

    subHandlers.put(PRODUCTION_STEP_INCREASE, (objId, session, eff) -> {
      int propertyId    = eff.get(EffectHandler.PARAM1);
      int step          = eff.get(EffectHandler.PARAM2);
      int level         = session.userGameInfo.titleId;

      switch (propertyId) {
        case MONEY_PROPERTY:
          long additionalMoney = (session.userIdol.totalCrt()*level/step);
          //session.userGameInfo.money += additionalMoney;
          session.userGameInfo.incrMoney(additionalMoney);
          session.effectResults.add(EffectResult.of(0,propertyId, (int)additionalMoney));
          return EffectHandler.SUCCESS;

        case VIEW_PROPERTY:
          long additionalView = (session.userIdol.totalPerf()*level/step);
          //session.userGameInfo.view += additionalView;
          session.userGameInfo.incrView(additionalView);
          session.effectResults.add(EffectResult.of(0,propertyId, (int)additionalView));
          return EffectHandler.SUCCESS;

        case FAN_PROPERTY:
          long additionalFan = (session.userIdol.totalAttr()*level/step);
          //session.userGameInfo.fan += additionalFan;
          session.userGameInfo.incrFan(additionalFan);
          session.effectResults.add(EffectResult.of(0,propertyId, (int)additionalFan));
          return EffectHandler.SUCCESS;

        default:
          return EffectHandler.UNKNOWN_PROPERTY;
      }
    });

    subHandlers.put(ID_CHANGE, (extAgrs, session, eff) -> {
      int propertyId    = eff.get(EffectHandler.PARAM1);

      switch (propertyId) {
        case AVATAR:
          int newAvatar = extAgrs.intParam %10;
          int newGender;
          try {
            newGender    = Integer.parseInt(extAgrs.strParam);
          }
          catch (Exception e) {
            newGender = 0;
            LOG.globalException("node", "UserEffectHandler:changeAvatar", String.format("fail to parse gender session: %s", session.id));
          }

          newAvatar = Math.max(newAvatar, 0);
          newGender    = newGender >= 0 && newGender <= 1 ? newGender : 0;
          session.userGameInfo.avatar = newAvatar;
          session.userGameInfo.gender = newGender;
          return EffectHandler.SUCCESS;

        case DISPLAY_NAME:
          return session.userGameInfo.replaceDisplayName(session, extAgrs.strParam);

        default:
          return EffectHandler.UNKNOWN_PROPERTY;
      }
    });

    subHandlers.put(SPECIAL_AVATAR_CHANGE, (extArgs, session, eff) -> {
      int newAvatarId   = eff.get(PARAM1);
      int updateGender  = extArgs.intParam;
      int maxAvail      = HeadData.headMap.size();

      if (updateGender < 0 || updateGender > 1)
        updateGender = 0;

      if (newAvatarId < 1 || newAvatarId > maxAvail)
        return EffectHandler.UNKNOWN_PROPERTY;
      session.userGameInfo.avatar = newAvatarId;
      session.userGameInfo.gender = updateGender;
      return EffectHandler.SUCCESS;
    });

    subHandlers.put(LINEAR_EXT_INCREASE, (extArgs, session, eff) -> {
      int propertyId    = eff.get(EffectHandler.PARAM1);
      int scl_percent   = eff.get(EffectHandler.PARAM2);
      int inr_amount    = eff.get(EffectManager.PARAM3);
      float percent     = scl_percent/100f;

      switch (propertyId) {
        case MONEY_PROPERTY:
          long additionalMoney = (long)(session.userIdol.totalCrt()*percent) + inr_amount;
          //session.userGameInfo.money += additionalMoney;
          session.userGameInfo.incrMoney(additionalMoney);
          session.effectResults.add(EffectResult.of(0,propertyId, (int)additionalMoney));
          return EffectHandler.SUCCESS;

        case VIEW_PROPERTY:
          long additionalView = (long)(session.userIdol.totalPerf()*percent) + inr_amount;
          //session.userGameInfo.view += additionalView;
          session.userGameInfo.incrView(additionalView);
          session.effectResults.add(EffectResult.of(0,propertyId, (int)additionalView));
          return EffectHandler.SUCCESS;

        case FAN_PROPERTY:
          long additionalFan = (long)(session.userIdol.totalAttr()*percent) + inr_amount;
          //session.userGameInfo.fan += additionalFan;
          session.userGameInfo.incrFan(additionalFan);
          session.effectResults.add(EffectResult.of(0,propertyId, (int)additionalFan));
          return EffectHandler.SUCCESS;

        default:
          return EffectHandler.UNKNOWN_PROPERTY;
      }
    });

    subHandlers.put(EXPONENT_INCREASE, (extArgs, session, eff) -> {
      int propertyId    = eff.get(EffectHandler.PARAM1);
      int coefficient   = eff.get(EffectManager.PARAM3);
      int level         = session.userGameInfo.titleId;
      long totalCrt     = session.userIdol.totalCrt();

      switch (propertyId) {
        case MONEY_PROPERTY:
          long additionalMoney = (long)(1000*(level+1)*(level+1)) + coefficient*totalCrt;
          //session.userGameInfo.money += additionalMoney;
          session.userGameInfo.incrMoney(additionalMoney);
          session.userGameInfo.incrMoney(additionalMoney);
          session.effectResults.add(EffectResult.of(0,propertyId, (int)additionalMoney));
          return EffectHandler.SUCCESS;

        case VIEW_PROPERTY:
          long additionalView = (long)(1000*(level+1)*(level+1)) + coefficient*totalCrt;
          //session.userGameInfo.view += additionalView;
          session.userGameInfo.incrView(additionalView);
          session.effectResults.add(EffectResult.of(0,propertyId, (int)additionalView));
          return EffectHandler.SUCCESS;

        case FAN_PROPERTY:
          long additionalFan = (long)(1000*(level+1)*(level+1)) + coefficient*totalCrt;
          //session.userGameInfo.fan += additionalFan;
          session.userGameInfo.incrFan(additionalFan);
          session.effectResults.add(EffectResult.of(0,propertyId, (int)additionalFan));
          return EffectHandler.SUCCESS;
        default:
          return EffectHandler.UNKNOWN_PROPERTY;
      }
    });

    subHandlers.put(PRODUCTION_INCREASE, ((extArgs, session, eff) -> {
      int productionType  = eff.get(EffectHandler.PARAM1);
      int amount          = eff.get(EffectHandler.PARAM2);
      boolean success     = session.userProduction.addProduction(session, productionType, amount);
      if (success) {
        session.effectResults.add(EffectResult.of(4000,productionType, amount));
        return EffectHandler.SUCCESS;
      }
      else {
        return EffectHandler.UNKNOWN_PROPERTY;
      }
    }));
  }

  public static UserEffectHandler inst() {
    return instance;
  }

  @Override
  public String handleEffect(ExtArgs extArgs, Session session, final List<Integer> effectFormat) {
    EffectHandler subHandler = subHandlers.get(effectFormat.get(0));
    if (subHandler != null) {
      String res = subHandler.handleEffect(extArgs, session, effectFormat);
      int propID = effectFormat.get(EffectHandler.PARAM1);
      if (res.equals("ok") && propID == TIME_PROPERTY) {
        session.userGameInfo.timeChange = true;
      }
      if (res.equals("ok") && propID == CRAZY_PROPERTY) {
        UserGroup group = GroupPool.getGroupFromPool(session.groupID);
        if (group != null && session.userGameInfo.crazyDegree >= 100)
          group.addRecord(session, Constant.GROUP_EVENT.GE_CRZ_DEGREE_EVT_ID, (int)(session.userGameInfo.crazyDegree), false);
      }
      return res;
    }
    else {
      return EffectHandler.UNKNOWN_FORMAT_TYPE;
    }
  }
}