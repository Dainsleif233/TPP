package top.syshub.tpp.modules;

import com.moandjiezana.toml.TomlWriter;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {

    private static final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("tpp.toml");

    public static void Initialize() {
        try {
            if (!Files.exists(configFile)) {
                ConfigClass config = new ConfigClass();
                config.tpp = new ConfigClass.TppConfig();
                config.tpp.enabled = true;

                TomlWriter writer = new TomlWriter();
                writer.write(config, configFile.toFile());
            }
        } catch (IOException e) {
            System.err.println("Failed to create config file: " + e.getMessage());
        }
    }
}