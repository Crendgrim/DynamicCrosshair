package mod.crend.dynamiccrosshair.neoforge;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.neoforged.fml.ModList;

@SuppressWarnings("unused")
public class PlatformUtilsImpl {
	public static boolean isModLoaded(String modid) {
		return ModList.get().isLoaded(modid);
	}

	public static Fluid getFluidFromBucket(BucketItem bucket) {
		return bucket.getFluid();
	}
}
