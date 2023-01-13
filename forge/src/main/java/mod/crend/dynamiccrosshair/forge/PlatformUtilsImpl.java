package mod.crend.dynamiccrosshair.forge;


import net.minecraftforge.fml.ModList;

public class PlatformUtilsImpl {
	public static boolean isModLoaded(String modid) {
		return ModList.get().isLoaded(modid);
	}
}
