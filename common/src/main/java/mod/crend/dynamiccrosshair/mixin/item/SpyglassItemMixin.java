package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairItem;
import mod.crend.dynamiccrosshair.api.InteractionType;
import net.minecraft.item.SpyglassItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SpyglassItem.class)
public class SpyglassItemMixin extends ItemMixin implements DynamicCrosshairItem {
	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (DynamicCrosshair.config.dynamicCrosshairForceHoldingSpyglass()) {
			return InteractionType.FORCE_CROSSHAIR;
		}
		return super.dynamiccrosshair$compute(context);
	}
}
