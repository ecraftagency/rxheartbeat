package com.gmtool;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;

import java.util.*;

/*@On Memory database T____T*/
@SuppressWarnings("unused")
public class DB {
  public static final Map<String, GmtUser> users;
  public static final Set<String> readPerms;
  public static final Set<String> rootPerms;

  static {
    users       = new HashMap<>();
    readPerms   = new HashSet<>();
    rootPerms = new HashSet<>();

    readPerms.addAll(Arrays.asList("getSession", "getEvents", "getRole100D", "getConfig",
            "getLDB", "getSessionId", "getPaymentInfo", "getShopInfo", "getStats"));

    rootPerms.addAll(Arrays.asList("injectSession", "genWebPaymentLink", "genIAPPaymentLink",
            "genNCExchangeLink", "genNPExchangeLink", "genGetRoleLink", "getSession",
            "sendMail", "sendPrivateMail", "getEvents", "setUserEventTime", "getRole100D",
            "updatePaymentPackage", "getConfig", "injectConstant", "getLDB", "getSessionId",
            "getPaymentInfo", "getShopInfo", "updateShopStatus", "getStats", "planEvent"));

    users.put("ly_mac_sau",   GmtUser.of("ly_mac_sau", "12345678").setPerms(rootPerms));
    users.put("duy79",        GmtUser.of("duy79", "12345678").setPerms(rootPerms));
    users.put("duy99",        GmtUser.of("duy99", "12345678").setPerms(rootPerms));
    users.put("vu_lo_gach",   GmtUser.of("vu_lo_gach", "n5t5lnsct").setPerms(rootPerms));
  }
  public static class DummyAuthProvider implements AuthProvider {
    @Override
    public void authenticate(JsonObject authInfo, Handler<AsyncResult<User>> resultHandler) {
      GmtUser user = DB.users.get(authInfo.getString("username"));
      if (user != null && authInfo.getString("password").equals(user.password)) {
        resultHandler.handle(Future.succeededFuture(user));
        return;
      }
      resultHandler.handle(Future.failedFuture("username password incorrect"));
    }
  }

  public static class GmtUser implements User {
    private JsonObject principal;
    public  String username;
    public  String password;
    private AuthProvider authProvider;
    public  int authTime;
    public  Set<String> perms;

    private GmtUser() {
      perms = new HashSet<>();
    }

    public static GmtUser of(String username, String password) {
      GmtUser u = new GmtUser();
      u.username = username;
      u.password = password;
      u.principal = new JsonObject();
      u.principal.put("username", username);
      u.principal.put("password", password);
      return u;
    }

    public GmtUser setPerm(String action) {
      perms.add(action);
      return this;
    }

    public GmtUser setPerms(Set<String> perms) {
      this.perms = perms;
      return this;
    }

    @Override
    public User isAuthorized(String authority, Handler<AsyncResult<Boolean>> resultHandler) {
      int curSec = (int)(System.currentTimeMillis()/1000);
      resultHandler.handle(Future.succeededFuture(curSec - authTime < 300));
      return this;
    }

    @Override
    public User clearCache() {
      authTime = 0;
      return this;
    }

    @Override
    public JsonObject principal() {
      return principal;
    }

    @Override
    public void setAuthProvider(AuthProvider authProvider) {
      this.authProvider = authProvider;
    }
  }
}