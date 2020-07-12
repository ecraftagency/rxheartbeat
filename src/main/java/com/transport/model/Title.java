package com.transport.model;

import com.heartbeat.db.impl.CBBadge;

public class Title {
  public String id;
  public String username;
  public String manifesto;
  public static Title of(String id, String username, String manifesto) {
    Title title = new Title();
    title.id = id;
    title.username = username;
    title.manifesto = manifesto;
    return title;
  }
}
