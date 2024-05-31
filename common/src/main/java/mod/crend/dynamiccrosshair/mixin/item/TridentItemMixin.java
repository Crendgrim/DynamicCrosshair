package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairRangedItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TridentItem.class)
public abstract class TridentItemMixin extends ItemMixin implements DynamicCrosshairRangedItem {

	@Shadow public abstract int getMaxUseTime(ItemStack stack);

	@Override
	public boolean dynamiccrosshair$isCharged(CrosshairContext context) {
		if (context.isActiveItem()) {
			int i = getMaxUseTime(context.getItemStack()) - context.getPlayer().getItemUseTimeLeft();
			return (i > 10);
		}
		return false;
	}
}
