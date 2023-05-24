package mod.crend.dynamiccrosshair.mixin;

import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(ParrotEntity.class)
public interface ParrotEntityAccessor {
    @Accessor
    public static Set<Item> getTAMING_INGREDIENTS() {
        throw new AssertionError();
    }
}
