package com.heartbeat.model.data;

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
//    CBGroup.getInstance().sync(Integer.toString(id), this, ar -> GroupPool.removeGroup(id));
//    GroupPool.removeGroup(id); //T_____________T im crazy
  }

  public int getRole(int memberId) {
    Member member = members.get(memberId);
    if (member != null)
      return member.role;
    return -1;
  }
}