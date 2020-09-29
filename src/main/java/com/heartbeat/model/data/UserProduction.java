package com.heartbeat.model.data;

import com.common.Constant;
import com.common.Msg;
import com.heartbeat.model.Session;
import com.statics.OfficeData;
import com.transport.EffectResult;

import java.util.concurrent.ThreadLocalRandom;

public class UserProduction extends com.transport.model.Production{
  public static transient int RECOVERY_DENOMINATOR        = 10000;
  public static transient int MAX_REC_INTERVAL            = 20*60;
  public static transient int CLAIM_ITEM                  = 1;

  public static final     int PRODUCE_FAN                 = 1;
  public static final     int PRODUCE_VIEW                = 2;
  public static final     int PRODUCE_GOLD                = 3; //sáng tác
  public static final     int RAMPAGE_BUFF_PERCENT        = 15;
  public static final     int RAMPAGE_BUFF_SCALE          = 9;

  public int dailyRampage;
  public int maxDailyRampage;

  public static UserProduction ofDefault() {
    UserProduction defaultUserProduction  = new UserProduction();
    defaultUserProduction.lastFanClaim    = 0;
    defaultUserProduction.lastViewClaim   = 0;
    defaultUserProduction.lastGoldClaim   = 0;
    defaultUserProduction.maxFanClaim     = 3;
    defaultUserProduction.maxViewClaim    = 3;
    defaultUserProduction.maxGoldClaim    = 3;
    defaultUserProduction.dailyRampage    = 0;
    defaultUserProduction.maxDailyRampage = 1;
    return defaultUserProduction;
  }

  /********************************************************************************************************************/

  public void reBalance(long curTotalCrt) {
    goldRecoverInv  = ((int)(curTotalCrt/RECOVERY_DENOMINATOR) + 1)*60;
    viewRecoverInv  = ((int)(curTotalCrt/RECOVERY_DENOMINATOR) + 1)*60;
    fanRecoverInv   = ((int)(curTotalCrt/RECOVERY_DENOMINATOR) + 1)*60;
    goldRecoverInv  = Math.min(goldRecoverInv, MAX_REC_INTERVAL);
    viewRecoverInv  = Math.min(viewRecoverInv, MAX_REC_INTERVAL);
    fanRecoverInv   = Math.min(fanRecoverInv, MAX_REC_INTERVAL);
  }

  public void newDay() {
    dailyRampage = 0;
  }

  public void updateProduction(Session session, long curMs) {
    int newInv        = ((int)(session.userIdol.totalCrt()/RECOVERY_DENOMINATOR) + 1)*60;
    newInv            = Math.min(newInv, MAX_REC_INTERVAL);

    int second        = (int)(curMs/1000);
    int goldDt        = second - lastGoldClaim;
    int viewDt        = second - lastViewClaim;
    int fanDt         = second - lastFanClaim;

    int currentTitleId = session.userGameInfo.titleId;
    OfficeData.OfficeLV currentLevelData = OfficeData.officeLV.get(currentTitleId);
    if (currentLevelData != null) {
      maxViewClaim  = currentLevelData.foodMaxTimes;
      maxFanClaim   = currentLevelData.soldierMaxTimes;
      maxGoldClaim  = currentLevelData.moneyMaxTimes;
    }

    int newGoldProduceCount = goldDt/goldRecoverInv;
    int newViewProduceCount = viewDt/viewRecoverInv;
    int newFanProduceCount  = fanDt/fanRecoverInv;

    lastGoldClaim += newGoldProduceCount*goldRecoverInv;
    lastViewClaim += newViewProduceCount*viewRecoverInv;
    lastFanClaim  += newFanProduceCount*fanRecoverInv;

    currentFanClaimCount  += newFanProduceCount;
    currentGoldClaimCount += newGoldProduceCount;
    currentViewClaimCount += newViewProduceCount;

    currentViewClaimCount = Math.min(currentViewClaimCount, maxViewClaim);
    currentGoldClaimCount = Math.min(currentGoldClaimCount, maxViewClaim);
    currentFanClaimCount  = Math.min(currentFanClaimCount, maxViewClaim);

    //update ivn after claim increase
    if (newGoldProduceCount >= 1)
      goldRecoverInv = newInv;
    if (newViewProduceCount >= 1)
      viewRecoverInv = newInv;
    if (newFanProduceCount >= 1)
      fanRecoverInv  = newInv;
  }

