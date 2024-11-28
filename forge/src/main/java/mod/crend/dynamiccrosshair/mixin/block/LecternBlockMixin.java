package mod.crend.dynamiccrosshair.mixin.block;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairBlock;
import net.minecraft.block.LecternBlock;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.property.BooleanProperty;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LecternBlock.class)
public class LecternBlockMixin implements DynamicCrosshairBlock {
	@Shadow @Final public static BooleanProperty HAS_BOOK;

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (!context.getPlayer().shouldCancelInteraction() && context.getBlockState().get(HAS_BOOK)) {
			return InteractionType.INTERACT_WITH_BLOCK;
		}
		Item handItem = context.getItem();
		if (handItem.equals(Items.WRITTEN_BOOK)
				|| handItem.equals(Items.WRITABLE_BOOK))
			return InteractionType.PLACE_ITEM_ON_BLOCK;
		return InteractionType.NO_ACTION;
	}
}
