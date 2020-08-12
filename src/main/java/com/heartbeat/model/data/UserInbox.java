package com.heartbeat.model.data;

import com.heartbeat.db.cb.CBInbox;
import com.heartbeat.db.dao.PublicMailBoxDAO;
import com.transport.model.Inbox;
import com.transport.model.MailObj;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class UserInbox extends Inbox {
  public static ConcurrentLinkedDeque<MailObj>  publicInbox;
  public static long                            CLAIM_EXPIRE    = 3*30*86400*1000L; //3 months in miliSecond
  public static int                             MAX_INBOX_ITEM  = 10;
  public static final String                    INBOX_DB_KEY    = "publicInbox";

  static {
    publicInbox = new ConcurrentLinkedDeque<>();
  }

  public static UserInbox ofDefault() {
    UserInbox ui          = new UserInbox();
    ui.lastMailCheckTime  = System.currentTimeMillis()/1000;
    ui.claimedMsg         = new HashMap<>();
    return ui;
  }

  public static void loadInboxFromDB() {
    PublicMailBoxDAO dao = CBInbox.getInstance().load(INBOX_DB_KEY);
    if (dao.publicMessage != null)
      for (MailObj mo : dao.publicMessage) {
        if (publicInbox.size() >= MAX_INBOX_ITEM)
          publicInbox.removeFirst();
        publicInbox.addLast(mo);
      }
  }

  private static void syncInboxToDB() {
    PublicMailBoxDAO dao  = new PublicMailBoxDAO();
    dao.lastSync          = System.currentTimeMillis();
    dao.publicMessage     = publicInbox;
    CBInbox.getInstance().sync(INBOX_DB_KEY, dao);
  }

  public static void addPublicMessage(MailObj msg) {
    if (msg == null)
      return;
    if (publicInbox.size() >= MAX_INBOX_ITEM)
      publicInbox.removeFirst();
    publicInbox.addLast(msg);

    syncInboxToDB();
  }

  /********************************************************************************************************************/

  public void reBalance(long curMs) {
    if (claimedMsg == null) {
      claimedMsg = new HashMap<>();
    }
    claimedMsg.entrySet().removeIf(entry -> curMs - entry.getKey() > CLAIM_EXPIRE);
  }
}