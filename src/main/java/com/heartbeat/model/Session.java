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
import com.statics.EventInfo;
import com.transport.EffectResult;
import com.transport.LoginRequest;
import com.transport.model.Group;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import static com.heartbeat.common.Constant.*;

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
  transient public int      groupID;
  //persistent data
  public UserProfile        userProfile;
  public UserGameInfo       userGameInfo;
  public UserProduction     userProduction;
  public UserIdol           userIdol;
  public UserInventory      userInventory;
  public UserFight          userFight;
  public UserTravel         userTravel;
  public UserDailyMission   userDailyMission;
  public UserAchievement    userAchievement;
  public UserMission        userMission;
  public UserRollCall       userRollCall;
  public UserEvent          userEvent;

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
    userDailyMission      = UserDailyMission.ofDefault();
    userAchievement       = UserAchievement.ofDefault();
    userMission           = UserMission.ofDefault();
    userRollCall          = UserRollCall.ofDefault();
    userEvent             = UserEvent.ofDefault();

    //todo reBalance
    userProduction.reBalance(userIdol.getTotalCreativity());

    long curMs            = System.currentTimeMillis();
    userProduction.updateProduction(this, curMs);
    userGameInfo.updateUserMedia(curMs);
    userAchievement.addAchieveRecord(Constant.ACHIEVEMENT.LOGIN_ACHIEVEMENT, 1);
    userAchievement.addAchieveRecord(Constant.ACHIEVEMENT.IDOL_ACHIEVEMENT, 1);
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

    //todo null check, consistency rebalance
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
      userRollCall          = UserRollCall.ofDefault();
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

    userIdol.reBalance();
    userGameInfo.reBalance();
    userInventory.reBalance();
    userProduction.reBalance(this.userIdol.getTotalCreativity());

    userProfile.lastLogin   = second;
    userTravel.chosenNPCId  = -1;
    lastHearBeatTime        = second;

    int dayDiff;
    if (userProfile.lastLogin > 0) {
      userProfile.loginCount++;
      dayDiff = Utilities.dayDiff(second, userProfile.lastLogin);
      if (dayDiff > 0) {
        userFight.newDay();
        userTravel.newDay();
        userDailyMission.newDay();
        userGameInfo.newDay();
        userInventory.newDay();

        userAchievement.addAchieveRecord(Constant.ACHIEVEMENT.LOGIN_ACHIEVEMENT, 1);
      }
      else {
        userFight.reLogin();
      }

      //todo critical
      int deltaTime = second - userProfile.lastLogin;

      //record time spent event
      long timeSpent = deltaTime > userGameInfo.time ? userGameInfo.time : deltaTime;
      userEvent.addEventRecord(EVENT.TIME_SPEND_EVT_ID, timeSpent, second);

      userGameInfo.time -= deltaTime;
      if (userGameInfo.time < 0)
        userGameInfo.time = 0;
    }
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
                //todo sync
                CBMapper.getInstance().mapOverride(Integer.toString(Group.GROUP_ID_TYPE_REMOVE),
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
          handler.handle(Future.failedFuture("permission_error"));
        }
      }
      else { //fuck, have valid gid but no group on group pool
        groupID = Group.GROUP_ID_TYPE_NONE;
        handler.handle(Future.failedFuture("user_have_no_group"));
      }
    }
    else {
      handler.handle(Future.failedFuture("user_have_no_group"));
    }
  }

  public void createGroup(int groupType, String name, String externalInform, String internalInform, Handler<AsyncResult<String>> handler) {
    if (userGameInfo.time < Constant.GROUP.CREATE_GROUP_TIME_COST) {
      handler.handle(Future.failedFuture("insufficient_time"));
      return;
    }
    if (Group.isValidGid(groupID)) { //user have group
      handler.handle(Future.failedFuture("user_already_have_group"));
    }
    else if (groupID == Group.GROUP_ID_TYPE_KICK) {
      handler.handle(Future.failedFuture("delay"));
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
            userGameInfo.useTime(this, Constant.GROUP.CREATE_GROUP_TIME_COST);
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
                UserGroup newGroup = UserGroup.of(Group.GROUP_ID_TYPE_NONE,
                        this, groupType, externalInform, internalInform, name);
                CBGroup.getInstance().add(Integer.toString(newGroup.id), newGroup, addRes -> {
                  if (addRes.succeeded()) {
                    groupID = Integer.parseInt(addRes.result());
                    userGameInfo.useTime(this, Constant.GROUP.CREATE_GROUP_TIME_COST);
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
          LOGGER.error(e.getMessage());
          groupID = Group.GROUP_ID_TYPE_NONE;
          handler.handle(Future.succeededFuture("ok"));
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
      handler.handle(Future.failedFuture("user_already_have_group"));
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
        else if (result.equals("pending")) {
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
            else if (result.equals("pending")) {
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
            handler.handle(Future.failedFuture("group_not_found"));
          }
        });
      }
    }
    else {
      try {
        int oldGID = Integer.parseInt(oldMap);
        if (oldGID == Group.GROUP_ID_TYPE_KICK) {
          handler.handle(Future.failedFuture("join_group_delay"));
        }
        else if (Group.isValidGid(oldGID)) { //fuck map say valid gid, but runtime gid == 0
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
      return "fail_no_permission";
    }
  }

  public String leaveGroup() {
    if (Group.isValidGid(groupID)) {
      UserGroup group = GroupPool.getGroupFromPool(groupID);
      if (group == null) { //todo critical
        String err = String.format("leave_group_fail_[sid:%d,gid:%d,runtime:%s,members:%s]",id, groupID, "no", "_");
        LOGGER.error(err);
        return err;
      }

      if (group.getRole(id) == Group.OWNER_ROLE) {
        return "leave_group_fail_admin";
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
        LOGGER.error(err);
        return err;
      }
    }
    else {
      return "user_have_no_group";
    }
  }

  public String approveMember(int memberId, String action) {
    //check online group
    UserGroup group = GroupPool.getGroupFromPool(this.groupID);
    if (group == null) {
      String err = String.format("user_have_no_group[sid:%d,gid:%d,runtime:%s]",id, groupID, "no");
      LOGGER.error(err);
      return err;
    }

    if (memberId == this.id) {
      return "approve_fail_malform";
    }

    //check role
    int role = group.getRole(this.id);
    if (role != Group.OWNER_ROLE && role != Group.MOD_ROLE) {
      return "approve_fail_permission";
    }

    //check member condition
    String state = CBMapper.getInstance().getValue(Integer.toString(memberId));
    if (!state.equals("")) {
      try {
        int currentGID = Integer.parseInt(state);
        if (currentGID == -1) {
          group.removePendingMember(memberId);
          return "approve_fail_delay";
        }
        else if (Group.isValidGid(currentGID)) {
          group.removePendingMember(memberId);
          return "approve_fail_user_already_have_group";
        }
        else {
          group.removePendingMember(memberId);
          return "approve_fail_unknown";
        }

      }
      catch (Exception e) {
        //malform
        LOGGER.error(String.format("malform_sid_gid_index[sid:%d,gid:%s]",memberId, state));
        CBMapper.getInstance().unmap(Integer.toString(memberId), ar -> {});
        group.removePendingMember(memberId);
        return "approve_fail_unknown";
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
      return "group_not_found";

    int myRole = group.getRole(this.id);
    if (myRole != Group.OWNER_ROLE)
      return "set_role_fail_permission]";

    if ((newRole == Group.MOD_ROLE && group.modCount() < 2) || newRole == Group.USER_ROLE) {
      return group.setRole(memberId, newRole);
    }
    return "set_role_fail";
  }

  public String setGroupInform(int type, String informMsg) {
    UserGroup group = GroupPool.getGroupFromPool(this.groupID);

    if (group == null)
      return "group_not_found";

    int role = group.getRole(this.id);

    if (role == Group.OWNER_ROLE || role == Group.MOD_ROLE) {
      return group.changeInform(informMsg, type);
    }
    else
      return "set_inform_fail_permission";
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
