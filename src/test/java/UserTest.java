import com.couchbase.client.java.ReactiveCluster;
import com.couchbase.client.java.kv.GetResult;
import com.gateway.HBGateway;
import com.heartbeat.HBServer;
import com.common.Constant;
import com.heartbeat.db.dao.PublicMailBoxDAO;
import com.transport.model.MailObj;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class UserTest {
  private static final Logger log = LoggerFactory.getLogger(HBGateway.class);

  public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

  public static void main(String[] args) throws ParseException, InterruptedException {
//    Calendar cal = Calendar.getInstance();
//    cal.setTime(dateFormat.parse("06/08/2020 20:54:00"));
//    int openTime  = (int) (cal.getTimeInMillis()/1000);
//    int openDelay = openTime - (int)(System.currentTimeMillis()/1000);
//    System.out.println(openDelay);
//    GlobalVariable.schThreadPool.schedule(() -> {
//      System.out.println("hello");
//    }, 1000, TimeUnit.MILLISECONDS);
//    /*
//          cal.setTime(dateFormat.parse(data.startDate));
//          long openTime   = cal.getTimeInMillis()/(1000);
//          long openDelay  = openTime - System.currentTimeMillis()/(1000);
//     */
//  }
    //Constant.RANKING.rankingInfo.setRankingTime("06/08/2020 21:40:30", "06/08/2020 21:40:40");
//    LeaderBoard<Integer, ScoreObj> ldb = new LeaderBoard<>(20);
//    ldb.record(1000, ScoreObj.of(1000, 10, ""));
//    ldb.record(1001, ScoreObj.of(1001, 9, ""));
//    ldb.record(1002, ScoreObj.of(1002, 8, ""));
//    ldb.record(1003, ScoreObj.of(1003, 2, ""));
//    ldb.record(1004, ScoreObj.of(1004, 6, ""));
//    ldb.record(1005, ScoreObj.of(1005, 5, ""));
//    ldb.record(1006, ScoreObj.of(1006, 4, ""));
//    ldb.record(1007, ScoreObj.of(1007, 3, ""));
//    ldb.record(1008, ScoreObj.of(1008, 2, ""));
//    ldb.record(1009, ScoreObj.of(1009, 1, ""));
//    ldb.close();
//
//    System.out.println(ldb.getRank(1008));
//
//    StringBuilder sb = new StringBuilder();
//    sideEffect(sb);
//    System.out.println(sb.toString());

//
//    HBServer.rxCluster       = ReactiveCluster.connect(Constant.DB.HOST, Constant.DB.USER, Constant.DB.PWD);
//    HBServer.rxSessionBucket = HBServer.rxCluster.bucket("sessions");
//    HBServer.rxIndexBucket   = HBServer.rxCluster.bucket("index");
//    HBServer.rxPersistBucket = HBServer.rxCluster.bucket("persist");
//    //loadMail();
//    Thread.sleep(1000);
//    syncMail();
//    Thread.sleep(1000);

    String defaultAddressNotLoopback = getDefaultAddressNotLoopback();
    System.out.println(defaultAddressNotLoopback);
    JsonObject json = new JsonObject();
    json.put("sessionId", 15);
    Integer nodeId = json.getInteger("nodeId");
    System.out.println(nodeId);
  }

  private static String getDefaultAddressNotLoopback() {
    Enumeration<NetworkInterface> nets;
    try {
      nets = NetworkInterface.getNetworkInterfaces();
    } catch (SocketException e) {
      log.error("could not get the network interfaces " + e.getMessage());
      return null;
    }

    NetworkInterface netinf;
    List<InetAddress> usableInetAdresses = new ArrayList<>();
    while (nets.hasMoreElements()) {
      netinf = nets.nextElement();

      Enumeration<InetAddress> addresses = netinf.getInetAddresses();

      while (addresses.hasMoreElements()) {
        InetAddress address = addresses.nextElement();
        log.info("found InetAddress: " + address.toString() + " on interface: " + netinf.getName());
        if (!address.isAnyLocalAddress() && !address.isMulticastAddress()
                && !(address instanceof Inet6Address) &&!address.isLoopbackAddress()) {
          usableInetAdresses.add(address);
        }
      }
    }

    if(usableInetAdresses.size() > 1) {
      throw new IllegalStateException("don't know which InetAddress to use, there are more than one: " + usableInetAdresses);
    } else if(usableInetAdresses.size() == 1) {
      log.info("found a InetAddress which we can use as default address: " + usableInetAdresses.get(0).toString());
      return usableInetAdresses.get(0).getHostAddress();
    }

    log.info("found no usable inet address");
    return null;
  }

  public static void sideEffect(StringBuilder sb) {
    sb.append("sideEffect");
  }

  public static void loadMail() {
    ConcurrentLinkedDeque<MailObj> inbox =  new ConcurrentLinkedDeque<>();
    GetResult gr = HBServer.rxIndexBucket.defaultCollection().get("halo").block();
    if (gr != null) {
      PublicMailBoxDAO dao = gr.contentAs(PublicMailBoxDAO.class);
      if (dao.publicMessage != null)
        for (MailObj mo : dao.publicMessage)
          inbox.addLast(mo);
      System.out.println(inbox.getLast().msg);
    }
  }

  public static void syncMail() {
    ConcurrentLinkedDeque<MailObj> inbox =  new ConcurrentLinkedDeque<>();
    for (int i = 0; i < 10; i++) {
      if (inbox.size() >= 5) {
        inbox.removeFirst();
      }
      //inbox.addLast(MailObj.of("hello " + i, new ArrayList<>(), MailObj.MSG_TYPE_PUBLIC), );
    }


    System.out.println(inbox.getLast().msg);


    PublicMailBoxDAO dao = new PublicMailBoxDAO();
    dao.lastSync = System.currentTimeMillis();
    dao.publicMessage = inbox;
    HBServer.rxIndexBucket.defaultCollection().upsert("halo", dao).block();
  }
}