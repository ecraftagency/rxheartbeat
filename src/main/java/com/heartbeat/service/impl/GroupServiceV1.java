package com.heartbeat.service.impl;

import com.common.Constant;
import com.common.LOG;
import com.common.Msg;
import com.heartbeat.db.cb.CBGroup;
import com.heartbeat.db.cb.CBMapper;
import com.heartbeat.db.cb.CBSession;
import com.heartbeat.model.GroupPool;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.heartbeat.model.data.UserGroup;
import com.heartbeat.service.GroupService;
import com.statics.WordFilter;
import com.transport.model.GameInfo;
import com.transport.model.Group;
import com.transport.model.Profile;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

public class GroupServiceV1 implements GroupService {
  @Override
  public void loadSessionGroup(Session session, Handler<AsyncResult<String>> handler) {
    CBMapper.getInstance().getValue(Integer.toString(session.id), ar -> {
      if (ar.succeeded()) { //have sid-gid mapping entry
        try {
          int gid = Integer.parseInt(ar.result());
          if (gid == Group.GROUP_ID_TYPE_KICK) { //user leave or kick from some group
            session.groupID = Group.GROUP_ID_TYPE_KICK;
            handler.handle(Future.succeededFuture("ok"));
          }
          if (gid == Group.GROUP_ID_TYPE_REMOVE) {
            session.groupID = Group.GROUP_ID_TYPE_REMOVE;
            handler.handle(Future.succeededFuture("ok"));
          }
          else if (Group.isValidGid(gid)) { //user have group
            Group group = GroupPool.getGroupFromPool(gid);
            if (group != null) { //and that group also online
              session.groupID = gid;
              handler.handle(Future.succeededFuture("ok"));
            }
            else { //group not online
              CBGroup.getInstance().load(ar.result(), gar -> {
                if (gar.succeeded()) {
                  GroupPool.addGroup(gar.result());
                  session.groupID = gid;
                }
                else { //user have sid-gid mapping entry but no doc under gid //todo critical part
                  CBMapper.getInstance().unmap(Integer.toString(session.id), a -> {});
                  session.groupID = Group.GROUP_ID_TYPE_NONE;
                }
                handler.handle(Future.succeededFuture("ok"));
              });
            }
          }
        }
        catch (Exception e) {
          session.groupID = Group.GROUP_ID_TYPE_NONE;
          handler.handle(Future.succeededFuture("ok"));
          LOG.globalException("node", "load session group", e);
        }
      }
      else { //there no sid-gid mapping entry
        session.groupID = Group.GROUP_ID_TYPE_NONE;
        handler.handle(Future.succeededFuture("ok"));
      }
    });
  }

