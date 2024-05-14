package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairItem;
import mod.crend.dynamiccrosshair.api.InteractionType;
import net.minecraft.item.FireworkRocketItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FireworkRocketItem.class)
public class FireworkRocketItemMixin implements DynamicCrosshairItem {
	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (context.isWithBlock() || context.player.isFallFlying()) {
			return InteractionType.USE_ITEM;
		}
		return InteractionType.NO_ACTION;
	}
}
