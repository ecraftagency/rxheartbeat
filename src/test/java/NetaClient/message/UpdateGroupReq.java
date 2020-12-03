package NetaClient.message;

import java.util.List;

public class UpdateGroupReq {
  public long       group_id;
  public String     name;
  public String     avatar_url;
  public long       owner_uin;
  public List<Long> push_all;
  public List<Long> pull_all;

  public static UpdateGroupReq of(long group_id, String name, String avatar_url, long owner_uin, List<Long> push_all, List<Long> pull_all) {
    UpdateGroupReq req = new UpdateGroupReq();
    req.group_id = group_id;
    req.name = name;
    req.avatar_url = avatar_url;
    req.owner_uin = owner_uin;
    req.pull_all = pull_all;
    req.push_all = push_all;
    return req;
  }
}