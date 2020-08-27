package com.statics;

import java.util.List;
import java.util.Map;

public class PaymentData {
  public static class PaymentDto implements Common.hasKey<String>{
    public String               id;
    public int                  webVal;
    public int                  iapVal;
    public int                  time;
    public int                  vip;
    public List<List<Integer>>  reward;

    @Override
    public String mapKey() {
      return id;
    }
  }

  public static Map<String, PaymentDto> paymentDtoMap;
  public static void loadJson(String jsonText) {
    paymentDtoMap = Common.loadMap(jsonText, PaymentDto.class);
  }
}