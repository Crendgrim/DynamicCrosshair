package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshair.DynamicCrosshairMod;
import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairItem;
import mod.crend.dynamiccrosshair.api.InteractionType;
import mod.crend.dynamiccrosshair.config.BlockCrosshairPolicy;
import net.minecraft.item.BlockItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin implements DynamicCrosshairItem {
	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (DynamicCrosshairMod.config.dynamicCrosshairHoldingBlock() == BlockCrosshairPolicy.IfInteractable) {
			if (context.isWithBlock()) {
				if (context.canPlaceItemAsBlock()) {
					return InteractionType.PLACE_BLOCK;
				} else return InteractionType.NO_ACTION;
			} else return InteractionType.EMPTY;
		} else return InteractionType.PLACE_BLOCK;
	}
}
