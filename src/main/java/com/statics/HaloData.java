package com.statics;

import com.heartbeat.model.data.UserIdol;
import com.transport.model.Idols;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.heartbeat.model.data.UserIdol.*;

public class HaloData {
  public static class Halo implements Common.hasKey<Integer> {
    public int id;
    public int level;
    public int type;
    public List<List<List<Integer>>> buff;
    //The first round is level-element array contain buff for each level
    //The second round is [0-3] element array specify which idol attribute it will effect [performance, creativity, attractive]
    //The last round is exact 2-element array which first element is attribute id and second is buff percent
    public List<Integer> updateItem;
    @Override
    public Integer mapKey() {
      return id;
    }
  }

  public static Map<Integer, Halo> haloMap;

  public static void loadJson(String haloJson) {
    haloMap = Common.loadMap(haloJson, Halo.class);
  }

  public static Idols.IdolHalo makeHalo(int haloId) {
    Halo halo = haloMap.get(haloId);
    if (halo == null)
      return null;

//        List<List<Integer>> defBuf = halo.buff.get(pHalo.level);
//    for (List<Integer> buff : defBuf) {
//      if (buff.size() != 2)
//        continue;
//
//      float creativityBuff    = 0.0f;
//      float performanceBuff   = 0.0f;
//      float attractiveBuff    = 0.0f;
//
//      switch (buff.get(0)) {
//        case 2:
//          creativityBuff += buff.get(1)*1.0/100.0;
//          break;
//        case 3:
//          performanceBuff += buff.get(1)*1.0/100.0;
//          break;
//        case 4:
//          attractiveBuff += buff.get(1)*1.0/100.0;
//          break;
//        default:
//          break;
//      }
//      pHalo.crtBufRate = creativityBuff;
//      pHalo.perfBufRate = performanceBuff;
//      pHalo.attrBufRate = attractiveBuff;
//    }

    return IdolHalo.of(haloId, 0, 0.0f, 0.0f, 0.0f, halo.updateItem);
  }

  public static String pHaloLevelUp(Idol idol, Idols.IdolHalo pHalo) {
    Halo halo = haloMap.get(pHalo.id);
    if (halo == null || halo.level != halo.buff.size())
      return "halo_level_up_fail";

    if (pHalo.level + 1 >= halo.level)
      return "halo_level_max";

    pHalo.level += 1;
    List<List<Integer>> defBuf = halo.buff.get(pHalo.level);

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

    UserIdol.onPropertiesChange(idol, HALO_UP_EVT);
    return "ok";
  }

  public static void gUpdateGroupHalo(Map<Integer, Idol> idolMap, Map<Integer, List<Idol>> halo2Idol) {
    idolMap.values().forEach(idol -> idol.groupHalo.clear()); // clear group halos

    for (Map.Entry<Integer, List<Idols.Idol>> entry : halo2Idol.entrySet()) {
      int haloID = entry.getKey();
      HaloData.Halo halo = HaloData.haloMap.get(haloID);
      if (halo != null) {
        int level = entry.getValue().size();
        if (level <= halo.level && level <= halo.buff.size() && level >= 1 && halo.buff.size() == halo.level) {
          List<List<Integer>> buffs     = halo.buff.get(level - 1);
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
            IdolHalo gHalo = IdolHalo.of(haloID, level, creativityBuff, performanceBuff, attractiveBuff, new ArrayList<>());
            idol.groupHalo.add(gHalo);
            UserIdol.onPropertiesChange(idol, HALO_UP_EVT);
          }
        }
      }
    }
  }
}
