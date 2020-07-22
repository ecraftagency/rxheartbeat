package com.heartbeat.model.data;

import com.heartbeat.effect.EffectHandler;
import com.heartbeat.effect.EffectManager;
import com.heartbeat.model.Session;
import com.statics.DailyMissionData;
import com.transport.model.DailyMission;

import java.util.HashMap;
import java.util.List;

public class UserDailyMission extends DailyMission {
  public static UserDailyMission ofDefault() {
    UserDailyMission res = new UserDailyMission();
    res.missionMap = new HashMap<>();
    for (DailyMissionData.DailyMissionDTO dm : DailyMissionData.missionMap.values()) {
      res.missionMap.put(dm.id, DailyMission.Mission.of(dm.id, 0, dm.type));
    }
    return res;
  }

  public void newDay() {
    for (Mission um : missionMap.values()) {
      um.dailyCount = 0;
      um.claim      = false;
    }
  }

  public void addRecord(int type) {
    for (Mission mission : missionMap.values()) {
      if (mission.type == type)
        mission.dailyCount++;
    }
  }

  public String claimReward(Session session, int missionID) {
    Mission uMission = missionMap.get(missionID);
    if (uMission == null)
      return "mission_not_found";
    DailyMissionData.DailyMissionDTO dto = DailyMissionData.missionMap.get(missionID);

    if (uMission.claim)
      return "already_claim_reward";

    if (dto == null)
      return "mission_data_not_found";

    if (uMission.dailyCount < dto.target)
      return "mission_impossible";

    session.effectResults.clear();

    for (List<Integer> reward : dto.rewards) {
      EffectHandler.ExtArgs extArgs = EffectHandler.ExtArgs.of(0, 0, "");
      EffectManager.inst().handleEffect(extArgs, session, reward);
    }

    uMission.claim = true;
    return "ok";
  }
}
