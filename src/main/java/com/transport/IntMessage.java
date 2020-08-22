package com.transport;

import io.vertx.core.json.JsonObject;

public class IntMessage {
  public static JsonObject resp(String cmd) {
    JsonObject jo = new JsonObject();
    jo.put("cmd", cmd);
    jo.put("msg", "ok");
    return jo;
  }
}