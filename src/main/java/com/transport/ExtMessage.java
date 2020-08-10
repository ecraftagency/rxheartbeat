package com.transport;

import com.transport.model.*;

import java.util.List;

public class ExtMessage {
  public String             cmd;
  public String             msg;
  public Data               data;
  public String             group;
  public int                serverTime;
  public long               userRemainTime;
  public boolean            timeChange;
  public List<EffectResult> effectResults;

  public static class Data {
    public GameInfo     gameInfo;
    public Idols        idols;
    public Production   production;
    public Profile      profile;
    public Inventory    inventory;
    public Fight        fight;
    public Travel       travel;
    public Title        title;
    public Group        group;
    public DailyMission dailyMission;
    public Achievement  achievement;
    public Mission      mission;
    public RollCall     rollCall;
    public Event        event;
    public Ranking      ranking;
    public int          currentGroupState;
    public String       extObj;
  }

  public static ExtMessage leaderBoard() {
    ExtMessage result = new ExtMessage();
    result.cmd = "";
    result.msg = "ok";
    result.group = "ldb";
    result.data = new Data();
    return result;
  }

  public static ExtMessage ranking() {
    ExtMessage result = new ExtMessage();
    result.cmd = "";
    result.msg = "ok";
    result.group = "ranking";
    result.data = new Data();
    return result;
  }

  public static ExtMessage rollCall() {
    ExtMessage result = new ExtMessage();
    result.cmd = "";
    result.msg = "ok";
    result.group = "rollcall";
    result.data = new Data();
    return result;
  }

  public static ExtMessage mission() {
    ExtMessage result = new ExtMessage();
    result.cmd = "";
    result.msg = "ok";
    result.group = "mission";
    result.data = new Data();
    return result;
  }

  public static ExtMessage event() {
    ExtMessage result = new ExtMessage();
    result.cmd = "";
    result.msg = "ok";
    result.group = "event";
    result.data = new Data();
    return result;
  }

  public static ExtMessage achievement() {
    ExtMessage result = new ExtMessage();
    result.cmd = "";
    result.msg = "ok";
    result.group = "achievement";
    result.data = new Data();
    return result;
  }

  public static ExtMessage daily_mission() {
    ExtMessage result = new ExtMessage();
    result.cmd = "";
    result.msg = "ok";
    result.group = "daily_mission";
    result.data = new Data();
    return result;
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

  public static ExtMessage group() {
    ExtMessage result = new ExtMessage();
    result.cmd = "";
    result.msg = "ok";
    result.group = "group";
    result.data = new Data();
    return result;
  }

  public void reset() {
    cmd             = "";
    msg             = "";
    group           = "";
    serverTime      = 0;
    userRemainTime  = 0;
    timeChange      = false;
    effectResults   = null;

    data.gameInfo           = null;
    data.idols              = null;
    data.production         = null;
    data.profile            = null;
    data.inventory          = null;
    data.fight              = null;
    data.travel             = null;
    data.title              = null;
    data.group              = null;
    data.dailyMission       = null;
    data.achievement        = null;
    data.mission            = null;
    data.rollCall           = null;
    data.event              = null;
    data.ranking            = null;
    data.currentGroupState  = 0;
    data.extObj             = "";
  }
}