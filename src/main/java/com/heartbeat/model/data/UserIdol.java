package com.heartbeat.model.data;

import com.heartbeat.effect.EffectHandler;
import com.heartbeat.effect.EffectManager;
import com.heartbeat.model.Session;
import com.statics.*;
import com.transport.model.Idols;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("unused")
public class UserIdol extends Idols {
  public static final int     CREATIVITY              = 2; //trí lực
  public static final int     PERFORMANCE             = 3; //chính trị
  public static final int     ATTRACTIVE              = 4; //mị lực
  public static final int     GROUP_HALO              = 1;
  public static final int     PERSONAL_HALO           = 0;

  public static final int     EXP_PER_UPGRADE         = 200;
  public static final int     EXP_UP_STEP             = 1;

  public static final int     CRT_UP_ITEM             = 67;
  public static final int     PERF_UP_ITEM            = 68;
  public static final int     ATTR_UP_ITEM            = 69;
  public static List<Integer> APT_UP_RATE             = new ArrayList<>();
  public static final int     APT_UP_COST             = 1;

  static {
    APT_UP_RATE.add(0);
    APT_UP_RATE.add(100);
    APT_UP_RATE.add(0);
    APT_UP_RATE.add(30);
    APT_UP_RATE.add(0);
    APT_UP_RATE.add(23);
  }

  private transient Map<Integer, List<Idol>> halo2Idol; //group Idol by Halos

  public static UserIdol ofDefault() {
    UserIdol defaultUserIdol = new UserIdol();
    defaultUserIdol.idolMap = new HashMap<>();
    Idol defaultIdol = buildIdol(1);
    if (defaultIdol != null)
      defaultUserIdol.addIdol(defaultIdol);
    return defaultUserIdol;
  }

  /********************************************************************************************************************/

  public void reBalance() {
    //groupByHalo();
    //HaloData.gUpdateGroupHalo(idolMap, halo2Idol);
  }

  public UserIdol() {
    idolMap   = new HashMap<>();
    halo2Idol = new HashMap<>();
  }

  public boolean addIdol(Idol idol) {
    if (idol != null) {
      Idol oldIdol = idolMap.get(idol.id);
      if (oldIdol != null)
        return false;
      idolMap.put(idol.id, idol);
      groupByHalo();
      HaloData.gUpdateGroupHalo(idolMap, halo2Idol);
      for (IdolHalo idolHalo : idol.personalHalos)
        HaloData.reCalcPHalo(idol, idolHalo);
      return true;
    }
    return false;
  }

  private void groupByHalo() {
    halo2Idol.clear();
    idolMap.values().forEach(idol -> idol.groupHaloIds.forEach(haloId -> {
      halo2Idol.computeIfAbsent(haloId, k -> new ArrayList<>());
      halo2Idol.get(haloId).add(idol);
    }));
  }

  public String levelUp(Session session, int idolId) { //todo update total properties
    Idol idol = idolMap.get(idolId);
    if (idol == null)
      return "idol_not_exist";
    int currentLV = idol.level;

    if (currentLV >= ServantLVData.servantLV.size())
      return "idol_max_level";

    ServantLVData.ServantLV nextLevel = ServantLVData.servantLV.get(currentLV + 1);

    if (nextLevel == null)
      return "idol_level_invalid";

    if (session.userGameInfo.money < nextLevel.exp)
      return "idol_level_insufficient_exp";

    ServantHonorData.ServantHonor curHonor = ServantHonorData.honorMap.get(idol.honorID);
    if (curHonor == null)
      return "idol_honor_invalid";

    if (idol.level >= curHonor.maxServantLV)
      return "idol_honor_max_level";

    session.userGameInfo.money -= nextLevel.exp;
    idol.level += 1;
    onPropertiesChange(idol);
    return "ok";
  }

