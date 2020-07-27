package com.heartbeat.model.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.heartbeat.common.Utilities;
import com.heartbeat.db.cb.CBMapper;
import com.heartbeat.effect.EffectHandler;
import com.heartbeat.effect.EffectManager;
import com.heartbeat.model.Session;
import com.statics.CrazyRewardData;
import com.statics.MediaData;
import com.statics.VipData;
import com.statics.WordFilter;

import java.util.HashMap;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserGameInfo extends com.transport.model.GameInfo {
  public static final int MAX_AVATAR                = 10;
  public static final int MAX_GENDER                = 2;
  public static final int MEDIA_INTERVAL            = 59;
  public static final int CLAIM_MEDIA_COUNT_ITEM    = 2; //hợp đồng truyền thông

  public static UserGameInfo ofDefault() {
    UserGameInfo defaultInfo      = new UserGameInfo();
    defaultInfo.avatar            = -1;
    defaultInfo.gender            = -1; //0 male, 1 female;
    defaultInfo.displayName       = "";
    defaultInfo.money             = 2000000000000000L;
    defaultInfo.view              = 2000000000000000L;
    defaultInfo.fan               = 2000000000000000L;
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
    return defaultInfo;
  }

  /********************************************************************************************************************/

  public void newDay() {
    crazyDegree = 0;
    crazyRewardClaim.clear();
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
      currMedia      -= 1;
      lastMediaClaim = (int)(curMs/1000);
      MediaData.Media media = MediaData.mediaMap.get(nextQuestion);
      if (media != null) {
        List<List<Integer>> rewards;
        EffectHandler.ExtArgs extArgs = EffectHandler.ExtArgs.ofDefault(0, -1, "");
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
    VipData.Vip oldVip  = VipData.getVipData(vipExp);
    vipExp += amount;

    VipData.Vip vip = VipData.getVipData(vipExp);

    if (vip.level > oldVip.level) { //level up
      session.userTravel.maxTravelClaim       = vip.travelLimit;
      session.userTravel.dailyTravelAddLimit  = vip.travelAddLimit;
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
        EffectHandler.ExtArgs extArgs = EffectHandler.ExtArgs.ofDefault(0, -1, "");
        session.effectResults.clear();
        for (List<Integer> re : cr.reward) {
          EffectManager.inst().handleEffect(extArgs, session, re);
        }
        crazyRewardClaim.put(milestone, true);
        return "ok";
      }
    }

    return "unknown_milestone";
  }
}