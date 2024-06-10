package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairItem;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ArmorItem.class, ElytraItem.class})
public class ArmorItemMixin implements DynamicCrosshairItem {
	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		ItemStack itemStack = context.getItemStack();
		EquipmentSlot slot = context.getPlayer().getPreferredEquipmentSlot(itemStack);

		if (!context.getPlayer().canUseSlot(slot)) {
			return InteractionType.EMPTY;
		}
		ItemStack itemStack2 = context.getPlayer().getEquippedStack(slot);
		if ((!EnchantmentHelper.hasAnyEnchantmentsWith(itemStack2, EnchantmentEffectComponentTypes.PREVENT_ARMOR_CHANGE) || context.getPlayer().isCreative())
				&& !ItemStack.areEqual(itemStack, itemStack2)) {
			return InteractionType.EQUIP_ITEM;
		}
		return InteractionType.NO_ACTION;
	}
}
