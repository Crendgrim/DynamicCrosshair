package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairItem;
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
