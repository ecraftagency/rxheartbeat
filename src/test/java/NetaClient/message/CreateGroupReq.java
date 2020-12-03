package NetaClient.message;

import java.util.List;

public class CreateGroupReq {
  public long           appid;
  public String         name;
  public int            type;
  public String         avatar_url;
  public long           owner_uin;
  public List<Long>     occupants_uins;

  public static CreateGroupReq of (long appid, String name, int type, String avatar_url, long owner_uin, List<Long> occupants_uins) {
    CreateGroupReq req = new CreateGroupReq();
    req.appid           = appid;
    req.name            = name;
    req.avatar_url      = avatar_url;
    req.occupants_uins  = occupants_uins;
    req.owner_uin       = owner_uin;
    req.type            = type;
    return req;
  }
}