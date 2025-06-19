package mod.crend.dynamiccrosshair.neoforge.mixin.item;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairItem;
import net.minecraft.item.EntityBucketItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityBucketItem.class)
public class EntityBucketItemMixin implements DynamicCrosshairItem {
	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (context.isWithBlock()) {
			return InteractionType.PLACE_ENTITY;
		}
		return InteractionType.NO_ACTION;
	}
}
