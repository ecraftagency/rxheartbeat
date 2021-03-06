package com.heartbeat.model.data;

import com.common.LOG;
import com.common.Msg;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.heartbeat.db.cb.CBGroup;
import com.heartbeat.effect.EffectHandler;
import com.heartbeat.effect.EffectManager;
import com.heartbeat.model.GroupPool;
import com.heartbeat.model.Session;
import com.heartbeat.scheduler.ExtendEventInfo;
import com.statics.GroupMissionData;
import com.transport.model.Group;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.statics.GroupMissionData.*;
import static com.common.Constant.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("unused")
public class UserGroup extends Group {
  public static final long MAX_GROUP_MEMBER = 25;

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
    re.evt2MS             = new HashMap<>();

    if (GroupMissionData.missionMap != null) {
      for (GroupMissionData.GroupMission gm : GroupMissionData.missionMap.values()) {
        re.evt2MS.putIfAbsent(gm.id, GroupMissionState.of(gm.id, 0, 0));
      }
    }

    Member owner          = Member.of(session.id, session.userGameInfo.displayName);
    owner.role            = OWNER_ROLE;
    owner.joinTime        = re.createTime;
    owner.titleId         = session.userGameInfo.titleId;
    owner.totalCrt        = session.userIdol.totalCrt();
    owner.totalPerf       = session.userIdol.totalPerf();
    owner.totalAttr       = session.userIdol.totalAttr();
    owner.gender          = session.userGameInfo.gender;
    owner.avatarId        = session.userGameInfo.avatar;
    re.members.put(session.id, owner);
    return re;
  }

  public void reEvaluateEventState() {
    for (ExtendEventInfo eei : GROUP_EVENT.evtMap.values()) {
      evt2MS.computeIfPresent(eei.eventId, (k,v) -> {
        if (v.cas != eei.startTime) {
          v.cas = eei.startTime;
          v.totalCM = 0;
        }
        isChange = true;
        return v;
      });
    }
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
    if (members.size() >= MAX_GROUP_MEMBER) {
      return Msg.map.getOrDefault(Msg.GROUP_FULL_SEAT, "group_full_seat");
    }

    if (joinType != AUTO_JOIN && joinType != REQUEST_JOIN)
      return Msg.map.getOrDefault(Msg.INVALID_GROUP_TYPE, "unknown_join_type");

    Member member           = Member.of(session.id, session.userGameInfo.displayName);
    member.titleId          = session.userGameInfo.titleId;
    member.totalCrt         = session.userIdol.totalCrt();
    member.totalPerf        = session.userIdol.totalPerf();
    member.totalAttr        = session.userIdol.totalAttr();
    member.avatarId         = session.userGameInfo.avatar;
    member.gender           = session.userGameInfo.gender;

    if (joinType == AUTO_JOIN) {
      members.put(member.id, member);
      session.groupID = id;
      isChange        = true;
      return "ok";
    }
    else {
      pendingMembers.put(member.id, member);
      isChange = true;
      return Msg.map.getOrDefault(Msg.GROUP_JOIN_PENDING, "group_join_pending");
    }
  }

  public synchronized String kickMember(int memberId) {
    if (members.get(memberId) != null) {
      members.remove(memberId);
      isChange = true;
      return "ok";
    }
    return Msg.map.getOrDefault(Msg.MEMBER_NOT_FOUND, "member_not_found");
  }

  public synchronized void removePendingMember(int memberId) {
    pendingMembers.remove(memberId);
  }

  public synchronized String approveGroup(int memberId, String action) {
    switch (action) {
      case "approve":
        if (members.size() >= MAX_GROUP_MEMBER)
          return Msg.map.getOrDefault(Msg.GROUP_FULL_SEAT, "group_full_seat");

        Member member = pendingMembers.get(memberId);
        if (member == null)
          return Msg.map.getOrDefault(Msg.MEMBER_NOT_FOUND, "member_not_found");
        members.put(memberId, member);
        pendingMembers.remove(memberId);
        isChange = true;
        return "ok";
      case "refuse":
        pendingMembers.remove(memberId);
        isChange = true;
        return "ok";
      case "refuse_all":
        pendingMembers.clear();
        isChange = true;
        return "ok";
      default:
        return Msg.map.getOrDefault(Msg.UNKNOWN_ERR, "unknown_err");
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

    return Msg.map.getOrDefault(Msg.MEMBER_NOT_FOUND, "member_not_found");
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
    return Msg.map.getOrDefault(Msg.UNKNOWN_ERR, "unknown_err");
  }

  public void addRecord(Session session, int missionId, int amount, boolean add) {
    ExtendEventInfo ei = GROUP_EVENT.evtMap.get(missionId);
    if (ei == null || ei.startTime <= 0) {
      return;
    }

    Member member = members.get(session.id);
    if (member == null) {
      LOG.globalException("group_mission: member not found,", "memberID: ", session.id, "groupId: ", id);
      return;
    }

    Mission mission = member.missions.get(missionId);
    if (mission == null) {
      LOG.globalException("group_mission: mission not found,", "memberID: ", session.id, "groupId: ", id, "missionId ", missionId);
      return;
    }

    if (member.cas != ei.startTime) {
      member.cas = ei.startTime;
      mission.resetMission();
    }

    if (add) {
      mission.count += amount;
    }
    else {
      mission.count = amount;
    }
    isChange = true;
  }

  public long calcTotalClaimedMember(int missionId) {
    return members.values().stream().filter(m -> m.missions.get(missionId) != null && m.missions.get(missionId).claim).count();
  }

  public Map<Integer, Integer> calcMissionHitMember() {
    Map<Integer, Integer> res = new HashMap<>();

    try {
      for (GroupMission gm : missionMap.values()) {
        ExtendEventInfo ei = GROUP_EVENT.evtMap.get(gm.id);
        if (ei == null)
          continue;

        int cas = ei.startTime;

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
          if (mission.count >= gm.hitCount)
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
      return Msg.map.getOrDefault(Msg.DTO_DATA_NOT_FOUND, "Không tìm thấy dữ liệu, vui lòng thử lại");

    Member member = members.get(session.id);
    if (member == null)
      return Msg.map.getOrDefault(Msg.MEMBER_NOT_FOUND, "Không tìm thấy dữ liệu thành viên");

    ExtendEventInfo ei = GROUP_EVENT.evtMap.get(missionId);
    if (ei == null)
      return Msg.map.getOrDefault(Msg.EVENT_NOT_FOUND, "Không tìm thấy dữ liệu sự kiện");

    Mission mission = member.missions.get(missionId);
    if (mission == null)
      return Msg.map.getOrDefault(Msg.MISSION_NOT_FOUND, "Không tìm thấy dữ liệu nhiệm vụ công ty");

    if (ei.startTime <= 0     ||
        ei.endTime <= 0       ||
        second < ei.startTime ||
        second > ei.endTime ||
        mission.claim) {
      return Msg.map.getOrDefault(Msg.GROUP_CLAIM_TIME_OUT, "Số lượng phần thưởng đã hết");
    }

//    if (calcTotalClaimedMember(missionId) >= MAX_GROUP_MEMBER)
//      return Msg.map.getOrDefault(Msg.TIMEOUT_CLAIM, "group_claim_timeout");
    GroupMissionState missionState = evt2MS.get(missionId);
    if (missionState == null || missionState.totalCM >= MAX_GROUP_MEMBER)
      return Msg.map.getOrDefault(Msg.GROUP_CLAIM_TIME_OUT, "Số lượng phần thưởng đã hết");

    if (!checkPreviousClaim(member, missionId))
      return Msg.map.getOrDefault(Msg.PREV_CLAIM, "Bạn phải hoàn thành/ nhận phần thưởng của NV trước");

    Map<Integer, Integer> hitMembers = calcMissionHitMember();
    Integer missionHitM = hitMembers.get(evt.id);
    if (missionHitM == null)
      return Msg.map.getOrDefault(Msg.MISSION_NOT_FOUND, "Không tìm thấy dữ liệu nhiệm vụ công ty");

    if (missionHitM < evt.hitMember) {//
      return Msg.map.getOrDefault(Msg.INSUFFICIENT_CLAIM, "Bạn chưa đạt yêu cầu nhận thưởng"); // :D, chưa đủ tuổi
    }

    session.effectResults.clear();
    EffectHandler.ExtArgs extArgs = EffectHandler.ExtArgs.of();

    List<List<Integer>> rewards = evt.getRewardPack(ei.rewardPack);

    for (List<Integer> reward : rewards) {
        EffectManager.inst().handleEffect(extArgs, session, reward);
    }

    mission.claim = true;
    missionState.totalCM++;
    return "ok";
  }

  //todo must finish prev mission before claim current mission
  private boolean checkPreviousClaim(Member member, int missionId) {
    for (int i = 1; i < missionId; i++) {
      Mission mission = member.missions.get(i);
      if (mission != null)
        if (!mission.claim)
          return false;
    }
    return true;
  }

  public void reBalance() {
    for (Member member : members.values()) {
      member.reBalance();
    }
    if (evt2MS == null)
      evt2MS = new HashMap<>();
    if (GroupMissionData.missionMap != null) {
      for (GroupMissionData.GroupMission gm : GroupMissionData.missionMap.values()) {
        evt2MS.putIfAbsent(gm.id, GroupMissionState.of(gm.id, 0, 0));
      }
    }
  }

  public void updateMemberInfo(Session session, String updateFields) {
    Member member = members.get(session.id);
    if (member == null)
      return;
    if ("totalProperty".equals(updateFields)) {
      member.totalCrt = session.userIdol.totalCrt();
      member.totalAttr = session.userIdol.totalAttr();
      member.totalPerf = session.userIdol.totalPerf();
      isChange = true;
    }
  }
}