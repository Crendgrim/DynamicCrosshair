package mod.crend.dynamiccrosshair.neoforge;

import mod.crend.dynamiccrosshair.DynamicCrosshairMod;
import mod.crend.dynamiccrosshairapi.DynamicCrosshair;
import mod.crend.dynamiccrosshairapi.DynamicCrosshairApi;
import net.minecraft.client.gui.screen.Screen;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;

@Mod(DynamicCrosshair.MOD_ID)
public class DynamicCrosshairNeoForge {
    public static final String REGISTER_API = "register_api";

    public DynamicCrosshairNeoForge() { }

    public static void registerApi(DynamicCrosshairApi api) {
        if (ModList.get().isLoaded(api.getModId())) {
            DynamicCrosshairMod.registerApi(api);
        }
    }
}
