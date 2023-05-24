package mod.crend.dynamiccrosshair.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BoatEntity.class)
public interface BoatEntityAccessor {
	@Invoker
	boolean invokeCanAddPassenger(Entity passenger);
}
