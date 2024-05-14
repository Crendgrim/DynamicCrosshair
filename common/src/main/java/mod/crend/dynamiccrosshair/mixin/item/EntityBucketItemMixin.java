package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshair.DynamicCrosshairMod;
import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairItem;
import mod.crend.dynamiccrosshair.api.InteractionType;
import mod.crend.dynamiccrosshair.config.BlockCrosshairPolicy;
import net.minecraft.item.EntityBucketItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityBucketItem.class)
public class EntityBucketItemMixin implements DynamicCrosshairItem {
	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (context.isWithBlock()) {
			if (DynamicCrosshairMod.config.dynamicCrosshairHoldingBlock() != BlockCrosshairPolicy.Disabled) {
				return InteractionType.PLACE_ENTITY;
			}
			return InteractionType.USE_ITEM_ON_BLOCK;
		} else {
			if (DynamicCrosshairMod.config.dynamicCrosshairHoldingBlock() == BlockCrosshairPolicy.Always) {
				return InteractionType.PLACE_ENTITY;
			}
		}
		return InteractionType.NO_ACTION;
	}
}
