package mod.crend.dynamiccrosshair.neoforge.mixin.entity;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.recipe.Ingredient;
//? if >=1.21.2
/*import net.minecraft.registry.tag.ItemTags;*/
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FurnaceMinecartEntity.class)
public class FurnaceMinecartEntityMixin implements DynamicCrosshairEntity {
	//? if <1.21.2 {
	@Shadow @Final private static Ingredient ACCEPTABLE_FUEL;
	//?} else
	/*@Shadow private int fuel;*/

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (
				//? if <1.21.2 {
				ACCEPTABLE_FUEL.test(context.getItemStack())
				//?} else
				/*context.getItemStack().isIn(ItemTags.FURNACE_MINECART_FUEL) && this.fuel + 3600 <= 32000*/
		) {
			return InteractionType.USE_ITEM_ON_ENTITY;
		}
		return InteractionType.NO_ACTION;
	}
}
