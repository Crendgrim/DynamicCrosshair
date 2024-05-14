package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairItem;
import mod.crend.dynamiccrosshair.api.InteractionType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.item.BoneMealItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BoneMealItem.class)
public class BoneMealItemMixin implements DynamicCrosshairItem {
	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (context.isWithBlock()) {
			BlockState blockState = context.getBlockState();
			Block block = context.getBlock();
			if (block instanceof Fertilizable fertilizable) {
				if (fertilizable.isFertilizable(context.getWorld(), context.getBlockPos(), blockState)) {
					return InteractionType.USE_ITEM_ON_BLOCK;
				}
			}
			if (context.getBlockState().isOf(Blocks.WATER) && context.getFluidState().getLevel() == 8) {
				return InteractionType.USE_ITEM_ON_BLOCK;
			}
		}
		return InteractionType.NO_ACTION;
	}
}
