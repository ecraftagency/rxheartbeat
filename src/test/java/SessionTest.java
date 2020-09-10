import com.heartbeat.model.Session;
import com.heartbeat.model.data.UserGameInfo;
import com.heartbeat.model.data.UserIdol;
import com.heartbeat.model.data.UserInventory;
import com.heartbeat.model.data.UserTravel;
import com.statics.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class SessionTest {
  public static void main(String[] args) {
    Session session = Session.of(100000);
    session.userTravel = UserTravel.ofDefault();
    session.userInventory = UserInventory.ofDefault();
    session.userGameInfo = UserGameInfo.ofDefault();
    session.userIdol = UserIdol.ofDefault();

    try {
      String servantJson = new String(Files.readAllBytes(Paths.get("data/json/servants.json")),
              StandardCharsets.UTF_8);
      ServantData.loadJson(servantJson);

      String officeJson = new String(Files.readAllBytes(Paths.get("data/json/office.json")),
              StandardCharsets.UTF_8);
      OfficeData.loadJson(officeJson);

      String specialityJson = new String(Files.readAllBytes(Paths.get("data/json/specialty.json")),
              StandardCharsets.UTF_8);
      SpecialityData.loadJson(specialityJson);

      String servantLVJson = new String(Files.readAllBytes(Paths.get("data/json/servantLV.json")),
              StandardCharsets.UTF_8);
      ServantLVData.loadJson(servantLVJson);

      String servantHaloJson = new String(Files.readAllBytes(Paths.get("data/json/servantHaloBase.json")),
              StandardCharsets.UTF_8);
      HaloBaseData.loadJson(servantHaloJson);

      String haloJson = new String(Files.readAllBytes(Paths.get("data/json/Halo.json")),
              StandardCharsets.UTF_8);
      HaloData.loadJson(haloJson);

      String propJson = new String(Files.readAllBytes(Paths.get("data/json/props.json")),
              StandardCharsets.UTF_8);
      PropData.loadJson(propJson);

      String mediaJson = new String(Files.readAllBytes(Paths.get("data/json/media.json")),
              StandardCharsets.UTF_8);
      MediaData.loadJson(mediaJson);

      String honorJson = new String(Files.readAllBytes(Paths.get("data/json/servantHonor.json")),
              StandardCharsets.UTF_8);
      ServantHonorData.loadJson(honorJson);

      String headJson = new String(Files.readAllBytes(Paths.get("data/json/head.json")),
              StandardCharsets.UTF_8);
      HeadData.loadJson(headJson);

      String dropJson = new String(Files.readAllBytes(Paths.get("data/json/drop.json")),
              StandardCharsets.UTF_8);
      DropData.loadJson(dropJson);

      String bookLimitJson = new String(Files.readAllBytes(Paths.get("data/json/servantBookLimit.json")),
              StandardCharsets.UTF_8);
      BookLimitData.loadJson(bookLimitJson);

      String fightBossJson     = new String(Files.readAllBytes(Paths.get("data/json/fightBoss.json")),
              StandardCharsets.UTF_8);
      FightBossData.loadJson(fightBossJson);

      String fightJson = new String(Files.readAllBytes(Paths.get("data/json/fight.json")),
              StandardCharsets.UTF_8);
      FightData.loadJson(fightJson);

      String gameShowJson = new String(Files.readAllBytes(Paths.get("data/json/gameshow.json")),
              StandardCharsets.UTF_8);
      GameShowData.loadJson(gameShowJson);

      String runShowJson     = new String(Files.readAllBytes(Paths.get("data/json/runshow.json")),
              StandardCharsets.UTF_8);
      RunShowData.loadJson(runShowJson);

      String shoppingJson    = new String(Files.readAllBytes(Paths.get("data/json/shopping.json")),
              StandardCharsets.UTF_8);
      ShoppingData.loadJson(shoppingJson);

      String travelJson    = new String(Files.readAllBytes(Paths.get("data/json/travel.json")),
              StandardCharsets.UTF_8);

      TravelData.loadJson(travelJson, () ->
              TravelData.npcTypeMap = TravelData.travelNPCMap
                      .values()
                      .stream()
                      .collect(Collectors.groupingBy(TravelData.TravelNPC::getType, Collectors.toList())));

      WordFilter.loadText("");
    }
    catch (Exception ioe) {
      //
    }

    for (int i = 0; i < 10000; i++) {
      String res = session.userTravel.claimTravel(session, 0);
      System.out.println(session.userTravel.chosenNPCId + " " + res);
    }
  }
}
