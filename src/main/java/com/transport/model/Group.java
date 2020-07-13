package com.transport.model;

import java.util.List;

public class Group {
  public int id;
  public int ownerId;
  public List<Integer> moderator;
  public int createTime;

  public static class Member {
    public int id;
    public String displayName;
    public int joinTime;
    public int role;
  }
}