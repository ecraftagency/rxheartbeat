
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.fluentd.logger.FluentLogger;

public class FluentD {
  private static FluentLogger ERROR = FluentLogger.getLogger("sbiz");

  public static void main(String[] args) {
    doApplicationLogic();
    Map<String, Object> data = new HashMap<String, Object>();
    Exception e = new IllegalArgumentException();
    data.put("msg", e.getMessage());
    data.put("trace", Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList()));
    ERROR.log("exception", data);
  }
  public static void doApplicationLogic() {
    List<Integer> l = new ArrayList<>();
    try {
      int a = l.get(10);
      System.out.println(a);
    }
    catch (Exception e) {
      Map<String, Object> data = new HashMap<String, Object>();
      data.put("msg", e.getMessage());
      data.put("trace", Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList()));
      ERROR.log("info", data);
    }
  }
}