package com.heartbeat.model.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.heartbeat.common.Constant;
import com.heartbeat.common.Utilities;
import com.statics.TextData;

import java.util.HashSet;

@SuppressWarnings("unused")
public class UserProfile {
  public String           password;
  public int              registerAt;
  public int              firstLogin;
  public int              lastLogin;
  public int              lastLogout;
  public int              registerVer;
  public int              loginToday; // login count today
  public int              dailyCount; // login count day
  public int              loginCount; // login count day
  public int              onlineTime; // online in seconds
  public int              banTo; // lock account time
  public String           banReason = Constant.EMPTY_STRING; // reason to lock account

  public int              clientVersion; // for upgrade version reward

  public String           lastHostAddress = Constant.EMPTY_STRING;
  public String           lastClientAddress = Constant.EMPTY_STRING;
  public String           lastOsPlatform = Constant.EMPTY_STRING;
  public String           lastDeviceUID = Constant.EMPTY_STRING;
  public String           lastClientSource = Constant.EMPTY_STRING;

  public String           facebookID = Constant.EMPTY_STRING;
  public String           facebookToken = Constant.EMPTY_STRING;
  public String           facebookName = Constant.EMPTY_STRING;
  public String           facebookAvatar = Constant.EMPTY_STRING;

  public int              languageCode;

  public String           registerCountry;
  public String           lastCountry;
  public HashSet<String>  deviceUIDs;
  public boolean          isCloneUser;//is seconds account on one device
  public int              totalPlayingTime;

  public UserProfile() {
    deviceUIDs = new HashSet<>();
    languageCode = TextData.LANGUAGE_CODE_DEFAULT;
    loginCount = 0;
  }

  public void newDay() {

  }

  public boolean addDeviceUID(String deviceUID) {
    lastDeviceUID = deviceUID;
    return deviceUIDs.add(deviceUID);
  }

  public String toJson() {
    return Utilities.gson.toJson(this);
  }

  public static UserProfile ofDefault() {
    UserProfile defaultUserProfile = new UserProfile();
    defaultUserProfile.registerAt = (int)(System.currentTimeMillis()/1000);
    defaultUserProfile.loginToday = 1;
    return defaultUserProfile;
  }
}