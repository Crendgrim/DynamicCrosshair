package mod.crend.dynamiccrosshair.mixin.block;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairBlock;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.DoorBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = DoorBlock.class)
public abstract class DoorBlockMixin implements DynamicCrosshairBlock {
	@Shadow public abstract BlockSetType getBlockSetType();

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (this.getBlockSetType().canOpenByHand()) {
			return InteractionType.INTERACT_WITH_BLOCK;
		}
		return InteractionType.EMPTY;
	}
}
