package com.heartbeat.controller;

import com.common.LOG;
import com.common.Msg;
import com.common.Utilities;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.ReactiveQueryResult;
import com.google.gson.Gson;
import com.heartbeat.HBServer;
import static com.common.Constant.*;
import com.common.GlobalVariable;
import com.heartbeat.model.GroupPool;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.heartbeat.model.data.UserGroup;
import com.heartbeat.service.GroupService;
import com.heartbeat.service.impl.GroupServiceV1;
import com.statics.GroupMissionData;
import com.transport.ExtMessage;
import com.transport.model.Group;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class GroupController implements Handler<RoutingContext> {
  GroupService groupService;
  public GroupController() {
    groupService = new GroupServiceV1();
  }

  @Override
  public void handle(RoutingContext ctx) {
    String cmd          = "";
    try {
      cmd               = ctx.getBodyAsJson().getString("cmd");
      String strUserId  = ctx.user().principal().getString("username");
      int lastIssued    = ctx.user().principal().getInteger("issueTime");
      Session session   = SessionPool.getSessionFromPool(Integer.parseInt(strUserId));

      if (session != null && session.userProfile.lastLogin == lastIssued) {
        ExtMessage resp;
        switch (cmd) {
          case "claimReward":
            resp = processClaimReward(session, ctx, System.currentTimeMillis());
            break;
          case "flushGroupState":
            resp = processFlushGroupState(session);
            break;
          case "switchJoinType":
            resp = processSwitchMode(session, ctx);
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
            processListGroup(session, cmd, ctx);
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
          case "getMemberInfo":
            processGetMemberInfo(ctx, cmd);
            return;
          default:
            resp = ExtMessage.group();
            resp.msg = "unknown_cmd";
            break;
        }
        resp.cmd              = cmd;
        resp.timeChange       = session.userGameInfo.timeChange;
        resp.userRemainTime   = session.userGameInfo.remainTime();

        ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
        session.effectResults.clear();
        session.userGameInfo.timeChange = false;
      }
      else {
        ctx.response().setStatusCode(401).end();
      }
    }
    catch (Exception e) {
      ctx.response().setStatusCode(404).end();
      LOG.globalException("node", cmd, e);
    }
  }

  private void processGetMemberInfo(RoutingContext ctx, String cmd) {
    ExtMessage resp = ExtMessage.group();
    resp.cmd        = cmd;
    int memberId    = ctx.getBodyAsJson().getInteger("memberId");
    groupService.getMemberInfo(memberId, ar -> {
      if (ar.succeeded()) {
        resp.msg = "ok";
        resp.data.extObj = Utilities.gson.toJson(ar.result());
        ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
      }
      else {
        resp.msg = ar.cause().getMessage();
        ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
      }
    });
  }

  private ExtMessage processClaimReward(Session session, RoutingContext ctx, long curMs) {
    ExtMessage resp = ExtMessage.group();

    if (session.userGameInfo.titleId < UNLOCK_FUNCTION.GROUP_UNLOCK_LEVEL) {
      resp.msg = Msg.map.getOrDefault(Msg.LEVEL_LIMIT, "level_limit");
      return resp;
    }

    UserGroup group = GroupPool.getGroupFromPool(session.groupID);
    int missionId   = ctx.getBodyAsJson().getInteger("missionId");

    if (group == null) {
      resp.msg      = Msg.map.getOrDefault(Msg.GROUP_NOT_FOUND, "group_not_found");
      return resp;
    }
    resp.msg            = group.claimReward(session, missionId, (int)(curMs/1000));
    resp.effectResults  = session.effectResults;
    resp.data.group     = group;
    resp.data.currentGroupState = session.groupID;
    return resp;
  }

  private ExtMessage processFlushGroupState(Session session) {
    ExtMessage resp = ExtMessage.group();

    if (session.userGameInfo.titleId < UNLOCK_FUNCTION.GROUP_UNLOCK_LEVEL) {
      resp.msg = Msg.map.getOrDefault(Msg.LEVEL_LIMIT, "level_limit");
      return resp;
    }

    if (session.groupID == Group.GROUP_ID_TYPE_REMOVE) {
      session.groupID = Group.GROUP_ID_TYPE_KICK;
      //CBMapper.getInstance().unmap(Integer.toString(session.id));
      resp.msg = "ok";
      return resp;
    }
    return resp;
  }

  private ExtMessage processSwitchMode(Session session, RoutingContext ctx) {
    int joinType = ctx.getBodyAsJson().getInteger("joinType");
    ExtMessage resp = ExtMessage.group();

    if (session.userGameInfo.titleId < UNLOCK_FUNCTION.GROUP_UNLOCK_LEVEL) {
      resp.msg = Msg.map.getOrDefault(Msg.LEVEL_LIMIT, "level_limit");
      return resp;
    }

    if (joinType < 0 || joinType > 1) {
      resp.msg = Msg.map.getOrDefault(Msg.INVALID_GROUP_TYPE, "switch_join_type_fail");
      return resp;
    }

     UserGroup group = GroupPool.getGroupFromPool(session.groupID);
     if (group == null) {
       resp.msg = Msg.map.getOrDefault(Msg.GROUP_NOT_FOUND, "group_not_found");
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

     resp.msg = Msg.map.getOrDefault(Msg.GROUP_PERM, "switch_type_fail_permission");
     return resp;
  }

  private ExtMessage processSetInform(Session session, RoutingContext ctx) {
    String informMsg  = ctx.getBodyAsJson().getString("inform");
    int    type       = ctx.getBodyAsJson().getInteger("type");
    //type 0 -> doi noi, type 1 -> doi ngoai
    ExtMessage resp   = ExtMessage.group();

    if (session.userGameInfo.titleId < UNLOCK_FUNCTION.GROUP_UNLOCK_LEVEL) {
      resp.msg = Msg.map.getOrDefault(Msg.LEVEL_LIMIT, "level_limit");
      return resp;
    }

    resp.data.currentGroupState = session.groupID;
    //resp.msg          = session.setGroupInform(type, informMsg);
    resp.msg          = groupService.setGroupInform(session, type, informMsg);
    resp.data.group   = GroupPool.getGroupFromPool(session.groupID);
    return resp;
  }

  private ExtMessage processSetRole(Session session, RoutingContext ctx) {
    //role ==
    int role        = ctx.getBodyAsJson().getInteger("role");
    int memberId    = ctx.getBodyAsJson().getInteger("memberId");

    ExtMessage resp = ExtMessage.group();

    if (session.userGameInfo.titleId < UNLOCK_FUNCTION.GROUP_UNLOCK_LEVEL) {
      resp.msg = Msg.map.getOrDefault(Msg.LEVEL_LIMIT, "level_limit");
      return resp;
    }

    if (session.id == memberId) {
      resp.msg = Msg.map.getOrDefault(Msg.DUP_MEMBER, "set_role_fail_dup_memberID");
      return resp;
    }

    //resp.msg = session.setGroupRole(memberId, role);
    resp.msg  = groupService.setGroupRole(session, memberId, role);
    resp.data.group = GroupPool.getGroupFromPool(session.groupID);
    resp.data.currentGroupState = session.groupID;
    return resp;
  }

  private ExtMessage processGroupApproval(Session session, RoutingContext ctx) {
    int memberId    = ctx.getBodyAsJson().getInteger("memberId");
    String action   = ctx.getBodyAsJson().getString("action");
    ExtMessage resp = ExtMessage.group();

    if (session.userGameInfo.titleId < UNLOCK_FUNCTION.GROUP_UNLOCK_LEVEL) {
      resp.msg = Msg.map.getOrDefault(Msg.LEVEL_LIMIT, "level_limit");
      return resp;
    }

    //resp.msg        = session.approveMember(memberId, action);
    resp.msg        = groupService.approveMember(session, memberId, action);
    resp.data.group = GroupPool.getGroupFromPool(session.groupID);
    resp.data.currentGroupState = session.groupID;
    return resp;
  }

  private void processListGroup(Session session, String cmd, RoutingContext ctx) {
    ExtMessage check = ExtMessage.group();
    if (session.userGameInfo.titleId < UNLOCK_FUNCTION.GROUP_UNLOCK_LEVEL) {
      check.msg = Msg.map.getOrDefault(Msg.LEVEL_LIMIT, "level_limit");
      ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(check));
      return;
    }

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
    String bucket = String.format("%s%d_%s", DB.BUCKET_PREFIX, HBServer.nodeId, DB.PERSIST_BUCKET);
    String query  = String.format("SELECT * from `%s` WHERE docType = \"group\"", bucket);
    HBServer.rxCluster.query(query)
            .flatMapMany(ReactiveQueryResult::rowsAsObject).collectList().subscribe(
            ar -> {
              StringBuilder builder = GlobalVariable.stringBuilder.get();
              builder.append("[");
              for (int i = 0; i < ar.size(); i++) {
                JsonObject row = ar.get(i);
                builder.append(row.getObject(bucket));
                if (i < ar.size() - 1)
                  builder.append(",");
              }
              builder.append("]");
              handler.handle(Future.succeededFuture(builder.toString()));
            },
            er -> {
              handler.handle(Future.succeededFuture("[]"));
              LOG.globalException("node", "fetchGroup", er.getCause());
            }
    );
  }

  private ExtMessage processLeaveGroup(Session session) {
    ExtMessage resp = ExtMessage.group();

    if (session.userGameInfo.titleId < UNLOCK_FUNCTION.GROUP_UNLOCK_LEVEL) {
      resp.msg = Msg.map.getOrDefault(Msg.LEVEL_LIMIT, "level_limit");
      return resp;
    }

    //resp.msg        = session.leaveGroup();
    resp.msg        = groupService.leaveGroup(session);
    resp.data.currentGroupState = session.groupID;
    return resp;
  }

  private ExtMessage processKickMember(Session session, RoutingContext ctx) {
    ExtMessage resp   = ExtMessage.group();

    if (session.userGameInfo.titleId < UNLOCK_FUNCTION.GROUP_UNLOCK_LEVEL) {
      resp.msg = Msg.map.getOrDefault(Msg.LEVEL_LIMIT, "level_limit");
      return resp;
    }

    int memberId      = ctx.getBodyAsJson().getInteger("memberId");
    //resp.msg          = session.kick(memberId);
    resp.msg          = groupService.kick(session, memberId);
    resp.data.group   = GroupPool.getGroupFromPool(session.groupID);
    resp.data.currentGroupState = session.groupID;
    return resp;
  }

  private void processJoinGroup(Session session, String cmd, RoutingContext ctx) {
    ExtMessage resp = ExtMessage.group();

    if (session.userGameInfo.titleId < UNLOCK_FUNCTION.GROUP_UNLOCK_LEVEL) {
      resp.msg = Msg.map.getOrDefault(Msg.LEVEL_LIMIT, "level_limit");
      ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
      return;
    }

    int groupId;
    try {
      groupId = ctx.getBodyAsJson().getInteger("groupId");
    }
    catch (Exception e) {
      ctx.response().setStatusCode(404).end();
      return;
    }
    groupService.joinGroup(session, groupId, joinAr -> {
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

    if (session.userGameInfo.titleId < UNLOCK_FUNCTION.GROUP_UNLOCK_LEVEL) {
      resp.msg = Msg.map.getOrDefault(Msg.LEVEL_LIMIT, "level_limit");
      return resp;
    }

    UserGroup group;
    if (Group.isValidGid(session.groupID) && (group = GroupPool.getGroupFromPool(session.groupID)) != null) {
      group.calcMissionHitMember();
      group.missions          = GroupMissionData.missionMap;
      resp.data.extObj        = Json.encode(GROUP_EVENT.evtMap);
      resp.data.group         = group;
      resp.msg                = "ok";
    }
    else if (session.groupID == Group.GROUP_ID_TYPE_KICK) {
      resp.msg        = Msg.map.getOrDefault(Msg.GROUP_DELAY, "user_group_delay");
    }
    else {
      resp.msg        = Msg.map.getOrDefault(Msg.NO_GROUP, "user_have_no_group");
    }
    resp.data.currentGroupState = session.groupID;
    return resp;
  }

  private void processRemoveGroup(Session session, RoutingContext ctx, String cmd) {
    ExtMessage resp = ExtMessage.group();

    if (session.userGameInfo.titleId < UNLOCK_FUNCTION.GROUP_UNLOCK_LEVEL) {
      resp.msg = Msg.map.getOrDefault(Msg.LEVEL_LIMIT, "level_limit");
      ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
      return;
    }

    groupService.removeGroup(session, crtRes -> {
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
    ExtMessage resp = ExtMessage.group();

    if (session.userGameInfo.titleId < UNLOCK_FUNCTION.GROUP_UNLOCK_LEVEL) {
      resp.msg = Msg.map.getOrDefault(Msg.LEVEL_LIMIT, "level_limit");
      ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
      return;
    }

    int groupType;
    String name, externalInform, internalInform;
    try {
      groupType = ctx.getBodyAsJson().getInteger("groupType");
      name = ctx.getBodyAsJson().getString("name");
      externalInform = ctx.getBodyAsJson().getString("externalInform");
      internalInform = ctx.getBodyAsJson().getString("internalInform");
    }
    catch (Exception e) {
      ctx.response().setStatusCode(404).end();
      LOG.globalException("node", "createGroup", e);
      return;
    }

    if (groupType < 0 || groupType > 1) {
      resp.msg = Msg.map.getOrDefault(Msg.INVALID_GROUP_TYPE, "invalid_group_type");
      ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
      return;
    }
    groupService.createGroup(session, groupType, name, externalInform, internalInform, crtRes -> {
      if (crtRes.succeeded()) {
        resp.msg          = "ok";
        resp.cmd          = cmd;
        resp.data.group   = GroupPool.getGroupFromPool(session.groupID);
      }
      else {
        resp.msg = crtRes.cause().getMessage();
        resp.cmd = cmd;
      }
      resp.data.currentGroupState = session.groupID;
      resp.timeChange             = session.userGameInfo.timeChange;
      ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
      session.userGameInfo.timeChange = false;
      session.effectResults.clear();
    });
  }
}