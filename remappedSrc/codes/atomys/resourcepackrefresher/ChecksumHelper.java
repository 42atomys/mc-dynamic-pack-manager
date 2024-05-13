
package codes.atomys.resourcepackrefresher;

import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;

public abstract class ChecksumHelper {
  public static String getMD5Checksum(String url) {
    URLConnection connection;
    try {
      connection = new URL(url).openConnection();
      InputStream inputStream = connection.getInputStream();
      MessageDigest digest = MessageDigest.getInstance("MD5");
      byte[] buffer = new byte[8192];
      int read = 0;
      while ((read = inputStream.read(buffer)) > 0) {
        digest.update(buffer, 0, read);
      }
      byte[] md5sum = digest.digest();
      BigInteger bigInt = new BigInteger(1, md5sum);
      String output = bigInt.toString(16);
      return output;
    } catch (Exception e) {
      e.printStackTrace();
    }

    return "";
  }
}
