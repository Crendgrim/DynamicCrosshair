package mod.crend.dynamiccrosshair.fabric;

import net.fabricmc.loader.api.FabricLoader;

public class PlatformUtilsImpl {
	public static boolean isModLoaded(String modid) {
		return FabricLoader.getInstance().isModLoaded(modid);
	}
}
