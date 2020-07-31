package com.heartbeat.common;

import com.heartbeat.model.data.PaymentTransaction;

import java.util.HashMap;

@SuppressWarnings("unused")
public class DeviceUID {
  public String deviceUID;
  public String osPlatform;
  public HashMap<Integer, Integer> registerAccounts;
  public HashMap<Integer, Integer> activeAccounts;
  public int lastActiveAt;
  public int totalPayment;
  public int lastPaymentAt;
  public int dailyPaymentCounts;
  public DeviceUID(String deviceUID, String osPlatform) {
    this.deviceUID = deviceUID;
    this.osPlatform = osPlatform;
    registerAccounts = new HashMap<>();
    activeAccounts = new HashMap<>();
  }

  public DeviceUIDUpdateInfo updateAccount(int userId, boolean isRegister) {
    int second = (int)(System.currentTimeMillis()/1000);
    DeviceUIDUpdateInfo info = new DeviceUIDUpdateInfo();
    info.isFirstActive = this.activeAccounts.size() <= 0;
    info.dayDiff = Utilities.dayDiff(lastActiveAt, second);
    info.isFirstActiveInDay = info.dayDiff != 0;

    activeAccounts.put(userId, second);
    lastActiveAt = second;
    if(isRegister){
      info.isFirstRegister = this.registerAccounts.size() <= 0;
      registerAccounts.put(userId, second);
    }
    return info;
  }

  public DeviceUIDPaymentInfo updatePayment(String userId, PaymentTransaction trans) {
    DeviceUIDPaymentInfo info = new DeviceUIDPaymentInfo();
    if(lastPaymentAt == 0)
      info.isFirstPayment = true;
    if( Utilities.dayDiff(lastPaymentAt, trans.payAt) != 0) {
      dailyPaymentCounts = 0;
    }
    lastPaymentAt = trans.payAt;
    dailyPaymentCounts++;
    totalPayment++;
    info.dailyCounts = dailyPaymentCounts;
    return info;
  }

  public static class DeviceUIDUpdateInfo {
    public int dayDiff;
    public boolean isFirstActive;
    public boolean isFirstActiveInDay;
    public boolean isFirstRegister;
  }
  public static class DeviceUIDPaymentInfo {
    public boolean isFirstPayment;
    public int dailyCounts;
  }
}