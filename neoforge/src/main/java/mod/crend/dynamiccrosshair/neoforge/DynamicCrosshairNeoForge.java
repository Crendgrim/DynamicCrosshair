package mod.crend.dynamiccrosshair.neoforge;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairApi;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;

@Mod(DynamicCrosshair.MOD_ID)
public class DynamicCrosshairNeoForge {
    public static final String REGISTER_API = "register_api";

    public DynamicCrosshairNeoForge() { }

    public static void registerApi(DynamicCrosshairApi api) {
        if (ModList.get().isLoaded(api.getModId())) {
            DynamicCrosshair.registerApi(api);
        }
    }
}