  public String addAptByExp(int idolId, int speciality) {
    Idol idol = idolMap.get(idolId);
    if (idol == null)
      return "idol_not_exist";
    if (idol.aptitudeExp < EXP_PER_UPGRADE)
      return "insufficient_aptitude_exp";

    int currentLimit = BookLimitData.getCurrentLimit(idolId, speciality, idol.level);
    if (currentLimit < 0)
      return "idol_book_limit_invalid";

    switch (speciality) {
      case CREATIVITY:
        if (idol.crtApt + EXP_UP_STEP > currentLimit)
          return "aptitude_limit";
        idol.crtApt += EXP_UP_STEP;
        break;
      case PERFORMANCE:
        if (idol.perfApt + EXP_UP_STEP > currentLimit)
          return "aptitude_limit";
        idol.perfApt += EXP_UP_STEP;
      case ATTRACTIVE:
        if (idol.attrApt + EXP_UP_STEP > currentLimit)
          return "aptitude_limit";
        idol.attrApt += EXP_UP_STEP;
    }
    idol.aptitudeExp -= EXP_PER_UPGRADE;
    onPropertiesChange(idol);
    return "ok";
  }

  public String addAptByItem(Session session, int idolId, int speciality, int step) {
    Idol idol = idolMap.get(idolId);
    if (idol == null)
      return "idol_not_exist";

    if (step < 1 || step > 5 || APT_UP_RATE.get(step) == 0) //+1 100%, +3 30%, +5 20%
      return "invalid_step";

    int currentLimit = BookLimitData.getCurrentLimit(idolId, speciality, idol.level);
    if (currentLimit < 0)
      return "idol_book_limit_invalid";

    int rate = APT_UP_RATE.get(step);
    int rand = ThreadLocalRandom.current().nextInt(1, 100); //both side inclusive
    switch (speciality) {
      case CREATIVITY:
        if (!session.userInventory.haveItem(CRT_UP_ITEM, APT_UP_COST))
          return "insufficient_item";

        if (idol.crtApt + EXP_UP_STEP > currentLimit)
          return "aptitude_limit";

        session.userInventory.useItem(CRT_UP_ITEM, APT_UP_COST);
        if (rand <= rate) {
          idol.crtApt += step;
          onPropertiesChange(idol);
          return "ok";
        }
        else {
          return "apt_up_fail";
        }
      case PERFORMANCE:
        if (!session.userInventory.haveItem(PERF_UP_ITEM, APT_UP_COST))
          return "insufficient_item";

        if (idol.perfApt + EXP_UP_STEP > currentLimit)
          return "aptitude_limit";

        session.userInventory.useItem(PERF_UP_ITEM, APT_UP_COST);
        if (rand <= rate) {
          idol.perfApt += step;
          onPropertiesChange(idol);
          return "ok";
        }
        else {
          return "apt_up_fail";
        }
      case ATTRACTIVE:
        if (!session.userInventory.haveItem(ATTR_UP_ITEM, APT_UP_COST))
          return "insufficient_item";

        if (idol.attrApt + EXP_UP_STEP > currentLimit)
          return "aptitude_limit";

        session.userInventory.useItem(ATTR_UP_ITEM, APT_UP_COST);
        if (rand <= rate) {
          idol.attrApt += step;
          onPropertiesChange(idol);
          return "ok";
        }
        else {
          return "apt_up_fail";
        }
      default:
        return "invalid_speciality";
    }
  }

  @com.fasterxml.jackson.annotation.JsonIgnore
  public long getTotalCreativity() {
    return idolMap.values().stream().mapToInt(e -> e.creativity).sum();
  }

  @com.fasterxml.jackson.annotation.JsonIgnore
  public long getTotalPerformance() {
    return idolMap.values().stream().mapToInt(e -> e.performance).sum();
  }

  @com.fasterxml.jackson.annotation.JsonIgnore
  public long getTotalAttractive() {
    return idolMap.values().stream().mapToInt(e -> e.attractive).sum();
  }

