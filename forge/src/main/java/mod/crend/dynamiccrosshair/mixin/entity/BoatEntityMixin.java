package mod.crend.dynamiccrosshair.mixin.entity;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairEntity;
import net.minecraft.entity.Entity;


import net.minecraft.entity.vehicle.VehicleInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

//? if <1.21.2 {
import net.minecraft.entity.vehicle.BoatEntity;

//?} else
/*import net.minecraft.entity.vehicle.AbstractBoatEntity;*/

@Mixin(
		//? if <1.21.2 {
		BoatEntity.class
		//?} else
		/*AbstractBoatEntity.class*/
)
public abstract class BoatEntityMixin implements DynamicCrosshairEntity {
	@Shadow protected abstract boolean canAddPassenger(Entity passenger);

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (this instanceof VehicleInventory) {
			return InteractionType.INTERACT_WITH_ENTITY;
		}
		if (!context.getPlayer().shouldCancelInteraction() && this.canAddPassenger(context.getPlayer())) {
			return InteractionType.MOUNT_ENTITY;
		}
		return InteractionType.NO_ACTION;
	}
}
