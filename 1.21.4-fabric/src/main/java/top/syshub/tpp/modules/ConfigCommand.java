package top.syshub.tpp.modules;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import top.syshub.tpp.TPP;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static net.minecraft.command.CommandSource.suggestMatching;

public class ConfigCommand {

    private static final Map<String, List<String>> MODULES = Map.of(
            "tpp", List.of("enabled")
    );

    private static final Map<String, List<String>> CONFIGS = Map.of(
            "enabled", List.of("true", "false")
    );

    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("tppconfig")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("save")
                        .executes(context -> executeSaveCommand())
                )
                .then(CommandManager.argument("module", StringArgumentType.string())
                        .suggests((context, builder) -> suggestMatching(
                                MODULES.keySet().stream(),
                                builder
                        ))
                        .then(CommandManager.argument("config", StringArgumentType.string())
                                .suggests((context, builder) -> {
                                    String module = context.getArgument("module", String.class);
                                    return suggestMatching(
                                            MODULES.getOrDefault(module, Collections.emptyList()),
                                            builder
                                    );
                                })
                                .then(CommandManager.argument("value", StringArgumentType.string())
                                        .suggests((context, builder) -> {
                                            String config = context.getArgument("config", String.class);
                                            return suggestMatching(
                                                    CONFIGS.getOrDefault(config, Collections.emptyList()),
                                                    builder
                                            );
                                        })
                                        .executes(context -> executeConfigCommand(
                                                context.getArgument("module", String.class),
                                                context.getArgument("config", String.class),
                                                context.getArgument("value", String.class)
                                        ))
                                )
                        )
                )
        );
    }

    private static int executeConfigCommand(String module, String config, String value) {
        try {
            switch (module) {
                default:
                    throw new SimpleCommandExceptionType(Text.literal("unknown module")).create();
                case "tpp":
                    switch (config) {
                        default:
                            throw new SimpleCommandExceptionType(Text.literal("unknown config")).create();
                        case "enabled":
                            TPP.config.tpp.enabled = Boolean.parseBoolean(value);
                            break;
                    }
                    break;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return 0;
    }

    private static int executeSaveCommand() {
        Config.SaveConfig();
        return 0;
    }
}