package mod.crend.dynamiccrosshair.mixin.entity;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairEntity;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WolfEntity.class)
public abstract class WolfEntityMixin extends TameableEntityMixin implements DynamicCrosshairEntity {

	@Shadow public abstract DyeColor getCollarColor();

	@Shadow public abstract boolean hasArmor();

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (this.isTamed() && this.isOwner(context.getPlayer())) {
			if (context.getItem() instanceof DyeItem dye && this.getCollarColor() != dye.getColor()) {
				return InteractionType.USE_ITEM_ON_ENTITY;
			}
			if (context.getItem() == Items.WOLF_ARMOR && !this.hasArmor() && !this.isBaby()) {
				return InteractionType.PLACE_ITEM_ON_ENTITY;
			} else if (context.getItem() == Items.SHEARS && this.hasArmor()) {
				return InteractionType.TAKE_ITEM_FROM_ENTITY;
			}
			return InteractionType.INTERACT_WITH_ENTITY;
		}
		return super.dynamiccrosshair$compute(context);
	}
}
