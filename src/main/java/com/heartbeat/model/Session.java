package com.heartbeat.model;

import com.common.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.reflect.TypeToken;
import com.heartbeat.HBServer;
import com.heartbeat.db.cb.CBSession;
import com.heartbeat.effect.EffectHandler;
import com.heartbeat.effect.EffectManager;
import com.heartbeat.model.data.*;
import com.heartbeat.model.data.UserLDB;
import com.statics.FightData;
import com.statics.PropData;
import com.stdprofile.thrift.StdProfile;
import com.stdprofile.thrift.StdProfileResult;
import com.transport.EffectResult;
import com.transport.LoginRequest;
import com.transport.model.CompactProfile;
import io.vertx.core.http.ServerWebSocket;
import org.apache.thrift.TException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import static com.common.Constant.*;

@SuppressWarnings("unused")
public class Session {
  transient public int        id;
  transient public int        m_ping_count;
  transient public boolean    isClose;
  transient public boolean    isRegister;
  transient public String     clientToken = Constant.EMPTY_STRING;

  transient public int        lastUpdateOnline;
  transient public int        lastUpdateLDB;
  transient public int        lastHearBeatTime;

  //kpi transient data
  transient public String deviceUID         = Constant.EMPTY_STRING;
  transient public String deviceName        = Constant.EMPTY_STRING;
  transient public String clientVersion     = Constant.EMPTY_STRING;
  transient public String clientSource      = Constant.EMPTY_STRING;
  transient public String osPlatForm        = Constant.EMPTY_STRING;
  transient public String osVersion         = Constant.EMPTY_STRING;
  transient public String buildSource       = "VI";
  transient public int    serverVersionInt  = Constant.GAME_INFO.SERVER_VERSION;
  transient public DeviceUID.DeviceUIDUpdateInfo deviceInfo;
  transient public ServerWebSocket wsCtx;

  //runtime data
  transient public int        groupID;
  transient public UserLDB    userLDB;
  //persistent data
  public UserProfile        userProfile; /**/
  public UserGameInfo       userGameInfo; /**/
  public UserProduction     userProduction; /**/
  public UserIdol           userIdol; /**/
  public UserInventory      userInventory; /**/
  public UserFight          userFight; /**/
  public UserTravel         userTravel;/**/
  public UserDailyMission   userDailyMission; /**/
  public UserAchievement    userAchievement; /**/
  public UserMission        userMission;/**/
  public UserRollCall       userRollCall; /**/
  public UserEvent          userEvent; /**/
  public UserRanking        userRanking;/**/
  public UserInbox          userInbox;/**/
  public UserPayment        userPayment;

  public List<EffectResult> effectResults;
  public int                registerAt;
  public int                lastPaymentCheck;

  public void initRegister(String password) {
    userProfile           = UserProfile.ofDefault();
    userGameInfo          = UserGameInfo.ofDefault();
    userProduction        = UserProduction.ofDefault();
    userIdol              = UserIdol.ofDefault();
    userInventory         = UserInventory.ofDefault();
    userFight             = UserFight.ofDefault();
    userTravel            = UserTravel.ofDefault();
    userProfile.password  = password;
    userDailyMission      = UserDailyMission.ofDefault();
    userAchievement       = UserAchievement.ofDefault();
    userMission           = UserMission.ofDefault();
    userRollCall          = UserRollCall.ofDefault();
    userEvent             = UserEvent.ofDefault();
    userRanking           = UserRanking.ofDefault();
    userLDB               = UserLDB.ofDefault();
    userInbox             = UserInbox.ofDefault();
    userPayment           = UserPayment.ofDefault();

    //todo reBalance
    userProduction.reBalance(userIdol.totalCrt());

    userIdol.userEvent      = userEvent;        //ref
    userIdol.userRanking    = userRanking;      //ref

    userRanking.sessionId   = id;
    userRanking.displayName = userGameInfo.displayName;

    long curMs            = System.currentTimeMillis();
    userProduction.updateProduction(this, curMs);
    userGameInfo.updateUserMedia(curMs);
    userAchievement.addAchieveRecord(Constant.ACHIEVEMENT.LOGIN_ACHIEVEMENT, 1);
    userAchievement.addAchieveRecord(Constant.ACHIEVEMENT.IDOL_ACHIEVEMENT, 1);

    registerAt              = (int)(curMs/1000);
  }

