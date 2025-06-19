package mod.crend.dynamiccrosshair.neoforge.mixin.block;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairBlock;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.CampfireBlockEntity;

//? if >=1.21.2
/*import net.minecraft.recipe.RecipePropertySet;*/
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CampfireBlock.class)
public class CampfireBlockMixin implements DynamicCrosshairBlock {
	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (context.getBlockEntity() instanceof CampfireBlockEntity campfire
				//? if <1.21.2 {
				&& campfire.getRecipeFor(context.getItemStack()).isPresent()
				//?} else
				/*&& context.getWorld().getRecipeManager().getPropertySet(RecipePropertySet.CAMPFIRE_INPUT).canUse(context.getItemStack())*/
		)
			return InteractionType.PLACE_ITEM_ON_BLOCK;
		return InteractionType.NO_ACTION;
	}
}
