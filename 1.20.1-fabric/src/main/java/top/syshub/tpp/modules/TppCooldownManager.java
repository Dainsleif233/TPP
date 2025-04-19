package top.syshub.tpp.modules;

import net.minecraft.server.network.ServerPlayerEntity;
import top.syshub.tpp.TPP;

import java.util.HashMap;
import java.util.Map;

public class TppCooldownManager {
    private static final Map<ServerPlayerEntity, Long> lastUsedTimes = new HashMap<>();

    public static void updateLastUsed(ServerPlayerEntity source) {
        lastUsedTimes.put(source, System.currentTimeMillis());
    }

    public static long getCooldownRemaining(ServerPlayerEntity source) {
        Long lastUsed = lastUsedTimes.get(source);
        if (lastUsed == null) return 0;

        long timePassed = System.currentTimeMillis() - lastUsed;
        long remaining = (TPP.config.tpp.cooldown * 1000L) - timePassed;
        return Math.max(remaining, 0);
    }
}