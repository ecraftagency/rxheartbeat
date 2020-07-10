import com.heartbeat.model.Session;
import com.heartbeat.model.data.UserGameInfo;
import com.heartbeat.model.data.UserIdol;
import com.heartbeat.model.data.UserInventory;
import com.statics.*;
import com.transport.model.Idols;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class HaloTest {
  public static void main(String[] args) {
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

      String vipJson    = new String(Files.readAllBytes(Paths.get("data/json/vip.json")),
              StandardCharsets.UTF_8);
      VipData.loadJson(vipJson);

      WordFilter.loadJson("");
    }
    catch (Exception ioe) {
      //
    }

    Session session = Session.of(0);
    session.userGameInfo = UserGameInfo.ofDefault();
    session.userIdol = UserIdol.ofDefault();
    session.userIdol.addIdol(UserIdol.buildIdol(48));
    session.userInventory = UserInventory.ofDefault();

    System.out.println("\nidol 48 halo 7 level up to max");
    for (int i = 0; i < 10; i++) {
      session.userIdol.idolPersonalHaloLevelUp(session, 48, 7);
      int level = session.userIdol.idolMap.get(48).personalHalos.get(0).level;
      float crtPBuf = session.userIdol.idolMap.get(48).personalHalos.get(0).crtBufRate;
      float perfPBuf = session.userIdol.idolMap.get(48).personalHalos.get(0).perfBufRate;
      float attrPBuf = session.userIdol.idolMap.get(48).personalHalos.get(0).attrBufRate;
      System.out.println(level + " " + crtPBuf + " " + perfPBuf + " " + attrPBuf);
    }

    System.out.println("\nidol 53");

    int level,id;float crtPBuf,perfPBuf,attrPBuf;
    session.userIdol.addIdol(UserIdol.buildIdol(53));
    for (Idols.IdolHalo pHalo : session.userIdol.idolMap.get(53).personalHalos) {
      id = pHalo.id;
      level = pHalo.level;
      crtPBuf = pHalo.crtBufRate;
      perfPBuf = pHalo.perfBufRate;
      attrPBuf = pHalo.attrBufRate;
      System.out.println("Halo ID: " +id + " level " + level + " " + crtPBuf + " " + perfPBuf + " " + attrPBuf);
    }


    System.out.println("\nidol 35 -> 42");
    for (int i = 0; i < 8; i++) {
      session.userIdol.addIdol(UserIdol.buildIdol(i + 35));
      level = session.userIdol.idolMap.get(i + 35).personalHalos.get(0).level;
      crtPBuf = session.userIdol.idolMap.get(i + 35).personalHalos.get(0).crtBufRate;
      perfPBuf = session.userIdol.idolMap.get(i + 35).personalHalos.get(0).perfBufRate;
      attrPBuf = session.userIdol.idolMap.get(i + 35).personalHalos.get(0).attrBufRate;
      System.out.println(level + " " + crtPBuf + " " + perfPBuf + " " + attrPBuf);
    }

//    userIdol.addIdol(UserIdol.buildIdol(49));
//    userIdol.addIdol(UserIdol.buildIdol(50));
//    userIdol.addIdol(UserIdol.buildIdol(51));
//    userIdol.addIdol(UserIdol.buildIdol(52));
//
//    System.out.println(userIdol.idolMap.get(48).groupHalo.get(0).level);
//    System.out.println(userIdol.idolMap.get(48).groupHalo.get(0).crtBufRate);
//    System.out.println(userIdol.idolMap.get(48).groupHalo.get(0).perfBufRate);
//    System.out.println(userIdol.idolMap.get(48).groupHalo.get(0).attrBufRate);
    System.out.println("");
  }
}
