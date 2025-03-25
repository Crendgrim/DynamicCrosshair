package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshairapi.VersionUtils;
import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairItem;

//? if >=1.21
/*import net.minecraft.component.EnchantmentEffectComponentTypes;*/
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
//? if <=1.21.4
import net.minecraft.item.ArmorItem;
//? if <1.21.2
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

//? if >1.21.4 {
/*@Mixin(value = VersionUtils.class, remap = false)
*///?} else {
@Mixin({
		ArmorItem.class
		//? if <1.21.2
		, ElytraItem.class
})
//?}
public class ArmorItemMixin /*? if <=1.21.4 {*/implements DynamicCrosshairItem/*?}*/ {
	//? if <=1.21.4 {
	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		ItemStack itemStack = context.getItemStack();
		EquipmentSlot slot = /*? if >=1.21 {*//*context.getPlayer()*//*?} else {*/LivingEntity/*?}*/.getPreferredEquipmentSlot(itemStack);

		//? if >=1.20.6 {
		/*if (!context.getPlayer().canUseSlot(slot)) {
			return InteractionType.EMPTY;
		}
		*///?}
		ItemStack itemStack2 = context.getPlayer().getEquippedStack(slot);
		boolean hasBindingCurse =
				//? if >=1.21 {
				/*EnchantmentHelper.hasAnyEnchantmentsWith(itemStack2, EnchantmentEffectComponentTypes.PREVENT_ARMOR_CHANGE)
				*///?} else {
				EnchantmentHelper.hasBindingCurse(itemStack2)
				//?}
		;
		if ((!hasBindingCurse || context.getPlayer().isCreative())
				&& !ItemStack.areEqual(itemStack, itemStack2)) {
			return InteractionType.EQUIP_ITEM;
		}
		return InteractionType.NO_ACTION;
	}
	//?}
}
