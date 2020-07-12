package com.heartbeat.effect;

import com.heartbeat.db.DataAccess;
import com.heartbeat.db.cb.CBTitle;
import com.heartbeat.model.Session;
import com.transport.model.Title;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TitleEffectHandler implements EffectHandler{
  public static final Map<Integer, String> titleMap;
  public DataAccess<Title>         dataAccess;
  static {
    titleMap = new HashMap<>();
    titleMap.put(1, "attractive_title");
    titleMap.put(2, "stylish_title");
    titleMap.put(3, "brand_title");
    titleMap.put(4, "dedicated_title");
    titleMap.put(5, "all_time_title");
  }

  public TitleEffectHandler() {
    dataAccess = CBTitle.getInstance();
  }

  @Override
  public String handleEffect(ExtArgs extArgs, Session session, List<Integer> effectFormat) {
    String key = titleMap.get(effectFormat.get(PARAM1));
    if (key == null)
      return "invalid_title_id";
    Title title = Title.of(Integer.toString(session.id), session.userGameInfo.displayName, extArgs.newDisplayName);
    if (dataAccess.add(key, title))
      return "ok";
    return "title_already_placed";
  }
}
