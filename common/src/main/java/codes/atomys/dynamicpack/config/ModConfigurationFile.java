package codes.atomys.dynamicpack.config;

import codes.atomys.dynamicpack.DynamicPack;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * The ModConfigurationFile class is a utility class responsible for saving and
 * loading
 * configuration settings for the "dynamic_pack_manager" mod. It supports two
 * file types: JSON and TOML.
 *
 * @see Configuration
 */
public final class ModConfigurationFile {

  /**
   * Private constructor to prevent instantiation of the utility class.
   * Throws {@link UnsupportedOperationException} if called.
   */
  private ModConfigurationFile() {
    throw new UnsupportedOperationException("Utility class");
  }

  private static FileType storedFileType;

  /**
   * A Runnable that saves the current configuration settings to a file.
   * The file format is determined by the storedFileType (either JSON or TOML).
   * If the file does not exist, the necessary directories are created.
   * The configuration is saved concurrently and with autosave enabled.
   */
  public final static Runnable saveRunnable = () -> {
    final Path path = Paths.get(
        storedFileType == FileType.JSON ? "config/dynamic_pack_manager.json" : "config/dynamic_pack_manager.toml");

    final File file = path.toFile();
    if (!file.exists())
      file.getParentFile().mkdirs();

    final FileConfig config = FileConfig.builder(path).concurrent().autosave().build();

    final Config general = Config.inMemory();
    // general.set("auto_revoke", Configuration.autoRevoke);

    final List<Config> packConfigs = new ArrayList<>();
    for (final DynamicPack pack : Configuration.packs) {
      final Config packConfig = Config.inMemory();
      packConfig.set("packname", pack.packname());
      packConfig.set("url", pack.url());
      packConfig.set("required", pack.required());
      packConfig.set("message", pack.message().orElse(null));
      packConfig.set("uuid", pack.uuid().toString());
      packConfig.set("hash", pack.hash());
      packConfigs.add(packConfig);
    }
    config.set("packs", packConfigs);

    config.set("general", general);

    config.close();
  };

  /**
   * Load the configuration from the file of the given filetype.
   * If the file does not exist, do nothing.
   * If the file contains invalid data, the invalid data is ignored.
   *
   * @param filetype the type of the file to load from.
   */
  public static void load(final FileType filetype) {
    storedFileType = filetype;
    final File file = new File(
        storedFileType == FileType.JSON ? "config/dynamic_pack_manager.json" : "config/dynamic_pack_manager.toml");

    if (!file.exists()) {
      // Create default pack
      Configuration.packs = List.of(DynamicPack.defaultpack);
      saveRunnable.run();
      return;
    }

    final FileConfig config = FileConfig.builder(file).concurrent().autosave().build();

    config.load();
    final Config general = config.getOrElse("general", () -> null);
    final List<Config> packConfigs = config.getOrElse("packs", () -> null);

    if (general == null || packConfigs == null) {
      config.close();
      return;
    }

    // Configuration.autoRevoke = general.getOrElse("auto_revoke", true);

    Configuration.packs = new ArrayList<>();
    for (final Config packConfig : packConfigs) {
      final String packname = packConfig.get("packname");
      final String url = packConfig.get("url");
      final boolean required = packConfig.get("required");
      final Optional<String> message = packConfig.getOptional("message");
      final String uuidString = packConfig.get("uuid");
      final UUID uuid = uuidString != null ? UUID.fromString(uuidString)
          : UUID.nameUUIDFromBytes(url.getBytes(StandardCharsets.UTF_8));
      final String hash = packConfig.get("hash");

      final DynamicPack pack = new DynamicPack(packname, url, required, message, uuid, hash);
      Configuration.packs.add(pack);
    }

    config.close();
  }

  /**
   * Enum representing the types of configuration files supported by the
   * application.
   * <p>
   * This enum defines the following file types:
   * </p>
   * <ul>
   * <li>{@link #JSON} - Represents a JSON configuration file.</li>
   * <li>{@link #TOML} - Represents a TOML configuration file.</li>
   * </ul>
   */
  public enum FileType {
    JSON,
    TOML
  }

}
