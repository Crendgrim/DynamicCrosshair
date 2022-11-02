package mod.crend.dynamiccrosshair;

import mod.crend.dynamiccrosshair.api.DynamicCrosshairApi;
import mod.crend.dynamiccrosshair.config.ConfigHandler;
import mod.crend.dynamiccrosshair.handler.VanillaApiImpl;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DynamicCrosshair implements ClientModInitializer {
    public static final String MOD_ID = "dynamiccrosshair";

    public static ConfigHandler config;
    public static final Map<String, DynamicCrosshairApi> apis = new HashMap<>();
    public static final Set<String> alwaysCheckedApis = new HashSet<>();
    public static final DynamicCrosshairApi vanillaApi = new VanillaApiImpl();

    public static void registerApi(DynamicCrosshairApi apiImpl) {
        if (FabricLoader.getInstance().isModLoaded(apiImpl.getModId())) {
            apiImpl.init();
            final String identifier = apiImpl.getNamespace();
            apis.put(identifier, apiImpl);
            if (apiImpl.forceCheck()) {
                alwaysCheckedApis.add(identifier);
            }
        }
    }

    @Override
    public void onInitializeClient() {
        config = new ConfigHandler();

        FabricLoader.getInstance().getEntrypointContainers(MOD_ID, DynamicCrosshairApi.class).forEach(entrypoint -> {
            registerApi(entrypoint.getEntrypoint());
        });
    }
}