  public String idolPersonalHaloLevelUp(Session session, int idolId, int haloId) {
    Idol idol = idolMap.get(idolId);
    if (idol == null)
      return "idol_not_exist";

    IdolHalo pHalo = null;
    for (IdolHalo halo : idol.personalHalos)
      if (halo.id == haloId)
        pHalo = halo;

    if (pHalo == null)
      return "halo_not_exist";

    if (!checkPrefixCondition(idol, pHalo))
      return "prefix_condition_not_match";

    List<Integer> updateItems = pHalo.updateItems;
    if (updateItems.size() != 4)
      return "halo_invalid";
    int itemId = updateItems.get(EffectHandler.PARAM1);
    int amount = updateItems.get(EffectHandler.PARAM2);

    if (!session.userInventory.haveItem(itemId, amount))
      return "insufficient_item";

    String result = HaloData.pHaloLevelUp(idol, pHalo);
    if (result.equals("ok"))
      session.userInventory.useItem(itemId, amount);
    return result;
  }

  private static boolean checkPrefixCondition(Idol idol, IdolHalo pHalo) {
    //check prefixCondition
    HaloData.HaloDTO haloDTO = HaloData.haloMap.get(pHalo.id);
    if (haloDTO == null)
      return false;

    if (haloDTO.preFixHalo == null)
      return true;

    if (haloDTO.preFixHalo.size() == 2) {
      int prefixID          = haloDTO.preFixHalo.get(0);
      int prefixLevelCond   = haloDTO.preFixHalo.get(1);
      IdolHalo preFixHalo   = null;
      for (IdolHalo pfHalo : idol.personalHalos) {
        if (pfHalo.id == prefixID)
          preFixHalo = pfHalo;
      }
      if (preFixHalo == null) //pHalo have prefixData but can not found prefixHalo
        return false;

      return preFixHalo.level == prefixLevelCond;
    }
    return true;
  }

  public String idolMaxLevelUnlock(Session session, int idolId) {
    Idol idol = idolMap.get(idolId);
    if (idol == null)
      return "idol_not_exist";

    ServantHonorData.ServantHonor honor     = ServantHonorData.honorMap.get(idol.honorID);
    ServantHonorData.ServantHonor nextHonor = ServantHonorData.honorMap.get(idol.honorID + 1); //todo aware of data inconsistency
    if (nextHonor == null || honor == null)
      return "idol_honor_max_level";
    List<List<Integer>> need = nextHonor.needFormat; //nguyên liệu cần để lên

    boolean isEnough = true;
    for (List<Integer> format : need) {
      int propId = format.get(EffectHandler.PARAM1);
      int amount = format.get(EffectHandler.PARAM2);
      if (!session.userInventory.haveItem(propId, amount))
        isEnough = false;
    }

    if (!isEnough)
      return "insufficient_item";

    for (List<Integer> format : need) {
      int propId = format.get(EffectHandler.PARAM1);
      int amount = format.get(EffectHandler.PARAM2);
      session.userInventory.useItem(propId, amount);
    }
    idol.honorID = nextHonor.honorID;

    //add aptitude exp
    EffectHandler.ExtArgs extArgs = EffectHandler.ExtArgs.ofDefault(idolId, 0, "");
    EffectManager.inst().handleEffect(extArgs, session, honor.rewardFormat);

    return "ok";
  }

  /********************************************************************************************************************/

