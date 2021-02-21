import com.sbizprofile.thrift.SbizProfile;
import com.sbizprofile.thrift.SbizProfileResult;
import com.sbizprofile.thrift.SbizProfileService;
import com.stdprofile.thrift.StdProfileResult;
import com.stdprofile.thrift.StdProfileService;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.*;

import java.io.IOException;

public class Thrift {
  public static void main(String[] args) throws TException, IOException, InterruptedException {
    TTransport transport = new TSocket("localhost", 8888);
    TFramedTransport ft = new TFramedTransport(transport);
    TProtocol protocol = new TBinaryProtocol(ft);
    transport.open();
    SbizProfileService.Client client = new SbizProfileService.Client(protocol);
    SbizProfile pof = new SbizProfile();
    pof.setUserId(1000000).setDisplayName("oh man").setUserName("bush");
    client.ow_put(pof);

    SbizProfileResult result;
    long s = System.currentTimeMillis();
     result = client.get(1000000);
    long e = System.currentTimeMillis();
    System.out.println(e-s);

    String stats = client.getStat(1);
    System.out.println(stats);
    transport.close();

//    TNonblockingTransport transport = new TNonblockingSocket("localhost", 8888);
//
//    SbizProfileService.AsyncIface client = new SbizProfileService.AsyncClient(new TBinaryProtocol.Factory(),
//            new TAsyncClientManager(),
//            transport);
//    AsyncMethodCallback<SbizProfileResult>  ar = new AsyncMethodCallback<SbizProfileResult>() {
//      @Override
//      public void onComplete(SbizProfileResult response) {
//        System.out.println(response);
//      }
//
//      @Override
//      public void onError(Exception exception) {
//        System.out.println(exception.getMessage());
//      }
//    };
//
//    client.get(1000000, ar);
//    Thread.sleep(2000);
  }
}