package mod.crend.dynamiccrosshair;

import mod.crend.dynamiccrosshair.api.DynamicCrosshairApi;
import mod.crend.dynamiccrosshair.config.Config;
import mod.crend.dynamiccrosshair.config.ConfigHandler;
import mod.crend.dynamiccrosshair.handler.VanillaApiImpl;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.util.ArrayList;
import java.util.List;

public class DynamicCrosshair implements ClientModInitializer {

    public static Config config;
    public static final List<DynamicCrosshairApi> apis = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        ConfigHandler.init();
        config = ConfigHandler.getConfig();

        apis.add(new VanillaApiImpl());
        FabricLoader.getInstance().getEntrypointContainers("dynamiccrosshair", DynamicCrosshairApi.class).forEach(entrypoint -> {
            apis.add(entrypoint.getEntrypoint());
        });
    }
}
