package codes.atomys.dynamicpack.config;

import codes.atomys.dynamicpack.DynamicPack;
import java.util.ArrayList;
import java.util.List;

/**
 * General Options for the config.
 */
public final class Configuration {

  /**
   * Private constructor to prevent instantiation of the utility class.
   * Throws {@link UnsupportedOperationException} if called.
   */
  private Configuration() {
    throw new UnsupportedOperationException("Utility class");
  }

  public static List<DynamicPack> packs = new ArrayList<>(); // List of packs to be loaded

  /**
   * Finds a dynamic pack by name.
   *
   * @param name the name of the pack to find
   * @return the pack with the given name, or null if no pack with that name exists
   */
  public static DynamicPack findPack(final String name) {
    return Configuration.packs.stream()
      .filter(pack -> pack.packname().equals(name))
      .findFirst()
      .orElse(null);
  }
}
