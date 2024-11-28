package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshair.DynamicCrosshairMod;
import mod.crend.dynamiccrosshair.config.UsableCrosshairPolicy;
import mod.crend.dynamiccrosshair.mixin.DynamicCrosshairBaseItem;
import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairItem;
import net.minecraft.block.AbstractPlantStemBlock;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ShearsItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShearsItem.class)
public class ShearsItemMixin extends DynamicCrosshairBaseItem implements DynamicCrosshairItem {

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (context.isWithBlock() && DynamicCrosshairMod.config.dynamicCrosshairHoldingUsableItem() != UsableCrosshairPolicy.Disabled) {
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
