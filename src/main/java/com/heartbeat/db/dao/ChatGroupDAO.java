package com.heartbeat.db.dao;

import com.transport.NetaGroup;

import java.util.Map;

public class ChatGroupDAO {
  public int lastSync;
  public Map<String, NetaGroup> chatGroups;

  public static ChatGroupDAO of(Map<String, NetaGroup> chatGroups) {
    ChatGroupDAO cg = new ChatGroupDAO();
    cg.chatGroups   = chatGroups;
    cg.lastSync     = (int)(System.currentTimeMillis()/1000);
    return cg;
  }
}