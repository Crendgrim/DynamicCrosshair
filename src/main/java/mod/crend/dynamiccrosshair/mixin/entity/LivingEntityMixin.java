package mod.crend.dynamiccrosshair.mixin.entity;

import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends EntityMixin implements DynamicCrosshairEntity {
	@Shadow public abstract float getHealth();
	@Shadow public abstract float getMaxHealth();
	@Shadow public abstract boolean isBaby();
	@Shadow public abstract boolean isSleeping();
	@Shadow public abstract boolean hasStatusEffect(/*? if >=1.20.6 {*//*RegistryEntry<StatusEffect>*//*?} else {*/StatusEffect/*?}*/ effect);
	@Shadow public abstract ItemStack getStackInHand(Hand hand);
}

