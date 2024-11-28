package mod.crend.dynamiccrosshair.mixin.block;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshair.mixin.DynamicCrosshairBaseBlock;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairBlock;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import net.minecraft.block.RedstoneOreBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RedstoneOreBlock.class)
public abstract class RedstoneOreMixin extends DynamicCrosshairBaseBlock implements DynamicCrosshairBlock {

	public RedstoneOreMixin(Settings settings) {
		super(settings);
	}

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (context.shouldInteract()) {
			return InteractionType.INTERACT_WITH_BLOCK;
		}
		return super.dynamiccrosshair$compute(context);
	}
}