  public void joinGroup(Session session, int joinedGID, Handler<AsyncResult<String>> handler) {
    UserGroup currentGroup = GroupPool.getGroupFromPool(session.groupID);
    if (currentGroup != null){
      handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.ALREADY_JOIN, "user_already_have_group")));
      return;
    }

    String oldMap = CBMapper.getInstance().getValue(Integer.toString(session.id));
    if (oldMap.equals("") && !Group.isValidGid(session.groupID)) { // no group, disk also agree
      UserGroup joinedGroup = GroupPool.getGroupFromPool(joinedGID);
      if (joinedGroup != null) {
        String result = joinedGroup.processJoinGroup(session);

        if (result.equals("ok")) {
          session.groupID = joinedGroup.id;
          CBMapper.getInstance().map(Integer.toString(session.groupID), Integer.toString(session.id), mar -> {});
          handler.handle(Future.succeededFuture("ok"));
        }
        else if (result.equals(Msg.map.getOrDefault(Msg.GROUP_JOIN_PENDING, "group_join_pending"))) {
          session.groupID = Group.GROUP_ID_TYPE_NONE;
          handler.handle(Future.failedFuture("ok"));
        }
        else {
          //rollback
          session.groupID = Group.GROUP_ID_TYPE_NONE;
          handler.handle(Future.failedFuture(result));
        }
      }
      else { //group not online
        CBGroup.getInstance().load(Integer.toString(joinedGID), loadAr -> {
          if (loadAr.succeeded()) {
            UserGroup loadedGroup = loadAr.result();
            GroupPool.addGroup(loadAr.result());
            String result = loadedGroup.processJoinGroup(session);

            if (result.equals("ok")) {
              session.groupID = loadedGroup.id;
              CBMapper.getInstance().map(Integer.toString(session.groupID), Integer.toString(session.id), mar -> {});
              handler.handle(Future.succeededFuture("ok"));
            }
            else if (result.equals(Msg.map.getOrDefault(Msg.GROUP_JOIN_PENDING, "group_join_pending"))) {
              session.groupID = Group.GROUP_ID_TYPE_NONE;
              handler.handle(Future.failedFuture("ok"));
            }
            else {
              //rollback
              session.groupID = Group.GROUP_ID_TYPE_NONE;
              handler.handle(Future.failedFuture(result));
            }
          }
          else {
            handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.GROUP_NOT_FOUND, "group_not_found")));
          }
        });
      }
    }
    else {
      try {
        int oldGID = Integer.parseInt(oldMap);
        if (oldGID == Group.GROUP_ID_TYPE_KICK || oldGID == Group.GROUP_ID_TYPE_REMOVE) {
          handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.GROUP_DELAY, "join_group_delay")));
        }
        else if (Group.isValidGid(oldGID)) { //fuck map say valid gid, but runtime gid == 0
          handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.UNKNOWN_ERR, "join_group_fail runtime and persistent mismatch")));
          LOG.globalException("node", "join group","alert, map value: " +  oldMap + " but runtime gid is zero sid: " + session.id);
        }
        else {
          handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.UNKNOWN_ERR, "join_group_fail_unknown")));
        }
      }
      catch (Exception e) {
        //mal form map field, delete it
        CBMapper.getInstance().unmap(Integer.toString(session.id), unmapR -> {});
        handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.UNKNOWN_ERR, "join_group_fail")));
      }
    }
  }

  @Override
  public void createGroup(Session session, int groupType, String name, String externalInform, String internalInform, Handler<AsyncResult<String>> handler) {
    if (session.userGameInfo.remainTime() < Constant.USER_GROUP.CREATE_GROUP_TIME_COST) {
      handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.INSUFFICIENT_TIME, "insufficient_time")));
      return;
    }

    if (!WordFilter.isValidInput(name, "VN")) {
      handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.INVALID_GROUP_NAME, "invalid_group_name")));
      return;
    }
    if (!WordFilter.isValidInput(externalInform, "VN")) {
      handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.INVALID_EXT_INFORM, "invalid_group_name")));
      return;
    }
    if (!WordFilter.isValidInput(internalInform, "VN")) {
      handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.INVALID_INT_INFORM, "invalid_group_name")));
      return;
    }

    if (Group.isValidGid(session.groupID)) { //user have group
      handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.NO_GROUP, "user_have_no_group")));
    }
    else if (session.groupID == Group.GROUP_ID_TYPE_KICK || session.groupID == Group.GROUP_ID_TYPE_REMOVE) {
      handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.GROUP_DELAY, "group_delay")));
    }
    else if (session.groupID == Group.GROUP_ID_TYPE_NONE) { //user have no group
      //first unmap sid_gid if have
      String oldGid = CBMapper.getInstance().getValue(Integer.toString(session.id));
      if (oldGid.equals("")) { // there no sid_gid map, perfect
        UserGroup newGroup = UserGroup.of(Group.GROUP_ID_TYPE_NONE,
                session, groupType, externalInform, internalInform, name);
        CBGroup.getInstance().add(Integer.toString(newGroup.id), newGroup, addRes -> {
          if (addRes.succeeded()) {
            session.groupID = Integer.parseInt(addRes.result());
            session.userGameInfo.useTime(session, Constant.USER_GROUP.CREATE_GROUP_TIME_COST);
            handler.handle(Future.succeededFuture("ok"));
          }
          else{
            handler.handle(Future.failedFuture(addRes.cause().getMessage()));
            LOG.globalException("node", "createGroup", addRes.cause());
          }
        });
      }
      else {
        // try for next login
        CBGroup.getInstance().load(oldGid, loadRes -> {
          if (loadRes.succeeded()) {
            handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.GROUP_DELAY, "group_delay")));
            LOG.globalException("node", "create group", "red alert: groupID zero but have sid_gid mapping and even a persistent group");
          }
          else { //have sid_gid mapping but don't have persistent group, ok (mean member of last delete group)
            CBMapper.getInstance().unmap(Integer.toString(session.id), unmapRes -> {
              if (unmapRes.succeeded()) {
                UserGroup newGroup = UserGroup.of(Group.GROUP_ID_TYPE_NONE,
                        session, groupType, externalInform, internalInform, name);
                CBGroup.getInstance().add(Integer.toString(newGroup.id), newGroup, addRes -> {
                  if (addRes.succeeded()) {
                    session.groupID = Integer.parseInt(addRes.result());
                    session.userGameInfo.useTime(session, Constant.USER_GROUP.CREATE_GROUP_TIME_COST);
                    handler.handle(Future.succeededFuture("ok"));
                  }
                  else{
                    handler.handle(Future.failedFuture(addRes.cause().getMessage()));
                  }
                });
              }
              else {
                handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.UNKNOWN_ERR, "unknown_err")));
              }
            });
          }
        });
      }
    }
    else {
      handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.UNKNOWN_GID, "unknown_gid")));
    }
  }

  @Override
  public void removeGroup(Session session, Handler<AsyncResult<String>> handler) {
    if (Group.isValidGid(session.groupID)) { //user have group
      UserGroup group = GroupPool.getGroupFromPool(session.groupID);
      if (group != null) {
        int role    = group.getRole(session.id);
        if (role == Group.OWNER_ROLE) {
          CBGroup.getInstance().remove(Integer.toString(session.groupID), revRes -> {
            if (revRes.succeeded()) {
              for (Group.Member member : group.members.values()) {
                Session memberSession = SessionPool.getSessionFromPool(member.id);
                if (memberSession != null)
                  memberSession.groupID = Group.GROUP_ID_TYPE_REMOVE;
                //todo sync, add expire to remove record 29/09/2020
                CBMapper.getInstance().mapOverride(Integer.toString(Group.GROUP_ID_TYPE_REMOVE),
                        Integer.toString(session.id), Group.KICK_EXPIRE, ar -> {});
              }
              handler.handle(Future.succeededFuture("ok"));
            }
            else {
              handler.handle(Future.failedFuture(revRes.cause().getMessage()));
            }
          });
        }
        else {
          handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.GROUP_PERM, "permission_error")));
        }
      }
      else { //fuck, have valid gid but no group on group pool
        session.groupID = Group.GROUP_ID_TYPE_NONE;
        handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.NO_GROUP, "user_have_no_group")));
      }
    }
    else {
      handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.NO_GROUP, "user_have_no_group")));
    }
  }

  @Override
  public String kick(Session session, int memberId) {
    UserGroup group = GroupPool.getGroupFromPool(session.groupID);
    if (group == null)
      return Msg.map.getOrDefault(Msg.GROUP_NOT_FOUND, "group_not_found");
    int role = group.getRole(session.id);

    int memberRole = group.getRole(memberId);
    if (memberRole == Group.OWNER_ROLE)
      return Msg.map.getOrDefault(Msg.OWNER_KICK, "fail_cant_kick_owner");

    if (role == Group.OWNER_ROLE || role == Group.MOD_ROLE) {
      Session kickedMember = SessionPool.getSessionFromPool(memberId);
      if (kickedMember != null) {
        kickedMember.groupID = Group.GROUP_ID_TYPE_KICK;
      }
      String result = group.kickMember(memberId);
      if (result.equals("ok")) {
        CBMapper.getInstance().mapOverride(Integer.toString(Group.GROUP_ID_TYPE_KICK),
                Integer.toString(memberId), Group.KICK_EXPIRE, ar -> {});
      }
      return result;
    }
    else {
      return Msg.map.getOrDefault(Msg.GROUP_PERM, "fail_no_permission");
    }
  }

  @Override
  public String leaveGroup(Session session) {
    if (Group.isValidGid(session.groupID)) {
      UserGroup group = GroupPool.getGroupFromPool(session.groupID);
      if (group == null) { //todo critical
        String err = String.format("leave_group_fail_[sid:%d,gid:%d,runtime:%s,members:%s]",
                session.id, session.groupID, "no", "_");
        LOG.globalException("node", "leave group", err);
        return err;
      }

      if (group.getRole(session.id) == Group.OWNER_ROLE) {
        return Msg.map.getOrDefault(Msg.OWNER_LEAVE, "leave_group_fail_admin");
      }

      String result = group.kickMember(session.id);
      if (result.equals("ok")) {
        session.groupID = Group.GROUP_ID_TYPE_KICK;
        CBMapper.getInstance().mapOverride(Integer.toString(Group.GROUP_ID_TYPE_KICK),
                Integer.toString(session.id), Group.KICK_EXPIRE, ar -> {});
        return "ok";
      }
      else { //todo critical
        String err = String.format("leave_group_fail_[sid:%d,gid:%d,runtime:%s,members:%s]",
                session.id, session.groupID, "valid", "no");
        LOG.globalException("node", "leave group", err);
        return err;
      }
    }
    else {
      return Msg.map.getOrDefault(Msg.NO_GROUP, "user_have_no_group");
    }
  }

  @Override
  public String approveMember(Session session, int memberId, String action) {
    //check online group
    UserGroup group = GroupPool.getGroupFromPool(session.groupID);
    if (group == null) {
      String err = String.format("user_have_no_group[sid:%d,gid:%d,runtime:%s]"
              ,session.id, session.groupID, "no");
      LOG.globalException("node", "approve member", err);
      return Msg.map.getOrDefault(Msg.NO_GROUP, err);
    }

    if (memberId == session.id) {
      return Msg.map.getOrDefault(Msg.SELF_APPROVE, "can_not_self_approve");
    }

    //check role
    int role = group.getRole(session.id);
    if (role != Group.OWNER_ROLE && role != Group.MOD_ROLE) {
      return Msg.map.getOrDefault(Msg.GROUP_PERM, "approve_fail_permission");
    }

    //check member condition
    String state = CBMapper.getInstance().getValue(Integer.toString(memberId));
    if (!state.equals("")) {
      try {
        int currentGID = Integer.parseInt(state);
        if (currentGID == -1) {
          group.removePendingMember(memberId);
          return Msg.map.getOrDefault(Msg.GROUP_DELAY, "approve_fail_delay");
        }
        else if (Group.isValidGid(currentGID)) {
          group.removePendingMember(memberId);
          return Msg.map.getOrDefault(Msg.ALREADY_JOIN, "approve_fail_user_already_have_group");
        }
        else {
          group.removePendingMember(memberId);
          return Msg.map.getOrDefault(Msg.UNKNOWN_ERR, "approve_fail_unknown");
        }
      }
      catch (Exception e) {
        //malform
        CBMapper.getInstance().unmap(Integer.toString(memberId), ar -> {});
        group.removePendingMember(memberId);
        LOG.globalException("node", "approve member", String.format("malform_sid_gid_index[sid:%d,gid:%s]",memberId, state));
        return Msg.map.getOrDefault(Msg.UNKNOWN_ERR, "approve_fail_unknown");
      }
    }

    String approveRes =  group.approveGroup(memberId, action);

    if (approveRes.equals("ok") && action.equals("approve")) {
      CBMapper.getInstance().map(Integer.toString(group.id), Integer.toString(memberId));
    }
    return approveRes;
  }

  @Override
  public String setGroupRole(Session session, int memberId, int newRole) {
    UserGroup group = GroupPool.getGroupFromPool(session.groupID);

    if (group == null)
      return Msg.map.getOrDefault(Msg.GROUP_NOT_FOUND, "group_not_found");

    int myRole = group.getRole(session.id);
    if (myRole != Group.OWNER_ROLE)
      return Msg.map.getOrDefault(Msg.GROUP_PERM, "set_role_fail_permission");

    if ((newRole == Group.MOD_ROLE && group.modCount() < 2) || newRole == Group.USER_ROLE) {
      return group.setRole(memberId, newRole);
    }
    return Msg.map.getOrDefault(Msg.UNKNOWN_ERR, "set_role_fail");
  }

  @Override
  public String setGroupInform(Session session, int type, String informMsg) {
    UserGroup group = GroupPool.getGroupFromPool(session.groupID);

    if (group == null)
      return Msg.map.getOrDefault(Msg.GROUP_NOT_FOUND, "group_not_found");

    int role = group.getRole(session.id);

    if (role == Group.OWNER_ROLE || role == Group.MOD_ROLE) {
      if (!WordFilter.isValidInput(informMsg, "VN")) {
        return Msg.map.getOrDefault(Msg.INVALID_EXT_INFORM, "invalid_inform");
      }
      return group.changeInform(informMsg, type);
    }
    else
      return Msg.map.getOrDefault(Msg.GROUP_PERM, "set_inform_fail_permission");
  }

  @Override
  public void getMemberInfo(int sessionId, Handler<AsyncResult<GameInfo>> handler) {
    Session session   = SessionPool.getSessionFromPool(sessionId);
    if (session != null) {
      handler.handle(Future.succeededFuture(session.userGameInfo));
    }
    else {
      CBSession.getInstance().load(Integer.toString(sessionId), ar -> {
        if (ar.succeeded()) {
          handler.handle(Future.succeededFuture(ar.result().userGameInfo));
        }
        else {
          handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.MEMBER_INFO_NOT_FOUND, "group_member_not_found")));
        }
      });
    }
  }
}

