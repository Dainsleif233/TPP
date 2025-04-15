package top.syshub.tpp;

import com.moandjiezana.toml.Toml;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import top.syshub.tpp.modules.*;

import java.nio.file.Path;

public class TPP implements ModInitializer {

    public static ConfigClass config;

    private ConfigClass loadConfig() {

        final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("tpp.toml");
        Toml toml = new Toml().read(configFile.toFile());

        return toml.to(ConfigClass.class);
    }

    @Override
    public void onInitialize() {
        Config.Initialize();
        config = loadConfig();

        Tpp.Initialize();
    }
}