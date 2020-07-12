package com.transport;

import com.heartbeat.db.impl.CBBadge;
import com.transport.model.*;

import java.util.List;

public class ExtMessage {
  public String             cmd;
  public String             msg;
  public Data               data;
  public String             group;
  public int                serverTime;
  public List<EffectResult> effectResults;

  public static class Data {
    public GameInfo   gameInfo;
    public Idols      idols;
    public Production production;
    public Profile    profile;
    public Inventory  inventory;
    public Fight      fight;
    public Travel     travel;
    public Title      title;
  }

  public static ExtMessage idol() {
    ExtMessage result = new ExtMessage();
    result.cmd = "";
    result.msg = "ok";
    result.group = "idol";
    result.data = new Data();
    return result;
  }

  public static ExtMessage fight() {
    ExtMessage result = new ExtMessage();
    result.cmd = "";
    result.msg = "ok";
    result.group = "fight";
    result.data = new Data();
    return result;
  }

  public static ExtMessage profile() {
    ExtMessage result = new ExtMessage();
    result.cmd = "";
    result.msg = "ok";
    result.group = "profile";
    result.data = new Data();
    return result;
  }

  public static ExtMessage production() {
    ExtMessage result = new ExtMessage();
    result.cmd = "";
    result.msg = "ok";
    result.group = "production";
    result.data = new Data();
    return result;
  }

  public static ExtMessage system() {
    ExtMessage result = new ExtMessage();
    result.cmd = "";
    result.msg = "ok";
    result.group = "system";
    result.data = new Data();
    return result;
  }

  public static ExtMessage item() {
    ExtMessage result = new ExtMessage();
    result.cmd = "";
    result.msg = "ok";
    result.group = "item";
    result.data = new Data();
    return result;
  }

  public static ExtMessage media() {
    ExtMessage result = new ExtMessage();
    result.cmd = "";
    result.msg = "ok";
    result.group = "media";
    result.data = new Data();
    return result;
  }

  public static ExtMessage travel() {
    ExtMessage result = new ExtMessage();
    result.cmd = "";
    result.msg = "ok";
    result.group = "travel";
    result.data = new Data();
    return result;
  }

  public static ExtMessage title() {
    ExtMessage result = new ExtMessage();
    result.cmd = "";
    result.msg = "ok";
    result.group = "title";
    result.data = new Data();
    return result;
  }

  public void reset() {
    cmd = "";
    data.profile = null;
    data.inventory = null;
    data.fight = null;
    data.idols = null;
    data.gameInfo = null;
    data.production = null;
    msg = "";
    group = "";
    serverTime = 0;
  }
}