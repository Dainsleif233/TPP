package top.syshub.tpp;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class Tpp implements ModInitializer {

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            TppCommand.register(dispatcher);
        });
    }
}
