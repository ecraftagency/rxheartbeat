package com.heartbeat.model.data;

import com.heartbeat.common.Constant;
import com.heartbeat.common.Utilities;
import com.heartbeat.effect.EffectHandler;
import com.heartbeat.effect.EffectManager;
import com.heartbeat.model.Session;
import com.statics.FightData;
import com.statics.GameShowData;
import com.statics.RunShowData;
import com.statics.ShoppingData;
import com.transport.model.Fight;
import com.transport.model.Idols;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("unused")
public class UserFight extends Fight {
  public static final int             IDOL_DAMAGE_SCALE           = 5000;
  public static final int             IDOL_FREE_FIGHT_ITEM        = 3;
  public static final int             SHOPPING_COEFFICIENT        = 4;
  public static final float           SHOPPING_MONEY_SCL          = 0.25f;
  public static final int             RUN_SHOW_DROP_RATE          = 33;
  public static final List<Integer>   RUN_SHOW_EXP_REWARD;

  static {
    RUN_SHOW_EXP_REWARD = Arrays.asList(1, 6, 5, 0);

    RunShowData.randomer = runShow -> {
      if (runShow.id == -1)
        return;
      runShow.randAptNPC  = ThreadLocalRandom.current()
              .nextLong(runShow.minAptNPC, runShow.maxAptNPC);
      runShow.randFanNPC  = ThreadLocalRandom.current()
              .nextLong(runShow.minFanNPC, runShow.maxFanNPC);

      int round = ((runShow.id - 1)%10)/2;
      int nItem = RunShowData.nItemPerShow.get(round% RunShowData.nItemPerShow.size());
      runShow.reward.clear();
      for (int i = 0; i < nItem; i++) {
        List<Integer> pack = RunShowData.runShowRewards.get(i);
        runShow.reward.add(pack.get(ThreadLocalRandom.current()
                .nextInt(0,pack.size())));
      }
    };

    ShoppingData.randomer = shopping -> {
      if (shopping.id == -1)
        return;
      int round = ((shopping.id - 1)%10)/2;
      int nItem = ShoppingData.nItemPerShopping.get(round% ShoppingData.nItemPerShopping.size());
      shopping.reward.clear();
      for (int i = 0; i < nItem; i++) {
        List<Integer> pack = ShoppingData.shoppingReward.get(i);
        shopping.reward.add(pack.get(ThreadLocalRandom.current()
                .nextInt(0,pack.size())));
      }
    };
  }

  public static UserFight ofDefault () {
    UserFight uf      = new UserFight();
    uf.currentFightLV = FightData.of(1);
    uf.usedIdols      = new ArrayList<>();
    uf.restoreIdols   = new ArrayList<>();

    uf.currentGameShow      = GameShowData.of(24);
    uf.gameShowUsedIdols    = new ArrayList<>();
    uf.gameShowRestoreIdols = new ArrayList<>();
    uf.currentRunShow       = RunShowData.of(1);
    uf.currentShopping      = ShoppingData.of(1);
    return uf;
  }

  public void newDay() { //update OFFLINE session 00:00, login on next day
    usedIdols.clear();
    restoreIdols.clear();
    gameShowUsedIdols.clear();
    gameShowRestoreIdols.clear();
    currentGameShow     = GameShowData.of(1);
    currentRunShow      = RunShowData.of(1);
    currentShopping     = ShoppingData.of(1);
  }

  public void reLogin() { //update OFFLINE session, relogin on the sameday
    long firstOpenTime    = Utilities.certainSecond(Constant.SCHEDULE.gameShowOneOpenHour, 0, 0);
    long firstCloseTime   = Utilities.certainSecond(Constant.SCHEDULE.gameShowOneCloseHour, 0, 0);
    long secondOpenTime   = Utilities.certainSecond(Constant.SCHEDULE.gameShowTwoOpenHour, 0, 0);
    long secondCloseTime  = Utilities.certainSecond(Constant.SCHEDULE.gameShowTwoCloseHour, 0, 0);

    long curMs            = System.currentTimeMillis();

    if (curMs < firstOpenTime || (curMs > firstCloseTime && curMs < secondOpenTime) || curMs > secondCloseTime) {
      gameShowUsedIdols.clear();
      gameShowRestoreIdols.clear();
      currentGameShow     = GameShowData.of(1);
    }
  }

