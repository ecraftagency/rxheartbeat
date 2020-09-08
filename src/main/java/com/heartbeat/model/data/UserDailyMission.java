package com.heartbeat.model.data;

import com.common.Msg;
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

  public void addRecord(int type, int amount) {
    for (Mission mission : missionMap.values()) {
      if (mission.type == type)
        mission.dailyCount += amount;
    }
  }

  public String claimReward(Session session, int missionID) {
    Mission uMission = missionMap.get(missionID);
    if (uMission == null)
      return Msg.msgMap.getOrDefault(Msg.RECORD_NOT_FOUND, "mission_not_found");
    DailyMissionData.DailyMissionDTO dto = DailyMissionData.missionMap.get(missionID);

    if (uMission.claim)
      return Msg.msgMap.getOrDefault(Msg.ALREADY_CLAIM, "already_claim");

    if (dto == null)
      return Msg.msgMap.getOrDefault(Msg.DTO_DATA_NOT_FOUND, "mission_data_not_found");

    if (uMission.dailyCount < dto.target)
      return Msg.msgMap.getOrDefault(Msg.INSUFFICIENT_CLAIM, "insufficient_claim");

    for (List<Integer> reward : dto.reward) {
      EffectHandler.ExtArgs extArgs = EffectHandler.ExtArgs.of();
      EffectManager.inst().handleEffect(extArgs, session, reward);
    }

    uMission.claim = true;
    return "ok";
  }
}
