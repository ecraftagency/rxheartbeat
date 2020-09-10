package com.heartbeat.effect;

import com.common.Msg;
import com.heartbeat.db.Cruder;
import com.heartbeat.db.cb.CBNetAward;
import com.heartbeat.model.Session;
import com.statics.VipData;
import com.statics.WordFilter;
import com.transport.model.NetAward;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetAwardEffectHandler implements EffectHandler{
  public static final Map<Integer, String> titleKeyMap;
  public static final Map<Integer, String> titleNameMap;

  public Cruder<NetAward> cruder;
  static {
    titleKeyMap = new HashMap<>();
    titleKeyMap.put(1, "attractive_title");
    titleKeyMap.put(2, "stylish_title");
    titleKeyMap.put(3, "brand_title");
    titleKeyMap.put(4, "dedicated_title");
    titleKeyMap.put(5, "all_time_title");

    titleNameMap = new HashMap<>();
    titleNameMap.put(1, "Ngôi Sao Lôi Cuốn");
    titleNameMap.put(2, "Ngôi Sao Phong Cách");
    titleNameMap.put(3, "Ngôi Sao Thương Hiệu");
    titleNameMap.put(4, "Ngôi Sao Cống Hiến");
    titleNameMap.put(5, "Ngôi Sao Toàn Năng");
  }

  public NetAwardEffectHandler() {
    cruder = CBNetAward.getInstance();
  }

  @Override
  public String handleEffect(ExtArgs extArgs, Session session, List<Integer> effectFormat) {
    String key = titleKeyMap.get(effectFormat.get(PARAM1));
    if (key == null)
      return "invalid_title_id";
    if (!WordFilter.isValidInput(extArgs.strParam, "VN"))
      return Msg.map.getOrDefault(Msg.AWARD_TITLE_INVALID, "award_title_invalid");

    NetAward netAward = NetAward.of(Integer.toString(session.id),"", session.userGameInfo.displayName, extArgs.strParam);
    netAward.userTitleId     = session.userGameInfo.titleId;
    netAward.totalCrt        = session.userIdol.totalCrt();
    netAward.totalPerf       = session.userIdol.totalPerf();
    netAward.totalAttr       = session.userIdol.totalAttr();
    netAward.curFightId      = session.userFight.currentFightLV.id;
    netAward.avatar          = session.userGameInfo.avatar;
    netAward.gender          = session.userGameInfo.gender;
    netAward.exp             = session.userGameInfo.exp;

    VipData.VipDto vip    = VipData.getVipData(session.userGameInfo.vipExp);
    int vipLv             = 0;
    if (vip != null)
      vipLv = vip.level;

    netAward.vipLevel        = vipLv;

    if (cruder.add(key, netAward))
      return "ok";
    return "title_already_placed";
  }
}
