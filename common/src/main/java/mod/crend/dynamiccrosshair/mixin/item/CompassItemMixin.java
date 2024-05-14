package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairItem;
import mod.crend.dynamiccrosshair.api.InteractionType;
import net.minecraft.block.Blocks;
import net.minecraft.item.CompassItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CompassItem.class)
public class CompassItemMixin implements DynamicCrosshairItem {
	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (context.isWithBlock() && context.getBlockState().isOf(Blocks.LODESTONE)) {
			return InteractionType.USE_ITEM_ON_BLOCK;
		}
		return InteractionType.NO_ACTION;
	}
}
