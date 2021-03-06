package com.transport.model;

import java.util.HashMap;

public class Profile {
  public String       result;       //Const.ResultCode
  public String       strUserId;
  public String       userName;
  public int          banTo;
  public String       banReason;
  public String       notMsg;
  public String       facebookId;
  public boolean      isRegister;
  public int          serverVersionInt;
  public String       clientSource;
  public String       buildSource;
  public String       osPlatform;
  public String       clientVersion;
  public String       jwtToken;
  public String       displayName;
  public int          avatar;
  public int          gender;
  public int          lastLogin;
  public int          registerAt;

  public HashMap<String, Boolean> gameFunctions;
  public HashMap<String, String>  gameTuning;
}
