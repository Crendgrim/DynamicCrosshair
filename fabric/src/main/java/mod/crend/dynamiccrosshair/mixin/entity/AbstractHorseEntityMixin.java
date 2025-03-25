package mod.crend.dynamiccrosshair.mixin.entity;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairEntity;
//? if >=1.20.6
/*import net.minecraft.entity.EquipmentSlot;*/
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractHorseEntity.class)
public abstract class AbstractHorseEntityMixin extends AnimalEntityMixin implements DynamicCrosshairEntity {
	@Shadow public abstract boolean isTame();

	//? if <=1.21.4 {
	@Shadow public abstract boolean canBeSaddled();

	@Shadow public abstract boolean isSaddled();
	//?}

	//? if <1.20.6 {
	@Shadow public abstract boolean isHorseArmor(ItemStack item);

	@Shadow public abstract boolean hasArmorSlot();

	@Shadow public abstract boolean hasArmorInSlot();
	//?}

	//? if >1.21.4 {
	/*@Shadow public abstract boolean canUseSlot(EquipmentSlot par1);
	*///?}

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (!this.hasPassengers() && !this.isBaby()) {
			if (this.isTame() && context.getPlayer().shouldCancelInteraction()) {
				return InteractionType.INTERACT_WITH_ENTITY;
			} else {
				ItemStack itemStack = context.getItemStack();
				if (!itemStack.isEmpty()) {
					if (
							//? if <=1.21.4 {
							this.canBeSaddled() && !this.isSaddled() && itemStack.isOf(Items.SADDLE)
							//?} else
							/*this.canUseSlot(EquipmentSlot.SADDLE) && !this.hasSaddleEquipped()*/
					) {
						return InteractionType.PLACE_ITEM_ON_ENTITY;
					}

					if (
							//? if <1.20.6 {
							this.hasArmorSlot() && this.isHorseArmor(itemStack) && !this.hasArmorInSlot()
							//?} else if <1.21.2 {
							/*this.canUseSlot(EquipmentSlot.BODY) && this.isHorseArmor(itemStack) && !this.isWearingBodyArmor()
							*///?} else
							/*this.canEquip(itemStack, EquipmentSlot.BODY) && !this.isWearingBodyArmor()*/
					) {
						return InteractionType.PLACE_ITEM_ON_ENTITY;
					}
				}

				return InteractionType.MOUNT_ENTITY;
			}
		}
		return super.dynamiccrosshair$compute(context);
	}
}
