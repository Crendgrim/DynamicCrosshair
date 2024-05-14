package mod.crend.dynamiccrosshair.mixin.block;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairBlock;
import mod.crend.dynamiccrosshair.api.InteractionType;
import net.minecraft.block.ComposterBlock;
import net.minecraft.item.ItemConvertible;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ComposterBlock.class)
public class ComposterBlockMixin implements DynamicCrosshairBlock {
	@Shadow @Final public static Object2FloatMap<ItemConvertible> ITEM_TO_LEVEL_INCREASE_CHANCE;

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey(context.getItem())) {
			return InteractionType.USE_ITEM_ON_BLOCK;
		}
		return InteractionType.NO_ACTION;
	}
}
