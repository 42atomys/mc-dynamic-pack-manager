package codes.atomys.dynamicpack.mixin;

import codes.atomys.dynamicpack.config.Configuration;
import java.util.List;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin that adds code to the PlayerList class to send the required dynamic
 * packs to a player after they have logged in.
 */
@Mixin(PlayerList.class)
public class PlayerListMixin {

  @Shadow @Final
  private MinecraftServer server;

  /**
   * Send the required dynamic packs to a player after they have logged in.
   * <p>
   * This is done by injecting code into the end of the PlayerList.placeNewPlayer method.
   * The required dynamic packs are sent to the player.
   * </p>
   *
   * @param connection the connection to the player
   * @param player the player to send the required dynamic packs to
   * @param cookie the cookie to use to send the required dynamic packs
   * @param ci the callback info
   */
  @Inject(
        method = "placeNewPlayer",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;teleport(DDDFF)V",
            shift = At.Shift.AFTER
        )
    )
  private void injectDynamicPackAfterTeleport(final Connection connection, final ServerPlayer player,
      final CommonListenerCookie cookie, final CallbackInfo ci) {

    Configuration.packs.forEach(dynamicPack -> {
      if (dynamicPack.required()) {
        dynamicPack.sendAddPacketToTargets(this.server, List.of(player), null);
      }
    });
  }
}