  public String produce(Session session, int produceType) {
    long curMs = System.currentTimeMillis();
    updateProduction(session, curMs);
    switch (produceType) {
      case PRODUCE_FAN:
        long totalFanAdd = session.userIdol.totalAttr();
        if (currentFanClaimCount > 0 && session.userGameInfo.view >= totalFanAdd) {
          currentFanClaimCount -= 1;
          if (session.userGameInfo.isActiveTime()) {
            session.userGameInfo.fan += totalFanAdd;
            session.userGameInfo.spendView(session, totalFanAdd);
          }
          lastFanClaim = (int)(curMs/1000);
          return "ok";
        }
        else
          return Msg.map.getOrDefault(Msg.CLAIM_PRODUCT_TIME_OUT, "claim_product_timeout");
      case PRODUCE_GOLD:
        if (currentGoldClaimCount > 0) {
          currentGoldClaimCount -= 1;
          if (session.userGameInfo.isActiveTime()) {
            long moneyAdd = session.userIdol.totalCrt();

            //todo calc rampage probability
            if (dailyRampage < maxDailyRampage) {
              int r = ThreadLocalRandom.current().nextInt(1, 101);
              if (r <= RAMPAGE_BUFF_PERCENT) {
                moneyAdd += RAMPAGE_BUFF_SCALE*moneyAdd;
                session.effectResults.add(EffectResult.of(Constant.EFFECT_RESULT.RAMPAGE_EFFECT_RESULT,1 ,0));
              }
              dailyRampage++;
            }

            session.userGameInfo.money += moneyAdd;
          }
          lastGoldClaim = (int)(curMs/1000);
          return "ok";
        }
        else
          return Msg.map.getOrDefault(Msg.CLAIM_PRODUCT_TIME_OUT, "claim_product_timeout");
      case PRODUCE_VIEW:
        if (currentViewClaimCount > 0) {
          currentViewClaimCount -= 1;
          if (session.userGameInfo.isActiveTime()) {
            session.userGameInfo.view += session.userIdol.totalPerf();
          }
          lastViewClaim = (int)(curMs/1000);
          return "ok";
        }
        else
          return "claim_product_timeout";
    }
    return Msg.map.getOrDefault(Msg.MALFORM_ARGS, "wrong_product_type");
  }

  public String multiProduce(Session session) {
    long curMs = System.currentTimeMillis();

    updateProduction(session, curMs);
    if (currentGoldClaimCount <= 0 && currentViewClaimCount <= 0 && currentFanClaimCount <= 0)
      return Msg.map.getOrDefault(Msg.CLAIM_PRODUCT_TIME_OUT, "claim_product_timeout");

    //pre checking
    long    totalFanAdd          = 0;
    boolean shouldDoFanProduce   = true;
    if (currentFanClaimCount > 0) {
      totalFanAdd = currentFanClaimCount*session.userIdol.totalAttr();
      if (session.userGameInfo.view + currentViewClaimCount*session.userIdol.totalPerf() < totalFanAdd)
        shouldDoFanProduce = false;
    }

    //gold
    if (currentGoldClaimCount > 0) {
      if (session.userGameInfo.isActiveTime()) {
        int rampageCnt = 0;
        for (int i = 0; i < currentGoldClaimCount; i++) {
          long moneyAdd = session.userIdol.totalCrt();

          //todo calc rampage probability
          if (dailyRampage < maxDailyRampage) {
            int r = ThreadLocalRandom.current().nextInt(1, 101);
            if (r <= RAMPAGE_BUFF_PERCENT) {
              moneyAdd += RAMPAGE_BUFF_SCALE*moneyAdd;
              rampageCnt++;
            }
            dailyRampage++;
          }
          session.userGameInfo.money += moneyAdd;
          if (rampageCnt > 0) {
            session.effectResults.add(EffectResult.of(Constant.EFFECT_RESULT.RAMPAGE_EFFECT_RESULT,rampageCnt ,0));
          }
        }
      }
      lastGoldClaim               = (int)(curMs/1000);
      currentGoldClaimCount       = 0;
    }

    //view
    if (currentViewClaimCount > 0) {
      if (session.userGameInfo.isActiveTime()) {
        session.userGameInfo.view  += currentViewClaimCount*session.userIdol.totalPerf();
      }
      lastViewClaim               = (int)(curMs/1000);
      currentViewClaimCount       = 0;
    }

    //fan
    if (currentFanClaimCount > 0 && shouldDoFanProduce) {
      if (session.userGameInfo.isActiveTime()) {
        session.userGameInfo.fan   += totalFanAdd;
        session.userGameInfo.spendView(session, totalFanAdd);
      }

      lastFanClaim                = (int)(curMs/1000);
      currentFanClaimCount        = 0;
    }

    if (shouldDoFanProduce)
      return "ok";
    else
      return Msg.map.getOrDefault(Msg.CLAIM_PRODUCT_INSUFFICIENT_VIEW, "unsufficient_view");
  }

  public void addProduction(Session session, int productType, int amount) {
    switch (productType){
      case PRODUCE_GOLD:
        currentGoldClaimCount  += amount;
        goldRecoverInv          = ((int)(session.userIdol.totalCrt()/RECOVERY_DENOMINATOR) + 1)*60;
        goldRecoverInv          = Math.min(goldRecoverInv, MAX_REC_INTERVAL);
        break;
      case PRODUCE_FAN:
        currentFanClaimCount += amount;
        fanRecoverInv          = ((int)(session.userIdol.totalCrt()/RECOVERY_DENOMINATOR) + 1)*60;
        fanRecoverInv          = Math.min(fanRecoverInv, MAX_REC_INTERVAL);
        break;
      case PRODUCE_VIEW:
        currentViewClaimCount += amount;
        viewRecoverInv          = ((int)(session.userIdol.totalCrt()/RECOVERY_DENOMINATOR) + 1)*60;
        viewRecoverInv          = Math.min(viewRecoverInv, MAX_REC_INTERVAL);
        break;
      default:
        break;
    }
  }
}