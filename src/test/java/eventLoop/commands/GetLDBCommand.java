package eventLoop.commands;

import eventLoop.EventLoop;
import eventLoop.LDB;
import eventLoop.ScoreObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.List;

public class GetLDBCommand implements EventLoop.Command {
  Handler<AsyncResult<List<ScoreObject>>> handler;
  LDB ldb;
  ScoreObject so;
  public GetLDBCommand(LDB ldb, Handler<AsyncResult<List<ScoreObject>>> handler) {
    this.handler = handler;
    this.ldb = ldb;
  }

  @Override
  public void execute() {
    List<ScoreObject> res = ldb.get();
    handler.handle(Future.succeededFuture(res));
  }
}
