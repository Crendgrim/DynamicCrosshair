package mod.crend.dynamiccrosshair.mixin.entity;

import mod.crend.dynamiccrosshair.mixin.DynamicCrosshairBaseEntity;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairEntity;

//? if >=1.21.2
/*import net.minecraft.entity.EquipmentSlot;*/
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
//? if >=1.20.6
/*import net.minecraft.registry.entry.RegistryEntry;*/
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends DynamicCrosshairBaseEntity implements DynamicCrosshairEntity {
	@Shadow public abstract float getHealth();
	@Shadow public abstract float getMaxHealth();
	@Shadow public abstract boolean isBaby();
	@Shadow public abstract boolean isSleeping();
	@Shadow public abstract boolean hasStatusEffect(/*? if >=1.20.6 {*//*RegistryEntry<StatusEffect>*//*?} else {*/StatusEffect/*?}*/ effect);
	@Shadow public abstract ItemStack getStackInHand(Hand hand);
	//? if >=1.21.2
	/*@Shadow public abstract boolean canEquip(ItemStack itemStack, EquipmentSlot equipmentSlot);*/
}

