package com.heartbeat.service.impl;

import com.common.Constant;
import com.common.LOG;
import com.common.Utilities;
import com.heartbeat.HBServer;
import com.heartbeat.Passport100D;
import com.heartbeat.db.cb.CBCounter;
import com.heartbeat.db.cb.CBMapper;
import com.heartbeat.db.cb.CBSession;
import com.heartbeat.model.GroupPool;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.heartbeat.model.data.UserGroup;
import com.heartbeat.service.AuthService;
import com.heartbeat.service.GroupService;
import com.transport.LoginRequest;
import com.transport.model.Group;
import com.transport.model.Profile;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;

@SuppressWarnings("unused")
public class SessionLoginService implements AuthService {
  GroupService groupService;

  public SessionLoginService() {
    groupService = new GroupServiceV1();
  }

  @Override
  public void processLogin(LoginRequest request, long curMs, Handler<AsyncResult<Profile>> handler) {
    String clientVersion = request.clientVersion;
    String clientSource = request.clientSource;
    String osPlatForm = request.osPlatform;
    String buildSource = request.buildSource;

    boolean versionCheck = versionCheck(clientVersion, clientSource, osPlatForm, buildSource, curMs);

    if (versionCheck) {
      int snsFlag = request.snsFlag;
      if (snsFlag == 1) {
        process100dLogin(request, handler);
      }
      else {
        processCommonLogin(request, handler);
      }
    }
    else {
      handler.handle(Future.failedFuture("login_client_update"));
    }
  }

  private void process100dLogin(LoginRequest request, Handler<AsyncResult<Profile>> handler) {
    String d100Token = request.snsToken;
    Passport100D.verify(d100Token, ar -> {
      if (ar.succeeded()) {
        try {
          Passport100D.Player pInfo = ar.result();
          String phoenixId = String.format("100d_%s", pInfo.player_id);
          String strUserId = CBMapper.getInstance().getValue(phoenixId);
          if (Utilities.isValidString(strUserId)) {
            handle100DLogin(request, Integer.parseInt(strUserId), strUserId, d100Token, handler);
          }
          else {
            registerAccount(request, rar -> {
              if (rar.succeeded()) {
                Session register = rar.result();
                register.userProfile.phoenixId = pInfo.player_id;
                Profile profile  = handleLoginResult("ok", register, "", request);
                CBMapper.getInstance().mapOverride(Integer.toString(register.id), phoenixId);

                //todo pref record
                JsonObject jsonMessage = new JsonObject().put("cmd", "createProfile");
                jsonMessage.put("phoenixId", pInfo.player_id);
                jsonMessage.put("profileId", register.id);
                HBServer.eventBus.send(Constant.SYSTEM_INFO.PREF_EVT_BUS, jsonMessage);

                handler.handle(Future.succeededFuture(profile));
              }
              else {
                handler.handle(Future.failedFuture(ar.cause()));
              }
            });
          }
        }
        catch (Exception e) {
          LOG.authException(e);
          handler.handle(Future.failedFuture("authorization_fail"));
        }
      }
      else {
        LOG.authException("Fail to verify 100D");
        handler.handle(Future.failedFuture(ar.cause()));
      }
    });
  }

  private void handle100DLogin(LoginRequest request, int userID, String strId,
                               String snsToken, Handler<AsyncResult<Profile>> handler) {
    Session oldSession = SessionPool.getSessionFromPool(userID);
    if (oldSession != null) {
      if (oldSession.id == userID) {
        //LOG.authException(String.format("Kick login. sessionId: %d", userID));
        Profile profile = handleLoginResult("kick", oldSession, snsToken, request);
        handler.handle(Future.succeededFuture(profile));
      }
      else {
        handler.handle(Future.failedFuture("login_wrong_pwd"));
      }
    }
    else {
      CBSession.getInstance().load(strId, ar -> {
        if (ar.succeeded()) {
          Session session = ar.result();
          if (session.isBan()) {
            handler.handle(Future.failedFuture("login_ban"));
            return;
          }
          session.id = userID;
          Profile profile = handleLoginResult("ok", session, snsToken, request);
          handler.handle(Future.succeededFuture(profile));
        }
        else {
          handler.handle(Future.failedFuture(ar.cause()));
        }
      });
    }
  }

  private void processCommonLogin(LoginRequest request, Handler<AsyncResult<Profile>> handler) {
    int userID            = Integer.parseInt(request.userID);
    String password       = request.password;
    String snsClientToken = request.facebookToken;

    if (userID > 0 && Utilities.isValidString(password)) {
      handleCommonLogin(request,userID, request.userID, password, snsClientToken, ar -> {
        if (ar.succeeded()) {
          handler.handle(Future.succeededFuture(ar.result()));
        }
        else {
          handler.handle(Future.failedFuture(ar.cause()));
        }
      });
    }
    else {
      registerAccount(request, ar -> {
        if (ar.succeeded()) {
          Session register = ar.result();
          Profile profile  = handleLoginResult("ok", register, snsClientToken, request);
          handler.handle(Future.succeededFuture(profile));
        }
        else {
          handler.handle(Future.failedFuture(ar.cause()));
        }
      });
    }
  }

