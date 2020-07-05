package com.heartbeat.effect;

import com.heartbeat.model.Session;
import com.heartbeat.model.data.UserIdol;
import com.transport.EffectResult;
import com.transport.model.Idols;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class IdolEffectHandlerV2 implements EffectHandler{
  private static final Logger LOGGER = LoggerFactory.getLogger(IdolEffectHandlerV2.class);

  @FunctionalInterface
  private interface SubHandler {
    String handleEffect(Session session, Idols.Idol idol, Map<Integer, Field> updateFields, final List<Integer> effectFormat);
  }
  private static final int LINEAR_INCREASE            = 21;
  private static final int SCALE_INCREASE             = 22;
  private static final int BOUND_INCREASE             = 23;
  private static final int STEP_INCREASE              = 24;
  private static final int RATE_INCREASE              = 25;

  private static final int CREATIVITY_PROPERTY        = 2;
  private static final int PERFORMANCE_PROPERTY       = 3;
  private static final int ATTRACTIVE_PROPERTY        = 4;
  private static final int APTITUDE_EXP_PROPERTY      = 5;

  private static Map<Integer, String> format2Field;

  static {
    format2Field = new HashMap<>();
    format2Field.put(CREATIVITY_PROPERTY,   "crtItemBuf");
    format2Field.put(PERFORMANCE_PROPERTY,  "perfItemBuf");
    format2Field.put(ATTRACTIVE_PROPERTY,   "attrItemBuf");
    format2Field.put(APTITUDE_EXP_PROPERTY, "aptitudeExp");
  }

  private Field getUserIdolField(String fieldName) {
    try {
      return Idols.Idol.class.getField(fieldName);
    }
    catch (Exception e) {
      LOGGER.error("UserGameInfo could not found field " + fieldName);
      return null;
    }
  }

  private static IdolEffectHandlerV2 instance = new IdolEffectHandlerV2();
  private Map<Integer, SubHandler> subHandlers;

  private IdolEffectHandlerV2() {
    subHandlers = new HashMap<>();
    subHandlers.put(LINEAR_INCREASE, (session, idol, fields, eff) -> {
      int amount        = eff.get(EffectHandler.PARAM2);
      String result     = EffectHandler.UNKNOWN_PROPERTY;

      for (Map.Entry<Integer, Field> entry : fields.entrySet()) {
        if (entry.getValue() != null) {
          try {
            int oldVale = entry.getValue().getInt(idol);
            entry.getValue().setInt(idol, oldVale + amount);
            session.effectResults.add(EffectResult.of(idol.id,entry.getKey(), amount));
            result = EffectHandler.SUCCESS;
          } catch (IllegalAccessException e) {
            LOGGER.error(e.getMessage());
            result     = EffectHandler.UNKNOWN_PROPERTY;
          }
        }
      }
      return result;
    });

    subHandlers.put(SCALE_INCREASE, (session, idol, fields, eff) -> {
      int scale                 = eff.get(EffectHandler.PARAM2);
      String result             = EffectHandler.UNKNOWN_PROPERTY;

      for (Map.Entry<Integer, Field> entry : fields.entrySet()) {
        if (entry.getValue() != null) {
          try {
            int     oldVale       = entry.getValue().getInt(idol);
            float   scl           = scale/100f;
            int     newValue      = oldVale + (int)(oldVale*scl); //todo overflow
            entry.getValue().setInt(idol, newValue);
            session.effectResults.add(EffectResult.of(idol.id,entry.getKey(), (long)(oldVale*scl)));
            result = EffectHandler.SUCCESS;
          } catch (IllegalAccessException e) {
            LOGGER.error(e.getMessage());
            result     = EffectHandler.UNKNOWN_PROPERTY;
          }
        }
      }
      return result;
    });

    subHandlers.put(BOUND_INCREASE, (session, idol, fields, eff) -> {
      int lowBound      = eff.get(EffectHandler.PARAM2);
      int upBound       = eff.get(EffectHandler.PARAM3);
      String result     = EffectHandler.UNKNOWN_PROPERTY;

      for (Map.Entry<Integer, Field> entry : fields.entrySet()) {
        if (entry.getValue() != null) {
          try {
            int     oldVale         = entry.getValue().getInt(idol);
            int     rangeIncrement  = ThreadLocalRandom.current().nextInt(lowBound, upBound);
            entry.getValue().setInt(idol, oldVale + rangeIncrement);
            session.effectResults.add(EffectResult.of(idol.id,entry.getKey(), rangeIncrement));
            result = EffectHandler.SUCCESS;
          } catch (IllegalAccessException e) {
            LOGGER.error(e.getMessage());
            result     = EffectHandler.UNKNOWN_PROPERTY;
          }
        }
      }
      return result;
    });

    subHandlers.put(STEP_INCREASE, (session, idol, fields, eff) -> {
      int step          = eff.get(EffectHandler.PARAM2);
      int level         = idol.level;
      String result     = EffectHandler.UNKNOWN_PROPERTY;

      for (Map.Entry<Integer, Field> entry : fields.entrySet()) {
        if (entry.getValue() != null) {
          try {
            int     oldVale         = entry.getValue().getInt(idol);
            int     increment       = step*level;
            entry.getValue().setInt(idol, oldVale + increment);
            session.effectResults.add(EffectResult.of(idol.id,entry.getKey(), increment));
            result = EffectHandler.SUCCESS;
          } catch (IllegalAccessException e) {
            LOGGER.error(e.getMessage());
            result     = EffectHandler.UNKNOWN_PROPERTY;
          }
        }
      }
      return result;
    });

    subHandlers.put(RATE_INCREASE, (session, idol, fields, eff) -> {
      int amount        = eff.get(EffectHandler.PARAM2);
      int rate          = eff.get(EffectHandler.PARAM3);
      String result     = EffectHandler.UNKNOWN_PROPERTY;

      for (Map.Entry<Integer, Field> entry : fields.entrySet()) {
        if (entry.getValue() != null) {
          try {
            int     oldVale         = entry.getValue().getInt(idol);
            float   _rate           = rate/100f;
            float   rand            = ThreadLocalRandom.current().nextFloat();
            if (_rate > rand) {
              entry.getValue().setInt(idol, oldVale + amount);
              session.effectResults.add(EffectResult.of(idol.id,entry.getKey(), amount));
              result = EffectHandler.SUCCESS;
            }
          } catch (IllegalAccessException e) {
            LOGGER.error(e.getMessage());
            result     = EffectHandler.UNKNOWN_PROPERTY;
          }
        }
      }
      return result;
    });

  }

  public static IdolEffectHandlerV2 inst() {
    return instance;
  }

  @Override
  public String handleEffect(ExtArgs extArgs, Session session, final List<Integer> effectFormat) {
    int effectType = effectFormat.get(PARAM0);
    Idols.Idol idol = session.userIdol.idolMap.get(extArgs.idolId);

    if (effectType  == 27 || effectType == 26) { //filter on idol first, this mean random idol
      int nIdol         = session.userIdol.idolMap.size();
      int item          = new Random().nextInt(nIdol);
      int i             = 0;
      int selectedIdol  = 1; //default for safe
      for (Integer key : session.userIdol.idolMap.keySet()) {
        if (i == item)
          selectedIdol = key;
        i++;
      }
      idol = session.userIdol.idolMap.get(selectedIdol);

      effectType = effectType == 27 ? 21 : 23; //shift effect type
    }

    SubHandler subHandler = subHandlers.get(effectType);

    if (idol == null)
      return UNKNOWN_IDOL;
    if (subHandler == null)
      return UNKNOWN_FORMAT_TYPE;

    int propertyId = effectFormat.get(PARAM1);
    Map<Integer, Field> updateFields  = new HashMap<>();

    if (propertyId == WILDCARD_PROPERTY && effectType >= 21 && effectType <= 25) {
      //wildcard filter, type 21 -> 25 have wildcard option
      updateFields.put(CREATIVITY_PROPERTY, getUserIdolField(format2Field.get(CREATIVITY_PROPERTY)));
      updateFields.put(PERFORMANCE_PROPERTY, getUserIdolField(format2Field.get(PERFORMANCE_PROPERTY)));
      updateFields.put(ATTRACTIVE_PROPERTY, getUserIdolField(format2Field.get(ATTRACTIVE_PROPERTY)));
    }
    else if (propertyId == RANDOM_PROPERTY && effectType >= 21 && effectType <= 25) {
      //random filter, type 21 -> 25 have random option
      //2 creativity, 3 performance, 4 attractive
      int randProp = ThreadLocalRandom.current().nextInt(2,5);
      //effectFormat.set(PARAM1, randProp);
      updateFields.put(randProp, getUserIdolField(format2Field.get(randProp)));
    }
    else {
      updateFields.put(propertyId, getUserIdolField(format2Field.get(propertyId)));
    }


    String result =  subHandler.handleEffect(session, idol, updateFields, effectFormat);

    if (result.equals(EffectHandler.SUCCESS)) {
      UserIdol.onPropertiesChange(idol, UserIdol.HALO_UP_EVT);
    }

    return result;
  }
}
