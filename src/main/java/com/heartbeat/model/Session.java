package com.heartbeat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.heartbeat.common.Constant;
import com.heartbeat.common.DeviceUID;
import com.heartbeat.common.GlobalVariable;
import com.heartbeat.common.Utilities;
import com.heartbeat.db.cb.CBGroup;
import com.heartbeat.db.cb.CBMapper;
import com.heartbeat.db.cb.CBSession;
import com.heartbeat.model.data.*;
import com.transport.EffectResult;
import com.transport.LoginRequest;
import com.transport.model.Group;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/*
group ref count I/O
session online  -> ++
session close   -> --
 */
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

  //runtime data
  public transient int groupID;
  //persistent data
  public UserProfile        userProfile;
  public UserGameInfo       userGameInfo;
  public UserProduction     userProduction;
  public UserIdol           userIdol;
  public UserInventory      userInventory;
  public UserFight          userFight;
  public UserTravel         userTravel;

  public List<EffectResult> effectResults;

  public void initRegister(String password) {
    userProfile           = UserProfile.ofDefault();
    userGameInfo          = UserGameInfo.ofDefault();
    userProduction        = UserProduction.ofDefault();
    userIdol              = UserIdol.ofDefault();
    userInventory         = UserInventory.ofDefault();
    userFight             = UserFight.ofDefault();
    userTravel            = UserTravel.ofDefault();
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
    userProfile.lastLogout        = (int)curMs/1000;
    int onlineTime                = userProfile.lastLogin - userProfile.lastLogout;
    userProfile.totalPlayingTime += onlineTime;

    CBSession cba = CBSession.getInstance();
    cba.sync(Integer.toString(id), this, ar -> SessionPool.removeSession(id));
    SessionPool.removeSession(id); //T___T
  }

  public void sync(CBSession cba) {
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
        userTravel.newDay();
      }
      else {
        userFight.reLogin();
      }
    }

    if (userTravel == null)
      userTravel = UserTravel.ofDefault();
    userProfile.lastLogin   = second;
    userTravel.chosenNPCId  = -1;
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

  public void removeGroup(Handler<AsyncResult<String>> handler) {
    if (groupID > 0) { //user have group
      UserGroup group = GroupPool.getGroupFromPool(groupID);
      if (group != null) {
        int role    = group.getRole(id);
        if (role == Group.OWNER_ROLE) {
          CBGroup.getInstance().remove(Integer.toString(groupID), revRes -> {
            if (revRes.succeeded()) {
              for (Group.Member member : group.members.values()) {
                Session session = SessionPool.getSessionFromPool(member.id);
                if (session != null)
                  session.groupID = 0;
                CBMapper.getInstance().unmap(Integer.toString(member.id), ar -> {});
              }
              handler.handle(Future.succeededFuture("ok"));
            }
            else {
              handler.handle(Future.failedFuture(revRes.cause().getMessage()));
            }
          });
        }
        else {
          handler.handle(Future.failedFuture("permission_error"));
        }
      }
      else { //fuck, have valid gid but no group on group pool
        groupID = 0;
        handler.handle(Future.failedFuture("user_have_no_group"));
      }
    }
    else {
      handler.handle(Future.failedFuture("user_have_no_group"));
    }
  }

  public void createGroup(int groupType, Handler<AsyncResult<String>> handler) {
    if (groupID > 0) { //user have group
      handler.handle(Future.failedFuture("user_already_have_group"));
    }
    else if (groupID == -1) {
      handler.handle(Future.failedFuture("delay"));
    }
    else if (groupID == 0) { //user have no group
      //first unmap sid_gid if have
      String oldGid = CBMapper.getInstance().getValue(Integer.toString(id));
      if (oldGid.equals("")) { // there no sid_gid map, perfect
        UserGroup newGroup = UserGroup.of(0, id, userGameInfo.displayName, groupType);
        CBGroup.getInstance().add(Integer.toString(newGroup.id), newGroup, addRes -> {
          if (addRes.succeeded()) {
            groupID = Integer.parseInt(addRes.result());
            handler.handle(Future.succeededFuture("ok"));
          }
          else{
            handler.handle(Future.failedFuture(addRes.cause().getMessage()));
          }
        });
      }
      else {
        // try for next login
        CBGroup.getInstance().load(oldGid, loadRes -> {
          if (loadRes.succeeded()) {
            LOGGER.error("red alert: groupID zero but have sid_gid mapping and even a persistent group");
            handler.handle(Future.failedFuture("delay"));
          }
          else { //have sid_gid mapping but don't have persistent group, ok (mean member of last delete group)
            CBMapper.getInstance().unmap(Integer.toString(id), unmapRes -> {
              if (unmapRes.succeeded()) {
                UserGroup newGroup = UserGroup.of(0, id, userGameInfo.displayName, groupType);
                CBGroup.getInstance().add(Integer.toString(newGroup.id), newGroup, addRes -> {
                  if (addRes.succeeded()) {
                    groupID = Integer.parseInt(addRes.result());
                    handler.handle(Future.succeededFuture("ok"));
                  }
                  else{
                    handler.handle(Future.failedFuture(addRes.cause().getMessage()));
                  }
                });
              }
              else {
                handler.handle(Future.failedFuture("unknown_err"));
              }
            });
          }
        });
      }
    }
    else {
      handler.handle(Future.failedFuture("unknown_gid"));
    }
  }

  public void loadSessionGroup(Handler<AsyncResult<String>> handler) {
    CBMapper.getInstance().getValue(Integer.toString(id), ar -> {
      if (ar.succeeded()) { //have sid-gid mapping entry
        try {
          int gid = Integer.parseInt(ar.result());
          if (gid == -1) { //user leave or kick from some group
            groupID = -1;
            handler.handle(Future.succeededFuture("ok"));
          }
          else if (gid > 0) { //user have group
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
                  groupID = 0;
                }
                handler.handle(Future.succeededFuture("ok"));
              });
            }
          }
        }
        catch (Exception e) {
          LOGGER.error(e.getMessage());
          groupID = 0;
          handler.handle(Future.succeededFuture("ok"));
        }
      }
      else { //there no sid-gid mapping entry
        groupID = 0;
        handler.handle(Future.succeededFuture("ok"));
      }
    });
  }

  public void joinGroup(int groupID, Handler<AsyncResult<String>> handler) {
    UserGroup currentGroup = GroupPool.getGroupFromPool(groupID);
    if (currentGroup != null){
      handler.handle(Future.failedFuture("user_already_have_group"));
      return;
    }

    String oldMap = CBMapper.getInstance().getValue(Integer.toString(id));
    if (oldMap.equals("") && this.groupID == 0) { // no group, disk also agree
      UserGroup joinedGroup = GroupPool.getGroupFromPool(groupID);
      if (joinedGroup != null) {
        CBMapper.getInstance().map(Integer.toString(joinedGroup.id), Integer.toString(id), mapAr -> {
          if (mapAr.succeeded()) {
            String result = joinedGroup.processJoinGroup(this);
            if (result.equals("ok")) {
              this.groupID = joinedGroup.id;
              handler.handle(Future.succeededFuture("ok"));
            }
            else {
              //rollback
              this.groupID = 0;
              joinedGroup.removeMember(this);
              handler.handle(Future.failedFuture(result));
            }
          }
          else {
            handler.handle(Future.failedFuture("join_group_fail_mapping"));
          }
        });
      }
      else { //group not online
        CBGroup.getInstance().load(Integer.toString(groupID), loadAr -> {
          if (loadAr.succeeded()) {
            UserGroup loadedGroup = loadAr.result();
            GroupPool.addGroup(loadAr.result());

            CBMapper.getInstance().map(Integer.toString(loadedGroup.id), Integer.toString(id), mapAr -> {
              if (mapAr.succeeded()) {
                String result = loadedGroup.processJoinGroup(this);
                if (result.equals("ok")) {
                  this.groupID = loadedGroup.id;
                  handler.handle(Future.succeededFuture("ok"));
                }
                else {
                  //rollback
                  this.groupID = 0;
                  loadedGroup.removeMember(this);
                  handler.handle(Future.failedFuture(result));
                }
              }
              else {
                handler.handle(Future.failedFuture("join_group_fail_mapping"));
              }
            });
          }
          else {
            handler.handle(Future.failedFuture("group_not_found"));
          }
        });
      }
    }
    else {
      try { // -1: kick, must wait | -number: pending member,
        int oldGID = Integer.parseInt(oldMap);
        if (oldGID == -1) {
          handler.handle(Future.failedFuture("join_group_delay"));
        }
        else if (oldGID > 0) { //fuck map say valid gid, but runtime gid == 0
          LOGGER.error("alert, map value: " +  oldMap + " but runtime gid is zero sid: " + id);
          handler.handle(Future.failedFuture("join_group_fail runtime and persistent mismatch"));
        }
        else {
          handler.handle(Future.failedFuture("join_group_fail_unknown"));
        }
      }
      catch (Exception e) {
        //mal form map field, delete it
        CBMapper.getInstance().unmap(Integer.toString(id), unmapR -> {});
        handler.handle(Future.failedFuture("join_group_fail"));
      }
    }
  }

  public String kick(int memberId) {
    UserGroup group = GroupPool.getGroupFromPool(groupID);
    if (group == null)
      return "fail_no_group";
    int role = group.getRole(this.id);

    int memberRole = group.getRole(memberId);
    if (memberRole == Group.OWNER_ROLE)
      return "fail_cant_kick_owner";

    if (role == Group.OWNER_ROLE || role == Group.MOD_ROLE) {
      Session session = SessionPool.getSessionFromPool(memberId);
      if (session != null) {
        session.groupID = -1;
      }
      String result = group.kickMember(memberId);
      if (result.equals("ok")) {
        CBMapper.getInstance().mapOverride("-1", Integer.toString(memberId), ar -> {});
      }
      return result;
    }
    else {
      return "fail_no_permission";
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
        CBSession cbAccess = CBSession.getInstance();
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
