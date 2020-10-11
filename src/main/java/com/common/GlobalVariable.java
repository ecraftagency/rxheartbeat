package com.common;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class GlobalVariable {
  static final int NUM_CORE = 8;//Runtime.getRuntime().availableProcessors();
  static final int NUM_THREAD_PER_POOL = NUM_CORE / 2;
  //public static ThreadPoolExecutor exeThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(NUM_THREAD_PER_POOL);
  public static ScheduledThreadPoolExecutor schThreadPool = (ScheduledThreadPoolExecutor)Executors.newScheduledThreadPool(NUM_THREAD_PER_POOL);

  public static DeviceUID.DeviceUIDUpdateInfo updateDeviceUID(String deviceUID, String osPlatform, int userId, boolean isRegister) throws Exception {
//    if(Utilities.isValidString(deviceUID)) {
//      CBDataAccess cbAccess = CBDataAccess.getInstance();
//      DeviceUID objDeviceUID = cbAccess.getDeviceUID(deviceUID);
//      if(objDeviceUID == null)
//        objDeviceUID = new DeviceUID(deviceUID, osPlatform);
//      DeviceUID.DeviceUIDUpdateInfo info = objDeviceUID.updateAccount(userId, isRegister);
//      cbAccess.updateDeviceUID(objDeviceUID);
//      return info;
//    }
//    return null;
    return null;
  }

  public static final ThreadLocal<StringBuilder> stringBuilder = new ThreadLocal<StringBuilder>() {
    @Override
    protected StringBuilder initialValue()
    {
      return new StringBuilder();
    }

    @Override
    public StringBuilder get() {
      StringBuilder b = super.get();
      b.setLength(0); // clear/reset the buffer
      return b;
    }
  };
}