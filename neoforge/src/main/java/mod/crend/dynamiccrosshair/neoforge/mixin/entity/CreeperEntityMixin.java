package mod.crend.dynamiccrosshair.neoforge.mixin.entity;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin extends MobEntityMixin implements DynamicCrosshairEntity {
	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (context.getItem() == Items.FLINT_AND_STEEL) {
			return InteractionType.USE_ITEM_ON_ENTITY;
		}
		return super.dynamiccrosshair$compute(context);
	}
}
