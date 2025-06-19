package mod.crend.dynamiccrosshair.neoforge.mixin.item;

import mod.crend.dynamiccrosshair.neoforge.mixin.DynamicCrosshairBaseItem;
import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairRangedItem;
import net.minecraft.item.CrossbowItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin extends DynamicCrosshairBaseItem implements DynamicCrosshairRangedItem {
	@Override
	public boolean dynamiccrosshair$isCharged(CrosshairContext context) {
		return CrossbowItem.isCharged(context.getItemStack());
	}
}
