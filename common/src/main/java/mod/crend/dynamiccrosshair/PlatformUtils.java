package mod.crend.dynamiccrosshair;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.nio.file.Path;

@SuppressWarnings("unused")
public class PlatformUtils {
	@ExpectPlatform
	public static boolean isModLoaded(String modid) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static Path getCrosshairDirectory() {
		throw new AssertionError();
	}
}
