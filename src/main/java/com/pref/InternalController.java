package com.pref;

import com.common.LOG;
import com.heartbeat.db.cb.AbstractCruder;
import com.transport.Identity;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.util.Map;

public class InternalController implements Handler<Message<JsonObject>> {
  private Map<Integer,AbstractCruder<Identity>> cbAccessMap;
  private CBPrefImpl prefService;
  public InternalController(){
    prefService = new CBPrefImpl(HBPref.refBuckets);
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
          return;
        case "linkAccount":
          processLinkAccount(ctx);
          return;
        case "claimLinkReward":
          processClaimLinkReward(ctx);
          return;
        default:
          JsonObject resp = new JsonObject();
          resp.put("msg", "unknown_command");
          ctx.reply(resp);
          break;
      }
    }
    catch (Exception e) {
      JsonObject resp = new JsonObject();
      resp.put("msg", e.getMessage());
      ctx.reply(resp);
      LOG.globalException("gateway", cmd, e);
    }
  }

  private void processClaimLinkReward(Message<JsonObject> ctx) {
    String phoenix = ctx.body().getString("phoenixId");
    prefService.claimLinkReward(phoenix, ar -> {
      JsonObject resp = new JsonObject();
      if (ar.succeeded())
        resp.put("msg", ar.result());
      else
        resp.put("msg", ar.cause().getMessage());
      ctx.reply(resp);
    });
  }

  private void processLinkAccount(Message<JsonObject> ctx) {
    String phoenixId  = ctx.body().getString("phoenixId");
    String upLinkId   = ctx.body().getString("upLinkId");
    prefService.linkIdentity(phoenixId, upLinkId, ar -> {
      JsonObject resp = new JsonObject();
      if (ar.succeeded())
        resp.put("msg", ar.result());
      else
        resp.put("msg", ar.cause().getMessage());
      ctx.reply(resp);
    });
  }

  private void processGetIdentity(Message<JsonObject> ctx) {
    String phoenixId  = ctx.body().getString("phoenixId");
    prefService.loadIdentity(phoenixId, ar -> {
      JsonObject resp = new JsonObject();
      if (ar.succeeded()) {
        resp.put("msg", "ok");
        resp.put("data", Json.encode(ar.result()));
      }
      else
        resp.put("msg", ar.cause().getMessage());
      ctx.reply(resp);
    });
  }

  private void processCreateProfile(Message<JsonObject> ctx) {
    String  phoenixId = ctx.body().getString("phoenixId");
    long    profileID = ctx.body().getLong("profileId");
    prefService.addProfile(phoenixId, profileID, ar -> {
      JsonObject resp = new JsonObject();
      if (ar.succeeded()) {
        resp.put("msg", "ok");
        resp.put("data", Json.encode(ar.result()));
      }
      else
        resp.put("msg", ar.cause().getMessage());
      ctx.reply(resp);
    });
  }
}