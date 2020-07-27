package com.heartbeat.model.data;

import com.transport.model.Achievement;

import java.util.Arrays;
import java.util.HashMap;

public class UserAchievement extends Achievement {
  public static UserAchievement ofDefault() {
    UserAchievement ua    = new UserAchievement();
    ua.records            = new HashMap<>();
    ua.claimedAchievement = Arrays.asList(0L,0L,0L,0L,0L);
    return ua;
  }


}