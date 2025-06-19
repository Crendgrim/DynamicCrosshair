package mod.crend.dynamiccrosshair.neoforge.mixin;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class DynamicCrosshairBaseEntity implements DynamicCrosshairEntity {

	@Shadow public abstract EntityType<?> getType();

	@Shadow public abstract boolean hasPassengers();

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (context.api().isAlwaysInteractable(getType())) {
			return InteractionType.INTERACT_WITH_ENTITY;
		}
		return InteractionType.EMPTY;
	}

}
