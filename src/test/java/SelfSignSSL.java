import javax.net.ssl.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Properties;

public class SelfSignSSL {
  public static void main( String[] args ) throws Exception {
    final Properties props = System.getProperties();
    props.setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());
    props.setProperty("com.sun.jndi.ldap.object.disableEndpointIdentification", Boolean.TRUE.toString());

    try {
      URL url = new URL("https://localhost:8080/loaderio-f8c2671f6ccbeec4f3a09a972475189c/");
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestProperty("Content-Type", "application/json");
      con.setRequestMethod("GET");
      System.out.println( con.getResponseCode() );
    }
    catch( javax.net.ssl.SSLHandshakeException e ) {
      System.out.println( e.getMessage() );
    }
  }

  static {
    disableSslVerification();
  }

  private static void disableSslVerification() {
    try {
      TrustManager[] trustAllCerts = new TrustManager[] {
        new X509TrustManager() {
          public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
          public void checkClientTrusted(X509Certificate[] certs, String authType) { }
          public void checkServerTrusted(X509Certificate[] certs, String authType) { }
        }
      };

      // Install the all-trusting trust manager
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, trustAllCerts, new java.security.SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

      // Create all-trusting host name verifier
      HostnameVerifier allHostsValid = (hostname, session) -> true;

      // Install the all-trusting host verifier
      HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    } catch (NoSuchAlgorithmException | KeyManagementException e) {
      e.printStackTrace();
    }
  }
}