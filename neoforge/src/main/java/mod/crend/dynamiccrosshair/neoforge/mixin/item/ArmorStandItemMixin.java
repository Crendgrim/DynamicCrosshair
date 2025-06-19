package mod.crend.dynamiccrosshair.neoforge.mixin.item;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairItem;
import net.minecraft.item.ArmorStandItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ArmorStandItem.class)
public class ArmorStandItemMixin implements DynamicCrosshairItem {
	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		return InteractionType.PLACE_ENTITY;
	}
}
