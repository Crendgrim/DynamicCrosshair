package mod.crend.dynamiccrosshair.mixin.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TameableEntity.class)
public abstract class TameableEntityMixin extends AnimalEntityMixin {
	@Shadow public abstract boolean isTamed();

	@Shadow public abstract boolean isOwner(LivingEntity entity);

	protected TameableEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}
}