  public static Idol buildIdol(int idolID) {
    Idol idol;
    ServantData.Servant servant = ServantData.servantMap.get(idolID);
    if (servant != null && servant.defaultProperties.size() == 3) {
      idol                        = new Idol();
      idol.id                     = servant.servantID;
      idol.level                  = 1;
      idol.specialityID           = servant.specialityID;
      idol.groupHaloIds           = new ArrayList<>();
      idol.personalHalos          = new ArrayList<>();
      idol.groupHalo              = new ArrayList<>();

      for (Integer haloId : servant.halo) {
        HaloData.HaloDTO haloDTO = HaloData.haloMap.get(haloId);
        if (haloDTO != null) {
          if (haloDTO.type == GROUP_HALO) {
            idol.groupHaloIds.add(haloId);
          }
          else if (haloDTO.type == PERSONAL_HALO) {
            IdolHalo pHalo = HaloData.makeHalo(haloId);
            if (pHalo != null)
              idol.personalHalos.add(pHalo);
          }
        }
      }

      idol.crtItemBuf             = 0;
      idol.perfItemBuf            = 0;
      idol.attrItemBuf            = 0;
      idol.crtAptBuf              = 0;
      idol.perfAptBuf             = 0;
      idol.attrAptBuf             = 0;
      idol.aptitudeExp            = 0;
      idol.aptitudeExp            = 2000;
      idol.totalCrtHLBuf          = 0;
      idol.totalPerfHLBuf         = 0;
      idol.totalAttrHLBuf         = 0;
      idol.honorID                = 1; //todo must have 1
      idol.crtApt                 = servant.defaultProperties.get(0);
      idol.perfApt                = servant.defaultProperties.get(1);
      idol.attrApt                = servant.defaultProperties.get(2);


      //todo this one 1 first shot, move to props change
      idol.crtAptBuf    = idol.crtApt*10 + idol.crtApt*idol.level*(idol.level + 1)/10;
      idol.perfAptBuf   = idol.perfApt*10 + idol.perfApt*idol.level*(idol.level + 1)/10;
      idol.attrAptBuf   = idol.attrApt*10 + idol.attrApt*idol.level*(idol.level + 1)/10;

      idol.creativity   = idol.crtAptBuf + idol.crtItemBuf;
      idol.performance  = idol.perfAptBuf + idol.perfItemBuf;
      idol.attractive   = idol.attrAptBuf + idol.attrItemBuf;
      return idol;
    }
    else {
      return null;
    }
  }

  /********************************************************************************************************************/

  public static void onPropertiesChange(Idol idol) {
    idol.crtAptBuf    = idol.crtApt*10 + idol.crtApt*idol.level*(idol.level + 1)/10;
    idol.perfAptBuf   = idol.perfApt*10 + idol.perfApt*idol.level*(idol.level + 1)/10;
    idol.attrAptBuf   = idol.attrApt*10 + idol.attrApt*idol.level*(idol.level + 1)/10;
    float sumCrtHLBufRate = (float)(idol.personalHalos.stream().mapToDouble(halo -> halo.crtBufRate).sum() +
            idol.groupHalo.stream().mapToDouble(halo -> halo.crtBufRate).sum());

    float sumPerfHLBufRate = (float)(idol.personalHalos.stream().mapToDouble(halo -> halo.perfBufRate).sum() +
            idol.groupHalo.stream().mapToDouble(halo -> halo.perfBufRate).sum());

    float sumAttrHLBufRate = (float)(idol.personalHalos.stream().mapToDouble(halo -> halo.attrBufRate).sum() +
            idol.groupHalo.stream().mapToDouble(halo -> halo.attrBufRate).sum());

    idol.totalCrtHLBuf = (int)(sumCrtHLBufRate *(idol.crtItemBuf + idol.crtAptBuf));
    idol.totalPerfHLBuf = (int)(sumPerfHLBufRate *(idol.perfItemBuf + idol.perfAptBuf));
    idol.totalAttrHLBuf = (int)(sumAttrHLBufRate *(idol.attrItemBuf + idol.attrAptBuf));

    idol.creativity   = idol.crtAptBuf + idol.crtItemBuf + idol.totalCrtHLBuf;
    idol.performance  = idol.perfAptBuf + idol.perfItemBuf + idol.totalPerfHLBuf;
    idol.attractive   = idol.attrAptBuf + idol.attrItemBuf + idol.totalAttrHLBuf;
  }
}