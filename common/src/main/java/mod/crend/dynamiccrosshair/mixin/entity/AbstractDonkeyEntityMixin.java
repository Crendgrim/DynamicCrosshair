package mod.crend.dynamiccrosshair.mixin.entity;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairEntity;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractDonkeyEntity.class)
public abstract class AbstractDonkeyEntityMixin extends AbstractHorseEntityMixin implements DynamicCrosshairEntity {
	@Shadow public abstract boolean hasChest();

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (!this.hasChest() && context.getItemStack().isOf(Items.CHEST)) {
			return InteractionType.PLACE_ITEM_ON_ENTITY;
		}
		return super.dynamiccrosshair$compute(context);
	}
}
