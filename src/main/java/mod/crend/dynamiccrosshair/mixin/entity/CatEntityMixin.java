package mod.crend.dynamiccrosshair.mixin.entity;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CatEntity.class)
public abstract class CatEntityMixin extends TameableEntityMixin implements DynamicCrosshairEntity {
	@Shadow public abstract DyeColor getCollarColor();

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (this.isTamed() && this.isOwner(context.getPlayer())) {
			if (context.getItem() instanceof DyeItem dye && this.getCollarColor() != dye.getColor()) {
				return InteractionType.USE_ITEM_ON_ENTITY;
			}
			return InteractionType.INTERACT_WITH_ENTITY;
		}
		if (isBreedingItem(context.getItemStack())) {
			return InteractionType.USE_ITEM_ON_ENTITY;
		}
		return super.dynamiccrosshair$compute(context);
	}
}
