package com.heartbeat.model.data;

import com.heartbeat.db.cb.CBGroup;
import com.heartbeat.model.GroupPool;
import com.heartbeat.model.Session;
import com.transport.model.Group;

import java.util.HashMap;

public class UserGroup extends Group {
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

    Member owner          = new Member();
    owner.displayName     = session.userGameInfo.displayName;
    owner.id              = session.id;
    owner.role            = OWNER_ROLE;
    owner.joinTime        = re.createTime;
    owner.titleId         = session.userGameInfo.titleId;
    owner.totalCrt        = session.userIdol.getTotalCreativity();
    owner.totalPerf       = session.userIdol.getTotalPerformance();
    owner.totalAttr       = session.userIdol.getTotalAttractive();
    owner.gender          = session.userGameInfo.gender;
    owner.avatarId        = session.userGameInfo.avatar;
    owner.productionCount = 0;
    owner.gameshowCount   = 0;
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
    member.productionCount  = 0;
    member.gameshowCount    = 0;


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
}