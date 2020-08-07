import com.heartbeat.common.Constant;
import com.heartbeat.common.GlobalVariable;
import com.heartbeat.ranking.impl.LeaderBoard;
import com.statics.ScoreObj;

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
    //Constant.RANKING.rankingInfo.setRankingTime("06/08/2020 21:40:30", "06/08/2020 21:40:40");
    LeaderBoard<Integer, ScoreObj> ldb = new LeaderBoard<>(20);
    ldb.record(1000, ScoreObj.of(1000, 10, ""));
    ldb.record(1001, ScoreObj.of(1001, 9, ""));
    ldb.record(1002, ScoreObj.of(1002, 8, ""));
    ldb.record(1003, ScoreObj.of(1003, 2, ""));
    ldb.record(1004, ScoreObj.of(1004, 6, ""));
    ldb.record(1005, ScoreObj.of(1005, 5, ""));
    ldb.record(1006, ScoreObj.of(1006, 4, ""));
    ldb.record(1007, ScoreObj.of(1007, 3, ""));
    ldb.record(1008, ScoreObj.of(1008, 2, ""));
    ldb.record(1009, ScoreObj.of(1009, 1, ""));
    ldb.close();

    System.out.println(ldb.getRank(1008));
  }
}
