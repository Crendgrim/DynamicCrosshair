package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairItem;
import mod.crend.dynamiccrosshair.api.InteractionType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ElytraItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ArmorItem.class, ElytraItem.class})
public class ArmorItemMixin implements DynamicCrosshairItem {
	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		EquipmentSlot slot = MobEntity.getPreferredEquipmentSlot(context.getItemStack());
		if (EnchantmentHelper.hasBindingCurse(context.getPlayer().getEquippedStack(slot))) {
			return InteractionType.NO_ACTION;
		}
		return InteractionType.EQUIP_ITEM;
	}
}
