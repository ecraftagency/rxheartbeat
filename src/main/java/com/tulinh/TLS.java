package com.tulinh;

import com.heartbeat.common.GlobalVariable;
import com.tulinh.controller.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.client.WebClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TLS extends AbstractVerticle {
  public static WebClient client;

  public static JedisPool redisPool = null;

  public static Jedis taskWriter = null;

  public static Map<Integer, AtomicInteger> localCounter;
  public static AtomicInteger requestCounter;

  @Override
  public void start(Promise<Void> startPromise) {
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    router.post("/api/wheel/get-item").handler(new WheelItem());
    router.post("/api/setup").handler(new SetUpHandler());

    router.get("/loaderio-af74b1534f539596f297be35c5caf523/").handler(ctx ->
            ctx.response().end("loaderio-af74b1534f539596f297be35c5caf523"));

    vertx.createHttpServer().requestHandler(router).listen(3000);

    WebClientOptions options = new WebClientOptions()
            .setUserAgent("Test Client/1.0");
    options.setKeepAlive(false);

    client = WebClient.create(vertx);

    requestCounter = new AtomicInteger();

    startPromise.complete();
  }

  public static void main(String[] args) {
    redisPool = new JedisPool(Const.poolConfig, "localhost");
    taskWriter = redisPool.getResource();

    localCounter = new HashMap<>();
    localCounter.put(0, new AtomicInteger());
    localCounter.put(1, new AtomicInteger());
    localCounter.put(2, new AtomicInteger());
    localCounter.put(3, new AtomicInteger());
    localCounter.put(4, new AtomicInteger());
    localCounter.put(5, new AtomicInteger());
    localCounter.put(6, new AtomicInteger());
    localCounter.put(7, new AtomicInteger());
    localCounter.put(8, new AtomicInteger());
    localCounter.put(9, new AtomicInteger());
    Vertx.vertx().deployVerticle(TLS.class.getName());
  }
}