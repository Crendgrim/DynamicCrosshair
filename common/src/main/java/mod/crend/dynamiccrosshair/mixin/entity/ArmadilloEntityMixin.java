package mod.crend.dynamiccrosshair.mixin.entity;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairEntity;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import net.minecraft.entity.passive.ArmadilloEntity;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;

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