  public static void serverStartup() {
    long firstOpenTime    = Utilities.certainSecond(Constant.SCHEDULE.gameShowOneOpenHour, 0, 0);
    long firstCloseTime   = Utilities.certainSecond(Constant.SCHEDULE.gameShowOneCloseHour, 0, 0);
    long secondOpenTime   = Utilities.certainSecond(Constant.SCHEDULE.gameShowTwoOpenHour, 0, 0);
    long secondCloseTime  = Utilities.certainSecond(Constant.SCHEDULE.gameShowTwoCloseHour, 0, 0);

    long curMs            = System.currentTimeMillis();

    Constant.SCHEDULE.gameShowOpen = (curMs >= firstOpenTime && curMs <= firstCloseTime)
            || (curMs >= secondOpenTime && curMs <= secondCloseTime);
  }

  /*GAME SHOW**********************************************************************************************************/

  public String handleFight(Session session, int idolId) {
    if (currentFightLV.level == 6) {
      return handleBossFight(session, idolId);
    }
    else
      return handleNormalFight(session);
  }

  private String handleNormalFight(Session session) {
    long fixConsume       = (int)(currentFightLV.fanNPC*0.01f);
    long totalTalent      = session.userIdol.getTotalCreativity()
            + session.userIdol.getTotalPerformance()
            + session.userIdol.getTotalAttractive();
    long expectedConsume  = (int)(currentFightLV.fanNPC* currentFightLV.aptNPC/totalTalent) + fixConsume;

    if (session.userGameInfo.fan >= expectedConsume) {
      session.userGameInfo.fan -= expectedConsume;

      //reward
      session.effectResults.clear();
      EffectHandler.ExtArgs extArgs = EffectHandler.ExtArgs.of();
      EffectManager.inst().handleEffect(extArgs, session, currentFightLV.reward);

      //next fight
      int nextFightId = currentFightLV.id + 1;
      if (nextFightId >= FightData.fightMap.size())
        return "max_fight";
      FightData.FightLV nextFightLV = FightData.of(nextFightId);
      if (nextFightLV == null)
        return "invalid_fight";
      currentFightLV = nextFightLV;

      return "ok";
    }
    else {
      long deduct                   =   (int)(session.userGameInfo.fan*totalTalent /
                                        currentFightLV.fanNPC) - fixConsume;
      session.userGameInfo.fan      = 0;
      currentFightLV.fanNPC        -= deduct;
      return "lose";
    }
  }

  private String handleBossFight(Session session, int idolId) {
    if (usedIdols.contains(idolId))
      return "idol_already_fought";
    Idols.Idol idol = session.userIdol.idolMap.get(idolId);

    if (idol == null)
      return "invalid_idol";

    if (currentFightLV.boss == null)
      return "invalid_boss";

    usedIdols.add(idolId);

    long totalAptBuf  = idol.crtApt + idol.perfApt + idol.attrApt;
    long totalProps   = idol.creativity + idol.performance + idol.attractive;
    long damage       = totalAptBuf*idol.level*IDOL_DAMAGE_SCALE + totalProps;

    if (damage >= currentFightLV.boss.hp) {
      session.effectResults.clear();
      EffectHandler.ExtArgs extArgs = EffectHandler.ExtArgs.of();
      EffectManager.inst().handleEffect(extArgs, session, currentFightLV.boss.reward);

      int nextFightId = currentFightLV.id + 1;
      if (nextFightId >= FightData.fightMap.size())
        return "max_fight";
      FightData.FightLV nextFightLV = FightData.of(nextFightId);
      if (nextFightLV == null)
        return "invalid_fight";
      currentFightLV = nextFightLV;
      usedIdols.clear();
      restoreIdols.clear();
      return "ok";
    }
    else {
      currentFightLV.boss.hp -= damage;
      return "lose";
    }
  }

  public String freeUsedIdol(Session session, int idolId) {
    if (restoreIdols.contains(idolId))
      return "idol_already_restore";

    int idx = usedIdols.indexOf(idolId);
    if (idx == -1)
      return "invalid_idol";

    if (!session.userInventory.haveItem(IDOL_FREE_FIGHT_ITEM, 1))
      return "insufficient_item";

    session.userInventory.useItem(IDOL_FREE_FIGHT_ITEM, 1);
    usedIdols.remove(idx);
    restoreIdols.add(idolId);
    return "ok";
  }

  public void setLevel(int levelId) {
    currentFightLV = FightData.fightMap.get(levelId);
  }


  /*GAME SHOW**********************************************************************************************************/

