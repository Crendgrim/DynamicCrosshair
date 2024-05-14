package mod.crend.dynamiccrosshair.mixin.entity;

import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairEntity;
import mod.crend.dynamiccrosshair.api.InteractionType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PandaEntity.class)
public abstract class PandaEntityMixin extends MobEntityMixin implements DynamicCrosshairEntity {
	protected PandaEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Shadow public abstract boolean isLyingOnBack();

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (this.isLyingOnBack()) {
			return InteractionType.INTERACT_WITH_ENTITY;
		}
		return super.dynamiccrosshair$compute(context);
	}
}
