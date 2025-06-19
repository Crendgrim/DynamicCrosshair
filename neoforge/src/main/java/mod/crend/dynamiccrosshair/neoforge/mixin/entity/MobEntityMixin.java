package mod.crend.dynamiccrosshair.neoforge.mixin.entity;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairEntity;

//? if >=1.20.6 {
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
//?}

//? if >=1.21
import net.minecraft.entity.Leashable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
//? if >=1.20.6
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntityMixin implements DynamicCrosshairEntity {

	//? if >=1.20.6 <1.21.2 {
	@Shadow public abstract boolean isHorseArmor(ItemStack stack);

	@Shadow public abstract boolean isWearingBodyArmor();

	@Shadow public abstract boolean canUseSlot(EquipmentSlot slot);
	//?}
	//? if >=1.21.2 {
	/*@Shadow public abstract boolean isWearingBodyArmor();
	@Shadow public abstract ItemStack getBodyArmor();
	*///?}
	//? if <1.21
	/*@Shadow public abstract boolean canBeLeashedBy(PlayerEntity player);*/

	//? if >1.21.4 {
	/*@Shadow public abstract boolean hasSaddleEquipped();
	*///?}

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		Item handItem = context.getItem();
		if (handItem instanceof SpawnEggItem) return InteractionType.USE_ITEM_ON_ENTITY;

		if (handItem == Items.LEAD) {
			if (
				//? if >=1.21 {
				this instanceof Leashable leashable && leashable.canBeLeashed()
				//?} else {
				/*this.canBeLeashedBy(context.getPlayer())
				*///?}
			) {
				return InteractionType.USE_ITEM_ON_ENTITY;
			}
			return InteractionType.NO_ACTION;
		}
		if (handItem == Items.NAME_TAG) {
			if (
					//? if >=1.20.6 {
					context.getItemStack()./*? if 1.21.3 {*//*contains*//*?} else {*/has/*?}*/(DataComponentTypes.CUSTOM_NAME)
					//?} else {
					/*context.getItemStack().hasCustomName()
					*///?}
			) {
				// rename armor stand
				return InteractionType.USE_ITEM_ON_ENTITY;
			}

		}
		return super.dynamiccrosshair$compute(context);
	}
}
