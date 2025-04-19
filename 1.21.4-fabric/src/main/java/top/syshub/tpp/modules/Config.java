package top.syshub.tpp.modules;

import com.moandjiezana.toml.TomlWriter;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static top.syshub.tpp.TPP.config;

public class Config {

    private static final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("tpp.toml");

    public static void Initialize() {
        try {
            if (!Files.exists(configFile)) {
                ConfigClass config = new ConfigClass();
                config.tpp.enabled = true;

                TomlWriter writer = new TomlWriter();
                writer.write(config, configFile.toFile());
            }
        } catch (IOException e) {
            System.err.println("Failed to create config file: " + e.getMessage());
        }

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ConfigCommand.register(dispatcher));
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