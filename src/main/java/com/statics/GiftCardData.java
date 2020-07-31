package com.statics;

import java.util.List;

public class GiftCardData {
  public static class GiftCardDto implements Common.hasKey<Integer> {
    public int                  id;
    public int                  type;
    public int                  expireDay;
    public String               desc;
    public List<List<Integer>>  initReward;
    public List<List<Integer>>  dailyReward;

    @Override
    public Integer mapKey() {
      return id;
    }
  }

  public static List<GiftCardDto> giftCardDtoMap;
  public static void loadJson(String jsonText) {
    giftCardDtoMap = Common.loadList(jsonText, GiftCardDto.class);
  }
}
