import com.common.GlobalVariable;
import com.transport.ExtMessage;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.Assert;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Pooling {
  public static void main(String[] args) throws Exception {

    GenericObjectPoolConfig<ExtMessage> config = new GenericObjectPoolConfig<>();
    config.setMaxIdle(1);
    config.setMaxTotal(8);

    AtomicInteger count = new AtomicInteger(0);

    ExtMsgPool pool = new ExtMsgPool(new ExtMsgFactory(), config);

    int limit = 10;

    for (int i = 0; i < limit; i++) {
      GlobalVariable.exeThreadPool.execute(new Runnable() {
        @Override
        public void run() {
          try {
            ExtMessage resp = pool.borrowObject();
            count.getAndIncrement();
            pool.returnObject(resp);
          }
          catch (Exception e) {
            //inorge
          }
        }
      });
    }

    GlobalVariable.exeThreadPool.awaitTermination(10, TimeUnit.SECONDS);

    System.out.println("Pool Stats:\n Created:[" + pool.getCreatedCount() + "], Borrowed:[" + pool.getBorrowedCount() + "]");
    Assert.assertEquals(limit, count.get());
    Assert.assertEquals(count.get(), pool.getBorrowedCount());
    Assert.assertEquals(1, pool.getCreatedCount());
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
