package mod.crend.dynamiccrosshair.mixin.block;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairBlock;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import net.minecraft.block.CakeBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CakeBlock.class)
public class CakeBlockMixin implements DynamicCrosshairBlock {
	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		// Special case: Cake gets eaten (modified), so "use" makes more sense to me
		if (context.getPlayer().canConsume(false) && context.shouldInteract()) {
			return InteractionType.USE_BLOCK;
		}
		return InteractionType.NO_ACTION;
	}
}
