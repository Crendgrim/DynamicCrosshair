package mod.crend.dynamiccrosshair.mixin;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ArmorStandEntity.class)
public interface ArmorStandEntityAccessor {
	@Invoker
	EquipmentSlot invokeGetSlotFromPosition(Vec3d hitPos);

	@Invoker
	boolean invokeIsSlotDisabled(EquipmentSlot slot);
}
