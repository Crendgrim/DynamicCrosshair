package mod.crend.dynamiccrosshair.neoforge.mixin.item;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairItem;
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
