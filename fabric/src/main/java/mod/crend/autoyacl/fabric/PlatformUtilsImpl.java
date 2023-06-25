package mod.crend.autoyacl.fabric;

import mod.crend.autoyacl.YaclHelper;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class PlatformUtilsImpl {
	public static boolean isModLoaded(String modid) {
		return FabricLoader.getInstance().isModLoaded(modid);
	}

	public static boolean isYaclLoaded() {
		return FabricLoader.getInstance().isModLoaded(YaclHelper.YACL_MOD_ID)
				&& FabricLoader.getInstance()
					.getModContainer(YaclHelper.YACL_MOD_ID)
					.map(modContainer -> modContainer.getMetadata().getVersion().getFriendlyString().startsWith("3."))
					.orElse(false);
	}

	public static Path resolveConfigFile(String configName) {
		return FabricLoader.getInstance().getConfigDir().resolve(configName);
	}
}
