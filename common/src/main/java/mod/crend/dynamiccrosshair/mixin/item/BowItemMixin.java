package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairRangedItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BowItem.class)
public abstract class BowItemMixin extends ItemMixin implements DynamicCrosshairRangedItem {

	@Shadow public abstract int getMaxUseTime(ItemStack stack);

	@Override
	public boolean dynamiccrosshair$isCharged(CrosshairContext context) {
		if (context.isActiveItem()) {
			float progress = BowItem.getPullProgress(getMaxUseTime(context.getItemStack()) - context.getPlayer().getItemUseTimeLeft());
			return (progress == 1.0f);
		}
		return false;
	}
}
