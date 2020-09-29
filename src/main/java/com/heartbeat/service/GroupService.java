package com.heartbeat.service;

import com.heartbeat.model.Session;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@SuppressWarnings("unused")
public interface GroupService {
  void loadSessionGroup(Session session, Handler<AsyncResult<String>> handler);
  void joinGroup(Session session, int joinedGID, Handler<AsyncResult<String>> handler);
  void createGroup(Session session, int groupType, String name, String externalInform, String internalInform, Handler<AsyncResult<String>> handler);
  void removeGroup(Session session, Handler<AsyncResult<String>> handler);
  String kick(Session session, int memberId);
  String leaveGroup(Session session);
  String approveMember(Session session, int memberId, String action);
  String setGroupRole(Session session, int memberId, int newRole);
  String setGroupInform(Session session, int type, String informMsg);
}