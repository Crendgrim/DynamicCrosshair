package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshair.DynamicCrosshairMod;
import mod.crend.dynamiccrosshair.config.UsableCrosshairPolicy;
import mod.crend.dynamiccrosshair.mixin.DynamicCrosshairBaseItem;
import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.item.ShovelItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import java.util.Map;

@Mixin(ShovelItem.class)
public class ShovelItemMixin extends DynamicCrosshairBaseItem implements DynamicCrosshairItem {
	@Shadow @Final protected static Map<Block, BlockState> PATH_STATES;

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (context.isWithBlock() && DynamicCrosshairMod.config.dynamicCrosshairHoldingUsableItem() != UsableCrosshairPolicy.Disabled) {
			if (PATH_STATES.get(context.getBlock()) != null) {
				if (context.getWorld().getBlockState(context.getBlockPos().up()).isAir()) {
					return InteractionType.USABLE_TOOL;
				}
			}
			if (context.getBlock() instanceof CampfireBlock && context.getBlockState().get(CampfireBlock.LIT)) {
				return InteractionType.USABLE_TOOL;
			}
		}
		return super.dynamiccrosshair$compute(context);
	}
}
