package com.heartbeat.model.data;

import com.heartbeat.common.Constant;
import com.heartbeat.db.cb.CBGroup;
import com.heartbeat.effect.EffectHandler;
import com.heartbeat.effect.EffectManager;
import com.heartbeat.model.GroupPool;
import com.heartbeat.model.Session;
import com.statics.GroupMissionData;
import com.transport.model.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.statics.GroupMissionData.*;
import static com.heartbeat.common.Constant.*;

public class UserGroup extends Group {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserGroup.class);

  public static UserGroup of(int id, Session session, int joinType, String externalInform, String internalInform, String name) {
    UserGroup re          = new UserGroup();
    re.id                 = id;
    re.owner              = session.id;
    re.createTime         = (int)(System.currentTimeMillis()/1000);
    re.members            = new HashMap<>();
    re.pendingMembers     = new HashMap<>();
    re.externalInform     = "";
    re.internalInform     = "";
    re.joinType           = joinType;
    re.isChange           = false;
    re.name               = name;
    re.externalInform     = externalInform;
    re.internalInform     = internalInform;

    Member owner          = Member.of(session.id, session.userGameInfo.displayName);
    owner.role            = OWNER_ROLE;
    owner.joinTime        = re.createTime;
    owner.titleId         = session.userGameInfo.titleId;
    owner.totalCrt        = session.userIdol.getTotalCreativity();
    owner.totalPerf       = session.userIdol.getTotalPerformance();
    owner.totalAttr       = session.userIdol.getTotalAttractive();
    owner.gender          = session.userGameInfo.gender;
    owner.avatarId        = session.userGameInfo.avatar;
    re.members.put(session.id, owner);
    return re;
  }

  @Override
  public void close() {
    CBGroup.getInstance().sync(Integer.toString(id), this, ar -> GroupPool.removeGroup(id));
  }

  public int getRole(int memberId) {
    Member member = members.get(memberId);
    if (member != null)
      return member.role;
    return -1;
  }

  public synchronized String processJoinGroup(Session session) {
    Member member           = Member.of(session.id, session.userGameInfo.displayName);
    member.titleId          = session.userGameInfo.titleId;
    member.totalCrt         = session.userIdol.getTotalCreativity();
    member.totalPerf        = session.userIdol.getTotalPerformance();
    member.totalAttr        = session.userIdol.getTotalAttractive();
    member.avatarId         = session.userGameInfo.avatar;
    member.gender           = session.userGameInfo.gender;


    if (members.size() >= MAX_GROUP_MEMBER) {
      return "group_full_seat";
    }

    if (joinType == AUTO_JOIN) {
      members.put(member.id, member);
      session.groupID = id;
      isChange        = true;
      return "ok";
    }
    else if (joinType == REQUEST_JOIN) {
      pendingMembers.put(member.id, member);
      isChange = true;
      return "pending";
    }
    else {
      return "unknown_join_type";
    }
  }

  public synchronized String kickMember(int memberId) {
    if (members.get(memberId) != null) {
      members.remove(memberId);
      isChange = true;
      return "ok";
    }
    return "member_not_found";
  }

  public synchronized void removePendingMember(int memberId) {
    pendingMembers.remove(memberId);
  }

  public synchronized String approveGroup(int memberId, String action) {
    if (action.equals("approve")) {
      if (members.size() >= 25)
        return "group_full_seat";

      Member member = pendingMembers.get(memberId);
      if (member == null)
        return "approve_fail_member_not_found";
      members.put(memberId, member);
      pendingMembers.remove(memberId);
      isChange = true;
      return "ok";
    }
    else if(action.equals("refuse")) {
      pendingMembers.remove(memberId);
      isChange = true;
      return "ok";
    }
    else {
      return "approve_fail_unknown_action";
    }
  }

  public synchronized long modCount() {
    return members.values().stream().filter(e -> e.role == Group.MOD_ROLE).count();
  }

  public synchronized String setRole(int memberId, int newRole) {
    Member member = members.get(memberId);
    if (member != null) {
      member.role = newRole;
      isChange = true;
      return "ok";
    }

    return "member_not_found";
  }

  public synchronized String changeInform(String informMsg, int type) {
    if (type == INTERNAL_INFORM) {
      this.internalInform = informMsg;
      isChange = true;
      return "ok";
    }
    if (type == EXTERNAL_INFORM) {
      this.externalInform = informMsg;
      isChange = true;
      return "ok";
    }
    return "set_inform_fail";
  }

  public void addRecord(Session session, int cas, int missionId) {
    Member member = members.get(session.id);
    if (member == null) {
      LOGGER.error("member_not_found");
      return;
    }
    Mission mission = member.missions.get(missionId);
    if (mission == null) {
      LOGGER.error("mission_not_found");
      return;
    }

    if (member.cas != cas) {
      member.cas = cas;
      mission.resetMission();
    }
    mission.count++;
    isChange = true;
  }

  public Map<Integer, Integer> calcMissionHitMember() {
    Map<Integer, Integer> res = new HashMap<>();

    try {
      int cas = Constant.GROUP.missionStart;

      for (GroupMission gm : missionMap.values()) {
        int hitMember = 0;
        for (Member member : members.values()) {
          if (member.cas != cas) {
            member.cas = cas;
            for (Mission mission : member.missions.values())
              mission.resetMission();
          }
          Mission mission = member.missions.get(gm.id);
          if (mission == null)
            continue;
          if (mission.count > gm.hitCount)
            hitMember++;
        }
        res.put(gm.id, hitMember);
      }
    }
    catch (Exception e) {
      res.clear();
    }

    this.missionHitMember = res;
    return res;
  }

  public String claimReward(Session session, int missionId, int second) {
    GroupMissionData.GroupMission evt = missionMap.get(missionId);
    if (evt == null)
      return "mission_not_found";

    Member member = members.get(session.id);
    if (member == null)
      return "member_not_found";

    Mission mission = member.missions.get(missionId);
    if (mission == null)
      return "mission_not_found";

    if (GROUP.missionStart <= 0     ||
        GROUP.messionEnd <= 0       ||
        second < GROUP.missionStart ||
        second > GROUP.messionEnd   ||
        mission.claim) {
      return "claim_reward_time_out";
    }

    Map<Integer, Integer> hitMembers = calcMissionHitMember();
    Integer missionHitM = hitMembers.get(evt.id);
    if (missionHitM == null)
      return "mission_not_found";

    if (missionHitM < evt.hitMember) {//
      return "mission_impossible"; // :D, chưa đủ tuổi
    }

    session.effectResults.clear();
    EffectHandler.ExtArgs extArgs = EffectHandler.ExtArgs.of(0, 0, "");

    List<Integer> rewardFormat = Arrays.asList(100,0,1,0);
    for (Integer item : evt.gift) {
      rewardFormat.set(1, item);
        EffectManager.inst().handleEffect(extArgs, session, rewardFormat);
    }

    mission.claim = true;

    return "ok";
  }
}