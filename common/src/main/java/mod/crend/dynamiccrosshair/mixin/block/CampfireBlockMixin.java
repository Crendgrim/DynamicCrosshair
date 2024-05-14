package mod.crend.dynamiccrosshair.mixin.block;

import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairBlock;
import mod.crend.dynamiccrosshair.api.InteractionType;
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
