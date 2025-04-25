package top.syshub.tpp.modules;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
            "tpp", List.of("enabled", "cooldown", "target")
    );

    private static final Map<String, List<String>> CONFIGS = Map.of(
            "enabled", List.of("true", "false"),
            "cooldown", List.of("clear", "0", "10", "20", "30", "44", "60"),
            "target", List.of("teammates", "allplayers")
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

    private static int executeConfigCommand(String module, String config, String value) throws CommandSyntaxException {
        validateConfig(module, config, value);

        switch (module) {
            case "tpp":
                switch (config) {
                    case "enabled":
                        TPP.config.tpp.enabled = Boolean.parseBoolean(value);
                        break;
                    case "cooldown":
                        if (value.equals("clear")) {
                            TppCooldownManager.clearCooldown();
                            break;
                        }
                        TPP.config.tpp.cooldown = Integer.parseInt(value);
                        break;
                    case "target":
                        switch (value) {
                            case "teammates":
                                TPP.config.tpp.target = ConfigClass.TppConfig.Target.teammates;
                                break;
                            case "allplayers":
                                TPP.config.tpp.target = ConfigClass.TppConfig.Target.allplayers;
                                break;
                        }
                        break;
                }
                break;
        }

        return 0;
    }

    private static int executeSaveCommand() {
        Config.SaveConfig();
        return 0;
    }

    private static void validateConfig(String module, String config, String value) throws CommandSyntaxException {
        if (!MODULES.containsKey(module)) throw new SimpleCommandExceptionType(Text.literal("未知模块")).create();

        switch (config) {
            default:
                throw new SimpleCommandExceptionType(Text.literal("未知配置项")).create();
            case "enabled":
                if (!value.equals("true") && !value.equals("false")) throw new SimpleCommandExceptionType(Text.literal("参数错误")).create();
                break;
            case "cooldown":
                if (value.equals("clear")) break;
                try {
                    int num = Integer.parseInt(value);
                    if (num < 0) throw new SimpleCommandExceptionType(Text.literal("参数错误")).create();
                } catch (NumberFormatException e) {
                    throw new SimpleCommandExceptionType(Text.literal("参数错误")).create();
                }
                break;
            case "target":
                try {
                    ConfigClass.TppConfig.Target.valueOf(value.toLowerCase());
                } catch (IllegalArgumentException e) {
                    throw new SimpleCommandExceptionType(Text.literal("参数错误")).create();
                }
                break;
        }
    }

}