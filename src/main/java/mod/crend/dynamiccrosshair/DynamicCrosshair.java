package mod.crend.dynamiccrosshair;

import mod.crend.dynamiccrosshair.config.Config;
import mod.crend.dynamiccrosshair.config.ConfigHandler;
import net.fabricmc.api.ClientModInitializer;

public class DynamicCrosshair implements ClientModInitializer {

    public static Config config;

    @Override
    public void onInitializeClient() {
        ConfigHandler.init();
        config = ConfigHandler.getConfig();
    }
}
