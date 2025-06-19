package mod.crend.dynamiccrosshair.mixin.entity;

import mod.crend.dynamiccrosshairapi.VersionUtils;
import org.spongepowered.asm.mixin.Mixin;

//? if >=1.20.5 {
import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairEntity;
import net.minecraft.entity.passive.ArmadilloEntity;
import net.minecraft.item.Items;
//?}

//? if >=1.20.5 {
@Mixin(ArmadilloEntity.class)
public abstract class ArmadilloEntityMixin extends AnimalEntityMixin implements DynamicCrosshairEntity {
	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (context.getItem() == Items.BRUSH) {
			return InteractionType.USE_ITEM_ON_ENTITY;
		}
		return super.dynamiccrosshair$compute(context);
	}
}
//?} else {
/*@Mixin(VersionUtils.class)
public interface ArmadilloEntityMixin {}
*///?}
