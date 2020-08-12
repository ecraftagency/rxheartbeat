package com.heartbeat.db.dao;

import com.transport.model.MailObj;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class PublicMailBoxDAO {
  public long           lastSync;
  public Queue<MailObj> publicMessage;

  public static PublicMailBoxDAO ofDefault() {
    PublicMailBoxDAO dao  = new PublicMailBoxDAO();
    dao.lastSync          = 0;
    dao.publicMessage     = new ConcurrentLinkedDeque<>();
    return dao;
  }
}