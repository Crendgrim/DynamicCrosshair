package mod.crend.dynamiccrosshair;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;

@SuppressWarnings("unused")
public class PlatformUtils {
	@ExpectPlatform
	public static boolean isModLoaded(String modid) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static Fluid getFluidFromBucket(BucketItem bucket) {
		throw new AssertionError();
	}
}
