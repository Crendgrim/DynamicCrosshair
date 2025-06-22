package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairItem;
import net.minecraft.item.BundleItem;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;

//? if >=1.20.6 {
/*import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;

*///?}

@Mixin(BundleItem.class)
public class BundleItemMixin implements DynamicCrosshairItem {
	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		//? if >=1.20.6 {
		/*BundleContentsComponent bundleContentsComponent = context.getItemStack().get(DataComponentTypes.BUNDLE_CONTENTS);
		if (bundleContentsComponent != null && !bundleContentsComponent.isEmpty()) {
			return InteractionType.USE_ITEM;
		}*///?} else {
		NbtCompound nbtCompound = context.getItemStack().getOrCreateNbt();
		if (nbtCompound.contains("Items")) {
			return InteractionType.USE_ITEM;
		}
		//?}
		return InteractionType.NO_ACTION;
	}
}