/*
  public void removeGroup(Handler<AsyncResult<String>> handler) {
    if (Group.isValidGid(groupID)) { //user have group
      UserGroup group = GroupPool.getGroupFromPool(groupID);
      if (group != null) {
        int role    = group.getRole(id);
        if (role == Group.OWNER_ROLE) {
          CBGroup.getInstance().remove(Integer.toString(groupID), revRes -> {
            if (revRes.succeeded()) {
              for (Group.Member member : group.members.values()) {
                Session session = SessionPool.getSessionFromPool(member.id);
                if (session != null)
                  session.groupID = Group.GROUP_ID_TYPE_REMOVE;
                //todo sync, add expire to remove record 29/09/2020
                CBMapper.getInstance().mapOverride(Integer.toString(Group.GROUP_ID_TYPE_REMOVE, Group.KICK_EXPIRE),
                        Integer.toString(member.id));
              }
              handler.handle(Future.succeededFuture("ok"));
            }
            else {
              handler.handle(Future.failedFuture(revRes.cause().getMessage()));
            }
          });
        }
        else {
          handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.GROUP_PERM, "permission_error")));
        }
      }
      else { //fuck, have valid gid but no group on group pool
        groupID = Group.GROUP_ID_TYPE_NONE;
        handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.NO_GROUP, "user_have_no_group")));
      }
    }
    else {
      handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.NO_GROUP, "user_have_no_group")));
    }
  }

  public void createGroup(int groupType, String name, String externalInform, String internalInform, Handler<AsyncResult<String>> handler) {
    if (userGameInfo.remainTime() < USER_GROUP.CREATE_GROUP_TIME_COST) {
      handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.INSUFFICIENT_TIME, "insufficient_time")));
      return;
    }

    if (!WordFilter.isValidInput(name, "VN")) {
      handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.INVALID_GROUP_NAME, "invalid_group_name")));
      return;
    }
    if (!WordFilter.isValidInput(externalInform, "VN")) {
      handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.INVALID_EXT_INFORM, "invalid_group_name")));
      return;
    }
    if (!WordFilter.isValidInput(internalInform, "VN")) {
      handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.INVALID_INT_INFORM, "invalid_group_name")));
      return;
    }

    if (Group.isValidGid(groupID)) { //user have group
      handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.NO_GROUP, "user_have_no_group")));
    }
    else if (groupID == Group.GROUP_ID_TYPE_KICK || groupID == Group.GROUP_ID_TYPE_REMOVE) {
      handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.GROUP_DELAY, "group_delay")));
    }
    else if (groupID == Group.GROUP_ID_TYPE_NONE) { //user have no group
      //first unmap sid_gid if have
      String oldGid = CBMapper.getInstance().getValue(Integer.toString(id));
      if (oldGid.equals("")) { // there no sid_gid map, perfect
        UserGroup newGroup = UserGroup.of(Group.GROUP_ID_TYPE_NONE,
                this, groupType, externalInform, internalInform, name);
        CBGroup.getInstance().add(Integer.toString(newGroup.id), newGroup, addRes -> {
          if (addRes.succeeded()) {
            groupID = Integer.parseInt(addRes.result());
            userGameInfo.useTime(this, USER_GROUP.CREATE_GROUP_TIME_COST);
            handler.handle(Future.succeededFuture("ok"));
          }
          else{
            handler.handle(Future.failedFuture(addRes.cause().getMessage()));
            LOG.globalException("node", "createGroup", addRes.cause());
          }
        });
      }
      else {
        // try for next login
        CBGroup.getInstance().load(oldGid, loadRes -> {
          if (loadRes.succeeded()) {
            handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.GROUP_DELAY, "group_delay")));
            LOG.globalException("node", "create group", "red alert: groupID zero but have sid_gid mapping and even a persistent group");
          }
          else { //have sid_gid mapping but don't have persistent group, ok (mean member of last delete group)
            CBMapper.getInstance().unmap(Integer.toString(id), unmapRes -> {
              if (unmapRes.succeeded()) {
                UserGroup newGroup = UserGroup.of(Group.GROUP_ID_TYPE_NONE,
                        this, groupType, externalInform, internalInform, name);
                CBGroup.getInstance().add(Integer.toString(newGroup.id), newGroup, addRes -> {
                  if (addRes.succeeded()) {
                    groupID = Integer.parseInt(addRes.result());
                    userGameInfo.useTime(this, USER_GROUP.CREATE_GROUP_TIME_COST);
                    handler.handle(Future.succeededFuture("ok"));
                  }
                  else{
                    handler.handle(Future.failedFuture(addRes.cause().getMessage()));
                  }
                });
              }
              else {
                handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.UNKNOWN_ERR, "unknown_err")));
              }
            });
          }
        });
      }
    }
    else {
      handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.UNKNOWN_GID, "unknown_gid")));
    }
  }

  public void loadSessionGroup(Handler<AsyncResult<String>> handler) {
    CBMapper.getInstance().getValue(Integer.toString(id), ar -> {
      if (ar.succeeded()) { //have sid-gid mapping entry
        try {
          int gid = Integer.parseInt(ar.result());
          if (gid == Group.GROUP_ID_TYPE_KICK) { //user leave or kick from some group
            groupID = Group.GROUP_ID_TYPE_KICK;
            handler.handle(Future.succeededFuture("ok"));
          }
          if (gid == Group.GROUP_ID_TYPE_REMOVE) {
            groupID = Group.GROUP_ID_TYPE_REMOVE;
            handler.handle(Future.succeededFuture("ok"));
          }
          else if (Group.isValidGid(gid)) { //user have group
            Group group = GroupPool.getGroupFromPool(gid);
            if (group != null) { //and that group also online
              groupID = gid;
              handler.handle(Future.succeededFuture("ok"));
            }
            else { //group not online
              CBGroup.getInstance().load(ar.result(), gar -> {
                if (gar.succeeded()) {
                  GroupPool.addGroup(gar.result());
                  groupID = gid;
                }
                else { //user have sid-gid mapping entry but no doc under gid //todo critical part
                  CBMapper.getInstance().unmap(Integer.toString(id), a -> {});
                  groupID = Group.GROUP_ID_TYPE_NONE;
                }
                handler.handle(Future.succeededFuture("ok"));
              });
            }
          }
        }
        catch (Exception e) {
          groupID = Group.GROUP_ID_TYPE_NONE;
          handler.handle(Future.succeededFuture("ok"));
          LOG.globalException("node", "load session group", e);
        }
      }
      else { //there no sid-gid mapping entry
        groupID = Group.GROUP_ID_TYPE_NONE;
        handler.handle(Future.succeededFuture("ok"));
      }
    });
  }

  public void joinGroup(int joinedGID, Handler<AsyncResult<String>> handler) {
    UserGroup currentGroup = GroupPool.getGroupFromPool(this.groupID);
    if (currentGroup != null){
      handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.ALREADY_JOIN, "user_already_have_group")));
      return;
    }

    String oldMap = CBMapper.getInstance().getValue(Integer.toString(id));
    if (oldMap.equals("") && !Group.isValidGid(this.groupID)) { // no group, disk also agree
      UserGroup joinedGroup = GroupPool.getGroupFromPool(joinedGID);
      if (joinedGroup != null) {
        String result = joinedGroup.processJoinGroup(this);

        if (result.equals("ok")) {
          this.groupID = joinedGroup.id;
          CBMapper.getInstance().map(Integer.toString(this.groupID), Integer.toString(this.id), mar -> {});
          handler.handle(Future.succeededFuture("ok"));
        }
        else if (result.equals(Msg.map.getOrDefault(Msg.GROUP_JOIN_PENDING, "group_join_pending"))) {
          this.groupID = Group.GROUP_ID_TYPE_NONE;
          handler.handle(Future.failedFuture("ok"));
        }
        else {
          //rollback
          this.groupID = Group.GROUP_ID_TYPE_NONE;
          handler.handle(Future.failedFuture(result));
        }
      }
      else { //group not online
        CBGroup.getInstance().load(Integer.toString(joinedGID), loadAr -> {
          if (loadAr.succeeded()) {
            UserGroup loadedGroup = loadAr.result();
            GroupPool.addGroup(loadAr.result());
            String result = loadedGroup.processJoinGroup(this);

            if (result.equals("ok")) {
              this.groupID = loadedGroup.id;
              CBMapper.getInstance().map(Integer.toString(this.groupID), Integer.toString(this.id), mar -> {});
              handler.handle(Future.succeededFuture("ok"));
            }
            else if (result.equals(Msg.map.getOrDefault(Msg.GROUP_JOIN_PENDING, "group_join_pending"))) {
              this.groupID = Group.GROUP_ID_TYPE_NONE;
              handler.handle(Future.failedFuture("ok"));
            }
            else {
              //rollback
              this.groupID = Group.GROUP_ID_TYPE_NONE;
              handler.handle(Future.failedFuture(result));
            }
          }
          else {
            handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.GROUP_NOT_FOUND, "group_not_found")));
          }
        });
      }
    }
    else {
      try {
        int oldGID = Integer.parseInt(oldMap);
        if (oldGID == Group.GROUP_ID_TYPE_KICK || oldGID == Group.GROUP_ID_TYPE_REMOVE) {
          handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.GROUP_DELAY, "join_group_delay")));
        }
        else if (Group.isValidGid(oldGID)) { //fuck map say valid gid, but runtime gid == 0
          handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.UNKNOWN_ERR, "join_group_fail runtime and persistent mismatch")));
          LOG.globalException("node", "join group","alert, map value: " +  oldMap + " but runtime gid is zero sid: " + id);
        }
        else {
          handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.UNKNOWN_ERR, "join_group_fail_unknown")));
        }
      }
      catch (Exception e) {
        //mal form map field, delete it
        CBMapper.getInstance().unmap(Integer.toString(id), unmapR -> {});
        handler.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.UNKNOWN_ERR, "join_group_fail")));
      }
    }
  }

  public String kick(int memberId) {
    UserGroup group = GroupPool.getGroupFromPool(groupID);
    if (group == null)
      return Msg.map.getOrDefault(Msg.GROUP_NOT_FOUND, "group_not_found");
    int role = group.getRole(this.id);

    int memberRole = group.getRole(memberId);
    if (memberRole == Group.OWNER_ROLE)
      return Msg.map.getOrDefault(Msg.OWNER_KICK, "fail_cant_kick_owner");

    if (role == Group.OWNER_ROLE || role == Group.MOD_ROLE) {
      Session session = SessionPool.getSessionFromPool(memberId);
      if (session != null) {
        session.groupID = Group.GROUP_ID_TYPE_KICK;
      }
      String result = group.kickMember(memberId);
      if (result.equals("ok")) {
        CBMapper.getInstance().mapOverride(Integer.toString(Group.GROUP_ID_TYPE_KICK),
                Integer.toString(memberId), Group.KICK_EXPIRE, ar -> {});
      }
      return result;
    }
    else {
      return Msg.map.getOrDefault(Msg.GROUP_PERM, "fail_no_permission");
    }
  }

  public String leaveGroup() {
    if (Group.isValidGid(groupID)) {
      UserGroup group = GroupPool.getGroupFromPool(groupID);
      if (group == null) { //todo critical
        String err = String.format("leave_group_fail_[sid:%d,gid:%d,runtime:%s,members:%s]",id, groupID, "no", "_");
        LOG.globalException("node", "leave group", err);
        return err;
      }

      if (group.getRole(id) == Group.OWNER_ROLE) {
        return Msg.map.getOrDefault(Msg.OWNER_LEAVE, "leave_group_fail_admin");
      }

      String result = group.kickMember(id);
      if (result.equals("ok")) {
        groupID = Group.GROUP_ID_TYPE_KICK;
        CBMapper.getInstance().mapOverride(Integer.toString(Group.GROUP_ID_TYPE_KICK),
                Integer.toString(id), Group.KICK_EXPIRE, ar -> {});
        return "ok";
      }
      else { //todo critical
        String err = String.format("leave_group_fail_[sid:%d,gid:%d,runtime:%s,members:%s]",id, groupID, "valid", "no");
        LOG.globalException("node", "leave group", err);
        return err;
      }
    }
    else {
      return Msg.map.getOrDefault(Msg.NO_GROUP, "user_have_no_group");
    }
  }

  public String approveMember(int memberId, String action) {
    //check online group
    UserGroup group = GroupPool.getGroupFromPool(this.groupID);
    if (group == null) {
      String err = String.format("user_have_no_group[sid:%d,gid:%d,runtime:%s]",id, groupID, "no");
      LOG.globalException("node", "approve member", err);
      return Msg.map.getOrDefault(Msg.NO_GROUP, err);
    }

    if (memberId == this.id) {
      return Msg.map.getOrDefault(Msg.SELF_APPROVE, "can_not_self_approve");
    }

    //check role
    int role = group.getRole(this.id);
    if (role != Group.OWNER_ROLE && role != Group.MOD_ROLE) {
      return Msg.map.getOrDefault(Msg.GROUP_PERM, "approve_fail_permission");
    }

    //check member condition
    String state = CBMapper.getInstance().getValue(Integer.toString(memberId));
    if (!state.equals("")) {
      try {
        int currentGID = Integer.parseInt(state);
        if (currentGID == -1) {
          group.removePendingMember(memberId);
          return Msg.map.getOrDefault(Msg.GROUP_DELAY, "approve_fail_delay");
        }
        else if (Group.isValidGid(currentGID)) {
          group.removePendingMember(memberId);
          return Msg.map.getOrDefault(Msg.ALREADY_JOIN, "approve_fail_user_already_have_group");
        }
        else {
          group.removePendingMember(memberId);
          return Msg.map.getOrDefault(Msg.UNKNOWN_ERR, "approve_fail_unknown");
        }
      }
      catch (Exception e) {
        //malform
        CBMapper.getInstance().unmap(Integer.toString(memberId), ar -> {});
        group.removePendingMember(memberId);
        LOG.globalException("node", "approve member", String.format("malform_sid_gid_index[sid:%d,gid:%s]",memberId, state));
        return Msg.map.getOrDefault(Msg.UNKNOWN_ERR, "approve_fail_unknown");
      }
    }

    String approveRes =  group.approveGroup(memberId, action);

    if (approveRes.equals("ok") && action.equals("approve")) {
      CBMapper.getInstance().map(Integer.toString(group.id), Integer.toString(memberId));
    }
    return approveRes;
  }

  public String setGroupRole(int memberId, int newRole) {
    UserGroup group = GroupPool.getGroupFromPool(this.groupID);

    if (group == null)
      return Msg.map.getOrDefault(Msg.GROUP_NOT_FOUND, "group_not_found");

    int myRole = group.getRole(this.id);
    if (myRole != Group.OWNER_ROLE)
      return Msg.map.getOrDefault(Msg.GROUP_PERM, "set_role_fail_permission");

    if ((newRole == Group.MOD_ROLE && group.modCount() < 2) || newRole == Group.USER_ROLE) {
      return group.setRole(memberId, newRole);
    }
    return Msg.map.getOrDefault(Msg.UNKNOWN_ERR, "set_role_fail");
  }

  public String setGroupInform(int type, String informMsg) {
    UserGroup group = GroupPool.getGroupFromPool(this.groupID);

    if (group == null)
      return Msg.map.getOrDefault(Msg.GROUP_NOT_FOUND, "group_not_found");

    int role = group.getRole(this.id);

    if (role == Group.OWNER_ROLE || role == Group.MOD_ROLE) {
      if (!WordFilter.isValidInput(informMsg, "VN")) {
        return Msg.map.getOrDefault(Msg.INVALID_EXT_INFORM, "invalid_inform");
      }
      return group.changeInform(informMsg, type);
    }
    else
      return Msg.map.getOrDefault(Msg.GROUP_PERM, "set_inform_fail_permission");
  }
   */