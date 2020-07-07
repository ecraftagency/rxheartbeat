package com.heartbeat.model.data;

import com.heartbeat.effect.EffectHandler;
import com.heartbeat.effect.EffectManager;
import com.heartbeat.model.Session;
import com.statics.TravelData;
import com.transport.model.Travel;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class UserTravel extends Travel {
  public static final int TRAVEL_CLAIM_INTERVAL = 20*60; //seconds
  public static final List<Integer> visitPercent = Arrays.asList(60, 30, 6, 4);

  public static UserTravel ofDefault() {
    UserTravel travel = new UserTravel();
    travel.currentTravelClaimCount  = 0;
    travel.maxTravelClaim           = 5;
    travel.lastTravelClaim          = 0;
    travel.chosenNPCId              = -1;
    return travel;
  }

  /********************************************************************************************************************/
  public void updateTravel(Session session, long curMs) {
    int second               = (int)(curMs/1000);
    int travelDt             = second - lastTravelClaim;
    int newTravelClaimCount  = travelDt/TRAVEL_CLAIM_INTERVAL;
    lastTravelClaim         += newTravelClaimCount*TRAVEL_CLAIM_INTERVAL;
    currentTravelClaimCount += newTravelClaimCount;
    currentTravelClaimCount  = Math.min(currentTravelClaimCount, maxTravelClaim);
  }

  public String claimTravel(Session session, long curMs) {
    updateTravel(session, curMs);
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
      return "miss";
    }

    List<TravelData.TravelNPC> npcList  = TravelData.npcTypeMap.get(npcType);

    if (npcList.size() == 0) {
      chosenNPCId = -1;
      return "miss";
    }

    TravelData.TravelNPC chosen   = npcList.get(ThreadLocalRandom.current().nextInt(npcList.size()));
    chosenNPCId                   = chosen.id;
    EffectHandler.ExtArgs extArgs = EffectHandler.ExtArgs.of(0,0,"");

    session.effectResults.clear();
    EffectManager.inst().handleEffect(extArgs, session, chosen.reward);
    return "ok";
  }
}