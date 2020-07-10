package com.statics;

import com.heartbeat.model.data.UserIdol;
import com.transport.model.Idols;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.heartbeat.model.data.UserIdol.*;

public class HaloData {
  public static class HaloDTO implements Common.hasKey<Integer> {
    //id,name,level,startLV,type,preFixHalo,updateItem,buff
    public int id;
    public String name;
    public int maxLevel;
    public int startLV;
    public int type;
    public List<Integer> preFixHalo;
    public List<Integer> updateItem;
    public List<List<List<Integer>>> buff;
    //The first round is level-element array contain buff for each level
    //The second round is [0-3] element array specify which idol attribute it will effect [performance, creativity, attractive]
    //The last round is exact 2-element array which first element is attribute id and second is buff percent
    @Override
    public Integer mapKey() {
      return id;
    }
  }

  public static Map<Integer, HaloDTO> haloMap;

  public static void loadJson(String haloJson) {
    haloMap = Common.loadMap(haloJson, HaloDTO.class);
  }

  public static Idols.IdolHalo makeHalo(int haloId) {
    HaloDTO haloDTO = haloMap.get(haloId);
    if (haloDTO == null)
      return null;
    return IdolHalo.of(haloId, haloDTO.startLV, 0.0f, 0.0f, 0.0f, haloDTO.updateItem);
  }

  public static void reCalcPHalo(Idol idol, IdolHalo pHalo) {
    HaloDTO haloDTO = haloMap.get(pHalo.id);
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
    UserIdol.onPropertiesChange(idol);
  }

  public static String pHaloLevelUp(Idol idol, Idols.IdolHalo pHalo) {
    HaloDTO haloDTO = haloMap.get(pHalo.id);
    if (haloDTO == null || (haloDTO.maxLevel - haloDTO.buff.size()) != 1)
      return "halo_level_up_fail";

    if (pHalo.level + 1 >= haloDTO.maxLevel)
      return "halo_level_max";

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

    UserIdol.onPropertiesChange(idol);
    return "ok";
  }

  public static void gUpdateGroupHalo(Map<Integer, Idol> idolMap, Map<Integer, List<Idol>> halo2Idol) {
    idolMap.values().forEach(idol -> idol.groupHalo.clear()); // clear group halos

    for (Map.Entry<Integer, List<Idols.Idol>> entry : halo2Idol.entrySet()) {
      int haloID = entry.getKey();
      HaloDTO haloDTO = HaloData.haloMap.get(haloID);
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
            IdolHalo gHalo = IdolHalo.of(haloID, level, creativityBuff, performanceBuff, attractiveBuff, new ArrayList<>());
            idol.groupHalo.add(gHalo);
            UserIdol.onPropertiesChange(idol);
          }
        }
      }
    }
  }
}
