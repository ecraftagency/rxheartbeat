package com.gmtool.controller;

import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class UserController implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext ctx) {
    JsonObject resp = new JsonObject();
    resp.put("session", "{\n" +
            "  \"userProfile\": {\n" +
            "    \"password\": \"2000000\",\n" +
            "    \"registerAt\": 1597653191,\n" +
            "    \"firstLogin\": 0,\n" +
            "    \"lastLogin\": 1597672308,\n" +
            "    \"lastLogout\": -55492,\n" +
            "    \"registerVer\": 0,\n" +
            "    \"loginToday\": 1,\n" +
            "    \"dailyCount\": 0,\n" +
            "    \"loginCount\": 9,\n" +
            "    \"onlineTime\": 0,\n" +
            "    \"banTo\": 0,\n" +
            "    \"banReason\": \"\",\n" +
            "    \"clientVersion\": 147,\n" +
            "    \"lastHostAddress\": \"\",\n" +
            "    \"lastClientAddress\": \"127.0.0.1\",\n" +
            "    \"lastOsPlatform\": \"android\",\n" +
            "    \"lastDeviceUID\": \"2ce24726-a434-11ea-bb37-0242ac130002\",\n" +
            "    \"lastClientSource\": \"VN\",\n" +
            "    \"facebookID\": \"\",\n" +
            "    \"facebookToken\": \"\",\n" +
            "    \"facebookName\": \"\",\n" +
            "    \"facebookAvatar\": \"\",\n" +
            "    \"languageCode\": 7,\n" +
            "    \"registerCountry\": null,\n" +
            "    \"lastCountry\": null,\n" +
            "    \"deviceUIDs\": [\n" +
            "      \"2ce24726-a434-11ea-bb37-0242ac130002\"\n" +
            "    ],\n" +
            "    \"isCloneUser\": false,\n" +
            "    \"totalPlayingTime\": 1494647834\n" +
            "  },\n" +
            "  \"userGameInfo\": {\n" +
            "    \"gender\": 0,\n" +
            "    \"avatar\": 1,\n" +
            "    \"displayName\": \"usejhjhjr\",\n" +
            "    \"money\": 2000000000,\n" +
            "    \"view\": 2000000000,\n" +
            "    \"fan\": 2000000000,\n" +
            "    \"talent\": 3000,\n" +
            "    \"time\": 153917,\n" +
            "    \"exp\": 1995,\n" +
            "    \"crazyDegree\": 0,\n" +
            "    \"titleId\": 2,\n" +
            "    \"vipExp\": 0,\n" +
            "    \"currMedia\": 3,\n" +
            "    \"maxMedia\": 4,\n" +
            "    \"lastMediaClaim\": 1597652800,\n" +
            "    \"nextQuestion\": 2,\n" +
            "    \"timeChange\": false,\n" +
            "    \"shopping\": {},\n" +
            "    \"crazyRewardClaim\": {},\n" +
            "    \"activeTime\": true\n" +
            "  },\n" +
            "  \"userProduction\": {\n" +
            "    \"currentGoldClaimCount\": 3,\n" +
            "    \"currentViewClaimCount\": 3,\n" +
            "    \"currentFanClaimCount\": 3,\n" +
            "    \"maxGoldClaim\": 3,\n" +
            "    \"maxViewClaim\": 3,\n" +
            "    \"maxFanClaim\": 3,\n" +
            "    \"lastGoldClaim\": 1597653180,\n" +
            "    \"lastViewClaim\": 1597653180,\n" +
            "    \"lastFanClaim\": 1597653180,\n" +
            "    \"goldRecoverInv\": 60,\n" +
            "    \"viewRecoverInv\": 60,\n" +
            "    \"fanRecoverInv\": 60,\n" +
            "    \"dailyRampage\": 0,\n" +
            "    \"maxDailyRampage\": 1\n" +
            "  },\n" +
            "  \"userIdol\": {\n" +
            "    \"idolMap\": {\n" +
            "      \"1\": {\n" +
            "        \"id\": 1,\n" +
            "        \"level\": 1,\n" +
            "        \"specialityID\": 6,\n" +
            "        \"creativity\": 30,\n" +
            "        \"performance\": 30,\n" +
            "        \"attractive\": 30,\n" +
            "        \"aptitudeExp\": 2000,\n" +
            "        \"crtItemBuf\": 0,\n" +
            "        \"perfItemBuf\": 0,\n" +
            "        \"attrItemBuf\": 0,\n" +
            "        \"groupHaloIds\": [],\n" +
            "        \"groupHalo\": [],\n" +
            "        \"personalHalos\": [],\n" +
            "        \"totalCrtHLBuf\": 0,\n" +
            "        \"totalPerfHLBuf\": 0,\n" +
            "        \"totalAttrHLBuf\": 0,\n" +
            "        \"crtApt\": 3,\n" +
            "        \"perfApt\": 3,\n" +
            "        \"attrApt\": 3,\n" +
            "        \"crtAptBuf\": 30,\n" +
            "        \"perfAptBuf\": 30,\n" +
            "        \"attrAptBuf\": 30,\n" +
            "        \"honorID\": 1\n" +
            "      },\n" +
            "      \"2\": {\n" +
            "        \"id\": 2,\n" +
            "        \"level\": 1,\n" +
            "        \"specialityID\": 2,\n" +
            "        \"creativity\": 61,\n" +
            "        \"performance\": 20,\n" +
            "        \"attractive\": 20,\n" +
            "        \"aptitudeExp\": 2000,\n" +
            "        \"crtItemBuf\": 0,\n" +
            "        \"perfItemBuf\": 0,\n" +
            "        \"attrItemBuf\": 0,\n" +
            "        \"groupHaloIds\": [],\n" +
            "        \"groupHalo\": [],\n" +
            "        \"personalHalos\": [],\n" +
            "        \"totalCrtHLBuf\": 0,\n" +
            "        \"totalPerfHLBuf\": 0,\n" +
            "        \"totalAttrHLBuf\": 0,\n" +
            "        \"crtApt\": 6,\n" +
            "        \"perfApt\": 2,\n" +
            "        \"attrApt\": 2,\n" +
            "        \"crtAptBuf\": 61,\n" +
            "        \"perfAptBuf\": 20,\n" +
            "        \"attrAptBuf\": 20,\n" +
            "        \"honorID\": 1\n" +
            "      },\n" +
            "      \"3\": {\n" +
            "        \"id\": 3,\n" +
            "        \"level\": 1,\n" +
            "        \"specialityID\": 3,\n" +
            "        \"creativity\": 20,\n" +
            "        \"performance\": 61,\n" +
            "        \"attractive\": 20,\n" +
            "        \"aptitudeExp\": 2000,\n" +
            "        \"crtItemBuf\": 0,\n" +
            "        \"perfItemBuf\": 0,\n" +
            "        \"attrItemBuf\": 0,\n" +
            "        \"groupHaloIds\": [],\n" +
            "        \"groupHalo\": [],\n" +
            "        \"personalHalos\": [],\n" +
            "        \"totalCrtHLBuf\": 0,\n" +
            "        \"totalPerfHLBuf\": 0,\n" +
            "        \"totalAttrHLBuf\": 0,\n" +
            "        \"crtApt\": 2,\n" +
            "        \"perfApt\": 6,\n" +
            "        \"attrApt\": 2,\n" +
            "        \"crtAptBuf\": 20,\n" +
            "        \"perfAptBuf\": 61,\n" +
            "        \"attrAptBuf\": 20,\n" +
            "        \"honorID\": 1\n" +
            "      },\n" +
            "      \"4\": {\n" +
            "        \"id\": 4,\n" +
            "        \"level\": 1,\n" +
            "        \"specialityID\": 4,\n" +
            "        \"creativity\": 20,\n" +
            "        \"performance\": 20,\n" +
            "        \"attractive\": 61,\n" +
            "        \"aptitudeExp\": 2000,\n" +
            "        \"crtItemBuf\": 0,\n" +
            "        \"perfItemBuf\": 0,\n" +
            "        \"attrItemBuf\": 0,\n" +
            "        \"groupHaloIds\": [],\n" +
            "        \"groupHalo\": [],\n" +
            "        \"personalHalos\": [],\n" +
            "        \"totalCrtHLBuf\": 0,\n" +
            "        \"totalPerfHLBuf\": 0,\n" +
            "        \"totalAttrHLBuf\": 0,\n" +
            "        \"crtApt\": 2,\n" +
            "        \"perfApt\": 2,\n" +
            "        \"attrApt\": 6,\n" +
            "        \"crtAptBuf\": 20,\n" +
            "        \"perfAptBuf\": 20,\n" +
            "        \"attrAptBuf\": 61,\n" +
            "        \"honorID\": 1\n" +
            "      },\n" +
            "      \"5\": {\n" +
            "        \"id\": 5,\n" +
            "        \"level\": 1,\n" +
            "        \"specialityID\": 6,\n" +
            "        \"creativity\": 40,\n" +
            "        \"performance\": 40,\n" +
            "        \"attractive\": 40,\n" +
            "        \"aptitudeExp\": 2000,\n" +
            "        \"crtItemBuf\": 0,\n" +
            "        \"perfItemBuf\": 0,\n" +
            "        \"attrItemBuf\": 0,\n" +
            "        \"groupHaloIds\": [],\n" +
            "        \"groupHalo\": [],\n" +
            "        \"personalHalos\": [],\n" +
            "        \"totalCrtHLBuf\": 0,\n" +
            "        \"totalPerfHLBuf\": 0,\n" +
            "        \"totalAttrHLBuf\": 0,\n" +
            "        \"crtApt\": 4,\n" +
            "        \"perfApt\": 4,\n" +
            "        \"attrApt\": 4,\n" +
            "        \"crtAptBuf\": 40,\n" +
            "        \"perfAptBuf\": 40,\n" +
            "        \"attrAptBuf\": 40,\n" +
            "        \"honorID\": 1\n" +
            "      }\n" +
            "    },\n" +
            "    \"dailyRampage\": 0,\n" +
            "    \"maxDailyRampage\": 1\n" +
            "  },\n" +
            "  \"userInventory\": {\n" +
            "    \"userItems\": {\n" +
            "      \"1\": 1000,\n" +
            "      \"2\": 1000,\n" +
            "      \"3\": 1000,\n" +
            "      \"4\": 1000,\n" +
            "      \"5\": 1000,\n" +
            "      \"6\": 1000,\n" +
            "      \"7\": 1000,\n" +
            "      \"8\": 1000,\n" +
            "      \"9\": 1000,\n" +
            "      \"10\": 1000,\n" +
            "      \"11\": 1000,\n" +
            "      \"12\": 1000,\n" +
            "      \"13\": 1000,\n" +
            "      \"14\": 1000,\n" +
            "      \"15\": 1000,\n" +
            "      \"16\": 1000,\n" +
            "      \"17\": 1000,\n" +
            "      \"18\": 1000,\n" +
            "      \"19\": 1000,\n" +
            "      \"20\": 1000,\n" +
            "      \"21\": 1000,\n" +
            "      \"22\": 1000,\n" +
            "      \"23\": 1000,\n" +
            "      \"24\": 1000,\n" +
            "      \"25\": 1000,\n" +
            "      \"26\": 1000,\n" +
            "      \"27\": 1000,\n" +
            "      \"28\": 1000,\n" +
            "      \"29\": 1000,\n" +
            "      \"30\": 1000,\n" +
            "      \"31\": 1000,\n" +
            "      \"32\": 1000,\n" +
            "      \"33\": 1000,\n" +
            "      \"34\": 1000,\n" +
            "      \"35\": 1000,\n" +
            "      \"36\": 1000,\n" +
            "      \"37\": 1000,\n" +
            "      \"38\": 1000,\n" +
            "      \"39\": 1000,\n" +
            "      \"40\": 1000,\n" +
            "      \"41\": 1000,\n" +
            "      \"42\": 1000,\n" +
            "      \"43\": 1000,\n" +
            "      \"44\": 1000,\n" +
            "      \"45\": 1000,\n" +
            "      \"46\": 1000,\n" +
            "      \"47\": 1000,\n" +
            "      \"48\": 1000,\n" +
            "      \"49\": 1000,\n" +
            "      \"50\": 1000,\n" +
            "      \"51\": 1000,\n" +
            "      \"52\": 1000,\n" +
            "      \"53\": 1000,\n" +
            "      \"54\": 1000,\n" +
            "      \"55\": 1000,\n" +
            "      \"56\": 1000,\n" +
            "      \"57\": 1000,\n" +
            "      \"58\": 1000,\n" +
            "      \"59\": 1000,\n" +
            "      \"60\": 1000,\n" +
            "      \"61\": 1000,\n" +
            "      \"62\": 1000,\n" +
            "      \"63\": 1000,\n" +
            "      \"64\": 1000,\n" +
            "      \"65\": 1000,\n" +
            "      \"66\": 1000,\n" +
            "      \"67\": 1000,\n" +
            "      \"68\": 1000,\n" +
            "      \"69\": 1000,\n" +
            "      \"70\": 1000,\n" +
            "      \"71\": 1000,\n" +
            "      \"72\": 1000,\n" +
            "      \"73\": 1000,\n" +
            "      \"74\": 1000,\n" +
            "      \"75\": 1000,\n" +
            "      \"76\": 1000,\n" +
            "      \"77\": 1000,\n" +
            "      \"78\": 1000,\n" +
            "      \"79\": 1000,\n" +
            "      \"80\": 1000,\n" +
            "      \"81\": 1000,\n" +
            "      \"82\": 1000,\n" +
            "      \"83\": 1000,\n" +
            "      \"84\": 1000,\n" +
            "      \"85\": 1000,\n" +
            "      \"86\": 1000,\n" +
            "      \"87\": 1000,\n" +
            "      \"88\": 1000,\n" +
            "      \"89\": 1000,\n" +
            "      \"90\": 1000,\n" +
            "      \"91\": 1000,\n" +
            "      \"92\": 1000,\n" +
            "      \"93\": 1000,\n" +
            "      \"94\": 1000,\n" +
            "      \"95\": 1000,\n" +
            "      \"96\": 1000,\n" +
            "      \"97\": 1000,\n" +
            "      \"98\": 1000,\n" +
            "      \"99\": 1000,\n" +
            "      \"100\": 1000,\n" +
            "      \"101\": 1000,\n" +
            "      \"102\": 1000,\n" +
            "      \"103\": 1000,\n" +
            "      \"104\": 1000,\n" +
            "      \"105\": 1000,\n" +
            "      \"106\": 1000,\n" +
            "      \"107\": 1000,\n" +
            "      \"108\": 1000,\n" +
            "      \"109\": 1000,\n" +
            "      \"110\": 1000,\n" +
            "      \"111\": 1000,\n" +
            "      \"112\": 1000,\n" +
            "      \"113\": 1000,\n" +
            "      \"114\": 1000,\n" +
            "      \"115\": 1000,\n" +
            "      \"116\": 1000,\n" +
            "      \"117\": 1000,\n" +
            "      \"118\": 1000,\n" +
            "      \"119\": 1000,\n" +
            "      \"120\": 1000,\n" +
            "      \"121\": 1000,\n" +
            "      \"122\": 1000,\n" +
            "      \"123\": 1000,\n" +
            "      \"124\": 1000,\n" +
            "      \"125\": 1000,\n" +
            "      \"126\": 1000,\n" +
            "      \"127\": 1000,\n" +
            "      \"128\": 1000,\n" +
            "      \"129\": 1000,\n" +
            "      \"130\": 1000,\n" +
            "      \"131\": 1000,\n" +
            "      \"132\": 1000,\n" +
            "      \"133\": 1000,\n" +
            "      \"134\": 1000\n" +
            "    },\n" +
            "    \"dailyMerge\": {}\n" +
            "  },\n" +
            "  \"userFight\": {\n" +
            "    \"currentFightLV\": {\n" +
            "      \"id\": 1,\n" +
            "      \"chapter\": 1,\n" +
            "      \"level\": 1,\n" +
            "      \"smallLevel\": 1,\n" +
            "      \"aptNPC\": 160,\n" +
            "      \"fanNPC\": 800,\n" +
            "      \"reward\": [\n" +
            "        102,\n" +
            "        18,\n" +
            "        0,\n" +
            "        0\n" +
            "      ],\n" +
            "      \"boss\": null\n" +
            "    },\n" +
            "    \"usedIdols\": [],\n" +
            "    \"restoreIdols\": [],\n" +
            "    \"currentGameShow\": {\n" +
            "      \"id\": 1,\n" +
            "      \"bosshp\": 200110,\n" +
            "      \"reward\": [\n" +
            "        [\n" +
            "          2,\n" +
            "          1,\n" +
            "          80,\n" +
            "          0\n" +
            "        ],\n" +
            "        [\n" +
            "          1,\n" +
            "          1,\n" +
            "          1000,\n" +
            "          0\n" +
            "        ],\n" +
            "        [\n" +
            "          102,\n" +
            "          22,\n" +
            "          0,\n" +
            "          0\n" +
            "        ]\n" +
            "      ]\n" +
            "    },\n" +
            "    \"gameShowUsedIdols\": [],\n" +
            "    \"gameShowRestoreIdols\": [],\n" +
            "    \"gameShowOpenCountDown\": 11170,\n" +
            "    \"currentRunShow\": {\n" +
            "      \"id\": 1,\n" +
            "      \"minFanNPC\": 2272500,\n" +
            "      \"maxFanNPC\": 3787500,\n" +
            "      \"minAptNPC\": 370872,\n" +
            "      \"maxAptNPC\": 618120,\n" +
            "      \"randFanNPC\": 3398901,\n" +
            "      \"randAptNPC\": 601268,\n" +
            "      \"reward\": [\n" +
            "        29\n" +
            "      ]\n" +
            "    },\n" +
            "    \"currentShopping\": {\n" +
            "      \"id\": 1,\n" +
            "      \"creativeNPC\": 303000,\n" +
            "      \"moneyNPC\": 3030000,\n" +
            "      \"reward\": [\n" +
            "        60\n" +
            "      ]\n" +
            "    }\n" +
            "  },\n" +
            "  \"userTravel\": {\n" +
            "    \"currentTravelClaimCount\": 0,\n" +
            "    \"maxTravelClaim\": 5,\n" +
            "    \"lastTravelClaim\": 0,\n" +
            "    \"chosenNPCId\": -1,\n" +
            "    \"travelInv\": 1200,\n" +
            "    \"dailyTravelAdd\": 0,\n" +
            "    \"dailyTravelAddLimit\": 5\n" +
            "  },\n" +
            "  \"userDailyMission\": {\n" +
            "    \"missionMap\": {\n" +
            "      \"1\": {\n" +
            "        \"id\": 1,\n" +
            "        \"dailyCount\": 0,\n" +
            "        \"type\": 3,\n" +
            "        \"claim\": false\n" +
            "      },\n" +
            "      \"2\": {\n" +
            "        \"id\": 2,\n" +
            "        \"dailyCount\": 0,\n" +
            "        \"type\": 2,\n" +
            "        \"claim\": false\n" +
            "      },\n" +
            "      \"3\": {\n" +
            "        \"id\": 3,\n" +
            "        \"dailyCount\": 0,\n" +
            "        \"type\": 1,\n" +
            "        \"claim\": false\n" +
            "      },\n" +
            "      \"4\": {\n" +
            "        \"id\": 4,\n" +
            "        \"dailyCount\": 0,\n" +
            "        \"type\": 4,\n" +
            "        \"claim\": false\n" +
            "      },\n" +
            "      \"5\": {\n" +
            "        \"id\": 5,\n" +
            "        \"dailyCount\": 0,\n" +
            "        \"type\": 3,\n" +
            "        \"claim\": false\n" +
            "      },\n" +
            "      \"6\": {\n" +
            "        \"id\": 6,\n" +
            "        \"dailyCount\": 0,\n" +
            "        \"type\": 2,\n" +
            "        \"claim\": false\n" +
            "      },\n" +
            "      \"7\": {\n" +
            "        \"id\": 7,\n" +
            "        \"dailyCount\": 0,\n" +
            "        \"type\": 1,\n" +
            "        \"claim\": false\n" +
            "      },\n" +
            "      \"8\": {\n" +
            "        \"id\": 8,\n" +
            "        \"dailyCount\": 0,\n" +
            "        \"type\": 4,\n" +
            "        \"claim\": false\n" +
            "      },\n" +
            "      \"9\": {\n" +
            "        \"id\": 9,\n" +
            "        \"dailyCount\": 0,\n" +
            "        \"type\": 5,\n" +
            "        \"claim\": false\n" +
            "      },\n" +
            "      \"10\": {\n" +
            "        \"id\": 10,\n" +
            "        \"dailyCount\": 0,\n" +
            "        \"type\": 6,\n" +
            "        \"claim\": false\n" +
            "      },\n" +
            "      \"11\": {\n" +
            "        \"id\": 11,\n" +
            "        \"dailyCount\": 0,\n" +
            "        \"type\": 7,\n" +
            "        \"claim\": false\n" +
            "      },\n" +
            "      \"12\": {\n" +
            "        \"id\": 12,\n" +
            "        \"dailyCount\": 0,\n" +
            "        \"type\": 8,\n" +
            "        \"claim\": false\n" +
            "      },\n" +
            "      \"13\": {\n" +
            "        \"id\": 13,\n" +
            "        \"dailyCount\": 0,\n" +
            "        \"type\": 9,\n" +
            "        \"claim\": false\n" +
            "      },\n" +
            "      \"14\": {\n" +
            "        \"id\": 14,\n" +
            "        \"dailyCount\": 0,\n" +
            "        \"type\": 10,\n" +
            "        \"claim\": false\n" +
            "      },\n" +
            "      \"15\": {\n" +
            "        \"id\": 15,\n" +
            "        \"dailyCount\": 0,\n" +
            "        \"type\": 11,\n" +
            "        \"claim\": false\n" +
            "      },\n" +
            "      \"16\": {\n" +
            "        \"id\": 16,\n" +
            "        \"dailyCount\": 0,\n" +
            "        \"type\": 12,\n" +
            "        \"claim\": false\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \"userAchievement\": {\n" +
            "    \"claimedAchievement\": {\n" +
            "      \"1\": [\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0\n" +
            "      ],\n" +
            "      \"2\": [\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0\n" +
            "      ],\n" +
            "      \"3\": [\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0\n" +
            "      ],\n" +
            "      \"4\": [\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0\n" +
            "      ],\n" +
            "      \"5\": [\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0\n" +
            "      ],\n" +
            "      \"6\": [\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0\n" +
            "      ],\n" +
            "      \"7\": [\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0\n" +
            "      ],\n" +
            "      \"8\": [\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0\n" +
            "      ],\n" +
            "      \"9\": [\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0\n" +
            "      ],\n" +
            "      \"10\": [\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0\n" +
            "      ],\n" +
            "      \"11\": [\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0\n" +
            "      ],\n" +
            "      \"12\": [\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0\n" +
            "      ],\n" +
            "      \"13\": [\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0\n" +
            "      ],\n" +
            "      \"14\": [\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0\n" +
            "      ]\n" +
            "    },\n" +
            "    \"records\": {\n" +
            "      \"1\": 1,\n" +
            "      \"2\": 0,\n" +
            "      \"3\": 0,\n" +
            "      \"4\": 2,\n" +
            "      \"5\": 0,\n" +
            "      \"6\": 0,\n" +
            "      \"7\": 0,\n" +
            "      \"8\": 0,\n" +
            "      \"9\": 0,\n" +
            "      \"10\": 0,\n" +
            "      \"11\": 0,\n" +
            "      \"12\": 0,\n" +
            "      \"13\": 0,\n" +
            "      \"14\": 0\n" +
            "    }\n" +
            "  },\n" +
            "  \"userMission\": {\n" +
            "    \"currentMissionId\": 1,\n" +
            "    \"complete\": false,\n" +
            "    \"currentCount\": 0,\n" +
            "    \"target\": 3\n" +
            "  },\n" +
            "  \"userRollCall\": {\n" +
            "    \"nClaimedDays\": 0,\n" +
            "    \"lastDailyClaimTime\": 0,\n" +
            "    \"todayClaim\": false,\n" +
            "    \"currentVipLevel\": 0,\n" +
            "    \"vipClaimed\": {},\n" +
            "    \"giftCards\": {}\n" +
            "  },\n" +
            "  \"userEvent\": {\n" +
            "    \"claimed\": {\n" +
            "      \"6\": [\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0\n" +
            "      ],\n" +
            "      \"7\": [\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0\n" +
            "      ],\n" +
            "      \"8\": [\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0\n" +
            "      ],\n" +
            "      \"9\": [\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0\n" +
            "      ],\n" +
            "      \"10\": [\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0\n" +
            "      ],\n" +
            "      \"11\": [\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0\n" +
            "      ],\n" +
            "      \"12\": [\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0\n" +
            "      ],\n" +
            "      \"13\": [\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0\n" +
            "      ],\n" +
            "      \"21\": [\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0\n" +
            "      ],\n" +
            "      \"6700\": [\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0,\n" +
            "        0\n" +
            "      ]\n" +
            "    },\n" +
            "    \"records\": {\n" +
            "      \"6\": 0,\n" +
            "      \"7\": 0,\n" +
            "      \"8\": 0,\n" +
            "      \"9\": 0,\n" +
            "      \"10\": 0,\n" +
            "      \"11\": 0,\n" +
            "      \"12\": 0,\n" +
            "      \"13\": 0,\n" +
            "      \"21\": 0,\n" +
            "      \"6700\": 0\n" +
            "    },\n" +
            "    \"evt2cas\": {\n" +
            "      \"6\": 0,\n" +
            "      \"7\": 0,\n" +
            "      \"8\": 0,\n" +
            "      \"9\": 0,\n" +
            "      \"10\": 0,\n" +
            "      \"11\": 0,\n" +
            "      \"12\": 0,\n" +
            "      \"13\": 0,\n" +
            "      \"21\": 0,\n" +
            "      \"6700\": 0\n" +
            "    }\n" +
            "  },\n" +
            "  \"userRanking\": {\n" +
            "    \"records\": {},\n" +
            "    \"claimed\": {},\n" +
            "    \"cas\": 0\n" +
            "  },\n" +
            "  \"userInbox\": {\n" +
            "    \"lastMailCheckTime\": 1597653191,\n" +
            "    \"claimedMsg\": {}\n" +
            "  },\n" +
            "  \"effectResults\": []\n" +
            "}");
    ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
  }
}