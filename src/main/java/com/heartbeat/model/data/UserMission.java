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
    um.currentCount     = 0;
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

    EffectHandler.ExtArgs extArgs = EffectHandler.ExtArgs.of();
    for (List<Integer> re : dto.rewardFormat)
      EffectManager.inst().handleEffect(extArgs, session, re);
    currentMissionId++;
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
        case Constant.ACHIEVEMENT.LOGIN_ACHIEVEMENT:
        case Constant.ACHIEVEMENT.STORE_ACHIEVEMENT:
        case 2*100: //hợp đồng truyền thông
        case 67*100: //cuộn cường hóa
          this.target       = (long)(dto.queryFormat.get(1));
          this.currentCount = achievement.records.get(queryField);
          return this.currentCount >= this.target;

        case Constant.ACHIEVEMENT.IDOL_LEVEL:
          int queryType       = dto.queryFormat.get(1);
          UserIdol idols      = session.userIdol;
          this.currentCount   = 0;

          if (queryType == Constant.ACHIEVEMENT.IDOL_SINGLE_QUERY) {
            int idolId  = dto.queryFormat.get(2);
            int cmpVal  = dto.queryFormat.get(3);
            for (Idols.Idol idol : idols.idolMap.values())
              if (idol.id == idolId && idol.level >= cmpVal) {
                this.currentCount = idol.level;
                this.target = cmpVal;
                return true;
              }
          }
          else if (queryType == Constant.ACHIEVEMENT.IDOL_MULTI_QUERY) {
            int cnt     = dto.queryFormat.get(2);
            int cmpVal  = dto.queryFormat.get(3);
            int curCnt  = 0;
            for (Idols.Idol idol : idols.idolMap.values()) {
              if (idol.level >= cmpVal)
                curCnt++;
            }
            this.currentCount = curCnt;
            this.target       = cnt;
            return curCnt >= cnt;
          }
          return false;

        case Constant.ACHIEVEMENT.IDOL_TITLE:
          int qType = dto.queryFormat.get(1);
          UserIdol userIdol = session.userIdol;

          if (qType == Constant.ACHIEVEMENT.IDOL_SINGLE_QUERY) {
            int idolId  = dto.queryFormat.get(2);
            int cmpVal  = dto.queryFormat.get(3);
            for (Idols.Idol idol : userIdol.idolMap.values())
              if (idol.id == idolId && idol.honorID >= cmpVal) {
                this.currentCount = idol.honorID;
                this.target = cmpVal;
                return true;
              }
          }
          else if (qType == Constant.ACHIEVEMENT.IDOL_MULTI_QUERY) {
            int cnt     = dto.queryFormat.get(2);
            int cmpVal  = dto.queryFormat.get(3);
            int curCnt  = 0;
            for (Idols.Idol idol : userIdol.idolMap.values()) {
              if (idol.honorID >= cmpVal)
                curCnt++;
            }
            this.currentCount = curCnt;
            this.target       = cnt;
            return curCnt >= cnt;
          }
          return false;

        case Constant.ACHIEVEMENT.TOTAL_TALENT_ACHIEVEMENT:
          this.currentCount = session.userIdol.getTotalCreativity() +
                  session.userIdol.getTotalPerformance() +
                  session.userIdol.getTotalCreativity();
          this.target = dto.queryFormat.get(1);
          return this.currentCount >= this.target;

        case Constant.ACHIEVEMENT.LEVEL_ACHIEVEMENT:
          this.target = dto.queryFormat.get(1);
          this.currentCount = session.userGameInfo.titleId;
          return session.userGameInfo.titleId >= this.target;

        case Constant.ACHIEVEMENT.GROUP_JOIN: //gia nhập liên minh
          this.currentCount = (session.groupID > 0) ? 1 : 0;
          this.target       = 1;
          return session.groupID > 0;
        default:
          return false;
      }
    }
    catch (Exception e) {
      return false;
    }
  }
}
