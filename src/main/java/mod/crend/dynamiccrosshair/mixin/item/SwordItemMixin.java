package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairItem;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import net.minecraft.item.SwordItem;
//? if <1.21.2 {
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
//?}
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SwordItem.class)
public class SwordItemMixin extends /*? if <1.21.2 {*/ToolItem/*?} else {*//*ItemMixin*//*?}*/ implements DynamicCrosshairItem {
	//? if <1.21.2 {
	public SwordItemMixin(ToolMaterial material, Settings settings) {
		super(material, settings);
	}
	//?}

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		return InteractionType.MELEE_WEAPON;
	}
}
