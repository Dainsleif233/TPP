package top.syshub.tpp.modules;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class Tpp {
    public static void Initialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> TppCommand.register(dispatcher));
    }
}
