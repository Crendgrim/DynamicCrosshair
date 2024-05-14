package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairItem;
import mod.crend.dynamiccrosshair.api.InteractionType;
import mod.crend.dynamiccrosshair.config.BlockCrosshairPolicy;
import net.minecraft.item.MinecartItem;
import net.minecraft.registry.tag.BlockTags;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MinecartItem.class)
public class MinecartItemMixin implements DynamicCrosshairItem {
	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (DynamicCrosshair.config.dynamicCrosshairHoldingBlock() == BlockCrosshairPolicy.IfInteractable) {
			if (context.isWithBlock()) {
				if (context.getBlockState().isIn(BlockTags.RAILS)) return InteractionType.PLACE_ENTITY;
			}
		} else return InteractionType.PLACE_ENTITY;
		return InteractionType.NO_ACTION;
	}
}
