package mod.crend.dynamiccrosshair.neoforge.mixin.block;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairBlock;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.TrapdoorBlock;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = TrapdoorBlock.class)
public class TrapdoorBlockMixin implements DynamicCrosshairBlock {
	@Shadow @Final private BlockSetType blockSetType;

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (this.blockSetType.canOpenByHand()) {
			return InteractionType.INTERACT_WITH_BLOCK;
		}
		return InteractionType.EMPTY;
	}
}
