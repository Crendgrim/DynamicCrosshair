package mod.crend.dynamiccrosshair.forge;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraftforge.fml.ModList;

@SuppressWarnings("unused")
public class PlatformUtilsImpl {
	public static boolean isModLoaded(String modid) {
		return ModList.get().isLoaded(modid);
	}

	public static Fluid getFluidFromBucket(BucketItem bucket) {
		return bucket.getFluid();
	}
}
