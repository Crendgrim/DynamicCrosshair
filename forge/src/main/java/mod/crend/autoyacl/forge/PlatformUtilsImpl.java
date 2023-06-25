package mod.crend.autoyacl.forge;

import mod.crend.autoyacl.YaclHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class PlatformUtilsImpl {
	public static boolean isModLoaded(String modid) {
		return ModList.get().isLoaded(modid);
	}

	public static boolean isYaclLoaded() {
		ModList modList = ModList.get();
		return modList.isLoaded(YaclHelper.YACL_MOD_ID)
				&& modList.getModContainerById(YaclHelper.YACL_MOD_ID)
					.map(modContainer -> modContainer.getModInfo().getVersion().getMajorVersion() == 3)
					.orElse(false);
	}

	public static Path resolveConfigFile(String configName) {
		return FMLPaths.CONFIGDIR.get().resolve(configName);
	}
}
