package NetaClient;

import NetaClient.message.*;
import NetaClient.protocols.NetaHandler;
import NetaClient.protocols.NetaService;

public class NetaHandlerV1 implements NetaHandler {
  public static NetaService service;
  @Override
  public void onRespEvt(String evt, Object resp) {
    switch (evt) {
      case "create_group":
        if (resp instanceof CreateGroupResp)
          processCreateGroup((CreateGroupResp) resp);
        break;
      case "list_group":
        if (resp instanceof ListGroupResp)
          processListGroup((ListGroupResp)resp);
        break;
      case "delete_conversation":
        if (resp instanceof DeleteGroupResp)
          processDeleteGroup((DeleteGroupResp)resp);
        break;
      case "update_group":
        if (resp instanceof UpdateGroupResp)
          processUpdateGroup((UpdateGroupResp)resp);
      default:
        break;
    }
  }

  private void processUpdateGroup(UpdateGroupResp resp) {
    if (resp.result == 0 && resp.group != null) {
      System.out.println(String.format("%s\t\t%s", resp.group.group_id, resp.group.name));
      System.out.println("occupants: " + resp.group.occupants_uins);
    }
    else {
      System.out.println(resp.result);
    }
  }

  private void processDeleteGroup(DeleteGroupResp resp) {
    System.out.println(resp.result);
  }

  private void processListGroup(ListGroupResp resp) {
    if (resp.result == 0 && resp.groups != null) {
      for (NetaGroup group : resp.groups) {
        System.out.println(String.format("%s\t\t%s", group.group_id, group.name));
        System.out.println("occupants: " + group.occupants_uins);
      }
    }
  }

  private void processCreateGroup(CreateGroupResp resp) {
    if (resp.result == 0 && resp.group != null) {
      System.out.println(String.format("%s\t\t%s", resp.group.group_id, resp.group.name));
      System.out.println("occupants: " + resp.group.occupants_uins);
    }
    else {
      System.out.println(resp.result);
    }
  }
}