package mod.crend.dynamiccrosshair.neoforge.mixin.entity;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AnimalEntity.class)
public abstract class AnimalEntityMixin extends MobEntityMixin implements DynamicCrosshairEntity {

	@Shadow public abstract boolean isBreedingItem(ItemStack stack);

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (this.isBreedingItem(context.getItemStack())) {
			return InteractionType.USE_ITEM_ON_ENTITY;
		}
		return super.dynamiccrosshair$compute(context);
	}
}
