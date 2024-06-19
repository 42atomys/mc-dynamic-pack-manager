
package codes.atomys.resourcepackrefresher;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.net.URLConnection;
import java.security.MessageDigest;

public abstract class ChecksumHelper {
  public static String getMD5Checksum(String url) {
    URLConnection connection;
    try {
      connection = new URI(url).toURL().openConnection();
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
    } catch (FileNotFoundException e) {
      System.err.println("[" + ResourcePackRefresher.MOD_ID + "]: The resourcepack url: "+ url +" is not a valid file. Please check your configuration.");
    } catch (Exception e) {
      e.printStackTrace();
    }

    return "";
  }
}
