package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairItem;
import mod.crend.dynamiccrosshair.api.InteractionType;
import net.minecraft.item.BoatItem;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BoatItem.class)
public class BoatItemMixin implements DynamicCrosshairItem {
	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		BlockHitResult boatHitResult = context.raycastWithFluid();
		if (boatHitResult.getType() == HitResult.Type.BLOCK) {
			return InteractionType.PLACE_ENTITY;
		}
		return InteractionType.NO_ACTION;
	}
}
