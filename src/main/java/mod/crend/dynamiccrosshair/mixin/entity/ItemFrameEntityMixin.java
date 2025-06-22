package mod.crend.dynamiccrosshair.mixin.entity;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemFrameEntity.class)
public abstract class ItemFrameEntityMixin implements DynamicCrosshairEntity {
	@Shadow public abstract ItemStack getHeldItemStack();

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (this.getHeldItemStack().isEmpty()) {
			if (context.getItemStack().isEmpty()) {
				return InteractionType.NO_ACTION;
			}
			return InteractionType.PLACE_ITEM_ON_ENTITY;
		}
		return InteractionType.TAKE_ITEM_FROM_ENTITY;
	}
}
