package mod.crend.dynamiccrosshair.mixin.entity;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.item.Items;

//? if >=1.20.6
import net.minecraft.registry.tag.ItemTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ParrotEntity.class)
public abstract class ParrotEntityMixin extends TameableEntityMixin implements DynamicCrosshairEntity {
	@Shadow public abstract boolean isInAir();

	//? if <1.20.6
	/*@Shadow @Final private static Set<Item> TAMING_INGREDIENTS;*/

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (!this.isTamed() 
				//? if >=1.20.6 {
				&& context.getItemStack().isIn(ItemTags.PARROT_FOOD)
				//?} else {
				/*&& TAMING_INGREDIENTS.contains(context.getItem())
				*///?}
		) {
			return InteractionType.USE_ITEM_ON_ENTITY;
		}
		if (context.getItem() == Items.COOKIE) {
			// :'(
			return InteractionType.USE_ITEM_ON_ENTITY;
		}
		if (!this.isInAir() && this.isTamed() && this.isOwner(context.getPlayer())) {
			return InteractionType.INTERACT_WITH_ENTITY;
		}
		return InteractionType.NO_ACTION;
	}
}
