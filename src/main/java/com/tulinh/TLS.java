package com.tulinh;

import com.tulinh.config.ItemConfig;
import com.tulinh.controller.WheelHistory;
import com.tulinh.controller.WheelInventory;
import com.tulinh.controller.WheelItem;
import com.tulinh.controller.WheelTurn;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.redis.client.*;
import io.vertx.ext.web.client.WebClient;

public class TLS extends AbstractVerticle {
  public static RedisConnection redis;
  public static RedisAPI redisApi;
  public static WebClient client;

  @Override
  public void start(Promise<Void> startPromise) {
    Redis.createClient(vertx, new RedisOptions()
    .setConnectionString("redis://localhost:6379")
    // allow at max 8 connections to redis
    .setMaxPoolSize(8)
    // allow 32 connection requests to queue waiting
    // for a connection to be available.
    .setMaxWaitingHandlers(32)).connect(connect -> {
      if (connect.succeeded()) {

        ItemConfig.initLsItem();

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.post("/api/wheel/get-item").handler(new WheelItem());
        router.post("/api/wheel/get-turn").handler(new WheelTurn());
        router.post("/api/wheel/get-history").handler(new WheelHistory());
        router.post("/api/wheel/get-inventory").handler(new WheelInventory());

        router.get("/loaderio-a654187e5a97475df91df772e1e916c5/").handler(ctx ->
                ctx.response().end("loaderio-a654187e5a97475df91df772e1e916c5"));

        vertx.createHttpServer().requestHandler(router).listen(3000);
        redis = connect.result();
        redisApi = RedisAPI.api(TLS.redis);


        WebClientOptions options = new WebClientOptions()
                .setUserAgent("Test Client/1.0");
        options.setKeepAlive(false);

        client = WebClient.create(vertx);
        startPromise.complete();
      }
    });

//    Redis.createClient(vertx, new RedisOptions())
//    .connect(onConnect -> {
//      if (onConnect.succeeded()) {
//        Router router = Router.router(vertx);
//        router.route().handler(BodyHandler.create());
//        router.post("/api/wheel/get-item").handler(new WheelItem());
//        router.post("/api/wheel/get-turn").handler(new WheelTurn());
//        router.post("/api/wheel/get-history").handler(new WheelHistory());
//        router.post("/api/wheel/get-inventory").handler(new WheelInventory());
//
//        router.get("/loaderio-a654187e5a97475df91df772e1e916c5/").handler(ctx ->
//                ctx.response().end("loaderio-a654187e5a97475df91df772e1e916c5"));
//
//        vertx.createHttpServer().requestHandler(router).listen(3000);
//        redis = onConnect.result();
//        redisApi = RedisAPI.api(TLS.redis);
//
//
//        WebClientOptions options = new WebClientOptions()
//                .setUserAgent("Test Client/1.0");
//        options.setKeepAlive(false);
//
//        client = WebClient.create(vertx);
//        startPromise.complete();
//      }
//    });
  }

  public static void main(String[] args) {
    Vertx.vertx().deployVerticle(TLS.class.getName());
  }
}