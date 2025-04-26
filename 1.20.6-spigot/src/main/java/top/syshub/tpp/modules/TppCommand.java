package top.syshub.tpp.modules;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

import static top.syshub.tpp.TPP.config;
import static top.syshub.tpp.TPP.plugin;

public class TppCommand implements TabExecutor {

    @Override
    @ParametersAreNonnullByDefault
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 1) return List.of();
        if (!config.tpp.enabled || !(sender instanceof Player) ) return List.of();
        if (config.tpp.target.equals(ConfigClass.TppConfig.Target.teammates) && ((Player) sender).getScoreboard().getEntryTeam(sender.getName()) == null) return List.of();
        return plugin.getServer().getOnlinePlayers().stream()
                .filter(p -> p != sender)
                .filter(p -> switch (config.tpp.target) {
                    case teammates ->
                            p.getScoreboard().getEntryTeam(p.getName()) != null && Objects.requireNonNull(p.getScoreboard().getEntryTeam(p.getName())).getName().equals(Objects.requireNonNull(p.getScoreboard().getEntryTeam(sender.getName())).getName());
                    case allplayers -> true;
                }).map(Player::getName).toList();
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target = plugin.getServer().getPlayer(args[0]);
        if (target != null && args.length == 1 && sender instanceof Player) {
            executeTpp((Player) sender, target);
            return true;
        }
        return false;
    }

    private static void executeTpp(Player source, Player target) {
        if (!checkCommandEnabled(source) || !checkCooldown(source) || !checkSameTeam(source, target)) return;

        source.teleport(target);
        TppCooldownManager.updateLastUsed(source);
    }

    private static boolean checkCommandEnabled(Player source) {
        if (!config.tpp.enabled) {
            source.sendMessage(ChatColor.RED + "命令未启用");
            return false;
        }
        return true;
    }

    private static boolean checkCooldown(Player source) {
        if (config.tpp.cooldown > 0) {
            long remaining = TppCooldownManager.getCooldownRemaining(source);
            if (remaining > 0) {
                source.sendMessage(ChatColor.RED + "传送冷却还剩 " + remaining / 1000 + " 秒");
                return false;
            }
        }
        return true;
    }

    private static boolean checkSameTeam(Player source, Player target) {
        if (config.tpp.target.equals(ConfigClass.TppConfig.Target.allplayers)) return true;
        if (source.getScoreboard().getEntryTeam(source.getName()) == null) {
            source.sendMessage(ChatColor.RED + "你不在任何队伍中");
            return false;
        }
        if ((source.getScoreboard().getEntryTeam(target.getName()) == null) || !Objects.requireNonNull(source.getScoreboard().getEntryTeam(source.getName())).getName().equals(Objects.requireNonNull(target.getScoreboard().getEntryTeam(target.getName())).getName())) {
            source.sendMessage(ChatColor.RED + "目标玩家不在你的队伍中");
            return false;
        }
        return true;
    }
}