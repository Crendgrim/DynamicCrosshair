package mod.crend.dynamiccrosshair.neoforge.mixin.block;

import mod.crend.dynamiccrosshair.neoforge.mixin.DynamicCrosshairBaseBlock;
import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairBlock;
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
