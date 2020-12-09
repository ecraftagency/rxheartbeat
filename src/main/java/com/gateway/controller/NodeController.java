package com.gateway.controller;

import com.common.Constant;
import com.transport.GatewayMsg;
import com.gateway.NodePool;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class NodeController implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext ctx) {
    GatewayMsg resp       = new GatewayMsg();
    resp.appStore         = Constant.GAME_INFO.APPLE_STORE_APP_LINK;
    resp.ggPlay           = Constant.GAME_INFO.CH_PLAY_APP_LINK;
    resp.minClientVersion = Constant.GAME_INFO.MIN_AVAILABLE_VERSION;
    resp.serverVersion    = Constant.GAME_INFO.SERVER_VERSION;
    resp.availableNodes   = NodePool.getNodes();
    resp.adminEmail       = Constant.GAME_INFO.ADMIN_MAIL;
    resp.refCodeLink      = Constant.GAME_INFO.REF_CODE_SHARE_LINK;
    resp.fanPage          = Constant.GAME_INFO.FAN_PAGE;
    ctx.response().setStatusCode(200).end(Json.encode(resp));
  }
}