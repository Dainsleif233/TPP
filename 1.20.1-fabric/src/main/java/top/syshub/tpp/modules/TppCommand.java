package top.syshub.tpp.modules;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.command.argument.*;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.command.CommandSource.suggestMatching;
import static top.syshub.tpp.TPP.config;

public class TppCommand {
    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("tpp")
                .then(CommandManager.argument("destination", EntityArgumentType.player())
                        .suggests((context, builder) -> {
                            ServerPlayerEntity executor = context.getSource().getPlayer();
                            if (!config.tpp.enabled || executor == null || executor.getScoreboardTeam() == null) return Suggestions.empty();

                            return suggestMatching(
                                    context.getSource().getServer().getPlayerManager().getPlayerList()
                                            .stream()
                                            .filter(p -> p != executor)
                                            .filter(p -> p.getScoreboardTeam() == executor.getScoreboardTeam())
                                            .map(p -> p.getName().getString()),
                                    builder
                            );
                        })
                        .executes(context -> executeTpp(
                                context.getSource(),
                                EntityArgumentType.getPlayer(context, "destination")
                        ))
                )
        );
    }

    private static int executeTpp(@NotNull ServerCommandSource source, ServerPlayerEntity destination) throws CommandSyntaxException {
        checkCommandEnabled();
        ServerPlayerEntity sourcePlayer = source.getPlayerOrThrow();
        checkSameTeam(sourcePlayer, destination);

        sourcePlayer.teleport(
                destination.getServerWorld(),
                destination.getX(), destination.getY(), destination.getZ(),
                destination.getYaw(), destination.getPitch()
        );
        return 0;
    }

    private static void checkCommandEnabled() throws CommandSyntaxException {
        if (!config.tpp.enabled) {
            throw new SimpleCommandExceptionType(Text.literal("命令未启用")).create();
        }
    }

    private static void checkSameTeam(@NotNull ServerPlayerEntity source, ServerPlayerEntity target) throws CommandSyntaxException {
        if (source.getScoreboardTeam() == null) {
            throw new SimpleCommandExceptionType(Text.literal("你不在任何队伍中")).create();
        }
        if (source.getScoreboardTeam() != target.getScoreboardTeam()) {
            throw new SimpleCommandExceptionType(Text.literal("目标玩家不在你的队伍中")).create();
        }
    }
}