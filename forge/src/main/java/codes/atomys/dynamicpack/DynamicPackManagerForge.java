package codes.atomys.dynamicpack;

import codes.atomys.dynamicpack.config.ModConfigurationFile;
import codes.atomys.dynamicpack.utils.Utils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.minecraftforge.fml.IExtensionPoint.DisplayTest.IGNORESERVERONLY;

/**
 * DynamicPackManager Forge Mod.
 */
// The value here should match an entry in the META-INF/mods.toml file
@Mod("dynamic_pack_manager")
public class DynamicPackManagerForge {

  // Directly references a log4j logger.
  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * Instantiates a new DynamicPackManager mod for Forge.
   */
  public DynamicPackManagerForge() {
    LOGGER.info("[DynamicPackManager] Starting...");

    if (FMLEnvironment.dist != Dist.DEDICATED_SERVER) {
      LOGGER.warn("[DynamicPackManager] Only supported on dedicated server!");
    } else {
      DistExecutor.safeRunWhenOn(Dist.DEDICATED_SERVER, () -> ServerSetup::setup);
    }
  }

  private static final class ServerSetup {
    private static void setup() {
      LOGGER.info("[DynamicPackManager] Loading...");

      Utils.modVersion(modVersion());
      Utils.setConfigurationFileType(ModConfigurationFile.FileType.TOML);

      // Setup config with TOML file type
      ModConfigurationFile.load(Utils.getConfigurationFileType());

      LOGGER.info("[DynamicPackManager] All done!");
      
      // Register server and game events that we are interested in.
      MinecraftForge.EVENT_BUS.register(DynamicPackManagerForge.class);
      // Make sure the mod being absent on the other network side does not cause the
      // client to display the server
      // as incompatible.
      ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
          () -> new IExtensionPoint.DisplayTest(() -> IGNORESERVERONLY, (a, b) -> true));
    }

    private static String modVersion() {
      return ModList.get().getModContainerById("dynamic_pack_manager").orElseThrow(NullPointerException::new)
          .getModInfo().getVersion().toString();
    }
  }
}
