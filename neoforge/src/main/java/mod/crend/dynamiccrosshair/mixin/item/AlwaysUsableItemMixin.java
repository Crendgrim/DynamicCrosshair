package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairItem;
import net.minecraft.item.GoatHornItem;
//? if <1.21.2 {
import net.minecraft.item.HoneyBottleItem;
import net.minecraft.item.MilkBucketItem;
//?}
import net.minecraft.item.WritableBookItem;
import net.minecraft.item.WrittenBookItem;
import org.spongepowered.asm.mixin.Mixin;

//? if <1.20.5
import net.minecraft.item.EnchantedGoldenAppleItem;
//? if >=1.21 <1.21.2
/*import net.minecraft.item.OminousBottleItem;*/

@Mixin(value = {
		//? if <1.20.5
		EnchantedGoldenAppleItem.class,
		GoatHornItem.class,
		//? if <1.21.2 {
		HoneyBottleItem.class,
		MilkBucketItem.class,
		//? if >=1.21
		/*OminousBottleItem.class,*/
		//?}
		WritableBookItem.class,
		WrittenBookItem.class
})
public class AlwaysUsableItemMixin implements DynamicCrosshairItem {
	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		return InteractionType.USE_ITEM;
	}
}
