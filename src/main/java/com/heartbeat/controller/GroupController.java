package com.heartbeat.controller;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.ReactiveQueryResult;
import com.heartbeat.HBServer;
import com.heartbeat.common.Constant;
import com.heartbeat.common.GlobalVariable;
import com.heartbeat.common.Utilities;
import com.heartbeat.db.cb.CBMapper;
import com.heartbeat.model.GroupPool;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.heartbeat.model.data.UserGroup;
import com.statics.CompanyEventData;
import com.transport.ExtMessage;
import com.transport.model.Group;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
          //delegate
          case "flushGroupState":
            resp = processFlushGroupState(session, ctx);
            break;
          case "switchJoinType":
            resp = processsSwitchMode(session, ctx);
            break;
          case "setInform":
            resp = processSetInform(session, ctx);
            break;
          case "setRole":
            resp = processSetRole(session, ctx);
            break;
          case "approve":
            resp = processGroupApproval(session, ctx);
            break;
          case "listGroup":
            processListGroup(cmd, ctx);
            return;
          case "leaveGroup":
            resp = processLeaveGroup(session);
            break;
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

  private ExtMessage processFlushGroupState(Session session, RoutingContext ctx) {
    ExtMessage resp = ExtMessage.group();
    if (session.groupID == Group.GROUP_ID_TYPE_REMOVE) {
      session.groupID = Group.GROUP_ID_TYPE_NONE;
      CBMapper.getInstance().mapOverride(Integer.toString(Group.GROUP_ID_TYPE_NONE),
              Integer.toString(session.id));
      resp.msg = "ok";
      return resp;
    }
    return resp;
  }

  private ExtMessage processsSwitchMode(Session session, RoutingContext ctx) {
    int joinType = ctx.getBodyAsJson().getInteger("joinType");
    ExtMessage resp = ExtMessage.group();
    if (joinType < 0 || joinType > 1) {
      resp.msg = "switch_join_type_fail";
      return resp;
    }

     UserGroup group = GroupPool.getGroupFromPool(session.groupID);
     if (group == null) {
       resp.msg = "group_not_found";
       return resp;
     }

     int role = group.getRole(session.id);
     if (role == Group.OWNER_ROLE || role == Group.MOD_ROLE) {
       if (joinType != group.joinType) {
         group.joinType = joinType;
         group.isChange = true;
       }
       resp.msg = "ok";
       resp.data.group = group;
       resp.data.currentGroupState = session.groupID;
       return resp;
     }

     resp.msg = "switch_type_fail_permission";
     return resp;
  }

  private ExtMessage processSetInform(Session session, RoutingContext ctx) {
    String informMsg  = ctx.getBodyAsJson().getString("inform");
    int    type       = ctx.getBodyAsJson().getInteger("type");
    //type 0 -> doi noi, type 1 -> doi ngoai
    ExtMessage resp   = ExtMessage.group();
    resp.data.currentGroupState = session.groupID;
    resp.msg          = session.setGroupInform(type, informMsg);
    resp.data.group   = GroupPool.getGroupFromPool(session.groupID);
    return resp;
  }

  private ExtMessage processSetRole(Session session, RoutingContext ctx) {
    //role ==
    int role        = ctx.getBodyAsJson().getInteger("role");
    int memberId    = ctx.getBodyAsJson().getInteger("memberId");

    ExtMessage resp = ExtMessage.group();
    if (session.id == memberId) {
      resp.msg = "set_role_fail_dup_memberID";
      return resp;
    }

    resp.msg = session.setGroupRole(memberId, role);
    resp.data.group = GroupPool.getGroupFromPool(session.groupID);
    resp.data.currentGroupState = session.groupID;
    return resp;
  }

  private ExtMessage processGroupApproval(Session session, RoutingContext ctx) {
    int memberId    = ctx.getBodyAsJson().getInteger("memberId");
    String action   = ctx.getBodyAsJson().getString("action");
    ExtMessage resp = ExtMessage.group();
    resp.msg        = session.approveMember(memberId, action);
    resp.data.group = GroupPool.getGroupFromPool(session.groupID);
    resp.data.currentGroupState = session.groupID;
    return resp;
  }

  private void processListGroup(String cmd, RoutingContext ctx) {
    fetchGroups(ar -> {
      if (ar.succeeded()) {
        ExtMessage resp   = ExtMessage.group();
        resp.data.extObj  = ar.result();
        resp.cmd          = cmd;
        resp.msg          = "ok";
        ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
      }
    });
  }

  private static void fetchGroups(Handler<AsyncResult<String>> handler) {
    HBServer.rxCluster.query("SELECT * from `persist` WHERE docType = \"group\"")
            .flatMapMany(ReactiveQueryResult::rowsAsObject).collectList().subscribe(
            ar -> {
              StringBuilder builder = GlobalVariable.stringBuilder.get();
              builder.append("[");
              for (int i = 0; i < ar.size(); i++) {
                JsonObject row = ar.get(i);
                builder.append(row.getObject("persist"));
                if (i < ar.size() - 1)
                  builder.append(",");
              }
              builder.append("]");
              handler.handle(Future.succeededFuture(builder.toString()));
            },
            er -> handler.handle(Future.succeededFuture("[]"))
    );
  }

  private ExtMessage processLeaveGroup(Session session) {
    ExtMessage resp = ExtMessage.group();
    resp.msg        = session.leaveGroup();
    resp.data.currentGroupState = session.groupID;
    return resp;
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
    if (Group.isValidGid(session.groupID) && (group = GroupPool.getGroupFromPool(session.groupID)) != null) {
      group.strStartDate  = Constant.COMPANY.EVENT_START;
      group.strEndDate    = Constant.COMPANY.EVENT_END;
      group.tasks         = CompanyEventData.eventMap;
      try {
        group.eventStartDate  = (int)(Utilities
                .getMillisFromDateString(Constant.COMPANY.EVENT_START, Constant.COMPANY.DATE_PATTERN));
        group.eventEndDate    = (int)(Utilities
                .getMillisFromDateString(Constant.COMPANY.EVENT_END, Constant.COMPANY.DATE_PATTERN));
      }
      catch (Exception e) {
        group.eventEndDate    = -1;
        group.eventStartDate  = -1;
      }
      resp.data.group = group;
      resp.msg        = "ok";
    }
    else if (session.groupID == Group.GROUP_ID_TYPE_KICK) {
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