package com.heartbeat.model.data;

import com.common.Msg;
import com.heartbeat.db.cb.CBInbox;
import com.heartbeat.db.dao.PublicMailBoxDAO;
import com.heartbeat.effect.EffectHandler;
import com.heartbeat.effect.EffectManager;
import com.heartbeat.model.Session;
import com.transport.model.Inbox;
import com.transport.model.MailObj;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
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

  public static MailObj haveMessage(long id) {
    for (MailObj obj : publicInbox) {
      if (obj.id == id)
        return obj;
    }
    return null;
  }

  public static long checkNewMessage(long lastCheck) {
    if (publicInbox.size() == 0)
      return 0L;
    MailObj obj = publicInbox.getLast();
    if (obj == null || lastCheck >= obj.id)
      return 0L;
    return obj.id;
  }

  /********************************************************************************************************************/

  public void reBalance(long curMs) {
    if (claimedMsg == null) {
      claimedMsg = new HashMap<>();
    }
    claimedMsg.entrySet().removeIf(entry -> curMs - entry.getKey() > CLAIM_EXPIRE);
    if (privateMails == null)
      privateMails = new ArrayDeque<>();
    if(claimedPrivateMsg == null)
      claimedPrivateMsg = new HashMap<>();
  }

  public String claimPublicInboxReward(Session session, long cas, long curMs) {
    if (claimedMsg.containsKey(cas))
      return Msg.msgMap.getOrDefault(Msg.ALREADY_CLAIM, "msg_reward_already_claim");

    MailObj obj = haveMessage(cas);
    if (obj == null)
      return Msg.msgMap.getOrDefault(Msg.MSG_NOT_EXIST, "msg_not_exist");

    if (obj.rewards == null)
      return Msg.msgMap.getOrDefault(Msg.MSG_REWARD_INVALID, "msg_reward_invalid");

    for (List<Integer> reward : obj.rewards) {
      if (reward == null || reward.size() != 4)
        continue;
      EffectManager.inst().handleEffect(EffectHandler.ExtArgs.of(), session, reward);
    }

    claimedMsg.put(cas, curMs);

    return "ok";
  }

  public void addPrivateMsg(MailObj msg) {
    if (msg == null)
      return;
    if (privateMails == null) {
      privateMails = new ArrayDeque<>();
    }

    if (privateMails.size() >= MAX_INBOX_ITEM)
      privateMails.removeFirst();
    privateMails.addLast(msg);
    lastPriMsgTime = msg.id;
  }

  public String claimPrivateInboxReward(Session session, long cas, long curMs) {
    if (claimedPrivateMsg.containsKey(cas))
      return Msg.msgMap.getOrDefault(Msg.MSG_REWARD_CLAIM_ALREADY, "msg_reward_already_claim");

    MailObj obj = getPrivateMsgById(cas);
    if (obj == null)
      return Msg.msgMap.getOrDefault(Msg.MSG_NOT_EXIST, "msg_not_exist");

    if (obj.rewards == null)
      return Msg.msgMap.getOrDefault(Msg.MSG_REWARD_INVALID, "msg_reward_invalid");

    for (List<Integer> reward : obj.rewards) {
      if (reward == null || reward.size() != 4)
        continue;
      EffectManager.inst().handleEffect(EffectHandler.ExtArgs.of(), session, reward);
    }

    claimedPrivateMsg.put(cas, curMs);

    return "ok";
  }

  private MailObj getPrivateMsgById(long id) {
    for (MailObj obj : privateMails) {
      if (obj.id == id)
        return obj;
    }
    return null;
  }

  public boolean haveNewPriMsg() {
    return lastMailCheckTime > lastPriMsgTime;
  }
}