  protected void handleCommonLogin(LoginRequest request, int userID, String strId,
                                   String password,
                                   String snsToken, Handler<AsyncResult<Profile>> handler) {

    Session oldSession = SessionPool.getSessionFromPool(userID);
    if (oldSession != null) {
      if (oldSession.id == userID && oldSession.userProfile.password.equals(password)) {
        LOG.authException(String.format("Kick login. sessionId: %d", userID));
        Profile profile = handleLoginResult("kick", oldSession, snsToken, request);
        handler.handle(Future.succeededFuture(profile));
      }
      else {
        handler.handle(Future.failedFuture("login_wrong_pwd"));
      }
    }
    else {
      CBSession.getInstance().load(strId, password, ar -> {
        if (ar.succeeded()) {
          Session session = ar.result();
          if (session.isBan()) {
            handler.handle(Future.failedFuture("login_ban"));
            return;
          }
          session.id = userID;
          Profile profile = handleLoginResult("ok", session, snsToken, request);
          handler.handle(Future.succeededFuture(profile));
        }
        else {
          handler.handle(Future.failedFuture(ar.cause()));
        }
      });
    }
  }

  private Profile handleLoginResult(String result, Session session,
                                           String snsToken,
                                           LoginRequest request) {
    Profile lr = new Profile();

    if (!result.equals("login_fail")) {
      String  strUserId     = Integer.toString(session.id);
      String  userName      = Constant.EMPTY_STRING;
      int     banTo         = 0;
      String  banReason     = Constant.EMPTY_STRING;
      String  facebookID    = Constant.EMPTY_STRING;
      String  notMsg        = Constant.EMPTY_STRING;

      if (result.equals("ok")) {
        session.clientToken = snsToken;
        session.lastHearBeatTime = (int)(System.currentTimeMillis()/1000);
        if (!session.isRegister) {
          session.updateClientInfo(request);
          notMsg = "hello again";
        }
        else {
          notMsg = "hello";
        }

        userName    = session.userGameInfo.displayName;
        banTo       = session.userProfile.banTo;
        banReason   = session.userProfile.banReason;
        facebookID  = session.userProfile.facebookID;

        SessionPool.addSession(session);
        session.updateLogin();
        groupService.loadSessionGroup(session, ar -> {
          UserGroup group = GroupPool.getGroupFromPool(session.groupID);
          if (group != null) {
            Group.Member member = group.members.get(session.id);
            if (member != null) {
              member.lastLogin = session.userProfile.lastLogin;
              group.isChange = true;
            }
          }
        });
      }
      else if (result.equals("kick")) {
        int second                    = (int)(System.currentTimeMillis()/1000);
        session.clientToken           = snsToken;
        session.lastHearBeatTime      = second;
        session.userProfile.lastLogin = second;
        notMsg = "hello again";

        userName    = session.userGameInfo.displayName;
        banTo       = session.userProfile.banTo;
        banReason   = session.userProfile.banReason;
        facebookID  = session.userProfile.facebookID;
      }

      lr.result             = result;
      lr.strUserId          = strUserId;
      lr.userName           = userName;
      lr.banTo              = banTo;
      lr.banReason          = banReason;
      lr.notMsg             = notMsg;
      lr.facebookId         = facebookID;
      lr.isRegister         = session.isRegister;
      lr.serverVersionInt   = session.serverVersionInt;
      lr.clientSource       = session.clientSource;
      lr.buildSource        = session.buildSource;
      lr.osPlatform         = session.osPlatForm;
      lr.clientVersion      = request.clientVersion;
      lr.displayName        = session.userGameInfo.displayName;
      lr.gender             = session.userGameInfo.gender;
      lr.avatar             = session.userGameInfo.avatar;
      lr.lastLogin          = session.userProfile.lastLogin;
      lr.registerAt         = session.registerAt;
      lr.gameFunctions      = new HashMap<>();

      lr.gameFunctions.putIfAbsent("giftCode", Constant.GAME_FUNCTIONS.GIFT_CODE);
      lr.gameFunctions.putIfAbsent("netaChat", Constant.GAME_FUNCTIONS.NETA_CHAT);

      return lr;
    }

    lr.result = result;
    return lr;
  }

  protected boolean versionCheck(String  clientVersion,  String clientSrc,
                                 String  osPlatForm,     String buildSource,
                                 long    curMs) {
    try {
      int clientVersionInt = Integer.parseInt(clientVersion);
      return clientVersionInt >= Constant.GAME_INFO.MIN_AVAILABLE_VERSION;
    }
    catch (Exception e) {
      return false;
    }
  }

  protected void registerAccount(LoginRequest message, Handler<AsyncResult<Session>> handler) {
    CBSession dbAccess = CBSession.getInstance();

    long    userId        = CBCounter.getInstance().increase(Constant.DB.ID_INCR_KEY, Constant.DB.ID_INIT);
    if (userId == -1) {
      handler.handle(Future.failedFuture("login_id_gen_fail"));
      return;
    }

    String  strUserID     = Long.toString(userId);
    Session session = Session.of((int)userId);

    session.initRegister(strUserID); //todo is as pwd, not encrypt here for fast

    dbAccess.sync(strUserID, session, ar -> {
      if (ar.succeeded()) {
        session.isRegister = true;
        session.updateClientInfo(message);
        if (session.deviceInfo != null)
          session.userProfile.isCloneUser = !session.deviceInfo.isFirstActive;
        String clientVersion = message.clientVersion;

        if (!clientVersion.isEmpty())
          session.userProfile.clientVersion = Integer.parseInt(clientVersion);

        LOG.console("createNewPlayer - userID: " + strUserID);

        handler.handle(Future.succeededFuture(session));
      }
      else {
        handler.handle(Future.failedFuture(ar.cause()));
      }
    });
  }
}
