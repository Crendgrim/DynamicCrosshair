package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairItem;
import mod.crend.dynamiccrosshair.api.InteractionType;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.EndCrystalItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EndCrystalItem.class)
public class EndCrystalItemMixin implements DynamicCrosshairItem {
	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (context.isWithBlock()) {
			Block block = context.getBlock();
			if ((block == Blocks.OBSIDIAN || block == Blocks.BEDROCK) && context.getWorld().isAir(context.getBlockPos().up())) {
				return InteractionType.PLACE_BLOCK;
			}
		}
		return InteractionType.NO_ACTION;
	}
}
