package com.heartbeat.effect;

import com.heartbeat.model.Session;

import java.util.List;

@FunctionalInterface
public interface EffectHandler {
  String SUCCESS = "ok";
  String UNKNOWN_FORMAT_TYPE    = "unknown_format_type";
  String UNKNOWN_PROPERTY       = "unknown_property";
  String UNKNOWN_IDOL           = "unknown_idol";
  String UNKNOWN_DROP_PACK      = "unknown_drop_pack";
  
  int    WILDCARD_PROPERTY      = 101;
  int    RANDOM_PROPERTY        = 100;

  int    PARAM0  = 0;
  int    PARAM1  = 1;
  int    PARAM2  = 2;
  int    PARAM3  = 3;

  class ExtArgs {
    int     objId;
    int     intParam;
    String  strParam;

    public static ExtArgs of() {
      return def;
    }
    public static ExtArgs ofDefault(int objId, int intParam, String strParam) {
      ExtArgs r   = new ExtArgs();
      r.objId     = objId;
      r.intParam  = intParam;
      r.strParam  = strParam;
      return r;
    }
  }

  ExtArgs def = new ExtArgs();

  String handleEffect(ExtArgs extArgs, Session session, final List<Integer> effectFormat);
}