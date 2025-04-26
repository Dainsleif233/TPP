package top.syshub.tpp;

import com.moandjiezana.toml.Toml;
import org.bukkit.plugin.java.JavaPlugin;
import top.syshub.tpp.modules.*;

import java.nio.file.Path;

public final class TPP extends JavaPlugin {

    public static JavaPlugin plugin;
    public static ConfigClass config;

    private ConfigClass loadConfig() {

        final Path configFile = getDataFolder().toPath().resolve("tpp.toml");
        Toml toml = new Toml().read(configFile.toFile());

        return toml.to(ConfigClass.class);
    }

    @Override
    public void onEnable() {
        plugin  = this;
        Config.Initialize();
        config = loadConfig();

        Tpp.Initialize();
    }

    @Override
    public void onDisable() {}
}
