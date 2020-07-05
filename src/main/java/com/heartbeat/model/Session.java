package com.heartbeat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.heartbeat.common.Constant;
import com.heartbeat.common.DeviceUID;
import com.heartbeat.common.GlobalVariable;
import com.heartbeat.common.Utilities;
import com.heartbeat.db.impl.CBDataAccess;
import com.heartbeat.model.data.*;
import com.transport.EffectResult;
import com.transport.LoginRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@SuppressWarnings("unused")
public class Session {
  private static final Logger LOGGER = LoggerFactory.getLogger(Session.class);
  transient public int        id;
  transient public int        m_ping_count;
  transient public boolean    isClose;
  transient public boolean    isRegister;
  transient public String     clientToken = Constant.EMPTY_STRING;

  //online stuff
  transient public String     nodeIp;
  transient public int        nodePort;
  transient public int        lastUpdateOnline;
  transient public int        lastHearBeatTime;

  //kpi transient data
  transient public String deviceUID     = Constant.EMPTY_STRING;
  transient public String deviceName    = Constant.EMPTY_STRING;
  transient public String clientVersion = Constant.EMPTY_STRING;
  transient public String clientSource  = Constant.EMPTY_STRING;
  transient public String osPlatForm    = Constant.EMPTY_STRING;
  transient public String osVersion     = Constant.EMPTY_STRING;
  transient public String buildSource   = "VI";
  transient public int serverVersionInt = Constant.GAME_INFO.SERVER_VERSION;
  transient public DeviceUID.DeviceUIDUpdateInfo deviceInfo;

  //persistent data
  public UserProfile        userProfile;
  public UserGameInfo       userGameInfo;
  public UserProduction     userProduction;
  public UserIdol           userIdol;
  public UserInventory      userInventory;
  public UserFight          userFight;
  public List<EffectResult> effectResults;

  public void initRegister(String password) {
    userProfile           = UserProfile.ofDefault();
    userGameInfo          = UserGameInfo.ofDefault();
    userProduction        = UserProduction.ofDefault();
    userIdol              = UserIdol.ofDefault();
    userInventory         = UserInventory.ofDefault();
    userFight             = UserFight.ofDefault();
    userProfile.password  = password;

    long curMs            = System.currentTimeMillis();
    userProduction.updateProduction(this, curMs);
    userGameInfo.updateUserMedia(curMs);
  }

  public void close() {
    if (!isClose) {
      isClose = true;
    }
    long curMs = System.currentTimeMillis();
    userProfile.lastLogout = (int)curMs/1000;
    int onlineTime = userProfile.lastLogin - userProfile.lastLogout;
    userProfile.totalPlayingTime += onlineTime;

    CBDataAccess cba = CBDataAccess.getInstance();
    cba.sync(Integer.toString(id), this, ar -> SessionPool.removeSession(id));
    SessionPool.removeSession(id); //T___T
  }

  public void sync(CBDataAccess cba) {
    cba.sync(Integer.toString(id), this, ar -> {
      if (ar.failed())
        LOGGER.error(ar.cause().getMessage());
    });
  }

  @JsonIgnore
  public boolean isBan() {
    return userProfile.banTo > (int)(System.currentTimeMillis()/1000);
  }

  public void updateLogin() {
    long curMs = System.currentTimeMillis();
    int second = (int)(curMs/1000);
    int dayDiff;
    if (userProfile.lastLogin > 0) {
      userProfile.loginCount++;
      dayDiff = Utilities.dayDiff(second, userProfile.lastLogin);
      if (dayDiff > 0) {
        userFight.newDay();
      }
      else {
        userFight.reLogin();
      }
    }

    userProfile.lastLogin = second;
  }

  public void updateClientInfo(LoginRequest message) {
    try {
      deviceUID       = message.deviceUID;
      deviceName      = message.deviceName;
      clientVersion   = message.clientVersion;
      clientSource    = message.clientSource;
      osPlatForm      = message.osPlatform;
      osVersion       = message.osVersion;
      buildSource     = message.buildSource;
      // update to profile
      userProfile.lastClientAddress = message.clientAddress;
      userProfile.lastOsPlatform    = osPlatForm;
      userProfile.lastClientSource  = clientSource;

      //todo language base for client

      if(Utilities.isValidString(deviceUID)) {
        if (osPlatForm.equals(Constant.GAME_INFO.OS_ANDROID) && deviceUID.equals(Constant.GAME_INFO.DEFAULT_DEVICE_ID_ANDROID)) {
          deviceUID = userProfile.password;
        }
        userProfile.addDeviceUID(deviceUID);
        deviceInfo = GlobalVariable.updateDeviceUID(deviceUID, osPlatForm, id, isRegister);
      }
    }
    catch(Exception ex) {
      LOGGER.error(ex.getMessage());
    }
  }

  /*HEARTBEAT**********************************************************************************************************/
  public void updateOnline(long curMs) {
    if(!isClose) {
      m_ping_count = 0;
      int second = (int)(curMs/1000);
      lastHearBeatTime = second;
      if(second - lastUpdateOnline >= Constant.ONLINE_INFO.ONLINE_RECORD_UPDATE_TIME) {//sync
        lastUpdateOnline = second;
        addUpdateSession(this);
      }
    }
  }

  public static void addUpdateSession(Session session) {
    if(session != null && !session.isClose) {
      updateQueue.add(session);
      updateOnlineTask.run();
      System.out.println(Thread.currentThread().getName());
      GlobalVariable.exeThreadPool.execute(updateOnlineTask);
    }
  }

  // update session online stuff
  static ConcurrentLinkedQueue<Session> updateQueue = new ConcurrentLinkedQueue<>();

  static Runnable updateOnlineTask = () -> {
    Session session = updateQueue.poll();
    if(session != null && !session.isClose) {
      try {
        CBDataAccess cbAccess = CBDataAccess.getInstance();
        session.sync(cbAccess);
      }
      catch(Exception ex) {
        LOGGER.error(ex.getMessage());
      }
    }
  };

  private Session() {

  }

  public static Session of(int id) {
    Session res = new Session();
    res.id = id;
    res.effectResults = new ArrayList<>();
    return res;
  }
}
