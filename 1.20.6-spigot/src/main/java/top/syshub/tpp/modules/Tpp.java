package top.syshub.tpp.modules;

import org.bukkit.Bukkit;

import java.util.Objects;

public class Tpp {
    public static void Initialize() {
        Objects.requireNonNull(Bukkit.getPluginCommand("tpp")).setExecutor(new TppCommand());
        Objects.requireNonNull(Bukkit.getPluginCommand("tpp")).setTabCompleter(new TppCommand());
    }
}
