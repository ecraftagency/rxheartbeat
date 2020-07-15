package com.heartbeat.controller;

import com.heartbeat.model.GroupPool;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.heartbeat.model.data.UserGroup;
import com.transport.ExtMessage;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/*
EtxMessage.data.currentGroupState = [số dương -> có group, -1 -> bị kich hoạc tự out, 0 -> ko có group]
EtxMessage.data.group -> group data , null nếu group stage = -1 hoặc 0
tạo group param group type 0 là auto join, 1 là phải chờ duyệt, chưa support tính năng duyệt
 */
public class GroupController implements Handler<RoutingContext> {
  private static final Logger LOGGER = LoggerFactory.getLogger(GroupController.class);

  @Override
  public void handle(RoutingContext ctx) {
    try {
      String cmd        = ctx.getBodyAsJson().getString("cmd");
      String strUserId  = ctx.user().principal().getString("username");
      Session session   = SessionPool.getSessionFromPool(Integer.parseInt(strUserId));
      if (session != null) {
        ExtMessage resp;
        switch (cmd) {
          case "kickMember":
            resp = processKickMember(session, ctx);
            break;
          case "joinGroup":
            processJoinGroup(session, cmd, ctx);
            return;
          case "groupInfo":
            resp = processGroupInfo(session);
            break;
          case "createGroup":
            processCreateGroup(session, ctx, cmd);
            return;
          case "removeGroup":
            processRemoveGroup(session, ctx, cmd);
            return;
          default:
            resp = ExtMessage.group();
            resp.msg = "unknown_cmd";
            break;
        }
        resp.cmd = cmd;
        ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
        session.effectResults.clear();
      }
      else {
        ctx.response().setStatusCode(401).end();
      }
    }
    catch (Exception e) {
      LOGGER.error(e.getMessage());
      ctx.response().setStatusCode(404).end();
    }
  }

  private ExtMessage processKickMember(Session session, RoutingContext ctx) {
    ExtMessage resp   = ExtMessage.group();
    int memberId      = ctx.getBodyAsJson().getInteger("memberId");
    resp.msg          = session.kick(memberId);
    resp.data.group   = GroupPool.getGroupFromPool(session.groupID);
    resp.data.currentGroupState = session.groupID;
    return resp;
  }

  private void processJoinGroup(Session session, String cmd, RoutingContext ctx) {
    ExtMessage resp = ExtMessage.group();
    int groupId;
    try {
      groupId = ctx.getBodyAsJson().getInteger("groupId");
    }
    catch (Exception e) {
      ctx.response().setStatusCode(404).end();
      return;
    }
    session.joinGroup(groupId, joinAr -> {
      if (joinAr.succeeded()) {
        resp.msg = "ok";
        resp.cmd = cmd;
        resp.data.group = GroupPool.getGroupFromPool(session.groupID);
      }
      else {
        resp.msg = joinAr.cause().getMessage();
        resp.cmd = cmd;
      }
      resp.data.currentGroupState = session.groupID;
      ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
    });
  }

  private ExtMessage processGroupInfo(Session session) {
    ExtMessage resp   = ExtMessage.group();
    UserGroup group;
    if (session.groupID > 0 && (group = GroupPool.getGroupFromPool(session.groupID)) != null) {
      resp.data.group = group;
      resp.msg        = "ok";
    }
    else if (session.groupID == -1) {
      resp.msg        = "user_group_delay";
    }
    else {
      resp.msg        = "user_have_no_group";
    }
    resp.data.currentGroupState = session.groupID;
    return resp;
  }

  private void processRemoveGroup(Session session, RoutingContext ctx, String cmd) {
    ExtMessage resp = ExtMessage.group();
    session.removeGroup(crtRes -> {
      if (crtRes.succeeded()) {
        resp.msg = "ok";
      }
      else {
        resp.msg = crtRes.cause().getMessage();
      }
      resp.cmd = cmd;
      resp.data.currentGroupState = session.groupID;
      ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
    });
  }

  private void processCreateGroup(Session session, RoutingContext ctx, String cmd) {
    int groupType;
    try {
      groupType = ctx.getBodyAsJson().getInteger("groupType");
    }
    catch (Exception e) {
      ctx.response().setStatusCode(404).end();
      return;
    }

    ExtMessage resp = ExtMessage.group();
    if (groupType < 0 || groupType > 1) {
      resp.msg = "invalid_group_type";
      ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
      return;
    }
    session.createGroup(groupType, crtRes -> {
      if (crtRes.succeeded()) {
        resp.msg = "ok";
        resp.cmd = cmd;
        resp.data.group = GroupPool.getGroupFromPool(session.groupID);
      }
      else {
        resp.msg = crtRes.cause().getMessage();
        resp.cmd = cmd;
      }
      resp.data.currentGroupState = session.groupID;
      ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
    });
  }
}