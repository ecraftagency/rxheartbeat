package eventLoop.commands;

import eventLoop.EventLoop;
import eventLoop.LDB;
import eventLoop.ScoreObject;

public class RecordCommand implements EventLoop.Command {
  LDB ldb;
  ScoreObject so;

  public RecordCommand(LDB ldb, ScoreObject so) {
    this.ldb = ldb;
    this.so  = so;
  }

  @Override
  public void execute() {
    ldb.record(so);
  }
}