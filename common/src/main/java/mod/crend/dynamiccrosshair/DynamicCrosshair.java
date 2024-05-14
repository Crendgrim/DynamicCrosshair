package mod.crend.dynamiccrosshair;

import mod.crend.dynamiccrosshair.api.DynamicCrosshairApi;
import mod.crend.dynamiccrosshair.config.ConfigHandler;
import mod.crend.dynamiccrosshair.impl.VanillaApiImpl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DynamicCrosshair {
    public static final String MOD_ID = "dynamiccrosshair";

    public static ConfigHandler config;
    public static final Map<String, DynamicCrosshairApi> apis = new HashMap<>();
    public static final Set<String> alwaysCheckedApis = new HashSet<>();
    public static final DynamicCrosshairApi vanillaApi = new VanillaApiImpl();

    public static void registerApi(DynamicCrosshairApi apiImpl) {
        apiImpl.init();
        final String identifier = apiImpl.getNamespace();
        DynamicCrosshair.apis.put(identifier, apiImpl);
        if (apiImpl.forceCheck()) {
            DynamicCrosshair.alwaysCheckedApis.add(identifier);
        }
    }

    public static void init() {
        config = new ConfigHandler();
    }
}
