package top.syshub.tpp.modules;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import top.syshub.tpp.TPP;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigCommand implements TabExecutor {

    private static final Map<String, List<String>> MODULES = Map.of(
            "tpp", List.of("enabled", "cooldown", "target")
    );

    private static final Map<String, List<String>> CONFIGS = Map.of(
            "enabled", List.of("true", "false"),
            "cooldown", List.of("clear", "0", "10", "20", "30", "44", "60"),
            "target", List.of("teammates", "allplayers")
    );

    @Override
    @ParametersAreNonnullByDefault
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) return Stream.concat(MODULES.keySet().stream(), Stream.of("save")).collect(Collectors.toList());
        if (args.length == 2) return MODULES.getOrDefault(args[0], Collections.emptyList()).stream().toList();
        if (args.length == 3) return CONFIGS.getOrDefault(args[1], Collections.emptyList()).stream().toList();
        return List.of();
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equals("save")) return executeSaveCommand();
        if (args.length == 3) {
            executeConfigCommand(sender, args[0], args[1], args[2]);
            return true;
        }
        return false;
    }

    private static void executeConfigCommand(CommandSender sender, String module, String config, String value) {
        if (!validateConfig(sender, module, config, value)) return;

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
    }

    private static boolean executeSaveCommand() {
        Config.SaveConfig();
        return true;
    }

    private static boolean validateConfig(CommandSender sender, String module, String config, String value) {
        if (!MODULES.containsKey(module)) {
            sender.sendMessage(ChatColor.RED + "未知模块");
            return false;
        }

        switch (config) {
            default:
                sender.sendMessage(ChatColor.RED + "未知配置项");
                return false;
            case "enabled":
                if (!value.equals("true") && !value.equals("false")) {
                    sender.sendMessage(ChatColor.RED + "参数错误");
                    return false;
                }
                break;
            case "cooldown":
                if (value.equals("clear")) break;
                try {
                    int num = Integer.parseInt(value);
                    if (num < 0) {
                        sender.sendMessage(ChatColor.RED + "参数错误");
                        return false;
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "参数错误");
                    return false;
                }
                break;
            case "target":
                try {
                    ConfigClass.TppConfig.Target.valueOf(value.toLowerCase());
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + "参数错误");
                    return false;
                }
                break;
        }
        return true;
    }
}