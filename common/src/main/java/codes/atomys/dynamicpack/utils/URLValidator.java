package codes.atomys.dynamicpack.utils;

import codes.atomys.dynamicpack.InvalidDynamicPackException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import net.minecraft.network.chat.Component;

/**
 * Utility class for validating URLs.
 *
 * <p>
 * This class provides a simple way to validate that a given URL is well-formed
 * and points to an accessible ZIP file. It first checks if the URL is
 * well-formed, then sends a HEAD request to the URL to check the HTTP response
 * code, and finally checks the content type of the response to ensure it is a
 * ZIP file.
 * </p>
 *
 * <p>
 * It is used to validate the URLs of resource packs.
 * </p>
 */
public final class URLValidator {

  /**
   * Exception thrown when a resource pack URL is invalid.
   */
  public static class InvalidResourcePackURLException extends InvalidDynamicPackException {
    /**
     * Constructs a InvalidResourcePackURLException.of with the specified detail message.
     *
     * @param component the detail message.
     */
    public InvalidResourcePackURLException(final Component component) {
      super(component);
    }

  }

  // Private constructor to prevent instantiation
  private URLValidator() {
    throw new UnsupportedOperationException("Utility class");
  }

  /**
   * Validates a URL by checking:
   * - The URL is well-formed.
   * - The HTTP response code is in the 2xx or 3xx range.
   * - The content is a ZIP file.
   *
   * @param urlString The URL to validate.
   * @return true if the URL is valid and points to an accessible ZIP file, false otherwise.
   */
  public static boolean isResourcePackURLValid(final String urlString) throws InvalidResourcePackURLException {
    // Check if the URL is well-formed
    if (!isValidURLFormat(urlString)) {
      throw InvalidResourcePackURLException.of("commands.dynamicpackmanager.add.error.malformed_url");
    }

    try {
      final URL url = URI.create(urlString).toURL();

      // Open the HTTP connection
      final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("HEAD"); // Use HEAD method to minimize data transfer
      connection.setInstanceFollowRedirects(true); // Follow redirects if necessary
      connection.setConnectTimeout(10000); // Set timeout to 10 seconds
      connection.setReadTimeout(10000); // Set timeout to 10 seconds
      connection.connect();

      // Get the HTTP response code
      final int responseCode = connection.getResponseCode();

      // Check if the response code is in the 2xx or 3xx range
      if (responseCode < 200 || responseCode >= 400) {
        throw InvalidResourcePackURLException.of("commands.dynamicpackmanager.add.error.invalid_response_code");
      }
      // Check the content type
      final String contentType = connection.getContentType();

      // Verify if the content type corresponds to a ZIP file
      if (!isZipContentType(contentType)) {
        throw InvalidResourcePackURLException.of("commands.dynamicpackmanager.add.error.not_a_zip_file");
      }

      return true;
    } catch (final IOException e) {
      e.printStackTrace();
      // Handle exceptions related to the HTTP connection
      throw InvalidResourcePackURLException.of("commands.dynamicpackmanager.add.error.unknown_error");
    }
  }

  /**
   * Checks if the URL format is valid.
   *
   * @param urlString The URL to check.
   * @return true if the format is valid, false otherwise.
   */
  private static boolean isValidURLFormat(final String urlString) {
    try {
      final URI uri = URI.create(urlString);
      uri.toURL(); // Ensures the URI can be converted to a URL

      // Check if the URL is absolute
      if (!uri.isAbsolute()) {
        return false;
      }

      return true;
    } catch (final Exception e) {
      return false;
    }
  }

  /**
   * Checks if the content type corresponds to a ZIP file.
   *
   * @param contentType The content type returned by the server.
   * @return true if it's a ZIP file, false otherwise.
   */
  private static boolean isZipContentType(final String contentType) {
    if (contentType == null) {
      return false;
    }

    // List of MIME types for ZIP files
    final String[] zipMimeTypes = {
      "application/zip",
      "application/x-zip-compressed",
      "multipart/x-zip",
      "application/octet-stream" // Common for binary files
    };

    for (final String mimeType : zipMimeTypes) {
      if (contentType.equalsIgnoreCase(mimeType)) {
        return true;
      }
    }

    return false;
  }
}
