package com.heartbeat.model.data;

import com.common.Msg;
import com.heartbeat.effect.EffectHandler;
import com.heartbeat.effect.EffectManager;
import com.heartbeat.model.Session;
import com.statics.*;
import com.transport.EffectResult;
import com.transport.model.Idols;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import static com.common.Constant.*;
import static com.common.Constant.USER_IDOL.*;

public class UserIdol extends Idols {
  public int                                  dailyRampage;
  public int                                  maxDailyRampage;

  private transient Map<Integer, List<Idol>>  halo2Idol;    //group Idol by Halos
  public  transient UserEvent                 userEvent;    //ref;
  public  transient UserRanking               userRanking;  //ref;
  public  transient Session                   session;      //ref;

  public static UserIdol ofDefault() {
    UserIdol defaultUserIdol = new UserIdol();
    defaultUserIdol.idolMap = new HashMap<>();

    for (Integer idolId : DEFAULT_IDOLS) {
      Idol defaultIdol = buildIdol(idolId);
      if (defaultIdol != null)
        defaultUserIdol.addIdol(defaultIdol);
    }

    defaultUserIdol.maxDailyRampage = 1;
    return defaultUserIdol;
  }

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

      idol.aptitudeExp            = INIT_APT_EXP;
      idol.honorID                = 1; //todo must have 1
      idol.crtApt                 = servant.defaultProperties.get(0);
      idol.perfApt                = servant.defaultProperties.get(1);
      idol.attrApt                = servant.defaultProperties.get(2);
      idol.crtItemBuf             = 0;
      idol.perfItemBuf            = 0;
      idol.attrItemBuf            = 0;

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

  public void newDay() {
    dailyRampage = 0;
  }

  public void onPropertiesChange(Idol idol) {
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

    int newCrt        = idol.crtAptBuf + idol.crtItemBuf + idol.totalCrtHLBuf;
    int newPerf       = idol.perfAptBuf + idol.perfItemBuf + idol.totalPerfHLBuf;
    int newAttr       = idol.attrAptBuf + idol.attrItemBuf + idol.totalAttrHLBuf;

    if (newCrt - idol.creativity > 0) {
      if (userEvent != null)
        userEvent.addEventRecord(COMMON_EVENT.TOTAL_TALENT_EVT_ID, newCrt - idol.creativity);
      if (userRanking != null)
        userRanking.addEventRecord(RANK_EVENT.TOTAL_TALENT_RANK_ID, newCrt - idol.creativity);
      idol.creativity = newCrt;
    }

    if (newPerf - idol.performance > 0) {
      if (userEvent != null)
        userEvent.addEventRecord(COMMON_EVENT.TOTAL_TALENT_EVT_ID, newPerf - idol.performance);
      if (userRanking != null)
        userRanking.addEventRecord(RANK_EVENT.TOTAL_TALENT_RANK_ID, newPerf - idol.performance);
      idol.performance = newPerf;
    }

    if (newAttr - idol.attractive > 0) {
      if (userEvent != null)
        userEvent.addEventRecord(COMMON_EVENT.TOTAL_TALENT_EVT_ID, newAttr - idol.attractive);
      if (userRanking != null)
        userRanking.addEventRecord(RANK_EVENT.TOTAL_TALENT_RANK_ID, newAttr - idol.attractive);
      idol.attractive = newAttr;
    }

    //update userProfile
    if (session != null) {
      session.userGameInfo.totalCrt = totalCrt();
      session.userGameInfo.totalPerf = totalPerf();
      session.userGameInfo.totalAttr = totalAttr();
    }
  }

  /********************************************************************************************************************/

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
      gUpdateGroupHalo(halo2Idol);

