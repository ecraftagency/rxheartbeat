package com.heartbeat.internal;

import com.common.Constant;
import com.common.GlobalVariable;
import com.common.Utilities;
import com.statics.PaymentData;

public class RequestGenerator {
  public static String getExchangeRequest = "http://%s:%d/exchange?userid=%s&roleid=%s&server_id=%s&order_id=%s&item_id=%s&money=%d&gold=%d&time=%d&sign=%s";
  public static String getRoleRequest = "http://%s:%d/api/getrole?userid=%s&server_id=%s&time=%d&sign=%s";

  public static String genGetRoleRequest(String userId, String serverId, int time) throws Exception {
    String ip       =  "18.141.216.52";//"127.0.0.1";//";
    int port        = 80;
    String sig = GlobalVariable.stringBuilder.get()
            .append(userId)
            .append(serverId)
            .append(time)
            .append(Constant.PAYMENT.SECRET).toString();
    String md5sig = Utilities.md5Encode(sig);

    String request = String.format(getRoleRequest, ip, port, userId, serverId, time, md5sig);
    System.out.println(request);
    return request;
  }
  public static String genPaymentRequest(String sessionId, PaymentData.PaymentDto paymentPackage) throws Exception {
    String ip       =  "18.141.216.52";//"127.0.0.1";//";
    int port        = 80;
    String userId   = "88989800";
    int    serverId = Integer.parseInt(sessionId)/ Constant.SYSTEM_INFO.MAX_USER_PER_NODE;
    String orderId  = System.currentTimeMillis() + "";
    String itemId   = paymentPackage.id;
    int money       = paymentPackage.webVal;
    int gold        = paymentPackage.iapVal;
    int time        = (int)(System.currentTimeMillis()/1000);


    String sig      = GlobalVariable.stringBuilder.get()
            .append(userId)
            .append(sessionId)
            .append(serverId)
            .append(orderId)
            .append(itemId)
            .append(money)
            .append(gold)
            .append(time)
            .append(Constant.PAYMENT.SECRET).toString();

    String md5sig   = Utilities.md5Encode(sig);


    return String.format(getExchangeRequest, ip, port, userId, sessionId, serverId, orderId, itemId, money, gold, time, md5sig);
  }
}
