package com.heartbeat.service.impl;

import com.common.Constant;
import com.common.Utilities;
import com.heartbeat.db.cb.CBCounter;
import com.heartbeat.db.cb.CBSession;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.heartbeat.service.AuthService;
import com.transport.LoginRequest;
import com.transport.model.Profile;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class SessionLoginService implements AuthService {
  private static final Logger LOGGER = LoggerFactory.getLogger(SessionLoginService.class);

  @Override
  public void processLogin(LoginRequest request, long curMs, Handler<AsyncResult<Profile>> handler) {
    int userID = Integer.parseInt(request.userID);

    String clientVersion = request.clientVersion;
    String clientSource = request.clientSource;
    String osPlatForm = request.osPlatform;
    String buildSource = request.buildSource;

    boolean versionCheck = versionCheck(clientVersion, clientSource, osPlatForm, buildSource, curMs);

    if (versionCheck) {
      String password = request.password;
      String snsClientToken = request.facebookToken;
      int snsFlag = request.snsflag;

      if (userID > 0 && Utilities.isValidString(password)) {
        handleGameLogin(request,userID, request.userID, password, snsClientToken, ar -> {
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
    else {
      handler.handle(Future.failedFuture("login_client_update"));
    }
  }

  protected void handleGameLogin(LoginRequest request, int userID, String strId,
                                    String password,
                                    String snsToken, Handler<AsyncResult<Profile>> handler) {

    Session oldSession = SessionPool.getSessionFromPool(userID);
    if (oldSession != null) {
      if (oldSession.id == userID && oldSession.userProfile.password.equals(password)) {
        LOGGER.warn(String.format("Kick login. sessionId: %d", userID));
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

  private static Profile handleLoginResult(String result, Session session,
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
        session.loadSessionGroup(ar -> {});
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

        LOGGER.info("createNewPlayer - userID: " + strUserID);

        handler.handle(Future.succeededFuture(session));
      }
      else {
        handler.handle(Future.failedFuture(ar.cause()));
      }
    });
  }
}
