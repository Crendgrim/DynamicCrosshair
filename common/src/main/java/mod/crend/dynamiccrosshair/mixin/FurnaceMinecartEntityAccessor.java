package mod.crend.dynamiccrosshair.mixin;

import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FurnaceMinecartEntity.class)
public interface FurnaceMinecartEntityAccessor {
    @Accessor
    static Ingredient getACCEPTABLE_FUEL() {
        throw new AssertionError();
    }
}