      for (IdolHalo idolHalo : idol.personalHalos) {
        reCalcPHalo(idol, idolHalo);
      }
      return true;
    }
    return false;
  }

  public String levelUp(Session session, int idolId) { //todo update total properties
    Idol idol = idolMap.get(idolId);
    if (idol == null)
      return Msg.map.getOrDefault(Msg.IDOL_NOT_EXIST, "idol_not_exist");
    int currentLV = idol.level;

    if (currentLV >= ServantLVData.servantLV.size())
      return Msg.map.getOrDefault(Msg.IDOL_MAX_LEVEL, "idol_max_level");

    ServantLVData.ServantLV nextLevel = ServantLVData.servantLV.get(currentLV + 1);

    if (nextLevel == null)
      return Msg.map.getOrDefault(Msg.DTO_DATA_NOT_FOUND, "idol_level_invalid");

    if (session.userGameInfo.money < nextLevel.exp)
      return Msg.map.getOrDefault(Msg.IDOL_LV_UP_INSUFFICIENT, "idol_lv_up_insufficient");

    ServantHonorData.ServantHonor curHonor = ServantHonorData.honorMap.get(idol.honorID);
    if (curHonor == null)
      return Msg.map.getOrDefault(Msg.DTO_DATA_NOT_FOUND, "idol_honor_invalid");

    if (idol.level >= curHonor.maxServantLV)
      return Msg.map.getOrDefault(Msg.IDOL_HONOR_MAX_LEVEL, "idol_honor_max_level");

    //calc rampage level buff
    if (        idol.level <= MAX_RAMPAGE_ALLOW_LV
            &&  dailyRampage < maxDailyRampage
            &&  !EXCLUDE_RAMPAGE_LEVEL.contains(idol.level)) {

      int r = ThreadLocalRandom.current().nextInt(0, 101);
      if (r <= RAMPAGE_BUFF_PERCENT) {
        dailyRampage++;
        idol.level += RAMPAGE_BUFF_LV_CNT;
        session.effectResults.add(EffectResult.of(EFFECT_RESULT.RAMPAGE_EFFECT_RESULT, 1,0));
      }
    }

    session.userGameInfo.spendMoney(session, nextLevel.exp);
    idol.level += 1;
    onPropertiesChange(idol);
    return "ok";
  }

  public String addAptByExp(int idolId, int speciality) {
    Idol idol = idolMap.get(idolId);
    if (idol == null)
      return Msg.map.getOrDefault(Msg.IDOL_NOT_EXIST, "idol_not_exist");
    if (idol.aptitudeExp < APT_EXP_COST_PER_UPGRADE)
      return Msg.map.getOrDefault(Msg.INSUFFICIENT_APT_EXP, "insufficient_apt_exp");

    int currentLimit = BookLimitData.getCurrentLimit(idolId, speciality, idol.level);
    if (currentLimit < 0)
      return Msg.map.getOrDefault(Msg.DTO_DATA_NOT_FOUND, "idol_book_limit_invalid");

    switch (speciality) {
      case CREATIVITY:
        if (idol.crtApt + EXP_UP_STEP > currentLimit)
          return Msg.map.getOrDefault(Msg.APT_LIMIT, "idol_apt_limit");
        idol.crtApt += EXP_UP_STEP;
        break;
      case PERFORMANCE:
        if (idol.perfApt + EXP_UP_STEP > currentLimit)
          return Msg.map.getOrDefault(Msg.APT_LIMIT, "idol_apt_limit");
        idol.perfApt += EXP_UP_STEP;
        break;
      case ATTRACTIVE:
        if (idol.attrApt + EXP_UP_STEP > currentLimit)
          return Msg.map.getOrDefault(Msg.APT_LIMIT, "idol_apt_limit");
        idol.attrApt += EXP_UP_STEP;
        break;
      default:
        break;
    }
    idol.aptitudeExp -= APT_EXP_COST_PER_UPGRADE;
    onPropertiesChange(idol);
    return "ok";
  }

  public String addAptByItem(Session session, int idolId, int speciality, int step) {
    Idol idol = idolMap.get(idolId);
    if (idol == null)
      return Msg.map.getOrDefault(Msg.IDOL_NOT_EXIST, "idol_not_exist");

    if (step < 1 || step > 5 || APT_UP_RATE.get(step) == 0) //+1 100%, +3 30%, +5 20%
      return Msg.map.getOrDefault(Msg.MALFORM_ARGS, "malform_args");

    int currentLimit = BookLimitData.getCurrentLimit(idolId, speciality, idol.level);
    if (currentLimit < 0)
      return Msg.map.getOrDefault(Msg.DTO_DATA_NOT_FOUND, "idol_book_limit_invalid");

    int rate = APT_UP_RATE.get(step);
    int rand = ThreadLocalRandom.current().nextInt(1, 101);
    switch (speciality) {
      case CREATIVITY:
        if (!session.userInventory.haveItem(CRT_UP_ITEM, APT_UP_COST))
          return Msg.map.getOrDefault(Msg.INSUFFICIENT_ITEM, "insufficient_item");

        if (idol.crtApt + EXP_UP_STEP > currentLimit)
          return Msg.map.getOrDefault(Msg.APT_LIMIT, "idol_apt_limit");

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
          return Msg.map.getOrDefault(Msg.INSUFFICIENT_ITEM, "insufficient_item");

        if (idol.perfApt + EXP_UP_STEP > currentLimit)
          return Msg.map.getOrDefault(Msg.APT_LIMIT, "aptitude_limit");

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
          return Msg.map.getOrDefault(Msg.INSUFFICIENT_ITEM, "insufficient_item");

        if (idol.attrApt + EXP_UP_STEP > currentLimit)
          return Msg.map.getOrDefault(Msg.APT_LIMIT, "idol_apt_limit");

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
        return Msg.map.getOrDefault(Msg.MALFORM_ARGS, "malform_args");
    }
  }

  @com.fasterxml.jackson.annotation.JsonIgnore
  public long totalCrt() {
    return idolMap.values().stream().mapToInt(e -> e.creativity).sum();
  }

  @com.fasterxml.jackson.annotation.JsonIgnore
  public long totalPerf() {
    return idolMap.values().stream().mapToInt(e -> e.performance).sum();
  }

  @com.fasterxml.jackson.annotation.JsonIgnore
  public long totalAttr() {
    return idolMap.values().stream().mapToInt(e -> e.attractive).sum();
  }

  public String idolPersonalHaloLevelUp(Session session, int idolId, int haloId) {
    Idol idol = idolMap.get(idolId);
    if (idol == null)
      return Msg.map.getOrDefault(Msg.IDOL_NOT_EXIST, "idol_not_exist");

    IdolHalo pHalo = null;
    for (IdolHalo halo : idol.personalHalos)
      if (halo.id == haloId)
        pHalo = halo;

    if (pHalo == null)
      return Msg.map.getOrDefault(Msg.HALO_NOT_EXIST, "halo_not_exist");

    if (!checkPrefixCondition(idol, pHalo))
      return Msg.map.getOrDefault(Msg.HALO_PREFIX_NOT_MATCH, "halo_prefix_not_match");

    HaloData.HaloDTO haloDTO = HaloData.haloMap.get(haloId);
    if (haloDTO == null)
      return Msg.map.getOrDefault(Msg.DTO_DATA_NOT_FOUND, "halo_data_not_found");
    List<Integer> updateItems = haloDTO.updateItem;

    if (updateItems.size() != 4)
      return Msg.map.getOrDefault(Msg.HALO_DATA_INVALID, "halo_invalid");
    int itemId = updateItems.get(EffectHandler.PARAM1);
    int amount = updateItems.get(EffectHandler.PARAM2);

    if (!session.userInventory.haveItem(itemId, amount))
      return Msg.map.getOrDefault(Msg.INSUFFICIENT_ITEM, "insufficient_item");

    String result = pHaloLevelUp(idol, pHalo);
    if (result.equals("ok")) {
      session.userInventory.useItem(itemId, amount);
    }
    return result;
  }

  public String idolMaxLevelUnlock(Session session, int idolId) {
    Idol idol = idolMap.get(idolId);
    if (idol == null)
      return Msg.map.getOrDefault(Msg.IDOL_NOT_EXIST, "idol_not_exist");

    ServantHonorData.ServantHonor honor     = ServantHonorData.honorMap.get(idol.honorID);
    ServantHonorData.ServantHonor nextHonor = ServantHonorData.honorMap.get(idol.honorID + 1); //todo aware of data inconsistency
    if (nextHonor == null || honor == null)
      return Msg.map.getOrDefault(Msg.IDOL_HONOR_MAX_LEVEL, "idol_honor_max_level");
    List<List<Integer>> need = nextHonor.needFormat; //nguyên liệu cần để lên

    boolean isEnough = true;
    for (List<Integer> format : need) {
      int propId = format.get(EffectHandler.PARAM1);
      int amount = format.get(EffectHandler.PARAM2);
      if (!session.userInventory.haveItem(propId, amount))
        isEnough = false;
    }

    if (!isEnough)
      return Msg.map.getOrDefault(Msg.INSUFFICIENT_ITEM, "insufficient_item");

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

  private void groupByHalo() {
    halo2Idol.clear();
    idolMap.values().forEach(idol -> idol.groupHaloIds.forEach(haloId -> {
      halo2Idol.computeIfAbsent(haloId, k -> new ArrayList<>());
      halo2Idol.get(haloId).add(idol);
    }));
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

  private void gUpdateGroupHalo(Map<Integer, List<Idol>> halo2Idol) {
    idolMap.values().forEach(idol -> idol.groupHalo.clear()); // clear group halos

    for (Map.Entry<Integer, List<Idols.Idol>> entry : halo2Idol.entrySet()) {
      int haloID = entry.getKey();
      HaloData.HaloDTO haloDTO = HaloData.haloMap.get(haloID);
      if (haloDTO != null) {
        int level = entry.getValue().size();
        if (level <= haloDTO.maxLevel && level <= haloDTO.buff.size() && level >= 1 && haloDTO.buff.size() == haloDTO.maxLevel) {
          List<List<Integer>> buffs     = haloDTO.buff.get(level - 1);
          List<Idols.Idol> updateIdols  = entry.getValue();
          float creativityBuff          = 0.0f;
          float performanceBuff         = 0.0f;
          float attractiveBuff          = 0.0f;

          for (List<Integer> buff : buffs) {
            if (buff.size() != 2)
              continue;

            switch (buff.get(0)) {
              case CREATIVITY:
                creativityBuff += buff.get(1)*1.0/100.0;
                break;
              case PERFORMANCE:
                performanceBuff += buff.get(1)*1.0/100.0;
                break;
              case ATTRACTIVE:
                attractiveBuff += buff.get(1)*1.0/100.0;
                break;
              default:
                break;
            }
          }

          for (Idol idol : updateIdols) {
            IdolHalo gHalo = IdolHalo.of(haloID, level, creativityBuff, performanceBuff, attractiveBuff);
            idol.groupHalo.add(gHalo);
            onPropertiesChange(idol);
          }
        }
      }
    }
  }

  private void reCalcPHalo(Idol idol, IdolHalo pHalo) {
    HaloData.HaloDTO haloDTO = HaloData.haloMap.get(pHalo.id);
    if (haloDTO == null || haloDTO.startLV != 1)
      return;
    List<List<Integer>> defBuf = haloDTO.buff.get(pHalo.level - 1);

    float creativityBuff    = 0.0f;
    float performanceBuff   = 0.0f;
    float attractiveBuff    = 0.0f;

    for (List<Integer> buff : defBuf) {
      if (buff.size() != 2)
        continue;

      switch (buff.get(0)) {
        case 2:
          creativityBuff += buff.get(1)*1.0/100.0;
          break;
        case 3:
          performanceBuff += buff.get(1)*1.0/100.0;
          break;
        case 4:
          attractiveBuff += buff.get(1)*1.0/100.0;
          break;
        default:
          break;
      }
    }

    pHalo.crtBufRate = creativityBuff;
    pHalo.perfBufRate = performanceBuff;
    pHalo.attrBufRate = attractiveBuff;
    onPropertiesChange(idol);
  }

  private String pHaloLevelUp(Idol idol, Idols.IdolHalo pHalo) {
    HaloData.HaloDTO haloDTO = HaloData.haloMap.get(pHalo.id);
    if (haloDTO == null || (haloDTO.maxLevel - haloDTO.buff.size()) != 1)
      return Msg.map.getOrDefault(Msg.HALO_LEVEL_UP_FAIL, "halo_level_up_fail");

    if (pHalo.level + 1 >= haloDTO.maxLevel)
      return Msg.map.getOrDefault(Msg.HALO_LEVEL_MAX, "halo_level_max");

    List<List<Integer>> defBuf = haloDTO.buff.get(pHalo.level);

    float creativityBuff    = 0.0f;
    float performanceBuff   = 0.0f;
    float attractiveBuff    = 0.0f;

    for (List<Integer> buff : defBuf) {
      if (buff.size() != 2)
        continue;

      switch (buff.get(0)) {
        case 2:
          creativityBuff += buff.get(1)*1.0/100.0;
          break;
        case 3:
          performanceBuff += buff.get(1)*1.0/100.0;
          break;
        case 4:
          attractiveBuff += buff.get(1)*1.0/100.0;
          break;
        default:
          break;
      }
    }

    pHalo.crtBufRate = creativityBuff;
    pHalo.perfBufRate = performanceBuff;
    pHalo.attrBufRate = attractiveBuff;
    pHalo.level += 1;
    onPropertiesChange(idol);
    return "ok";
  }
}