package mod.crend.dynamiccrosshair.mixin.block;

import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairBlock;
import mod.crend.dynamiccrosshair.api.InteractionType;
import net.minecraft.block.CakeBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CakeBlock.class)
public class CakeBlockMixin implements DynamicCrosshairBlock {
	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		// Special case: Cake gets eaten (modified), so "use" makes more sense to me
		if (context.player.canConsume(false) && context.shouldInteract()) {
			return InteractionType.USE_BLOCK;
		}
		return InteractionType.NO_ACTION;
	}
}
