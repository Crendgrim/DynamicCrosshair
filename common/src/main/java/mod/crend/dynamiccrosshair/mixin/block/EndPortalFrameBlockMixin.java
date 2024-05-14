package mod.crend.dynamiccrosshair.mixin.block;

import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairBlock;
import mod.crend.dynamiccrosshair.api.InteractionType;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.item.Items;
import net.minecraft.state.property.BooleanProperty;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EndPortalFrameBlock.class)
public class EndPortalFrameBlockMixin implements DynamicCrosshairBlock {
	@Shadow @Final public static BooleanProperty EYE;

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (!context.getBlockState().get(EYE) && context.getItemStack().isOf(Items.ENDER_EYE)) {
			return InteractionType.PLACE_ITEM_ON_BLOCK;
		}
		return InteractionType.NO_ACTION;
	}
}
