package mod.crend.dynamiccrosshair.mixin.item;

//? if >1.21.4 {
/*import mod.crend.dynamiccrosshairapi.VersionUtils;
import org.spongepowered.asm.mixin.Mixin;
@Mixin(VersionUtils.class)
public class SwordItemMixin {

}
*///?} else {
import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairItem;
import net.minecraft.item.SwordItem;


import org.spongepowered.asm.mixin.Mixin;

//? if <1.21.2 {
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
//?} else {
/*import mod.crend.dynamiccrosshair.mixin.DynamicCrosshairBaseItem;

*///?}

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