package mod.crend.dynamiccrosshair.neoforge;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

@SuppressWarnings("unused")
public class PlatformUtilsImpl {
	public static boolean isModLoaded(String modid) {
		return ModList.get().isLoaded(modid);
	}

	public static Path getCrosshairDirectory() {
		return FMLPaths.CONFIGDIR.get().resolve("dynamiccrosshair");
	}
}
