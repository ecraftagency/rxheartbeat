import com.common.Constant;
import com.common.GlobalVariable;
import com.common.Utilities;

public class RequestGenerator {
  public static String getRoleRequest = "http://%s:%d/api/getrole?userid=%s&server_id=%s&time=%d&sign=%s";
  public static String getExchangeRequest = "http://%s:%d/exchange?userid=%s&roleid=%s&server_id=%s&order_id=%s&item_id=%s&money=%d&gold=%d&time=%d&sign=%s";
  public static String getIAPExchangeRequest = "http://%s:%d/iapexchange?userid=%s&roleid=%s&server_id=%s&order_id=%s&payload=%s&item_id=%s&money=%d&gold=%d&time=%d&sign=%s";
  public static void main(String[] args) throws Exception {

    /*
    /exchange?userid=99845214&roleid=100282&server_id=1&order_id=HPC20191227120038&item_id=1&money=10000&gold=30000&time=1577422838&sign=6ec40a6a9227e651d9c8cb2bf3e3f71f
    http://127.0.0.1:8181/exchange/userid=778538&roleid=100283&server_id=1&order_id=qsd9cdasdsada9925&item_id=16_50000&money=10000&gold=1&time=1577422907&sign=62bafc819917e5adcfa4f2ac379daf5b
    * */
    String ip =  "127.0.0.1";//"159.65.135.32";///"128.199.191.93";
    int port = 80;
    String userid = "88989800";
    String serverid = "2";
    String roleid = "2000000";
    String orderid = System.currentTimeMillis() + "";
    String itemid = "mg01.p13";
    String payload = "asda87dh3ie328d";
    int money = 10000;
    int gold = 40000;
    long time = System.currentTimeMillis();
    String sig = GlobalVariable.stringBuilder.get()
            .append(userid)
            .append(serverid)
            .append(time)
            .append(Constant.PAYMENT.SECRET).toString();
    String md5sig = Utilities.md5Encode(sig);

    String request = String.format(getRoleRequest, ip, port, userid, serverid, time, md5sig);
    System.out.println(request);

    sig = GlobalVariable.stringBuilder.get()
            .append(userid)
            .append(roleid)
            .append(serverid)
            .append(orderid)
            .append(itemid)
            .append(money)
            .append(gold)
            .append(time)
            .append(Constant.PAYMENT.SECRET).toString();

    md5sig = Utilities.md5Encode(sig);
    request = String.format(getExchangeRequest, ip, port, userid, roleid, serverid, orderid, itemid, money, gold, time, md5sig);
    System.out.println(request);

//    for (int i = 0; i < 1; i++) {
//      orderid = System.currentTimeMillis() + "";
//      sig = GlobalVariable.stringBuilder.get()
//              .append(userid)
//              .append(roleid)
//              .append(serverid)
//              .append(orderid)
//              .append(itemid)
//              .append(money)
//              .append(gold)
//              .append(time)
//              .append(Constant.SECRET).toString();
//
//      md5sig = md5Encode(sig);
//      String res = httpGet(String.format(getExchangeRequest, ip, port, userid, roleid, serverid, orderid, itemid, money, gold, time, md5sig));
//      System.out.println(i + " " + res);
//    }
//
//    for (int i = 0; i < 1; i++) {
//      orderid = System.currentTimeMillis() + "";
//      sig = GlobalVariable.stringBuilder.get()
//              .append(userid)
//              .append(roleid)
//              .append(serverid)
//              .append(orderid)
//              .append(payload)
//              .append(itemid)
//              .append(money)
//              .append(gold)
//              .append(time)
//              .append(Constant.SECRET).toString();
//
//      md5sig = md5Encode(sig);
//      String res = httpGet(String.format(getIAPExchangeRequest, ip, port, userid, roleid, serverid, orderid, payload, itemid, money, gold, time, md5sig));
//      System.out.println(i + " " + res);
//    }
//
    sig = GlobalVariable.stringBuilder.get()
            .append(userid)
            .append(roleid)
            .append(serverid)
            .append(orderid)
            .append(payload)
            .append(itemid)
            .append(money)
            .append(gold)
            .append(time)
            .append(Constant.PAYMENT.SECRET).toString();

    md5sig = Utilities.md5Encode(sig);
    request = String.format(getIAPExchangeRequest, ip, port, userid, roleid, serverid, orderid, payload, itemid, money, gold, time, md5sig);
    System.out.println(request);
  }
}
