package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshair.mixin.DynamicCrosshairBaseItem;
import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairRangedItem;

//? if >=1.21
/*import net.minecraft.entity.LivingEntity;*/
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TridentItem.class)
public abstract class TridentItemMixin extends DynamicCrosshairBaseItem implements DynamicCrosshairRangedItem {

	@Shadow public abstract int getMaxUseTime(ItemStack stack/*? if >=1.21 {*//*, LivingEntity user*//*?}*/);

	@Override
	public boolean dynamiccrosshair$isCharged(CrosshairContext context) {
		if (context.isActiveItem()) {
			int i = getMaxUseTime(context.getItemStack()/*? if >=1.21 {*//*, context.getPlayer()*//*?}*/) - context.getPlayer().getItemUseTimeLeft();
			return (i > 10);
		}
		return false;
	}
}
