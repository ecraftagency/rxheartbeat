package com.heartbeat.model.data;

import com.heartbeat.effect.EffectHandler;
import com.heartbeat.effect.EffectManager;
import com.heartbeat.model.Session;
import com.statics.DailyGiftData;
import com.transport.model.RollCall;

import java.util.List;
/*
open daily gift UI  -> req DailyGiftInfo
click claim gift    -> req claimDailyGift
nếu get info ok thì show ngày hiện tại = nClaimedDay + 1, % số đó với mapSize đề lấy key rend quà

getDailyGiftInfo (new acc)    -> ok    0 0           -> UI: day: (0+1)      gift:data.get(1),data.get(2)....11 entries
claimDailyGift                -> ok    1 15992...    -> UI: day:1(claimed)  gift:claimed,data.get(2).....11 entries
getDailyGiftInfo (same day)   -> delay 1 15992...    -> UI: day:1(claimed)  gift:claimed,data.get(2).....11 entries
getDailyGiftInfo (next day)   -> ok    1 15992...    -> UI: day: (1+1)      gift:claimed,data.get(2).....11 entries
.....
 */

public class UserRollCall extends RollCall {
  public static UserRollCall ofDefault() {
    UserRollCall rollCall       = new UserRollCall();
    rollCall.nClaimedDays       = 0;
    rollCall.lastDailyClaimTime = 0;
    rollCall.todayClaim         = false;
    return rollCall;
  }

  public String getDailyGiftInfo(long curMs) {
    int second = (int)(curMs/1000);
    //int dayDiff = Utilities.dayDiff(lastDailyClaimTime, second);
    int dayDiff   = (second - lastDailyClaimTime) >= 60 ? 1 : 0;

    if (dayDiff <= 0) {
      todayClaim = false;
      return "delay";
    }
    todayClaim = true;
    return "ok";
  }

  public String claimDailyGift(Session session, long curMs) {
    if (DailyGiftData.dailyGiftDtoMap == null || DailyGiftData.dailyGiftDtoMap.size() < 1)
      return "daily_data_not_found";

    int second = (int)(curMs/1000);
    //int dayDiff = Utilities.dayDiff(lastDailyClaimTime, second);
    int dayDiff   = (second - lastDailyClaimTime) >= 60 ? 1 : 0;

    if (dayDiff <= 0 || this.todayClaim) {
      return "daily_gift_already_claimed";
    }

    int roll = (nClaimedDays + 1)%DailyGiftData.dailyGiftDtoMap.size();
    try {
      List<List<Integer>> reward = DailyGiftData.dailyGiftDtoMap.get(roll).reward;
      for (List<Integer> r : reward)
        EffectManager.inst().handleEffect(EffectHandler.ExtArgs.of(), session, r);

      nClaimedDays++;
      lastDailyClaimTime  = second;
      todayClaim          = false;
      return "ok";
    }
    catch (Exception e) {
      return "reward_not_found";
    }
  }
}
