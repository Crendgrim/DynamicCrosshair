package mod.crend.dynamiccrosshair.neoforge.mixin.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TameableEntity.class)
public abstract class TameableEntityMixin extends AnimalEntityMixin {
	@Shadow public abstract boolean isTamed();

	@Shadow public abstract boolean isOwner(LivingEntity entity);
}
