import java.net.URL;
public class SelfSignSSL {
  public static void main( String[] args ) throws Exception {
    try {
      new URL( args[0] ).openConnection().getInputStream();
      System.out.println( "Succeeded." );
    }
    catch( javax.net.ssl.SSLHandshakeException e ) {
      System.out.println( "SSL exception." );
    }
  }
}