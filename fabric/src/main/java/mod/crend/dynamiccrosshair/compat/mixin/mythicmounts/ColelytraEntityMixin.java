package mod.crend.dynamiccrosshair.compat.mixin.mythicmounts;

import com.yahoo.chirpycricket.mythicmounts.entity.mounts.ColelytraEntity;
import net.minecraft.item.Item;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ColelytraEntity.class)
public class ColelytraEntityMixin {
    @Inject(method="isDyeIngredient", at=@At("HEAD"), cancellable = true)
    private void dynamicCrosshair$isDyeIngredient(Item item, CallbackInfoReturnable<DyeColor> cir) {
        // Make the method fail because it tries to force-cast ClientWorld to ServerWorld without checking.
        // This happens when we check if the item in the player's hand is a breeding item.
        cir.setReturnValue(null);
    }
}