  public void close() {
    if (!isClose) {
      isClose = true;
    }
    long curMs = System.currentTimeMillis();
    userProfile.lastLogout        = (int)curMs/1000;
    int onlineTime                = userProfile.lastLogin - userProfile.lastLogout;
    userProfile.totalPlayingTime += onlineTime;

    CBSession cba = CBSession.getInstance();
    cba.sync(Integer.toString(id), this, ar -> SessionPool.removeSession(id));
    syncStdProfile();
    SessionPool.removeSession(id); //T___T
  }

  public void sync(CBSession cba) {
    cba.sync(Integer.toString(id), this, ar -> {
      if (ar.failed())
        LOG.poolException(ar);
    });
  }

  @JsonIgnore
  public boolean isBan() {
    return userProfile.banTo > (int)(System.currentTimeMillis()/1000);
  }

  public void updateLogin() {
    long curMs = System.currentTimeMillis();
    int second = (int)(curMs/1000);

    //todo null check, consistency reBalance
    if (userInventory.expireItems == null)
      userInventory.expireItems = new HashMap<>();

    if (userDailyMission == null)
      userDailyMission = UserDailyMission.ofDefault();

    if (userTravel == null)
      userTravel = UserTravel.ofDefault();

    if (userGameInfo.crazyRewardClaim == null) {
      userGameInfo.crazyRewardClaim = new HashMap<>();
    }

    if (userAchievement == null) {
      userAchievement = UserAchievement.ofDefault();
    }

    if (userMission == null) {
      userMission = UserMission.ofDefault();
    }

    if (userRollCall == null) {
      userRollCall = UserRollCall.ofDefault();
    }
    else {
      userRollCall.reBalance();
    }

    if (userEvent == null) {
      userEvent = UserEvent.ofDefault();
    }
    else {
      userEvent.reBalance();
    }

    if (userRanking == null)
      userRanking = UserRanking.ofDefault();
    else {
      userRanking.reBalance();
    }

    if (userInbox == null)
      userInbox = UserInbox.ofDefault();
    else {
      userInbox.reBalance(curMs);
    }

    if (userPayment == null) {
      userPayment = UserPayment.ofDefault();
    }
    else {
      userPayment.reBalance();
    }

    userGameInfo.reBalance();
    userInventory.reBalance();
    userProduction.reBalance(this.userIdol.totalCrt());

    userLDB                 = UserLDB.ofDefault();
    userIdol.userEvent      = userEvent;        //ref
    userIdol.userRanking    = userRanking;      //ref
    userIdol.session        = this;

    userRanking.sessionId   = id;
    userRanking.displayName = userGameInfo.displayName;
    userTravel.chosenNPCId  = -1;

    int dayDiff;
    if (userProfile.lastLogin > 0) {
      userProfile.loginCount++;
      dayDiff = Utilities.dayDiff(second, userProfile.lastLogin);
      if (dayDiff > 0) {
        userProfile.newDay();
        userFight.newDay();
        userDailyMission.newDay();
        userGameInfo.newDay();
        userInventory.newDay();
        userTravel.newDay();
        userProduction.newDay();
        userIdol.newDay();
        userEvent.newDay();
        userAchievement.addAchieveRecord(Constant.ACHIEVEMENT.LOGIN_ACHIEVEMENT, 1);
      }
      else {
        userFight.reLogin();
      }

      //todo critical
      int deltaTime = second - userProfile.lastLogin;

      //record time spent event
      long remainTime = userGameInfo.remainTime();
      long timeSpent  = deltaTime > remainTime ? remainTime : deltaTime;
      userEvent.addEventRecord(COMMON_EVENT.TIME_SPEND_EVT_ID, timeSpent);
      userGameInfo.subtractTime(deltaTime);
    }

    userProfile.lastLogin   = second;
    lastHearBeatTime        = second;

    userGameInfo.totalCrt   = userIdol.totalCrt();
    userGameInfo.totalPerf  = userIdol.totalPerf();
    userGameInfo.totalAttr  = userIdol.totalAttr();
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
      LOG.authException(ex);
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
      if (second - lastUpdateLDB >= ONLINE_INFO.ONLINE_RECORD_LDB_TIME) {
        lastUpdateLDB = second;
        updateLDBScore();
      }
    }
  }

  public void updateLDBScore() {
    long totalCrt = userIdol.totalCrt();
    long totalPerf = userIdol.totalPerf();
    long totalAttr = userIdol.totalAttr();
    long totalTalent = totalCrt + totalPerf + totalAttr;
    if (userLDB != null) {
      userLDB.addLdbRecord(this, LEADER_BOARD.TALENT_LDB_ID, totalTalent);
      userLDB.addLdbRecord(this, LEADER_BOARD.FIGHT_LDB_ID, userFight.currentFightLV.id);
    }
  }

  public static void addUpdateSession(Session session) {
    if(session != null && !session.isClose) {
      updateQueue.add(session);
      updateOnlineTask.run();
      //GlobalVariable.exeThreadPool.execute(updateOnlineTask);
    }
  }

  // update session online stuff
  static ConcurrentLinkedQueue<Session> updateQueue = new ConcurrentLinkedQueue<>();

  static Runnable updateOnlineTask = () -> {
    Session session = updateQueue.poll();
    if(session != null && !session.isClose) {
      try {
        CBSession cbAccess = CBSession.getInstance();
        session.sync(cbAccess);
      }
      catch(Exception ex) {
        LOG.poolException(ex);
      }
    }
  };

  private Session() {

  }

  public void syncGroupInfo(String syncField) {
    UserGroup group = GroupPool.getGroupFromPool(groupID);
    if (group != null)
      HBServer.executor.executeBlocking(promise -> {
        group.updateMemberInfo(this, "totalProperty");
        promise.complete();
      }, res -> {});
  }

  public static Session of(int id) {
    Session res = new Session();
    res.id = id;
    res.effectResults = new ArrayList<>();
    return res;
  }

  public void gmtBan(int banTo) {
    this.userProfile.banTo = banTo;
    this.userProfile.lastLogin = 0;
  }

  //todo don't call without catch
  public void gmtUpdateName(String newDisplayName) {
    String res = this.userGameInfo.replaceDisplayName(this, newDisplayName);
    if (!res.equals("ok"))
      throw new RuntimeException(res);
  }

  public void gmtAddItem(int itemId, int amount) {
    if (!PropData.propMap.containsKey(itemId))
      return;

    int curAmount = userInventory.updateAndGet().getItemCnt(itemId);
    int newAmount = Math.max(curAmount + amount, 0);
    userInventory.addItem(this, itemId, newAmount);
  }

  public void gmtCastEffect(String eff) {
    Type listOfInt  = new TypeToken<List<Integer>>() {}.getType();
    List<Integer> effect = Utilities.gson.fromJson(eff, listOfInt);
    EffectManager.inst().handleEffect(EffectHandler.ExtArgs.of(), this, effect);
  }

  public void syncStdProfile() {
    StdProfile profile = new StdProfile();

    profile.setUserId(this.id);
    profile.setDisplayName(this.userGameInfo.displayName);

    profile.setGender(userGameInfo.gender);
    profile.setAvatarVersion((short)userGameInfo.avatar);//avatar
    profile.setBirthdate(userGameInfo.vipExp);
    UserGroup group = GroupPool.getGroupFromPool(groupID);
    String groupName = group != null ? group.name : "";

    profile.setStatusText(groupName);
    profile.setStatus(userGameInfo.titleId);
    profile.setCertDate(userFight.currentFightLV.id);

    StringBuilder builder = GlobalVariable.stringBuilder.get();
    builder.append(userGameInfo.totalCrt).append("-")
           .append(userGameInfo.totalPerf).append("-")
           .append(userGameInfo.totalAttr).append("-")
           .append(userGameInfo.exp);
    profile.setEmail(builder.toString());

    try {
      HBServer.thriftClient.ow_put(profile);
    } catch (TException e) {
      System.out.println(e.getMessage());
    }
  }

  public static CompactProfile getProfileFromCache(int sessionId) {
    try {
      StdProfileResult res = HBServer.thriftClient.get(sessionId);
      if (res.data != null && res.data.getUserId() > 0) {
        CompactProfile cProf  = new CompactProfile();
        StdProfile sProf      = res.data;
        cProf.userId          = sProf.getUserId();
        cProf.displayName     = sProf.getDisplayName();
        cProf.gender          = sProf.getGender();
        cProf.avatar          = sProf.getAvatarVersion();
        cProf.groupName       = sProf.getStatusText();
        cProf.titleId         = sProf.getStatus();
        cProf.curFightLV      = FightData.fightMap.getOrDefault(sProf.getCertDate(), FightData.of(1));
        cProf.vipExp          = sProf.getBirthdate();
        String[] attr         = sProf.getEmail().split("-");

        if (attr.length != 4) {
          cProf.exp = cProf.totalAttr = cProf.totalCrt = cProf.totalPerf = 0;
        }
        else {
          cProf.totalCrt  = Long.parseLong(attr[0]);
          cProf.totalPerf = Long.parseLong(attr[1]);
          cProf.totalAttr = Long.parseLong(attr[2]);
          cProf.exp       = Long.parseLong(attr[3]);
        }
        return cProf;
      }
      return null;
    }
    catch (TException e) {
      return null;
    }
  }
}