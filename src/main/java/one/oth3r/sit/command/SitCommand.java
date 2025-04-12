package one.oth3r.sit.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import one.oth3r.sit.utl.Data;
import one.oth3r.sit.utl.Logic;
import one.oth3r.sit.utl.Utl;

import me.lucko.fabric.api.permissions.v0.Permissions;

import java.util.concurrent.CompletableFuture;

public class SitCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("sit")
                .requires(Permissions.require("sit.use", 0))
                        .executes((context2) -> sitUse(context2.getSource()))
                // .executes((context2) -> command(context2.getSource(), context2.getInput()))
                // .then(CommandManager.argument("args", StringArgumentType.string())
                //         .requires((commandSource) -> commandSource.hasPermissionLevel(2))
                //         .suggests(SitCommand::getSuggestions)
                //         .executes((context2) -> command(context2.getSource(), context2.getInput()))));
        );
    }

    public static CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        builder.suggest("reload");
        builder.suggest("purgeChairEntities");
        return builder.buildFuture();
    }

    // sit command
    private static int sitUse(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();

        if (player != null) {
            if (!Logic.sitLooking(player)) {
                BlockPos pos = player.getBlockPos();

                if (!(player.getY() - ((int) player.getY()) > 0.00)) {
                    pos = pos.add(0, -1, 0);
                }

                // if already sitting, ignore
                if (Data.getSitEntity(player) != null) return 1;

                // try to make the player sit
                Logic.sit(player, pos, null);
            }
        }
        return 1;
    }

    // reload command
    private static int sitReload(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();

        if (player != null) {
            Logic.reload();
            player.sendMessage(Utl.messageTag().append(Utl.lang("sit!.chat.reloaded").formatted(Formatting.GREEN)));
        } else {
            Logic.reload();
            Data.LOGGER.info(Utl.lang("sit!.chat.reloaded").getString());
        }
        return 1;
    }

    // purge command
    private static int sitPurge(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();

        if (player != null) {
            Utl.Entity.purge(player,true);
        }
        return 1;
    }
}
