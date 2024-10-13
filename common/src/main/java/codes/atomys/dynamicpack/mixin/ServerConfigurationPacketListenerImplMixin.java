package codes.atomys.dynamicpack.mixin;

// import codes.atomys.dynamicpack.config.Configuration;
import java.util.Queue;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
// import net.minecraft.server.network.config.ServerResourcePackConfigurationTask;
// import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin adds a hook to the ConfigurationTask queue of the ServerConfigurationPacketListenerImpl,
 * allowing us to run the DynamicPackCommand on the server when the pack is changed.
 */
@Mixin(ServerConfigurationPacketListenerImpl.class)
public class ServerConfigurationPacketListenerImplMixin {

  // Shadowing a private field from the target class
  @Shadow
  private Queue<ConfigurationTask> configurationTasks;

  @Inject(method = "addOptionalTasks", at = @At("TAIL"))
  private void injectAddOptionalTasks(final CallbackInfo ci) {
    // Configuration.packs.forEach(dynamicPack -> {
    //   if (dynamicPack.required()) {
    //     this.configurationTasks.add(new ServerResourcePackConfigurationTask(dynamicPack.getServerResourcePackInfo()));
    //   }
    // });
  }
}
