package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairItem;
import mod.crend.dynamiccrosshair.api.InteractionType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.item.ShovelItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(ShovelItem.class)
public class ShovelItemMixin extends ItemMixin implements DynamicCrosshairItem {
	@Shadow @Final protected static Map<Block, BlockState> PATH_STATES;

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (context.isWithBlock()) {
			if (PATH_STATES.get(context.getBlock()) != null) {
				if (context.world.getBlockState(context.getBlockPos().up()).isAir()) {
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
