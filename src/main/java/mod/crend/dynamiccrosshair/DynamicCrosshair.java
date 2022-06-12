package mod.crend.dynamiccrosshair;

import mod.crend.dynamiccrosshair.api.DynamicCrosshairApi;
import mod.crend.dynamiccrosshair.config.Config;
import mod.crend.dynamiccrosshair.config.ConfigHandler;
import mod.crend.dynamiccrosshair.handler.VanillaApiImpl;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DynamicCrosshair implements ClientModInitializer {

    public static Config config;
    public static final Map<String, DynamicCrosshairApi> apis = new HashMap<>();
    public static final Set<String> alwaysCheckedApis = new HashSet<>();

    public static void addApi(DynamicCrosshairApi apiImpl) {
        final String identifier = apiImpl.getNamespace();
        if (identifier.equals(Identifier.DEFAULT_NAMESPACE) || FabricLoader.getInstance().isModLoaded(identifier)) {
            apis.put(identifier, apiImpl);
        }
        if (apiImpl.forceCheck()) {
            alwaysCheckedApis.add(identifier);
        }
    }

    @Override
    public void onInitializeClient() {
        ConfigHandler.init();
        config = ConfigHandler.getConfig();

        FabricLoader.getInstance().getEntrypointContainers("dynamiccrosshair", DynamicCrosshairApi.class).forEach(entrypoint -> {
            addApi(entrypoint.getEntrypoint());
        });
        addApi(new VanillaApiImpl());
    }
}
