import com.heartbeat.common.Constant;
import com.heartbeat.common.GlobalVariable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class UserTest {
  public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

  public static void main(String[] args) throws ParseException {
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
    Constant.RANKING.rankingInfo.setRankingTime("06/08/2020 21:40:30", "06/08/2020 21:40:40");
  }
}
