package codes.atomys.dynamicpack.server.commands;

import codes.atomys.dynamicpack.DynamicPack;
import codes.atomys.dynamicpack.InvalidDynamicPackException;
import codes.atomys.dynamicpack.config.Configuration;
import codes.atomys.dynamicpack.config.ModConfigurationFile;
import codes.atomys.dynamicpack.utils.Utils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

/**
 * Write a doc.
 *
 * @see net.minecraft.server.commands.ServerPackCommand
 */
public final class DynamicPackCommand {

  private DynamicPackCommand() {
  }

  /**
   * Write a doc.
   */
  private static final SuggestionProvider<CommandSourceStack> SUGGEST_PACK = (commandContext, suggestionsBuilder) -> {
    final Collection<DynamicPack> collection = Configuration.packs;
    return SharedSuggestionProvider.suggest(collection.stream().map(DynamicPack::packname), suggestionsBuilder);
  };

  /**
   * Register the command on the given dispatcher.
   *
   * @param dispatcher the CommandDispatcher to register the command on
   */
  public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
    Utils.LOGGER.info("[DynamicPackManager] Command registered");
    dispatcher.register(
      Commands.literal("dynamicpack")
        .requires(source -> source.hasPermission(2))
        .then(Commands.literal("list")
          .executes(ctx -> {
            if (Configuration.packs.isEmpty()) {
              ctx.getSource().sendSuccess(() -> Component.translatable("text.dynamicpackmanager.no_packs", "/dynamicpack add <packname> <url> <required> (message)").withStyle(ChatFormatting.RED), true);
              return 1;
            }

            final MutableComponent message = Component.translatable("text.dynamicpackmanager.available_packs").withStyle(ChatFormatting.GRAY);
            for (final DynamicPack pack : Configuration.packs) {
              final MutableComponent packComponent = Component.literal("\n> ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal(pack.packname()).withStyle(ChatFormatting.GOLD))
                .append("\n")
                .append(Component.literal("| ").withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.translatable("text.dynamicpackmanager.pack.url").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(pack.url()).withStyle(ChatFormatting.GRAY))
                .append("\n")
                .append(Component.literal("| ").withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.translatable("text.dynamicpackmanager.pack.uuid").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(pack.uuid().toString()).withStyle(ChatFormatting.LIGHT_PURPLE));
                
              if (!pack.hash().isEmpty()) {
                packComponent.append("\n")
                  .append(Component.literal("| ").withStyle(ChatFormatting.DARK_GRAY))
                  .append(Component.translatable("text.dynamicpackmanager.pack.hash").withStyle(ChatFormatting.GRAY))
                  .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                  .append(Component.literal(pack.hash()).withStyle(ChatFormatting.GRAY));
              }

              packComponent.append("\n")
                .append(Component.literal("| ").withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.translatable("text.dynamicpackmanager.pack.required").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                .append(Component.translatable("text.dynamicpackmanager.options." + pack.required()).withStyle(pack.required() ? ChatFormatting.GREEN : ChatFormatting.RED));

              if (pack.hasPrompt()) {
                packComponent.append("\n")
                  .append(Component.literal("| ").withStyle(ChatFormatting.DARK_GRAY))
                  .append(Component.translatable("text.dynamicpackmanager.pack.message").withStyle(ChatFormatting.GRAY))
                  .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                  .append(Component.literal(pack.message().get()).withStyle(ChatFormatting.AQUA));
              }

              message.append("\n").append(packComponent);
            }

            message
              .append("\n\n")
              .append(Component.literal(">>> ").withStyle(ChatFormatting.DARK_GRAY))
              .append(Component.translatable("text.dynamicpackmanager.support_us_on_patreon", Component.literal("Patreon").withStyle(style -> style.withUnderlined(true).withColor(ChatFormatting.GOLD))).withStyle(
                style ->
                  style.withColor(ChatFormatting.GRAY)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://patreon.com/42atomys"))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("text.dynamicpackmanager.support_us_on_patreon.tooltip")))
              ));

            ctx.getSource().sendSuccess(() -> message, true);
            return 1;
          })
        )
        .then(
          Commands.literal("add")
          .then(
            Commands.argument("name", StringArgumentType.word())
            .then(
              Commands.argument("url", StringArgumentType.string())
              .then(
                Commands.argument("required", BoolArgumentType.bool())
                .then(
                    Commands.argument("message", StringArgumentType.greedyString())
                    .executes(ctx -> {
                      final DynamicPack pack = DynamicPack.from(
                        ctx.getArgument("name", String.class),
                        ctx.getArgument("url", String.class),
                        ctx.getArgument("required", Boolean.class),
                        Optional.of(ctx.getArgument("message", String.class)),
                        Optional.empty(), // Optional.
                        Optional.empty() // Optional.
                      );

                      return addPack(ctx, pack);
                    })
                )
                .executes(ctx -> {
                  final DynamicPack pack = DynamicPack.from(
                    ctx.getArgument("name", String.class),
                    ctx.getArgument("url", String.class),
                    ctx.getArgument("required", Boolean.class),
                    Optional.empty(), // Optional.
                    Optional.empty(), // Optional.
                    Optional.empty() // Optional.
                  );

                  return addPack(ctx, pack);
                })
              )
            )
          )
        )
        .then(Commands.literal("update")
          .then(Commands.argument("packname", StringArgumentType.word())
            .suggests(SUGGEST_PACK)
            .then(Commands.literal("name")
              .then(Commands.argument("value", StringArgumentType.word())
                .executes(ctx -> updatePack(ctx, "name"))
              )
            )
            .then(Commands.literal("required")
              .then(Commands.argument("value", BoolArgumentType.bool())
                .executes(ctx -> updatePack(ctx, "required"))
              )
            )
            .then(Commands.literal("message")
              .then(Commands.argument("value", StringArgumentType.greedyString())
                .executes(ctx -> updatePack(ctx, "message"))
              )
            )
          )
        )
        .then(Commands.literal("remove")
          .then(Commands.argument("packname", StringArgumentType.word())
            .suggests(SUGGEST_PACK)
            .executes(ctx -> {
              final DynamicPack dynamicpack = getPack(ctx);

              dynamicpack.sendRemovePacketToAll(ctx.getSource().getServer()); // Send a packet to all connections.
              Configuration.packs.remove(dynamicpack); // Remove the pack from the list.
              ModConfigurationFile.saveRunnable.run(); // Save the list.

              ctx.getSource().sendSuccess(() -> Component.translatable("commands.dynamicpackmanager.remove.success", dynamicpack.packname()).withStyle(ChatFormatting.GREEN), true);
              return 1;
            })
          )
        )
        .then(Commands.literal("send")
          .then(Commands.argument("targets", EntityArgument.players())
            .then(Commands.argument("packname", StringArgumentType.word())
              .suggests(SUGGEST_PACK)
              .then(Commands.argument("message", StringArgumentType.greedyString())
                .executes(ctx -> {
                  return sendPack(ctx, ctx.getArgument("message", String.class));
                })
              )
              .executes(ctx -> {
                return sendPack(ctx, null);
              })
            )
          )
        )
        .then(Commands.literal("reloadconfig")
          .executes(ctx -> {
            ModConfigurationFile.load(Utils.getConfigurationFileType());
            ctx.getSource().sendSuccess(() -> Component.translatable("commands.dynamicpackmanager.reloadconfig.success").withStyle(ChatFormatting.GREEN), true);
            return 1;
          })
        )
    );
  }

  private static int addPack(final CommandContext<CommandSourceStack> ctx, final DynamicPack dynamicpack) {
    try {
      dynamicpack.validate();
    } catch (final InvalidDynamicPackException e) {
      ctx.getSource().sendFailure(Component.translatable("commands.dynamicpackmanager.add.failure", dynamicpack.packname()).append(e.getComponent()).withStyle(ChatFormatting.RED));
      return 0;
    }

    Configuration.packs.add(dynamicpack);
    ModConfigurationFile.saveRunnable.run();
    
    ctx.getSource().sendSuccess(() -> Component.translatable("commands.dynamicpackmanager.add.success", dynamicpack.packname()).withStyle(ChatFormatting.GREEN), true);
    return 1;
  }

  private static int updatePack(final CommandContext<CommandSourceStack> ctx, final String field) {
    DynamicPack dynamicpack = getPack(ctx);
    final DynamicPack oldPack = dynamicpack;

    switch (field) {
      case "name":
        dynamicpack = dynamicpack.withName(StringArgumentType.getString(ctx, "value"));
        break;
      case "required":
        dynamicpack = dynamicpack.withRequired(BoolArgumentType.getBool(ctx, "value"));
        break;
      case "message":
        dynamicpack = dynamicpack.withMessage(Optional.of(StringArgumentType.getString(ctx, "value")));
        break;
      default:
        ctx.getSource().sendFailure(Component.translatable("commands.dynamicpackmanager.update.failure", dynamicpack.packname(), field)
        .append(Component.translatable("commands.dynamicpackmanager.update.error.unknown_field", field))
        .withStyle(ChatFormatting.RED));
        return 0;
    }

    try {
      dynamicpack.validate();
    } catch (final InvalidDynamicPackException e) {
      ctx.getSource().sendFailure(Component.translatable("commands.dynamicpackmanager.update.failure", dynamicpack.packname(), field).append(e.getComponent()).withStyle(ChatFormatting.RED));
      return 0;
    }

    final DynamicPack newPack = dynamicpack;
    Configuration.packs.remove(oldPack);
    Configuration.packs.add(newPack);
    ModConfigurationFile.saveRunnable.run();
    
    ctx.getSource().sendSuccess(() -> Component.translatable("commands.dynamicpackmanager.update.success", newPack.packname(), field).withStyle(ChatFormatting.GREEN), true);
    return 1;
  }

  private static int sendPack(final CommandContext<CommandSourceStack> ctx, final String customMessage) throws CommandSyntaxException {
    final Collection<ServerPlayer> targets = EntityArgument.getPlayers(ctx, "targets");
    final DynamicPack dynamicpack = getPack(ctx);

    // Send drop pack packet to targets to ensure no deprecated data are present
    dynamicpack.sendRemovePacketToTargets(ctx.getSource().getServer(), targets);

    // Send add pack packet to targets
    dynamicpack.sendAddPacketToTargets(ctx.getSource().getServer(), targets, customMessage);
    ctx.getSource().sendSuccess(() -> Component.translatable("commands.dynamicpackmanager.send.success", dynamicpack.packname(), targets.size()).withStyle(ChatFormatting.GREEN), true);
    return 1;
  }

  /**
   * Retrieve a dynamic pack by its name from the given context.
   * <p>
   * If the pack does not exist, send a failure message to the source.
   * </p>
   *
   * @param ctx the context to retrieve the pack from
   * @return the dynamic pack with the given name, or 0 if no pack with that name exists
   */
  private static DynamicPack getPack(final CommandContext<CommandSourceStack> ctx) {
    final String packname = ctx.getArgument("packname", String.class);
    final DynamicPack dynamicpack = Configuration.findPack(packname);
    if (dynamicpack == null) {
      ctx.getSource().sendFailure(Component.translatable("commands.dynamicpackmanager.errors.unknown_pack", packname)
      .withStyle(ChatFormatting.RED));
      return null;
    }

    return dynamicpack;
  }
}
