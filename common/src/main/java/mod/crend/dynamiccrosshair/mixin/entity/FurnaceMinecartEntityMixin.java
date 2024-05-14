package mod.crend.dynamiccrosshair.mixin.entity;

import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairEntity;
import mod.crend.dynamiccrosshair.api.InteractionType;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FurnaceMinecartEntity.class)
public class FurnaceMinecartEntityMixin implements DynamicCrosshairEntity {
	@Shadow @Final private static Ingredient ACCEPTABLE_FUEL;

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (ACCEPTABLE_FUEL.test(context.getItemStack())) {
			return InteractionType.USE_ITEM_ON_ENTITY;
		}
		return InteractionType.NO_ACTION;
	}
}
