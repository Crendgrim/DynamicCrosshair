package mod.crend.dynamiccrosshair.mixin;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairBlock;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Block.class)
public abstract class DynamicCrosshairBaseBlock extends AbstractBlock implements DynamicCrosshairBlock {

	public DynamicCrosshairBaseBlock(Settings settings) {
		super(settings);
	}

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		BlockState blockState = context.getBlockState();
		if (context.api().isAlwaysInteractable(blockState)) {
			return InteractionType.INTERACT_WITH_BLOCK;
		}
		if (context.getPlayer().isCreative() && context.api().isAlwaysInteractableInCreativeMode(blockState)) {
			return InteractionType.INTERACT_WITH_BLOCK;
		}

		return InteractionType.EMPTY;
	}
}
