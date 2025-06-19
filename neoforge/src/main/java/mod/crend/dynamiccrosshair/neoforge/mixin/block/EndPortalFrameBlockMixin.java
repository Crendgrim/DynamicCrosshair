package mod.crend.dynamiccrosshair.neoforge.mixin.block;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairBlock;
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
