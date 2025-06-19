package mod.crend.dynamiccrosshair.neoforge.mixin.item;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairItem;
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
