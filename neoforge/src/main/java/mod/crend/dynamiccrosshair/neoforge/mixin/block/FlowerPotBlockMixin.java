package mod.crend.dynamiccrosshair.neoforge.mixin.block;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import java.util.Map;

@Mixin(FlowerPotBlock.class)
public class FlowerPotBlockMixin implements DynamicCrosshairBlock {
	@Shadow @Final private Block content;

	@Shadow @Final private static Map<Block, Block> CONTENT_TO_POTTED;

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		// Special case: Flower pots behave oddly
		Item handItem = context.getItem();
		boolean potItemIsAir = this.content == Blocks.AIR;
		boolean handItemIsPottable = handItem instanceof BlockItem && CONTENT_TO_POTTED.containsKey(((BlockItem) handItem).getBlock());
		if (potItemIsAir && handItemIsPottable) {
			return InteractionType.PLACE_ITEM_ON_BLOCK;
		}
		if (!potItemIsAir && !handItemIsPottable) {
			return InteractionType.TAKE_ITEM_FROM_BLOCK;
		}
		return InteractionType.NO_ACTION;
	}
}
