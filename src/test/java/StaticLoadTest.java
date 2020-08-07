import com.heartbeat.common.Constant;
import com.heartbeat.model.data.UserAchievement;
import com.statics.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class StaticLoadTest {
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

      String companyEventJson = new String(Files.readAllBytes(Paths.get("data/json/companyEvent.json")),
              StandardCharsets.UTF_8);
      GroupMissionData.loadJson(companyEventJson);

      String crazyReward = new String(Files.readAllBytes(Paths.get("data/json/crazyReward.json")),
              StandardCharsets.UTF_8);
      CrazyRewardData.loadJson(crazyReward);


      String dailyMission = new String(Files.readAllBytes(Paths.get("data/json/dailyMission.json")),
              StandardCharsets.UTF_8);
      DailyMissionData.loadJson(dailyMission);

      String achievement = new String(Files.readAllBytes(Paths.get("data/json/achievement.json")),
              StandardCharsets.UTF_8);
      AchievementData.loadJson(achievement);

      String mission = new String(Files.readAllBytes(Paths.get("data/json/mission.json")),
              StandardCharsets.UTF_8);
      MissionData.loadJson(mission);

      String dailyGift = new String(Files.readAllBytes(Paths.get("data/json/dailyGift.json")),
              StandardCharsets.UTF_8);
      DailyGiftData.loadJson(dailyGift);

      String vipGift = new String(Files.readAllBytes(Paths.get("data/json/vipGift.json")),
              StandardCharsets.UTF_8);
      VipGiftData.loadJson(vipGift);

      String giftCard = new String(Files.readAllBytes(Paths.get("data/json/giftCard.json")),
              StandardCharsets.UTF_8);
      GiftCardData.loadJson(giftCard);

      String shop = new String(Files.readAllBytes(Paths.get("data/json/shop.json")),
              StandardCharsets.UTF_8);
      ShopData.loadJson(shop);

      String itemMerge = new String(Files.readAllBytes(Paths.get("data/json/itemMerge.json")),
              StandardCharsets.UTF_8);
      ItemMergeData.loadJson(itemMerge);

      String event = new String(Files.readAllBytes(Paths.get("data/json/event.json")),
              StandardCharsets.UTF_8);
      EventData.loadJson(event);

      String rank = new String(Files.readAllBytes(Paths.get("data/json/rankingReward.json")),
              StandardCharsets.UTF_8);
      RankingData.loadJson(rank);

      WordFilter.loadJson("");
    }
    catch (Exception ioe) {
      System.out.println(ioe.getMessage());
    }

    UserAchievement ac = UserAchievement.ofDefault();
    ac.recordClaim(4, 1);
    System.out.println(ac.checkClaim(4, 33));
    for (Long l : ac.claimedAchievement.get(4))
      System.out.println(Long.toBinaryString(l));
//    List<AchievementData.AchievementDto> dtos = AchievementData.getAchievementDto(4, 6);
//    for (AchievementData.AchievementDto dto : dtos)
//      System.out.println(dto.milestoneValue);
  }
}
