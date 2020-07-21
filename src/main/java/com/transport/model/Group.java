package com.transport.model;
import com.statics.GroupMissionData;

import java.util.*;

@SuppressWarnings("unused")
public class Group {
  public static final int OWNER_ROLE          = 0;
  public static final int MOD_ROLE            = 1;
  public static final int USER_ROLE           = 2;

  public static final int AUTO_JOIN           = 0;
  public static final int REQUEST_JOIN        = 1;

  public static final int KICK_EXPIRE         = 2; //minute

  public static final int GROUP_ID_TYPE_NONE    = 0;
  public static final int GROUP_ID_TYPE_KICK    = -1;
  public static final int GROUP_ID_TYPE_REMOVE  = -2;

  public static final int MAX_GROUP_MEMBER    = 25;
  public static final int INTERNAL_INFORM     = 0;
  public static final int EXTERNAL_INFORM     = 1;

  public int                      id;
  public int                      createTime;
  public String                   name;
  public int                      owner;
  public Map<Integer, Member>     members;
  public Map<Integer, Member>     pendingMembers;
  public String                   externalInform;
  public String                   internalInform;
  public int                      joinType;
  public String                   docType           = "group";
  public transient boolean        isChange;

  //runtime data
  public Map<Integer, GroupMissionData.GroupMission>  missions;
  public Map<Integer, Integer>                        missionHitMember;
  public int            missionStartDate;
  public int            missionEndDate;
  public String         strStartDate;
  public String         strEndDate;

  public void close() {

  }

  public static class Mission {
    public int id;
    public int count;
    public boolean claim;
    public static Mission of(int id, int count) {
      Mission res = new Mission();
      res.id = id;
      res.count = count;
      res.claim = true;
      return res;
    }
    public void resetMission() {
      count = 0;
      claim = false;
    }
  }

  public static class Member {
    public int      id;
    public String   displayName;
    public int      joinTime;
    public int      titleId;
    public int      role;
    public long     totalCrt;
    public long     totalPerf;
    public long     totalAttr;
    public int      avatarId;
    public int      gender;
    public int      cas;
    public Map<Integer, Mission> missions;

    public static   Member of(int id, String displayName) {
      Member member       = new Member();
      member.id           = id;
      member.displayName  = displayName;
      member.role         = USER_ROLE;
      member.joinTime     = (int)(System.currentTimeMillis());
      member.cas          = 0;
      member.missions     = new HashMap<>();
      if (GroupMissionData.missionMap != null) {
        for (GroupMissionData.GroupMission gm : GroupMissionData.missionMap.values()) {
          Mission mission = Mission.of(gm.id, 0);
          member.missions.put(gm.id, mission);
        }
      }

      return member;
    }
  }

  public static boolean isValidGid(int gid) {
    return gid > 0;
  }
}