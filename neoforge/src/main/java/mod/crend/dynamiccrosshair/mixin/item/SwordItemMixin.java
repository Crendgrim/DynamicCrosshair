package mod.crend.dynamiccrosshair.mixin.item;

import org.spongepowered.asm.mixin.Mixin;

//? if >1.21.4 {
/*import mod.crend.dynamiccrosshairapi.VersionUtils;
/^@Mixin(value = VersionUtils.class, remap = false)
public class SwordItemMixin { }
^/*///?} else {

//? if <1.21.2 {
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
//?} else {
/*import mod.crend.dynamiccrosshair.mixin.DynamicCrosshairBaseItem;
*///?}
import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairItem;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import net.minecraft.item.SwordItem;

@Mixin(SwordItem.class)
public class SwordItemMixin extends /*? if <1.21.2 {*/ToolItem/*?} else {*//*DynamicCrosshairBaseItem*//*?}*/ implements DynamicCrosshairItem {
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
//?}
