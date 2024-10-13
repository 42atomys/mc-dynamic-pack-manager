package codes.atomys.dynamicpack;

import codes.atomys.dynamicpack.config.ModConfigurationFile;
import codes.atomys.dynamicpack.utils.Utils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * DynamicPackManager NeoForge Mod.
 */
// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod("dynamic_pack_manager")
public class DynamicPackManagerNeoForge {

  // Directly references a log4j logger.
  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * Instantiates a new DynamicPackManager mod for NeoForge.
   *
   * @param eventBus the event bus
   */
  public DynamicPackManagerNeoForge(final IEventBus eventBus) {
    LOGGER.info("[DynamicPackManager] Starting...");

    if (FMLEnvironment.dist != Dist.DEDICATED_SERVER) {
      LOGGER.warn("[DynamicPackManager] Only supported on dedicated server!");
    } else {
      ServerSetup.setup(eventBus);
    }
  }

  private static final class ServerSetup {
    private static void setup(final IEventBus eventBus) {
      LOGGER.info("[DynamicPackManager] Loading...");

      Utils.modVersion(modVersion());
      Utils.setConfigurationFileType(ModConfigurationFile.FileType.TOML);

      // Setup config with TOML file type
      ModConfigurationFile.load(Utils.getConfigurationFileType());

      LOGGER.info("[DynamicPackManager] All done!");
    }

    private static String modVersion() {
      return ModList.get().getModContainerById("dynamic_pack_manager").orElseThrow(NullPointerException::new)
          .getModInfo().getVersion().toString();
    }
  }
}
