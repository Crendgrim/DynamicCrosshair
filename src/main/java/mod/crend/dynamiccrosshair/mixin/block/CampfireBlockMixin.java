package mod.crend.dynamiccrosshair.mixin.block;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairBlock;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.CampfireBlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CampfireBlock.class)
public class CampfireBlockMixin implements DynamicCrosshairBlock {
	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (context.getBlockEntity() instanceof CampfireBlockEntity campfire && campfire.getRecipeFor(context.getItemStack()).isPresent())
			return InteractionType.PLACE_ITEM_ON_BLOCK;
		return InteractionType.NO_ACTION;
	}
}
