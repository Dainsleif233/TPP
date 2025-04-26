package top.syshub.tpp.modules;

import com.moandjiezana.toml.TomlWriter;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static top.syshub.tpp.TPP.plugin;
import static top.syshub.tpp.TPP.config;

public class Config {

    private static final Path configFolder = plugin.getDataFolder().toPath();
    private static final Path configFile = configFolder.resolve("tpp.toml");

    public static void Initialize() {
        try {
            if (!Files.exists(configFolder)) Files.createDirectory(configFolder);
            if (!Files.exists(configFile)) {
                ConfigClass config = new ConfigClass();
                config.tpp.enabled = true;

                TomlWriter writer = new TomlWriter();
                writer.write(config, configFile.toFile());
            }
        } catch (IOException e) {
            System.err.println("Failed to create config file: " + e.getMessage());
        }

        Objects.requireNonNull(Bukkit.getPluginCommand("tppconfig")).setExecutor(new ConfigCommand());
        Objects.requireNonNull(Bukkit.getPluginCommand("tppconfig")).setTabCompleter(new ConfigCommand());
    }

    public static void SaveConfig() {
        try {
            TomlWriter writer = new TomlWriter();
            writer.write(config, configFile.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}