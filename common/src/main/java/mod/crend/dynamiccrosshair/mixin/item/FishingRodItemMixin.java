package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairItem;
import mod.crend.dynamiccrosshair.api.InteractionType;
import net.minecraft.item.FishingRodItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FishingRodItem.class)
public class FishingRodItemMixin implements DynamicCrosshairItem {
	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (context.player.fishHook == null) {
			return InteractionType.RANGED_WEAPON;
		} else {
			return InteractionType.USE_ITEM;
		}
	}
}
