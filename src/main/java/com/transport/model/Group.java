package com.transport.model;
import java.util.*;

public class Group {
  public static final int OWNER_ROLE          = 0;
  public static final int MOD_ROLE            = 1;
  public static final int USER_ROLE           = 2;

  public static final int AUTO_JOIN           = 0;
  public static final int REQUEST_JOIN        = 1;

  public static final int KICK_EXPIRE         = 2; //minute

  public static final int GROUP_ID_TYPE_NONE  = 0;
  public static final int GROUP_ID_TYPE_KICK  = -1;

  public static final int MAX_GROUP_MEMBER    = 25;
  public static final int INTERNAL_INFORM     = 0;
  public static final int EXTERNAL_INFORM     = 1;

  public int                      id;
  public int                      createTime;
  public int                      owner;
  public Map<Integer, Member>     members;
  public Map<Integer, Member>     pendingMembers;
  public String                   externalInform;
  public String                   internalInform;
  public int                      joinType;
  public String                   docType           = "group";
  public transient boolean        isChange;


  public void close() {

  }

  public static class Member {
    public int      id;
    public String   displayName;
    public int      joinTime;
    public int      role;

    public static   Member of(int id, String displayName) {
      Member member = new Member();
      member.id = id;
      member.displayName = displayName;
      member.role = USER_ROLE;
      member.joinTime = (int)(System.currentTimeMillis());
      return member;
    }
  }

  public static boolean isValidGid(int gid) {
    return gid > 0;
  }
}