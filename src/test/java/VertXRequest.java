import com.transport.ExtMessage;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.junit5.web.VertxWebClientExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.vertx.junit5.web.TestRequest.*;

@ExtendWith({
  VertxExtension.class,
  VertxWebClientExtension.class
})
public class VertXRequest {
  @Test
  public void test1(WebClient client, VertxTestContext testContext) {
    testRequest(client, HttpMethod.POST, "/api/auth")
      .with(
        requestHeader("Content-Type", "text/json")
      )
      .sendJson(ExtMessage.fight(), testContext);
  }
}