package NetaClient.message;

import java.util.List;

public class NetaGroup {
  public long       group_id;
  public int        type;
  public String     name;
  public String     avatar_url;
  public long       owner_uin;
  public long       create_uin;
  public List<Long> occupants_uins;
  public long       created_at;
  public long       updated_at;
  public long       last_message_id;
}