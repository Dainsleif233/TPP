package top.syshub.tpp.modules;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

import static top.syshub.tpp.TPP.config;

public class TppCooldownManager {
    private static final Map<Player, Long> lastUsedTimes = new HashMap<>();

    public static void updateLastUsed(Player source) {
        lastUsedTimes.put(source, System.currentTimeMillis());
    }

    public static long getCooldownRemaining(Player source) {
        Long lastUsed = lastUsedTimes.get(source);
        if (lastUsed == null) return 0;

        long timePassed = System.currentTimeMillis() - lastUsed;
        long remaining = (config.tpp.cooldown * 1000L) - timePassed;
        return Math.max(remaining, 0);
    }

    public static void clearCooldown() {
        lastUsedTimes.clear();
    }
}