  public String handleGameShowFight(Session session, int idolId) {
    if (gameShowUsedIdols.contains(idolId))
      return "idol_already_fought";
    Idols.Idol idol = session.userIdol.idolMap.get(idolId);

    if (idol == null)
      return "invalid_idol";

    if (currentGameShow.id ==  -1)
      return "game_show_max";

    gameShowUsedIdols.add(idolId);

    long totalAptBuf  = idol.crtApt + idol.perfApt + idol.attrApt;
    long totalProps   = idol.creativity + idol.performance + idol.attractive;
    long damage       = totalAptBuf*idol.level*IDOL_DAMAGE_SCALE + totalProps;

    if (damage >= currentGameShow.bosshp) {
      session.effectResults.clear();
      EffectHandler.ExtArgs extArgs = EffectHandler.ExtArgs.of();
      for (List<Integer> r : currentGameShow.reward)
        EffectManager.inst().handleEffect(extArgs, session, r);

      int nextFightId = currentGameShow.id + 1;
      GameShowData.GameShow nextShow = GameShowData.of(nextFightId);
      if (nextShow == null)
        currentGameShow = GameShowData.ofNullObject();
      else
        currentGameShow = nextShow;

      return "ok";
    }
    else {
      currentGameShow.bosshp -= damage;
      return "lose";
    }
  }

  public String freeUsedGameShowIdol(Session session, int idolId) {
    if (gameShowRestoreIdols.contains(idolId))
      return "idol_already_restore";

    int idx = gameShowUsedIdols.indexOf(idolId);
    if (idx == -1)
      return "invalid_idol";

    if (!session.userInventory.haveItem(IDOL_FREE_FIGHT_ITEM, 1))
      return "insufficient_item";

    session.userInventory.useItem(IDOL_FREE_FIGHT_ITEM, 1);
    gameShowUsedIdols.remove(idx);
    gameShowRestoreIdols.add(idolId);
    return "ok";
  }

  /*RUN SHOW***********************************************************************************************************/

  public String handleRunShowFight(Session session) {
    if (currentRunShow.id == -1)
      return "max_run_show";

    long fixConsume       = (int)(currentRunShow.randFanNPC*0.1f);
    long totalTalent      = session.userIdol.getTotalCreativity()
            + session.userIdol.getTotalPerformance()
            + session.userIdol.getTotalAttractive();
    long expectedConsume  = (currentRunShow.randFanNPC*currentRunShow.randAptNPC/totalTalent) + fixConsume;

    if (session.userGameInfo.fan >= expectedConsume) {
      session.userGameInfo.fan -= expectedConsume;

      //reward
      session.effectResults.clear();
      EffectHandler.ExtArgs extArgs = EffectHandler.ExtArgs.of();

      List<Integer> rewardFormat = Arrays.asList(100,0,1,0);
      for (Integer item : currentRunShow.reward) {
        rewardFormat.set(1, item);
        int rand = ThreadLocalRandom.current().nextInt(1, 100 + 1);
        if (rand <= RUN_SHOW_DROP_RATE)
          EffectManager.inst().handleEffect(extArgs, session, rewardFormat);
      }

      EffectManager.inst().handleEffect(extArgs, session, RUN_SHOW_EXP_REWARD);

      //next fight
      int nextFightId = currentRunShow.id + 1;
      currentRunShow = RunShowData.of(nextFightId);

      return "ok";
    }
    else {
      session.userGameInfo.fan      = 0;
      return "lose";
    }
  }

  public String handleMultiRunShowFight(Session session, int time) {
    if (time < 0)
      return "invalid_time";
    if (currentRunShow.id == -1 || currentRunShow.id + time - 1 > RunShowData.runShowMap.size())
      return "max_run_show";

    long totalExpectConsume = 0;

    for (int i = 0; i < time; i++) {
      RunShowData.RunShow rs = RunShowData.of(currentRunShow.id + i);
      if (rs.id == -1)
        return "run_show_invalid";
      long avrFanNPC        = (rs.minFanNPC + rs.maxFanNPC)/2;
      long avrAptNPC        = (rs.minAptNPC + rs.maxAptNPC)/2;
      long fixConsume       = (long)(avrAptNPC*0.1f);
      long totalTalent      = session.userIdol.getTotalCreativity()
              + session.userIdol.getTotalPerformance()
              + session.userIdol.getTotalAttractive();
      long expectedConsume  = (avrFanNPC*avrAptNPC/totalTalent) + fixConsume;
      totalExpectConsume   += expectedConsume;
    }

    //reward
    session.effectResults.clear();
    List<Integer> rewardFormat = Arrays.asList(100,0,1,0);
    EffectHandler.ExtArgs extArgs = EffectHandler.ExtArgs.of();

    if (session.userGameInfo.fan >= totalExpectConsume) {
      session.userGameInfo.fan -= totalExpectConsume;

      for (int i = 0; i < time; i++) {
        RunShowData.RunShow rs = RunShowData.of(i + currentRunShow.id);
        if (rs.id == -1)
          return "run_show_invalid";

        for (Integer item : rs.reward) {
          rewardFormat.set(1, item);
          int rand = ThreadLocalRandom.current().nextInt(1, 100 + 1);
          if (rand <= RUN_SHOW_DROP_RATE)
            EffectManager.inst().handleEffect(extArgs, session, rewardFormat);
        }

        EffectManager.inst().handleEffect(extArgs, session, RUN_SHOW_EXP_REWARD);
      }

      //next fight
      int nextFightId = currentRunShow.id + time;
      currentRunShow = RunShowData.of(nextFightId);

      return "ok";
    }
    else {
      session.userGameInfo.fan      = 0;
      return "lose";
    }
  }

