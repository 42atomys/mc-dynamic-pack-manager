package codes.atomys.dynamicpack.mixin;

import codes.atomys.dynamicpack.server.commands.DynamicPackCommand;
import codes.atomys.dynamicpack.utils.Utils;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin adds the dynamicpack command to the dispatcher before the ForgeEventFactory.onCommandRegister call.
 */
@Mixin(value = Commands.class)
public class CommandsMixin {

  @Shadow @Final
  private CommandDispatcher<CommandSourceStack> dispatcher;

  /**
   * Injects code before the call to ForgeEventFactory.onCommandRegister in the Commands constructor.
   *
   * @param selection the command selection.
   * @param context   the command context.
   * @param ci        the callback info.
   */
  @Inject(method = "<init>", at = @At("TAIL"))
  private void injectDynamicPackCommand(final Commands.CommandSelection selection, final CommandBuildContext context, final CallbackInfo ci) {
    Utils.LOGGER.info("[Dynamic Pack Manager] /dynamicpack command added.");
    DynamicPackCommand.register(this.dispatcher);
  }
}
