package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairItem;
import mod.crend.dynamiccrosshair.api.InteractionType;
import net.minecraft.block.BrushableBlock;
import net.minecraft.item.BrushItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BrushItem.class)
public class BrushItemMixin implements DynamicCrosshairItem {
	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (context.isWithBlock() && context.getBlock() instanceof BrushableBlock) {
			return InteractionType.USE_ITEM_ON_BLOCK;
		}
		return InteractionType.NO_ACTION;
	}
}
