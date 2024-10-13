package codes.atomys.dynamicpack.utils;

import codes.atomys.dynamicpack.config.ModConfigurationFile;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for common functions used in DynamicPackManager.
 */
public final class Utils {

  // Private constructor to prevent instantiation
  private Utils() {
    throw new UnsupportedOperationException("Utility class");
  }

  public static final Style SUCCESS_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(5635925));
  public static final Style ERROR_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(16733525));
  public static final Logger LOGGER = LoggerFactory.getLogger("DynamicPackManager");

  private static String modVersion;
  private static ModConfigurationFile.FileType configurationFileType;

  /**
   * Gets the type of the configuration file currently in use.
   *
   * @return the type of the configuration file
   */
  public static ModConfigurationFile.FileType getConfigurationFileType() {
    return configurationFileType;
  }

  /**
   * Sets the configuration file type.
   * <p>
   * This value is used when loading and saving the configuration file.
   * </p>
   *
   * @param configurationFileType the type of the configuration file to use
   */
  public static void setConfigurationFileType(final ModConfigurationFile.FileType configurationFileType) {
    Utils.configurationFileType = configurationFileType;
  }

  /**
   * Gets the mod version.
   *
   * @return the mod version
   */
  public static String modVersion() {
    return modVersion;
  }

  /**
   * Sets the mod name.
   *
   * @param version the mod version
   */
  public static void modVersion(final String version) {
    modVersion = version;
  }

}
