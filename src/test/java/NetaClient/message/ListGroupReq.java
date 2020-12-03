package NetaClient.message;

public class ListGroupReq {
  long  uin;
  int   pindex;
  int   psize;
  int   sort_by;

  public static ListGroupReq of(long uin, int pindex, int psize, int sort_by) {
    ListGroupReq req  = new ListGroupReq();
    req.uin           = uin;
    req.pindex        = pindex;
    req.psize         = psize;
    req.sort_by       = sort_by;
    return req;
  }
}
