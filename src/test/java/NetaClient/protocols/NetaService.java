package NetaClient.protocols;

import java.util.List;

public interface NetaService {
  void createGroup(String groupName, List<Long> occupants);
  void listGroup();
  void updateGroup(long group_id, String name, List<Long> pushIds, List<Long> pullIds);
  void deleteGroup(long groupId);
  void setHandler(NetaHandler listener);
}