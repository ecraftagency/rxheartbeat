package com.tulinh.controller;

import io.vertx.ext.web.RoutingContext;

public class WheelItem extends BenchWheelItem {
  @Override
  protected String getUserId(RoutingContext ctx) {
    return ctx.request().getParam("megaID");
  }
}