  /*SHOPPING***********************************************************************************************************/

  public String handleShoppingFight(Session session) {
    if (currentShopping.id == -1)
      return "max_shopping";

    long moneyConsume;
    long base         = SHOPPING_COEFFICIENT*currentShopping.creativeNPC;
    long totalCrt     = session.userIdol.getTotalCreativity();

    if (base >= totalCrt) {
      moneyConsume = currentShopping.moneyNPC*currentShopping.creativeNPC /totalCrt;
    }
    else {
      moneyConsume = (long)(currentShopping.moneyNPC*SHOPPING_MONEY_SCL);
    }

    if (session.userGameInfo.money >= moneyConsume) {
      session.userGameInfo.money -= moneyConsume;

      //reward
      session.effectResults.clear();
      EffectHandler.ExtArgs extArgs = EffectHandler.ExtArgs.of();

      List<Integer> rewardFormat = Arrays.asList(100,0,1,0);
      for (Integer item : currentShopping.reward) {
        rewardFormat.set(1, item);
        int rand = ThreadLocalRandom.current().nextInt(1, 100 + 1);
        if (rand <= RUN_SHOW_DROP_RATE)
          EffectManager.inst().handleEffect(extArgs, session, rewardFormat);
      }

      int nextFightId   = currentShopping.id + 1;
      currentShopping   = ShoppingData.of(nextFightId);
      return "ok";
    }
    else {
      return "lose";
    }
  }

  public String handleMultiShoppingFight(Session session, int time) {
    if (time < 0)
      return "invalid_time";

    if (currentShopping.id == -1 || currentShopping.id + time - 1 > ShoppingData.shoppingMap.size())
      return "max_shopping";


    long totalMoneyConsume = 0;
    for (int i = 0; i < time; i++) {
      ShoppingData.Shopping sp = ShoppingData.of(currentShopping.id + i);
      if (sp.id == -1)
        return "run_show_invalid";

      long moneyConsume;
      long base         = SHOPPING_COEFFICIENT*currentShopping.creativeNPC;
      long totalCrt     = session.userIdol.getTotalCreativity();

      if (base >= totalCrt) {
        moneyConsume = currentShopping.moneyNPC*currentShopping.creativeNPC /totalCrt;
      }
      else {
        moneyConsume = (long)(currentShopping.moneyNPC*SHOPPING_MONEY_SCL);
      }
      totalMoneyConsume += moneyConsume;
    }

    if (session.userGameInfo.money >= totalMoneyConsume) {
      session.userGameInfo.money -= totalMoneyConsume;

      //reward
      session.effectResults.clear();
      List<Integer> rewardFormat = Arrays.asList(100,0,1,0);
      EffectHandler.ExtArgs extArgs = EffectHandler.ExtArgs.of();

      for (int i = 0; i < time; i++) {
        ShoppingData.Shopping sp = ShoppingData.of(currentShopping.id + 1);
        if (sp.id == -1)
          return "run_show_invalid";

        for (Integer item : sp.reward) {
          rewardFormat.set(1, item);
          int rand = ThreadLocalRandom.current().nextInt(1, 100 + 1);
          if (rand <= RUN_SHOW_DROP_RATE)
            EffectManager.inst().handleEffect(extArgs, session, rewardFormat);
        }
      }

      int nextShoppingId  = currentShopping.id + time;
      currentShopping      = ShoppingData.of(nextShoppingId);
      return "ok";
    }
    else {
      return "lose";
    }
  }
}