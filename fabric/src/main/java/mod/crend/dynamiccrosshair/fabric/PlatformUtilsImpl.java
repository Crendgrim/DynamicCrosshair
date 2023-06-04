package mod.crend.dynamiccrosshair.fabric;

import mod.crend.dynamiccrosshair.mixin.fabric.IBucketItemMixin;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;

@SuppressWarnings("unused")
public class PlatformUtilsImpl {
	public static boolean isModLoaded(String modid) {
		return FabricLoader.getInstance().isModLoaded(modid);
	}

	public static Fluid getFluidFromBucket(BucketItem bucket) {
		return ((IBucketItemMixin) bucket).getFluid();
	}
}
