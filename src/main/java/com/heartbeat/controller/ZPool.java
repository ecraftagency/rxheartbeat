package com.heartbeat.controller;

import com.transport.ExtMessage;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

@SuppressWarnings("unused")
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
