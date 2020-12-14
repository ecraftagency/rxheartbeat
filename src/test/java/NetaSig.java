import javassist.bytecode.ByteArray;
import org.apache.commons.codec.binary.Hex;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class NetaSig {
  public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException {
    int appId       = 4;
    String appKey   = "ajufjV4FJ3rQGK8y";
    int timeStamp   = (int)(System.currentTimeMillis()/1000);
    int nonce       = (int)(Math.floor(Math.random() * 89999) + 10000);
    String content  = String.format("application_id=%d&auth_key=%s&nonce=%d&timestamp=%d", appId, appKey, nonce, timeStamp);
    String sig      = generateSignature(content, appKey);
    System.out.println(nonce);
    System.out.println(timeStamp);
    System.out.println(sig);

    //tc-token: b26d4a1ba60a79dbf251c14ab4f84535e3d846d7

    /*
    {
    "application_id": 4,
    "created_at": "2020-12-12 06:10:24.87327202 +0000 UTC",
    "nonce": 40456,
    "token": "b26d4a1ba60a79dbf251c14ab4f84535e3d846d7",
    "updated_at": "2020-12-12 06:10:24.87327202 +0000 UTC"
}
     */
  }

  public static String generateSignature(String content, String key) throws NoSuchAlgorithmException, InvalidKeyException {
    Mac mac = Mac.getInstance("HmacSHA1");
    byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
    SecretKeySpec signingKey = new SecretKeySpec(keyBytes, mac.getAlgorithm());
    mac.init(signingKey);
    byte[] rawMac = mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
    byte[] hexBytes = new Hex().encode(rawMac);
    return new String(hexBytes, StandardCharsets.UTF_8);
  }
}
