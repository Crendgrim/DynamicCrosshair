package mod.crend.dynamiccrosshair.compat;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.compat.arcanus.ArcanusApiImpl;
import net.fabricmc.loader.api.FabricLoader;

public class CompatLoader {
    public static void load() {
        if (FabricLoader.getInstance().isModLoaded("arcanus")) {
            DynamicCrosshair.addApi(new ArcanusApiImpl());
        }
    }
}
