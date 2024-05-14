package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairItem;
import mod.crend.dynamiccrosshair.api.InteractionType;
import net.minecraft.item.HoneycombItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HoneycombItem.class)
public class HoneycombItemMixin implements DynamicCrosshairItem {
	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (context.isWithBlock() && HoneycombItem.UNWAXED_TO_WAXED_BLOCKS.get().get(context.getBlock()) != null) {
			return InteractionType.USE_ITEM_ON_BLOCK;
		}
		return InteractionType.NO_ACTION;
	}
}
