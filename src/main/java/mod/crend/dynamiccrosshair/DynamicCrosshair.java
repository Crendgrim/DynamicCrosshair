package mod.crend.dynamiccrosshair;

import mod.crend.dynamiccrosshair.api.DynamicCrosshairApi;
import mod.crend.dynamiccrosshair.config.Config;
import mod.crend.dynamiccrosshair.config.ConfigHandler;
import mod.crend.dynamiccrosshair.handler.VanillaApiImpl;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.util.HashMap;
import java.util.Map;

public class DynamicCrosshair implements ClientModInitializer {

    public static Config config;
    public static final Map<String, DynamicCrosshairApi> apis = new HashMap<>();

    public static void addApi(DynamicCrosshairApi apiImpl) {
        apis.put(apiImpl.getNamespace(), apiImpl);
    }

    @Override
    public void onInitializeClient() {
        ConfigHandler.init();
        config = ConfigHandler.getConfig();

        addApi(new VanillaApiImpl());
        FabricLoader.getInstance().getEntrypointContainers("dynamiccrosshair", DynamicCrosshairApi.class).forEach(entrypoint -> {
            addApi(entrypoint.getEntrypoint());
        });
    }
}
