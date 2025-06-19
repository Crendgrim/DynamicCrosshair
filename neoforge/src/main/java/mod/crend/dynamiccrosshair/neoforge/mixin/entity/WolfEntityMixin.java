package mod.crend.dynamiccrosshair.neoforge.mixin.entity;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairEntity;

//? if >=1.21.2 {
/*import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
*///?}
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.DyeItem;
//? if >=1.20.5
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WolfEntity.class)
public abstract class WolfEntityMixin extends TameableEntityMixin implements DynamicCrosshairEntity {

	@Shadow public abstract DyeColor getCollarColor();

	//? if >=1.20.5 <1.21.2
	@Shadow public abstract boolean hasArmor();

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (this.isTamed() && this.isOwner(context.getPlayer())) {
			if (context.getItem() instanceof DyeItem dye && this.getCollarColor() != dye.getColor()) {
				return InteractionType.USE_ITEM_ON_ENTITY;
			}
			//? if >=1.21.2 {
			/*if (this.canEquip(context.getItemStack(), EquipmentSlot.BODY) && !this.isWearingBodyArmor() && !this.isBaby()) {
				return InteractionType.PLACE_ITEM_ON_ENTITY;
			} else if (context.getItemStack().isOf(Items.SHEARS)
					&& this.isWearingBodyArmor()
					&& (!EnchantmentHelper.hasAnyEnchantmentsWith(this.getBodyArmor(), EnchantmentEffectComponentTypes.PREVENT_ARMOR_CHANGE) || context.getPlayer().isCreative())) {
				return InteractionType.TAKE_ITEM_FROM_ENTITY;
			}
			*///?} else if >=1.20.5 {
			if (context.getItem() == Items.WOLF_ARMOR && !this.hasArmor() && !this.isBaby()) {
				return InteractionType.PLACE_ITEM_ON_ENTITY;
			} else if (context.getItem() == Items.SHEARS && this.hasArmor()) {
				return InteractionType.TAKE_ITEM_FROM_ENTITY;
			}
			//?}

			return InteractionType.INTERACT_WITH_ENTITY;
		}
		return super.dynamiccrosshair$compute(context);
	}
}
