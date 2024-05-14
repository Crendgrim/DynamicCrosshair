package mod.crend.dynamiccrosshair.mixin.block;

import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairBlock;
import mod.crend.dynamiccrosshair.api.InteractionType;
import net.minecraft.block.RedstoneOreBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RedstoneOreBlock.class)
public abstract class RedstoneOreMixin extends BlockMixin implements DynamicCrosshairBlock {

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
