package mod.crend.dynamiccrosshair.neoforge.mixin.entity;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MinecartEntity.class)
public abstract class MinecartEntityMixin implements DynamicCrosshairEntity {
	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		return InteractionType.MOUNT_ENTITY;
	}
}
