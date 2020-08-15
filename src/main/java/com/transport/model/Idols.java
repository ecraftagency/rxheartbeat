package com.transport.model;

import java.util.List;
import java.util.Map;

public class Idols {
  public Map<Integer, Idol> idolMap;

  public static class Idol {
    public int                        id;
    public int                        level;
    public int                        specialityID;
    public int                        creativity;       // sáng tạo
    public int                        performance;      // biểu diễn
    public int                        attractive;       // cuốn hút
    public int                        aptitudeExp;      // exp tư chất

    public int                        crtItemBuf;
    public int                        perfItemBuf;
    public int                        attrItemBuf;

    public List<Integer>              groupHaloIds;     // vòng sáng //todo change field
    public List<IdolHalo>             groupHalo;
    public List<IdolHalo>             personalHalos;    //todo add field

    public int                        totalCrtHLBuf;
    public int                        totalPerfHLBuf;
    public int                        totalAttrHLBuf;

    public int                        crtApt;
    public int                        perfApt;
    public int                        attrApt;

    public int                        crtAptBuf;
    public int                        perfAptBuf;
    public int                        attrAptBuf;

    public int                        honorID;
  }

  public static class IdolHalo {
    public int                        id;
    public int                        level;
    public float                      crtBufRate;
    public float                      perfBufRate;
    public float                      attrBufRate;

    public static IdolHalo of(int id, int level, float crt, float perf, float attr) {
      IdolHalo pHalo    = new IdolHalo();
      pHalo.id          = id;
      pHalo.level       = level;
      pHalo.crtBufRate  = crt;
      pHalo.perfBufRate = perf;
      pHalo.attrBufRate = attr;
      return pHalo;
    }
  }
}



