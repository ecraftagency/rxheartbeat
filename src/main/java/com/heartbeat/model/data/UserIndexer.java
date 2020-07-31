package com.heartbeat.model.data;

import com.heartbeat.common.Constant;
import com.heartbeat.common.Utilities;

@SuppressWarnings("unused")
public class UserIndexer {
  // backup profile
  public int regAt;
  public String displayName = Constant.EMPTY_STRING;
  public String pass;
  public String zaloId;
  public String fbId;

  public UserIndexer() {
  }

  public UserIndexer(int regAt) {
    this.regAt = regAt;
  }

  public void updateProfile(UserProfile userProfile) {
    if(userProfile != null) {
      pass = userProfile.password;
      fbId = userProfile.facebookID;
    }
  }

  public void updateUserGameInfo(UserGameInfo userGameInfo) {
    if (userGameInfo != null) {
      displayName = userGameInfo.displayName;
    }
  }

  public UserProfile toUserProfile() {
    UserProfile userProfile = new UserProfile();
    userProfile.registerAt = regAt;
    userProfile.password = pass;
    userProfile.facebookID =  fbId;
    userProfile.lastLogin = (int)(System.currentTimeMillis()/1000) - Constant.DAY_SECONDS;
    return userProfile;
  }
}
