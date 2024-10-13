package codes.atomys.dynamicpack.mixin;

import codes.atomys.dynamicpack.server.commands.DynamicPackCommand;
import codes.atomys.dynamicpack.utils.Utils;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPacketListener.class)
abstract class ClientPacketListenerMixin {
  @Shadow
  private CommandDispatcher<SharedSuggestionProvider> commands = new CommandDispatcher<>();

  @Shadow @Final
  private ClientSuggestionProvider suggestionsProvider;

  @Shadow @Final
  private FeatureFlagSet enabledFeatures;

  @Shadow @Final
  private RegistryAccess.Frozen registryAccess;

  @Inject(method = "handleLogin", at = @At("RETURN"))
  private void handleLogin(final ClientboundLoginPacket packet, final CallbackInfo info) {
    final CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();

    DynamicPackCommand.register(dispatcher);
  }

  // @SuppressWarnings({ "unchecked", "rawtypes" })
  @Inject(method = "handleCommands", at = @At("RETURN"))
  private void handleCommands(final ClientboundCommandsPacket packet, final CallbackInfo info) {
    // Add the commands to the vanilla dispatcher for completion.
    // It's done here because both the server and the client commands have
    // to be in the same dispatcher and completion results.
    // ClientCommandInternals.addCommands((CommandDispatcher) commandDispatcher,
    //     (FabricClientCommandSource) commandSource);
    Utils.LOGGER.info("[DynamicPackManager] handle commands: " + packet.getRoot(CommandBuildContext.simple(this.registryAccess, this.enabledFeatures)));
    // this.commands = new CommandDispatcher<>(packet.getRoot(CommandBuildContext.simple(this.registryAccess, this.enabledFeatures)));
  }

  @Inject(method = "sendUnsignedCommand", at = @At("HEAD"), cancellable = true)
  private void sendUnsignedCommand(final String command, final CallbackInfoReturnable<Boolean> cir) {
    Utils.LOGGER.info("[DynamicPackManager] send unsigned command: " + command);
    // if (ClientCommandInternals.executeCommand(command)) {
    //   cir.setReturnValue(true);
    // }
    // if (!SignableCommand.hasSignableArguments(this.parseCommand(command))) {
    //   this.send(new ServerboundChatCommandPacket(command));
    //   return true;
    // } else {
    //   return false;
    // }
  }

  @Inject(method = "sendCommand", at = @At("HEAD"), cancellable = true)
  private void sendCommand(final String command, final CallbackInfo info) {
    Utils.LOGGER.info("[DynamicPackManager] send command: " + command);
    // if (ClientCommandInternals.executeCommand(command)) {
    //   info.cancel();
    // }
    // SignableCommand<SharedSuggestionProvider> signableCommand = SignableCommand.of(this.parseCommand(command));
    // if (signableCommand.arguments().isEmpty()) {
    //   this.send(new ServerboundChatCommandPacket(command));
    // } else {
    //   Instant instant = Instant.now();
    //   long l = Crypt.SaltSupplier.getLong();
    //   LastSeenMessagesTracker.Update update = this.lastSeenMessages.generateAndApplyUpdate();
    //   ArgumentSignatures argumentSignatures = ArgumentSignatures.signCommand(signableCommand, string -> {
    //     SignedMessageBody signedMessageBody = new SignedMessageBody(string, instant, l, update.lastSeen());
    //     return this.signedMessageEncoder.pack(signedMessageBody);
    //   });
    //   this.send(new ServerboundChatCommandSignedPacket(command, instant, l, argumentSignatures, update.update()));
    // }
  }
}
