package mod.crend.dynamiccrosshair.neoforge.mixin.item;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairItem;
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
