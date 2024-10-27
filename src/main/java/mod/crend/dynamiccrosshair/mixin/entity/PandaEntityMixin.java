package mod.crend.dynamiccrosshair.mixin.entity;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairEntity;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import net.minecraft.entity.passive.PandaEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PandaEntity.class)
public abstract class PandaEntityMixin extends MobEntityMixin implements DynamicCrosshairEntity {
	@Shadow public abstract boolean isLyingOnBack();

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (this.isLyingOnBack()) {
			return InteractionType.INTERACT_WITH_ENTITY;
		}
		return super.dynamiccrosshair$compute(context);
	}
}
