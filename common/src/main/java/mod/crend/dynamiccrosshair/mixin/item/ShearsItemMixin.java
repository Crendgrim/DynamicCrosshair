package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairItem;
import mod.crend.dynamiccrosshair.api.InteractionType;
import net.minecraft.block.AbstractPlantStemBlock;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ShearsItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShearsItem.class)
public class ShearsItemMixin extends ItemMixin implements DynamicCrosshairItem {

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (context.isWithBlock()) {
			BlockState blockState = context.getBlockState();
			Block block = blockState.getBlock();
			if (block instanceof AbstractPlantStemBlock plantStemBlock && !plantStemBlock.hasMaxAge(blockState)) {
				return InteractionType.USABLE_TOOL;
			}
			if (!context.getPlayer().shouldCancelInteraction() && block instanceof BeehiveBlock && blockState.get(BeehiveBlock.HONEY_LEVEL) >= 5) {
				return InteractionType.USABLE_TOOL;
			}
		}
		return super.dynamiccrosshair$compute(context);
	}
}
