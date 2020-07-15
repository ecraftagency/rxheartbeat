package com.heartbeat.model.data;

import com.heartbeat.db.cb.CBGroup;
import com.heartbeat.model.GroupPool;
import com.heartbeat.model.Session;
import com.transport.model.Group;

import java.util.HashMap;

public class UserGroup extends Group {
  public static UserGroup of(int id, int ownerId, String ownerName, int joinType) {
    UserGroup re          = new UserGroup();
    re.id                 = id;
    re.owner              = ownerId;
    re.createTime         = (int)(System.currentTimeMillis()/1000);
    re.members            = new HashMap<>();
    re.pendingMembers     = new HashMap<>();
    re.externalInform     = "";
    re.internalInform     = "";
    re.joinType           = joinType;
    re.isChange           = false;

    Member owner          = new Member();
    owner.displayName     = ownerName;
    owner.id              = ownerId;
    owner.role            = OWNER_ROLE;
    owner.joinTime        = re.createTime;
    re.members.put(ownerId, owner);
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
    Member member = Member.of(session.id, session.userGameInfo.displayName);

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
      return "ok";
    }
    else {
      return "unknown_join_type";
    }
  }

  //user for reverse if mapping not success
  public synchronized void removeMember(Session session) {
    members.remove(session.id);
  }

  public synchronized String kickMember(int memberId) {
    if (members.get(memberId) != null) {
      members.remove(memberId);
      isChange = true;
      return "ok";
    }
    return "member_not_found";
  }
}