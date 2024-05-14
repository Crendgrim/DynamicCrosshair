package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairItem;
import mod.crend.dynamiccrosshair.api.InteractionType;
import net.minecraft.item.SpawnEggItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SpawnEggItem.class)
public class SpawnEggItemMixin implements DynamicCrosshairItem {
	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (context.isWithBlock()) {
			return InteractionType.USE_ITEM_ON_BLOCK;
		}
		if (context.isWithEntity()) {
			return InteractionType.USE_ITEM_ON_ENTITY;
		}
		return InteractionType.NO_ACTION;
	}
}
