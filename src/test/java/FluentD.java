
import java.util.HashMap;
import java.util.Map;

import org.fluentd.logger.FluentLogger;

public class FluentD {
  private static FluentLogger LOG = FluentLogger.getLogger("fluentd");

  public static void main(String[] args) {
    doApplicationLogic();
  }
  public static void doApplicationLogic() {
    // ...
    Map<String, Object> data = new HashMap<String, Object>();
    data.put("from", "vunguyen");
    data.put("to", "aaa");
    LOG.log("node1", data);
    // ...
  }
}