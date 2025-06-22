package mod.crend.dynamiccrosshair.mixin.block;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairBlock;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairItem;
import net.minecraft.block.AbstractCandleBlock;
import net.minecraft.state.property.BooleanProperty;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractCandleBlock.class)
public class AbstractCandleBlockMixin implements DynamicCrosshairBlock {
	@Shadow @Final public static BooleanProperty LIT;

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (context.getBlockState().get(LIT) && context.isMainHand()) {
			return switch (((DynamicCrosshairItem) context.getItem()).dynamiccrosshair$compute(context)) {
				case USE_ITEM,
					 USE_ITEM_ON_BLOCK,
					 CONSUME_ITEM,
					 CHARGE_ITEM,
					 EQUIP_ITEM,
					 SPYGLASS,
					 PLACE_BLOCK,
					 NO_ACTION
						-> InteractionType.NO_ACTION;
				default -> InteractionType.INTERACT_WITH_BLOCK;
			};
		}
		return InteractionType.NO_ACTION;
	}
}
