package com.statics;

import com.transport.model.Idols;

import java.util.List;
import java.util.Map;

public class HaloData {
  public static class HaloDTO implements Common.hasKey<Integer> {
    public int        id;
    public String     name;
    public int        maxLevel;
    public int        startLV;
    public int        type;
    public List<Integer>              preFixHalo;
    public List<Integer>              updateItem;
    public List<List<List<Integer>>>  buff;
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
    return Idols.IdolHalo.of(haloId, haloDTO.startLV, 0.0f, 0.0f, 0.0f);
  }
}