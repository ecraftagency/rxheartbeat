package NetaClient.protocols;

public interface NetaHandler {
  void onRespEvt(String evt, Object resp);
}