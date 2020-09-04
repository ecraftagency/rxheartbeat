package com.heartbeat.effect;

import com.heartbeat.db.Cruder;
import com.heartbeat.db.cb.CBTitle;
import com.heartbeat.model.Session;
import com.statics.VipData;
import com.transport.model.Title;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TitleEffectHandler implements EffectHandler{
  public static final Map<Integer, String> titleKeyMap;
  public static final Map<Integer, String> titleNameMap;

  public Cruder<Title> cruder;
  static {
    titleKeyMap = new HashMap<>();
    titleKeyMap.put(1, "attractive_title");
    titleKeyMap.put(2, "stylish_title");
    titleKeyMap.put(3, "brand_title");
    titleKeyMap.put(4, "dedicated_title");
    titleKeyMap.put(5, "all_time_title");

    titleNameMap = new HashMap<>();
    titleKeyMap.put(1, "Ngôi Sao Lôi Cuốn");
    titleKeyMap.put(2, "Ngôi Sao Phong Cách");
    titleKeyMap.put(3, "Ngôi Sao Thương Hiệu");
    titleKeyMap.put(4, "Ngôi Sao Cống Hiến");
    titleKeyMap.put(5, "Ngôi Sao Toàn Năng");
  }

  public TitleEffectHandler() {
    cruder = CBTitle.getInstance();
  }

  @Override
  public String handleEffect(ExtArgs extArgs, Session session, List<Integer> effectFormat) {
    String key = titleKeyMap.get(effectFormat.get(PARAM1));
    if (key == null)
      return "invalid_title_id";
    Title title = Title.of(Integer.toString(session.id),"", session.userGameInfo.displayName, extArgs.newDisplayName);
    title.userTitleId     = session.userGameInfo.titleId;
    title.totalCrt        = session.userIdol.totalCrt();
    title.totalPerf       = session.userIdol.totalPerf();
    title.totalAttr       = session.userIdol.totalAttr();
    title.curFightId      = session.userFight.currentFightLV.id;
    VipData.VipDto vip    = VipData.getVipData(session.userGameInfo.vipExp);
    int vipLv             = 0;
    if (vip != null)
      vipLv = vip.level;

    title.vipLevel        = vipLv;

    if (cruder.add(key, title))
      return "ok";
    return "title_already_placed";
  }
}
