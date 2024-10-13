package codes.atomys.dynamicpack;

import codes.atomys.dynamicpack.config.ModConfigurationFile;
import codes.atomys.dynamicpack.utils.Utils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The main class for the "DynamicPackManager" mod for Fabric.
 *
 * <p>
 * This class implements the {@link ModInitializer} interface, which is
 * a special interface for Fabric mods that want to run code on the client side
 * when the mod is initialized.
 * </p>
 *
 * <p>
 * In this class, the {@link #onInitialize()} method is overridden to
 * print a message to the console, print the version of the mod, and load the
 * configuration file with the {@link ModConfigurationFile.FileType#JSON} type.
 * </p>
 *
 * @see ModInitializer
 */
public class DynamicPackManagerFabric implements ModInitializer {

  public final Logger logger = LogManager.getLogger("dynamic_pack_manager");

  /**
   * This method is called once the mod is initialized on the server side.
   * <p>
   * It logs messages to the console, prints the version of the mod, and loads
   * the configuration file with the {@link ModConfigurationFile.FileType#JSON}
   * file type.
   * </p>
   */
  @Override
  public void onInitialize() {
    this.logger.info("[DynamicPackManager] Loading...");

    Utils.modVersion(this.modVersion());
    Utils.setConfigurationFileType(ModConfigurationFile.FileType.JSON);

    // Setup config with JSON file type
    ModConfigurationFile.load(Utils.getConfigurationFileType());

    this.logger.info("[DynamicPackManager] All done!");
  }

  /**
   * Returns the version of the mod as a string.
   *
   * <p>
   * The method uses the {@link FabricLoader} to get the mod container for the
   * "dynamic_pack_manager" mod, and then retrieves the mod's metadata and
   * version.
   * </p>
   *
   * <p>
   * The method returns the friendly string representation of the version.
   * </p>
   *
   * @return the version of the mod as a string
   */
  private String modVersion() {
    return FabricLoader.getInstance().getModContainer("dynamic_pack_manager").orElseThrow(NullPointerException::new)
        .getMetadata().getVersion().getFriendlyString();
  }
}
