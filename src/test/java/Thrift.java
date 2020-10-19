import com.stdprofile.thrift.StdProfileResult;
import com.stdprofile.thrift.StdProfileService;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class Thrift {
  public static void main(String[] args) throws TException {
    TTransport transport = new TSocket("13.229.140.173", 8888);
    TFramedTransport ft = new TFramedTransport(transport);
    TProtocol protocol = new TBinaryProtocol(ft);
    transport.open();
    StdProfileService.Client client = new StdProfileService.Client(protocol);
    StdProfileResult res = client.get(1000000);
    if (res.data != null)
      System.out.println(res.data.getDisplayName());
    transport.close();
  }
}
