package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairRangedItem;
import net.minecraft.item.CrossbowItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin extends ItemMixin implements DynamicCrosshairRangedItem {
	@Override
	public boolean dynamiccrosshair$isCharged(CrosshairContext context) {
		return CrossbowItem.isCharged(context.getItemStack());
	}
}
