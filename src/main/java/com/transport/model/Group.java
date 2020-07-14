package com.transport.model;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Group {
  public static final int OWNER_ROLE  = 0;
  public static final int MOD_ROLE    = 1;
  public static final int USER_ROLE   = 2;

  public static final int AUTO_JOIN       = 0;
  public static final int REQUEST_JOIN    = 1;

  public int                      id;
  public int                      createTime;
  public int                      owner;
  public Map<Integer, Member>     members;
  public Map<Integer, Member>     pendingMembers;
  public String                   externalInform;
  public String                   internalInform;
  public int                      joinType;
  public String                   docType           = "group";
  public transient AtomicInteger  refCount;

  public void close() {

  }

  public static class Member {
    public int      id;
    public String   displayName;
    public int      joinTime;
    public int      role;
  }
}