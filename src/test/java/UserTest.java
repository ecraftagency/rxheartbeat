import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.heartbeat.common.Constant;
import com.heartbeat.common.Utilities;
import tulinh.Const;

public class UserTest {
  public static void main(String[] args) {
    //Const.staticItems.forEach(item -> System.out.println(Utilities.gson.toJson(item)));

    GsonBuilder gsonBuilder  = new GsonBuilder();
    // Allowing the serialization of static fields
    gsonBuilder.excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT);
    // Creates a Gson instance based on the current configuration
    Gson gson = gsonBuilder.create();
    String json = gson.toJson(new Constant.GROUP());
    System.out.println(json);
  }
}
