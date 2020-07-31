package com.heartbeat.model.data;

import com.heartbeat.effect.EffectHandler;
import com.heartbeat.effect.EffectManager;
import com.heartbeat.model.Session;
import com.statics.DailyGiftData;
import com.statics.GiftCardData;
import com.statics.VipData;
import com.statics.VipGiftData;
import com.transport.model.RollCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
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
  private static final Logger LOGGER = LoggerFactory.getLogger(UserRollCall.class);

  public static UserRollCall ofDefault() {
    UserRollCall rollCall       = new UserRollCall();
    rollCall.nClaimedDays       = 0;
    rollCall.lastDailyClaimTime = 0;
    rollCall.todayClaim         = false;
    rollCall.vipClaimed         = new HashMap<>();
    rollCall.giftCards          = new HashMap<>();
    return rollCall;
  }

  public void reBalance() {
    if (this.vipClaimed == null)
      this.vipClaimed = new HashMap<>();
    if (this.giftCards == null)
      this.giftCards = new HashMap<>();
  }

  public String getRollCallInfo(Session session, long curMs) {
    int second = (int)(curMs/1000);
    int dayDiff   = (second - lastDailyClaimTime) >= 60 ? 1 : 0; //Utilities.dayDiff(lastDailyClaimTime, second);

    //todo change to daydiff
    todayClaim = dayDiff <= 0;

    //vip info
    VipData.Vip vipDto = VipData.getVipData(session.userGameInfo.vipExp);
    currentVipLevel = (vipDto != null) ? vipDto.level : 0;

    //giftCard info
    reCalcGiftCardInfo(session, second);

    return "ok";
  }

  private void reCalcGiftCardInfo(Session session, int second) {
    for (Map.Entry<Integer, GiftInfo> entry : giftCards.entrySet()) {
      GiftCardData.GiftCardDto giftDto = GiftCardData.giftCardDtoMap.stream()
              .filter(e -> e.type == entry.getKey()) //key = type of gift [1,2,3] -> [week, month, year]
              .findAny()
              .orElse(null);

      if (giftDto == null) {
        entry.getValue().boughtTime     = 0;
        entry.getValue().remainDay      = 0;
        entry.getValue().lastClaimTime  = 0;
        entry.getValue().todayClaim     = false;
        String err = String.format("data_inconsistency[sessionId:%d, giftType:%d]", session.id, entry.getKey());
        LOGGER.error(err);
        continue;
      }

      //todo critical
      int nDays = (second - lastDailyClaimTime) >= 60 ? 1 : 0; //Utilities.dayDiff(entry.getValue().boughtTime, second);
      if (nDays > giftDto.expireDay) {
        entry.getValue().boughtTime     = 0;
        entry.getValue().remainDay      = 0;
        entry.getValue().lastClaimTime  = 0;
        entry.getValue().todayClaim     = false;
        continue;
      }

      entry.getValue().remainDay  = giftDto.expireDay - nDays;
      int giftClaimDayDiff = (second - lastDailyClaimTime) >= 60 ? 1 : 0; //Utilities.dayDiff(entry.getValue().lastClaimTime, second);
      entry.getValue().todayClaim = giftClaimDayDiff <= 0;
    }

    giftCards.entrySet().removeIf(e -> e.getValue().remainDay == 0);
  }

  public String claimDailyGift(Session session, long curMs) {
    if (DailyGiftData.dailyGiftDtoMap == null || DailyGiftData.dailyGiftDtoMap.size() < 1)
      return "daily_data_not_found";

    int second = (int)(curMs/1000);
    int dayDiff   = (second - lastDailyClaimTime) >= 60 ? 1 : 0; //Utilities.dayDiff(lastDailyClaimTime, second);

    if (dayDiff <= 0 || this.todayClaim) {
      return "daily_gift_already_claimed";
    }

    int roll = (nClaimedDays + 1)%DailyGiftData.dailyGiftDtoMap.size();
    if (roll == 0)
      roll = DailyGiftData.dailyGiftDtoMap.size();

    try {
      List<List<Integer>> reward = DailyGiftData.dailyGiftDtoMap.get(roll).reward;
      for (List<Integer> r : reward)
        EffectManager.inst().handleEffect(EffectHandler.ExtArgs.of(), session, r);

      nClaimedDays++;
      lastDailyClaimTime  = second;
      todayClaim          = true;
      return "ok";
    }
    catch (Exception e) {
      return "reward_not_found";
    }
  }

  public String claimVipGift(Session session, long curMs, int claimLevel) {
    VipData.Vip cur = VipData.getVipData(session.userGameInfo.vipExp);
    if (cur == null)
      return "vip_data_not_found";

    if (cur.level == 0 || claimLevel > cur.level || cur.level > VipGiftData.vipGiftDtoMap.size()) {
      currentVipLevel = cur.level;
      if (cur.level > VipGiftData.vipGiftDtoMap.size()) {
        String err = String.format("data_inconsistency[curVipLevel:%d,vipGiftSize:%d]",cur.level, VipGiftData.vipGiftDtoMap.size());
        LOGGER.error(err);
      }
      return "mission_impossible";
    }

    if (vipClaimed.containsKey(claimLevel)) {
      currentVipLevel = cur.level;
      return "vip_gift_already_claim";
    }

    VipGiftData.VipGiftDto dto = VipGiftData.vipGiftDtoMap.get(claimLevel);
    if (dto == null || dto.reward == null) {
      currentVipLevel = cur.level;
      return "vip_gift_data_not_found";
    }

    dto.reward.forEach(r -> EffectManager.inst().handleEffect(EffectHandler.ExtArgs.of(), session, r));

    currentVipLevel = cur.level;
    vipClaimed.put(claimLevel, curMs);
    return "ok";
  }

  public String claimGiftCardDailyGift(Session session, long curMs, int giftType) {
    int second  = (int)(curMs/1000);
    reCalcGiftCardInfo(session, second);

    GiftInfo giftInfo = giftCards.get(giftType);
    if (giftInfo == null)
      return "gift_card_not_found";

    GiftCardData.GiftCardDto giftDto = GiftCardData.giftCardDtoMap.stream()
            .filter(e -> e.type == giftType) //key = type of gift [1,2,3] -> [week, month, year]
            .findAny()
            .orElse(null);

    if (giftDto == null || giftDto.dailyReward == null)
      return "gift_card_data_not_found";

    int nDays = (second - giftInfo.lastClaimTime) >= 60 ? 1 : 0; //Utilities.dayDiff(giftInfo.boughtTime, second);
    int claimDayDiff = (second - giftInfo.lastClaimTime) >= 60 ? 1 : 0; //Utilities.dayDiff(giftInfo.lastClaimTime, second);


    if (nDays > giftDto.expireDay) {
      return "gift_card_expire";
    }

    if (claimDayDiff <= 0 || giftInfo.todayClaim) {
      return "gift_card_daily_already_claimed";
    }

    for (List<Integer> r : giftDto.dailyReward)
      EffectManager.inst().handleEffect(EffectHandler.ExtArgs.of(), session, r);

    giftInfo.remainDay      = giftDto.expireDay - nDays;
    giftInfo.lastClaimTime  = second;
    giftInfo.todayClaim     = true;
    return "ok";
  }

  public String addGiftCard(Session session, long curMs, int type) {
    GiftCardData.GiftCardDto giftDto = GiftCardData.giftCardDtoMap.stream()
            .filter(e -> e.type == type)
            .findAny()
            .orElse(null);

    if (giftDto == null || giftDto.initReward == null)
      return "gift_card_data_not_found";

    int second  = (int)(curMs/1000);

    GiftInfo giftInfo       = new GiftInfo();
    giftInfo.todayClaim     = true;
    giftInfo.remainDay      = giftDto.expireDay;
    giftInfo.lastClaimTime  = second;
    giftInfo.boughtTime     = second;
    giftInfo.giftType       = type;

    for (List<Integer> r : giftDto.initReward)
      EffectManager.inst().handleEffect(EffectHandler.ExtArgs.of(), session, r);

    giftCards.put(type, giftInfo);
    return "ok";
  }
}
