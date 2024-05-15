package mod.crend.dynamiccrosshair;

import dev.architectury.injectables.annotations.ExpectPlatform;

@SuppressWarnings("unused")
public class PlatformUtils {
	@ExpectPlatform
	public static boolean isModLoaded(String modid) {
		throw new AssertionError();
	}
}
