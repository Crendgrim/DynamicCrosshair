package mod.crend.dynamiccrosshair.fabric;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

@SuppressWarnings("unused")
public class PlatformUtilsImpl {
	public static boolean isModLoaded(String modid) {
		return FabricLoader.getInstance().isModLoaded(modid);
	}

	public static Path getCrosshairDirectory() {
		return FabricLoader.getInstance().getConfigDir().resolve("dynamiccrosshair");
	}
}
