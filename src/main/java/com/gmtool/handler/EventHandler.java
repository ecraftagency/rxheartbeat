package com.gmtool.handler;

import com.common.Constant;
import com.gmtool.NodeCache;
import com.gmtool.model.Event;
import com.heartbeat.event.ExtEventInfo;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.text.SimpleDateFormat;
import java.util.*;
import static com.gmtool.GMTool.templateEngine;

public class EventHandler implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext ctx) {
    ctx.put("nodes", NodeCache.inst().getNodes());
    ctx.put("userEvent", getUserEvents());

    templateEngine.render(ctx.data(), "webroot/html/navbar.ftl", nar -> {
      if (nar.succeeded()) {
        templateEngine.render(ctx.data(), "webroot/html/event.ftl", rar -> {
          if (rar.succeeded()) {
            ctx.response().putHeader("Content-Type", "text/html");
            ctx.response().putHeader("Cache-Control", "no-store");
            ctx.response().end(rar.result());
          } else {
            ctx.fail(rar.cause());
          }
        });
      }
      else {
        ctx.fail(nar.cause());
      }
    });
  }

  private static List<Event> getUserEvents() {
    List<Event> userEvent = new ArrayList<>();
    SimpleDateFormat sdf  = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);
    sdf.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

    for (ExtEventInfo ei : Constant.USER_EVENT.evtMap.values()){
        String  name        = Event.id2Name.getOrDefault(ei.eventId, "");
        String  strStart;
        String  strEnd;

        try {
          Date start  = new Date(System.currentTimeMillis());
          strStart    = sdf.format(start);
          Date end    = new Date(ei.endTime*1000);
          strEnd      = sdf.format(end);
        }
        catch (Exception e) {
          strEnd    = "";
          strStart  = "";
        }
        String  active    = ei.active ? "Active" : "InActive";
        Event event       = Event.of(ei.eventId, name, strStart, strEnd, active);
        userEvent.add(event);
    }
    return userEvent;
  }
}
