package com.heartbeat.model.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.common.Utilities;
import com.heartbeat.db.cb.CBMapper;
import com.heartbeat.effect.EffectHandler;
import com.heartbeat.effect.EffectManager;
import com.heartbeat.model.Session;
import com.statics.*;
import com.transport.model.GameInfo;

import java.util.HashMap;
import java.util.List;
import static com.common.Constant.*;
import static com.common.Constant.USER_GAME_INFO.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserGameInfo extends GameInfo {
  public static UserGameInfo ofDefault() {
    UserGameInfo defaultInfo      = new UserGameInfo();
    defaultInfo.avatar            = -1;
    defaultInfo.gender            = -1; //0 male, 1 female;
    defaultInfo.displayName       = "";
    defaultInfo.money             = 2000000000L;
    defaultInfo.view              = 2000000000L;
    defaultInfo.fan               = 2000000000L;
    defaultInfo.talent            = 3000;
    defaultInfo.titleId           = 1;
    defaultInfo.time              = 0;
    defaultInfo.exp               = 2000;
    defaultInfo.currMedia         = 3;
    defaultInfo.maxMedia          = 3;
    defaultInfo.lastMediaClaim    = 0;
    defaultInfo.vipExp            = 0;
    defaultInfo.crazyDegree       = 0;
    defaultInfo.nextQuestion      = MediaData.nextRandQuestion();
    defaultInfo.crazyRewardClaim  = new HashMap<>();
    defaultInfo.timeChange        = false;
    defaultInfo.shopping          = new HashMap<>();
    return defaultInfo;
  }

  /********************************************************************************************************************/

  public boolean spendMoney(Session session, long amount) {
    if (money < amount)
      return false;
    money -= amount;
    try {
      session.userEvent.addEventRecord(USER_EVENT.MONEY_SPEND_EVT_ID, amount);
      session.userRanking.addEventRecord(RANKING.MONEY_SPEND_RANK_ID, amount);
    }
    catch (Exception e) {
      //
    }
    return true;
  }

  public boolean spendView(Session session, long amount) {
    if (view < amount)
      return false;
    view -= amount;
    try {
      session.userEvent.addEventRecord(USER_EVENT.VIEW_SPEND_EVT_ID, amount);
      session.userRanking.addEventRecord(RANKING.VIEW_SPEND_RANK_ID, amount);
    }
    catch (Exception e){
      //
    }
    return true;
  }

  public boolean spendFan(Session session, long amount) {
    if (fan < amount)
      return false;
    fan -= amount;
    try {
      session.userEvent.addEventRecord(USER_EVENT.FAN_SPEND_EVT_ID, amount);
      session.userRanking.addEventRecord(RANKING.FAN_SPEND_RANK_ID, amount);
    }
    catch (Exception e) {
      //
    }
    return true;
  }

  public void newDay() {
    crazyDegree = 0;
    crazyRewardClaim.clear();
    shopping.clear();
  }

  public void reBalance() {
    if (shopping == null)
      shopping = new HashMap<>();
  }

  public String updateDisplayName(Session session,  String dName) throws Exception {
    dName = dName.trim();
    if (Utilities.isValidString(dName)) {
      if (WordFilter.isValidUserName(dName, session.buildSource)) {
        String sha256DisplayName = Utilities.sha256Hash(dName);
        if (CBMapper.getInstance().map(Integer.toString(session.id), sha256DisplayName).equals("ok")) {
          this.displayName = dName;
          return "ok";
        }
        else
          return "display_name_exist";
      }
      else
        return "display_name_invalid";
    }
    else
      return "display_name_invalid";
  }

  public String replaceDisplayName(Session session, String displayName){
    displayName = displayName.trim();
    if (Utilities.isValidString(displayName)) {
      if (WordFilter.isValidUserName(displayName, session.buildSource)) {
        try {
          String sha256DisplayName = Utilities.sha256Hash(displayName);
          if (CBMapper.getInstance().map(Integer.toString(session.id), sha256DisplayName).equals("ok")) {
            String oldDisplayName = this.displayName;
            this.displayName = displayName;
            String sha256OldDisplayName = Utilities.sha256Hash(oldDisplayName);
            CBMapper.getInstance().unmap(sha256OldDisplayName);
            return "ok";
          }
          else
            return "display_name_exist";
        }
        catch (Exception e) {
          return "display_name_invalid";
        }

      }
      else
        return "display_name_invalid";
    }
    else
      return "display_name_invalid";
  }

  /*MEDIA**************************************************************************************************************/

  public void updateUserMedia(long curMs) {
    int second        = (int)(curMs/1000);
    int mediaDt       = second - lastMediaClaim;
    int newMediaCount = mediaDt/MEDIA_INTERVAL;
    lastMediaClaim   += newMediaCount*MEDIA_INTERVAL;
    currMedia        += newMediaCount;
    currMedia         = Math.min(currMedia, maxMedia);
  }

  public String claimMedia(Session session, int answer) {
    long curMs = System.currentTimeMillis();
    updateUserMedia(curMs);

    if (currMedia > 0) {
      currMedia            -= 1;
      lastMediaClaim        = (int)(curMs/1000);
      MediaData.Media media = MediaData.mediaMap.get(nextQuestion);

      if (media != null && time > 0) {
        List<List<Integer>> rewards;
        EffectHandler.ExtArgs extArgs = EffectHandler.ExtArgs.of();

        if (answer == 1) {
          rewards = media.reward_1;
          for (List<Integer> reward : rewards)
            EffectManager.inst().handleEffect(extArgs, session, reward);
        }

        if (answer == 2) {
          rewards = media.reward_2;
          for (List<Integer> reward : rewards)
            EffectManager.inst().handleEffect(extArgs, session, reward);
        }

        nextQuestion = MediaData.nextRandQuestion();
      }

      return "ok";
    }
    else
      return "media_time_out";
  }

  public void addMediaClaim(int amount) {
    currMedia += amount;
    currMedia  = Math.min(currMedia, maxMedia);
  }

  public void addVipExp(Session session, int amount) {
    if (amount <= 0)
      return;
    VipData.VipDto oldVip  = VipData.getVipData(vipExp);
    vipExp += amount;

    VipData.VipDto vip = VipData.getVipData(vipExp);

    if (vip.level > oldVip.level) { //level up
      session.userTravel.maxTravelClaim       = vip.travelLimit;
      session.userTravel.dailyTravelAddLimit  = vip.travelAddLimit;
      session.userIdol.maxDailyRampage        = vip.idolLevelUpLimit;
      session.userProduction.maxDailyRampage  = vip.createLimit;
      //reward idol
      for (int lv = oldVip.level + 1; lv <= vip.level; lv++) {
        VipData.VipDto v = VipData.vipMap.get(lv);
        if (v != null && v.rewardFormat != null && v.rewardFormat.size() == 4) {
          EffectManager.inst().handleEffect(EffectHandler.ExtArgs.of(), session, v.rewardFormat);
        }
      }
    }
  }

  public String claimCrazyReward(Session session, int milestone) {
    if (crazyDegree < milestone)
      return "insufficient_crazy_degree";
    if (crazyRewardClaim.get(milestone) != null)
      return "already_claim";
    for (CrazyRewardData.CrazyReward cr : CrazyRewardData.crazyRewardMap.values()) {
      if (milestone == cr.milestone) {
        if (cr.reward == null)
          return "crazy_claim_fail";
        EffectHandler.ExtArgs extArgs = EffectHandler.ExtArgs.of();
        for (List<Integer> re : cr.reward) {
          EffectManager.inst().handleEffect(extArgs, session, re);
        }
        crazyRewardClaim.put(milestone, true);
        return "ok";
      }
    }

    return "unknown_milestone";
  }

  public boolean useTime(Session session, long amount) {
    reBalanceTime(session);
    if (amount > this.time)
      return false;
    this.time -= amount;
    if (this.time < 0)
      this.time = 0;
    timeChange = true;

    //todo delta time is always >= real time consume, but just let it be
    session.userEvent.addEventRecord(USER_EVENT.TIME_SPEND_EVT_ID, amount);

    return true;
  }

  public void addTime(long amount) {
    this.time += amount;
    timeChange = true;
  }

  public void subtractTime(long delta) {
    this.time -= delta;
    if (this.time < 0)
      this.time = 0;
  }

  public boolean isActiveTime() {
    return time > 0 || titleId < TIME_ACTIVE_LEVEL;
  }

  public long remainTime() {
    return time;
  }

  private void reBalanceTime(Session session) {
    int second    = (int)(System.currentTimeMillis()/1000);
    int deltaTime = second - session.lastHearBeatTime;

    //todo delta time is always >= real time consume, but just let it be
    long timeSpent = deltaTime > time ? time : deltaTime;
    session.userEvent.addEventRecord(USER_EVENT.TIME_SPEND_EVT_ID, timeSpent);

    session.userGameInfo.time -= deltaTime;
    if (session.userGameInfo.time < 0)
      session.userGameInfo.time = 0;
    session.lastHearBeatTime = second;
  }

  /*SHOPPING***********************************************************************************************************/
  public String buyShopItem(Session session, int itemId) {
    if (time <= 0)
      return "time_out";

    ShopData.ShopDto dto = ShopData.shopDtoMap.get(itemId);

    if (dto == null || dto.format == null || dto.format.size() == 0)
      return "shop_data_not_found";

    if (shopping.getOrDefault(itemId, 0) >= dto.dailyLimit)
      return "shop_daily_limit";

    VipData.VipDto vipDto = VipData.getVipData(vipExp);
    if (vipDto == null)
      return "vip_data_not_found";

    if (vipDto.level < dto.vipCond)
      return "vip_condition_mismatch";

    if (useTime(session, dto.timeCost)) {
      for (List<Integer> f : dto.format)
        EffectManager.inst().handleEffect(EffectHandler.ExtArgs.of(), session, f);

      int upt = shopping.getOrDefault(itemId, 0) + 1;
      shopping.put(itemId, upt);
      return "ok";
    }
    else {
      return "insufficient_time";
    }
  }
}