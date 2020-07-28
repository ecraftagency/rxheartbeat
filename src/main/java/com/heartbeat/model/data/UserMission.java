package com.heartbeat.model.data;

import com.heartbeat.common.Constant;
import com.heartbeat.effect.EffectHandler;
import com.heartbeat.effect.EffectManager;
import com.heartbeat.model.Session;
import com.statics.MissionData;
import com.transport.model.Idols;
import com.transport.model.Mission;

import java.util.List;

public class UserMission extends Mission {
  public static UserMission ofDefault() {
    UserMission um      = new UserMission();
    um.currentMissionId = 1;
    um.complete         = false;
    return um;
  }

  public String unlockMission(Session session) {
    if (!checkAccomplishment(session))
      return "mission_impossible";

    MissionData.MissionDto dto = MissionData.missionDtoMap.get(currentMissionId);
    if (dto == null)
      return "mission_data_not_found";

    if (dto.rewardFormat == null)
      return "reward_formal_invalid";

    EffectHandler.ExtArgs extArgs = EffectHandler.ExtArgs.ofDefault(0,-1,"");
    for (List<Integer> re : dto.rewardFormat)
      EffectManager.inst().handleEffect(extArgs, session, re);
    currentMissionId++;
    if (MissionData.missionDtoMap.get(currentMissionId) == null || currentMissionId > MissionData.missionDtoMap.size())
      currentMissionId = -1;

    this.complete = checkAccomplishment(session);
    return "ok";
  }

  public void updateAccomplishment(Session session) {
    this.complete = checkAccomplishment(session);
  }

  public boolean checkAccomplishment(Session session) {
    try {
      MissionData.MissionDto dto = MissionData.missionDtoMap.get(currentMissionId);
      if (dto == null) {
        return false;
      }

      if (dto.queryFormat == null || dto.queryFormat.size() < 2)
        return false;

      UserAchievement achievement = session.userAchievement;
      if (achievement == null)
        return false;

      int queryField = dto.queryFormat.get(0);
      switch (queryField) {
        case Constant.ACHIEVEMENT.CRT_ACHIEVEMENT:
        case Constant.ACHIEVEMENT.VIEW_ACHIEVEMENT:
        case Constant.ACHIEVEMENT.FAN_ACHIEVEMENT:
        case Constant.ACHIEVEMENT.MEDIA_ACHIEVEMENT:
        case Constant.ACHIEVEMENT.FIGHT_ACHIEVEMENT:
        case Constant.ACHIEVEMENT.GAMESHOW_ACHIEVEMENT:
        case Constant.ACHIEVEMENT.SHOPPING_ACHIEVEMENT:
        case Constant.ACHIEVEMENT.RUNSHOW_ACHIEVEMENT:
        case Constant.ACHIEVEMENT.TRAVEL_ACHIEVEMENT:
          long cmpValue  = (long)(dto.queryFormat.get(1));
          long curValue = achievement.records.get(queryField);
          return curValue >= cmpValue;
        case Constant.ACHIEVEMENT.IDOL_LEVEL:
          int queryType = dto.queryFormat.get(1);
          UserIdol idols = session.userIdol;
          if (queryType == Constant.ACHIEVEMENT.IDOL_SINGLE_QUERY) {
            int idolId  = dto.queryFormat.get(2);
            int cmpVal  = dto.queryFormat.get(3);
            for (Idols.Idol idol : idols.idolMap.values())
              if (idol.id == idolId && idol.level >= cmpVal)
                return true;
          }
          else if (queryType == Constant.ACHIEVEMENT.IDOL_MULTI_QUERY) {
            int cnt     = dto.queryFormat.get(2);
            int cmpVal  = dto.queryFormat.get(3);
            int curCnt  = 0;
            for (Idols.Idol idol : idols.idolMap.values()) {
              if (idol.level >= cmpVal)
                curCnt++;
            }
            return curCnt >= cnt;
          }
          return false;
        default:
          return false;
      }
    }
    catch (Exception e) {
      return false;
    }
  }
}
