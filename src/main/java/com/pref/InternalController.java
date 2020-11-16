package com.pref;

import com.common.LOG;
import com.couchbase.client.java.ReactiveBucket;
import com.heartbeat.db.cb.AbstractCruder;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class InternalController implements Handler<Message<JsonObject>> {
  private Map<Integer,AbstractCruder<Identity>> cbAccessMap;

  public InternalController(){
    cbAccessMap = new HashMap<>();
    for (Map.Entry<Integer, ReactiveBucket> entry : HBPref.refBuckets.entrySet())
      cbAccessMap.put(entry.getKey(), new AbstractCruder<>(Identity.class, entry.getValue()));
  }

  @Override
  public void handle(Message<JsonObject> ctx) {
    String  cmd       = "";

    try {
      JsonObject json = ctx.body();
      cmd             = json.getString("cmd");

      switch (cmd) {
        case "createProfile":
          processCreateProfile(ctx);
          return;
        case "getIdentity":
          processGetIdentity(ctx);
        default:break;
      }
    }
    catch (Exception e) {
      LOG.globalException("gateway", cmd, e);
    }
  }

  private void processGetIdentity(Message<JsonObject> ctx) {

  }

  private void processCreateProfile(Message<JsonObject> ctx) {

  }
}