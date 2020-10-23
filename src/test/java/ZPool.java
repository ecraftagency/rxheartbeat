import com.transport.ExtMessage;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

@SuppressWarnings("unused")

//  second, minute, hour, day of month, month, day(s) of week
//  * "0 0 * * * *"           = the top of every hour of every day.
//  * "*/10 * * * * *"        = every ten seconds.
//  * "0 0 8-10 * * *"        = 8, 9 and 10 o'clock of every day.
//  * "0 0 8,10 * * *"        = 8 and 10 o'clock of every day.
//  * "0 0/30 8-10 * * *"     = 8:00, 8:30, 9:00, 9:30 and 10 o'clock every day.
//  * "0 0 9-17 * * MON-FRI"  = on the hour nine-to-five weekdays
//  * "0 0 0 25 12 ?"         = every Christmas Day at midnight
//  (*) means match any
//  */X means "every X"
//  ? ("no specific value")
public class ZPool {
  public static final ExtMsgPool msgPool;

  static {
    GenericObjectPoolConfig<ExtMessage> config = new GenericObjectPoolConfig<>();
    config.setMaxIdle(1);
    config.setMaxTotal(100000);
    msgPool = new ExtMsgPool(new ExtMsgFactory(), config);
  }

  public static class ExtMsgPool extends GenericObjectPool<ExtMessage> {

    public ExtMsgPool(PooledObjectFactory<ExtMessage> factory) {
      super(factory);
    }

    public ExtMsgPool(PooledObjectFactory<ExtMessage> factory, GenericObjectPoolConfig<ExtMessage> config) {
      super(factory, config);
    }
  }

  public static class ExtMsgFactory extends BasePooledObjectFactory<ExtMessage> {

    @Override
    public ExtMessage create() {
      ExtMessage result = new ExtMessage();
      result.cmd = "";
      result.msg = "ok";
      result.group = "";
      result.data = new ExtMessage.Data();
      return result;
    }

    @Override
    public PooledObject<ExtMessage> wrap(ExtMessage obj) {
      return new DefaultPooledObject<>(obj);
    }

    @Override
    public void passivateObject(PooledObject<ExtMessage> p) {
      p.getObject().reset();
    }

    @Override
    public boolean validateObject(PooledObject<ExtMessage> p) {
      return true;
    }
  }
}
