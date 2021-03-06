package NetaClient;

import NetaClient.protocols.NetaService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
  public static void main(String[] args) throws InterruptedException {
    NetaService netaService = new NetaClientV1();
    netaService.setHandler(new NetaHandlerV1());
    Thread.sleep(2000);
    List<Long> emptyList = new ArrayList<>();
    NetaHandlerV1.service = netaService;

    List<Long> uins_28 = Arrays.asList( 281474976710851L,   281474976710916L,    281474977316558L,  281474976710855L,   281474976710828L,
                                        281474976710854L,   281474977316610L,    281474976981262L,  281474976711244L,   281474976710805L,
                                        281474976711125L,   281474976710932L,    281474976710681L,  281474976710808L,   281474976981339L,
                                        281474976710815L,   281474976710814L,    281474976710686L,  281474976710817L,   281474976710763L,
                                        281474976710688L,   281474976710816L,    281474976711074L,  281474976710818L,   281474977316517L,
                                        281474977316525L,   281474976710703L,    281474976710702L,  281474976710706L,   281474976710772L
    );


    List<Long> uins_30 = Arrays.asList( 3096224743817578L, 3096224743817574L, 3096224744741439L, 3096224744741413L,
                                        3096224744741499L, 3096224744741498L, 3096224743817508L);

    List<Long> mix      = Arrays.asList(281474976710851L,   281474976710916L,    281474977316558L,  281474976710855L,   281474976710828L,
                                        3096224743817578L, 3096224743817574L, 3096224744741439L, 3096224744741413L,
                                        281474976710854L,   281474977316610L,    281474976981262L,  281474976711244L,   281474976710805L
    );
    //netaService.listGroup();
    netaService.createGroup(4, "awk {print $1}", emptyList);
//    netaService.updateGroup(286097293284097L, "awk {print $1}",Arrays.asList(), mix);

//    for (Long uin : mix) {
//      netaService.updateGroup(286097293284097L, "awk {print $1}",Arrays.asList(uin), new ArrayList<>());
//    }
  }
}