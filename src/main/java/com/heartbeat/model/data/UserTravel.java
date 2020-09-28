package com.heartbeat.model.data;

import com.common.Msg;
import com.heartbeat.effect.EffectHandler;
import com.heartbeat.effect.EffectManager;
import com.heartbeat.model.Session;
import com.statics.TravelData;
import com.statics.VipData;
import com.transport.model.Travel;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class UserTravel extends Travel {
  public static final int           TRAVEL_CLAIM_INTERVAL = 20*60; //seconds
  public static final List<Integer> visitPercent = Arrays.asList(60, 30, 6, 4);
  public static final int           COEFF_0 = 100000;
  public static final int           COEFF_1 = 10000;


  public static UserTravel ofDefault() {
    UserTravel travel = new UserTravel();
    travel.currentTravelClaimCount  = 0;
    travel.maxTravelClaim           = 5;
    travel.lastTravelClaim          = 0;
    travel.chosenNPCId              = -1;
    travel.travelInv                = TRAVEL_CLAIM_INTERVAL;
    travel.dailyTravelAdd           = 0;
    travel.dailyTravelAddLimit      = 5;
    return travel;
  }

  /********************************************************************************************************************/
  public void updateTravel(Session session, long curMs) {
    VipData.VipDto vip          = VipData.getVipData(session.userGameInfo.vipExp);
    maxTravelClaim           = vip.travelLimit;
    dailyTravelAddLimit      = vip.travelAddLimit;

    int second               = (int)(curMs/1000);
    int travelDt             = second - lastTravelClaim;
    int newTravelClaimCount  = travelDt/TRAVEL_CLAIM_INTERVAL;
    lastTravelClaim         += newTravelClaimCount*TRAVEL_CLAIM_INTERVAL;
    currentTravelClaimCount += newTravelClaimCount;
    currentTravelClaimCount  = Math.min(currentTravelClaimCount, maxTravelClaim);
  }

  public String claimTravel(Session session, long curMs) {
    updateTravel(session, curMs);

    if (currentTravelClaimCount < 1) {
      chosenNPCId = -1;
      return Msg.map.getOrDefault(Msg.CLAIM_TRAVEL_INSUFFICIENT, "claim_travel_insufficient_count");
    }

    int rand = ThreadLocalRandom.current().nextInt(1, 101),acc = 0,npcType = 0;

    for (int i = 0; i < visitPercent.size(); i++){
      acc += visitPercent.get(i);
      if (rand <= acc) {
        npcType = i + 1;
        break;
      }
    }

    if (npcType < 1 || npcType > 3 || TravelData.npcTypeMap.size() != 3) {
      chosenNPCId = -1;
      currentTravelClaimCount      -= 1;
      return Msg.map.getOrDefault(Msg.CLAIM_TRAVEL_MISS, "claim_travel_miss");
    }

    List<TravelData.TravelNPC> npcList  = TravelData.npcTypeMap.get(npcType);

    if (npcList.size() == 0) {
      chosenNPCId = -1;
      currentTravelClaimCount      -= 1;
      return Msg.map.getOrDefault(Msg.CLAIM_TRAVEL_MISS, "claim_travel_miss");
    }

    TravelData.TravelNPC chosen   = npcList.get(ThreadLocalRandom.current().nextInt(npcList.size()));
    chosenNPCId                   = chosen.id;
    EffectHandler.ExtArgs extArgs = EffectHandler.ExtArgs.of();
    currentTravelClaimCount      -= 1;
    lastTravelClaim               = (int)(curMs/1000);

    //reward
    if (session.userGameInfo.isActiveTime()) {
      EffectManager.inst().handleEffect(extArgs, session, chosen.reward);
    }

    return "ok";
  }

  public String claimMultiTravel(Session session, long curMs) {
    updateTravel(session, curMs);

    if (currentTravelClaimCount < 1) {
      chosenNPCId = -1;
      return Msg.map.getOrDefault(Msg.CLAIM_TRAVEL_INSUFFICIENT, "claim_travel_insufficient_count");
    }

    while (currentTravelClaimCount > 0) {
      int rand = ThreadLocalRandom.current().nextInt(1, 101),acc = 0,npcType = 0;

      for (int i = 0; i < visitPercent.size(); i++){
        acc += visitPercent.get(i);
        if (rand <= acc) {
          npcType = i + 1;
          break;
        }
      }

      if (npcType < 1 || npcType > 3 || TravelData.npcTypeMap.size() != 3) {
        chosenNPCId              = -1;
        currentTravelClaimCount -= 1;
        continue;
      }

      List<TravelData.TravelNPC> npcList  = TravelData.npcTypeMap.get(npcType);

      if (npcList.size() == 0) {
        chosenNPCId               = -1;
        currentTravelClaimCount  -= 1;
        continue;
      }

      TravelData.TravelNPC chosen   = npcList.get(ThreadLocalRandom.current().nextInt(npcList.size()));
      chosenNPCId                   = chosen.id;
      EffectHandler.ExtArgs extArgs = EffectHandler.ExtArgs.of();
      currentTravelClaimCount      -= 1;
      lastTravelClaim               = (int)(curMs/1000);

      if (session.userGameInfo.isActiveTime()) {
        EffectManager.inst().handleEffect(extArgs, session, chosen.reward);
      }
    }

    return "ok";
  }

  public void newDay() {
    dailyTravelAdd = 0;
  }

  public String addTravelClaim(Session session) {
    if (dailyTravelAdd >= dailyTravelAddLimit)
      return Msg.map.getOrDefault(Msg.MAX_TRAVEL_ADD, "max_travel_add");

    long viewConsume = COEFF_0 + COEFF_1*dailyTravelAdd*dailyTravelAdd; //100k + 10k*d^2

    if (session.userGameInfo.view < viewConsume)
      return Msg.map.getOrDefault(Msg.INSUFFICIENT_VIEW, "insufficient_view");

    session.userGameInfo.spendView(session, viewConsume);
    dailyTravelAdd++;
    currentTravelClaimCount++;
    currentTravelClaimCount  = Math.min(currentTravelClaimCount, maxTravelClaim);
    return "ok";
  }

  public String addMultiTravelClaim(Session session, int count) {
    if (dailyTravelAdd + count > dailyTravelAddLimit || count <= 0)
      return Msg.map.getOrDefault(Msg.MAX_TRAVEL_ADD, "max_travel_add");

    long viewConsume = 0;
    for (int i = 0; i < count; i++) {
      viewConsume += (COEFF_0 + COEFF_1*(dailyTravelAdd + i)*(dailyTravelAdd + i)); //100k + 10k*d^2
    }

    if (session.userGameInfo.view < viewConsume)
      return Msg.map.getOrDefault(Msg.INSUFFICIENT_VIEW, "insufficient_view");

    session.userGameInfo.spendView(session, viewConsume);
    dailyTravelAdd += count;
    currentTravelClaimCount += count;
    currentTravelClaimCount  = Math.min(currentTravelClaimCount, maxTravelClaim);
    return "ok";
  }
}