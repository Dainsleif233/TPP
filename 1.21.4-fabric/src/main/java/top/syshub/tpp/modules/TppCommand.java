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

import java.util.Set;

import static net.minecraft.command.CommandSource.suggestMatching;
import static top.syshub.tpp.TPP.config;

public class TppCommand {
    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("tpp")
                .then(CommandManager.argument("target", EntityArgumentType.player())
                        .suggests((context, builder) -> {
                            ServerPlayerEntity executor = context.getSource().getPlayer();
                            if (!config.tpp.enabled || executor == null ) return Suggestions.empty();
                            if (config.tpp.target.equals("teammates") && executor.getScoreboardTeam() == null) return Suggestions.empty();

                            return suggestMatching(
                                    context.getSource().getServer().getPlayerManager().getPlayerList().stream()
                                            .filter(p -> p != executor)
                                            .filter(p -> switch (config.tpp.target) {
                                                case "teammates" -> p.getScoreboardTeam() == executor.getScoreboardTeam();
                                                case "allplayers" -> true;
                                                default -> false;
                                            }).map(p -> p.getName().getString()),
                                    builder
                            );
                        })
                        .executes(context -> executeTpp(
                                context.getSource(),
                                EntityArgumentType.getPlayer(context, "target")
                        ))
                )
        );
    }

    private static int executeTpp(@NotNull ServerCommandSource source, ServerPlayerEntity target) throws CommandSyntaxException {
        checkCommandEnabled();
        ServerPlayerEntity sourcePlayer = source.getPlayerOrThrow();
        checkCooldown(sourcePlayer);
        checkSameTeam(sourcePlayer, target);

        sourcePlayer.teleport(
                target.getServerWorld(),
                target.getX(), target.getY(), target.getZ(),
                Set.of(),
                target.getYaw(), target.getPitch(),
                true
        );
        TppCooldownManager.updateLastUsed(sourcePlayer);
        return 0;
    }

    private static void checkCommandEnabled() throws CommandSyntaxException {
        if (!config.tpp.enabled) {
            throw new SimpleCommandExceptionType(Text.literal("命令未启用")).create();
        }
    }

    private static void checkCooldown(@NotNull ServerPlayerEntity source) throws CommandSyntaxException {
        if (config.tpp.cooldown > 0) {
            long remaining = TppCooldownManager.getCooldownRemaining(source);
            if (remaining > 0) {
                throw new SimpleCommandExceptionType(Text.literal("传送冷却还剩 " + remaining / 1000 + " 秒")).create();
            }
        }
    }

    private static void checkSameTeam(@NotNull ServerPlayerEntity source, ServerPlayerEntity target) throws CommandSyntaxException {
        if (config.tpp.target.equals("allplayers")) return;
        if (source.getScoreboardTeam() == null) {
            throw new SimpleCommandExceptionType(Text.literal("你不在任何队伍中")).create();
        }
        if (source.getScoreboardTeam() != target.getScoreboardTeam()) {
            throw new SimpleCommandExceptionType(Text.literal("目标玩家不在你的队伍中")).create();
        }
    }
}