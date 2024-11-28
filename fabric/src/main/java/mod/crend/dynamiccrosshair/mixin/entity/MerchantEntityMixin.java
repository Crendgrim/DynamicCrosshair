package mod.crend.dynamiccrosshair.mixin.entity;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairEntity;
import net.minecraft.entity.passive.MerchantEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MerchantEntity.class)
public abstract class MerchantEntityMixin extends MobEntityMixin implements DynamicCrosshairEntity {
	@Shadow public abstract boolean hasCustomer();

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (!this.hasCustomer() && !this.isSleeping()) {
			return InteractionType.INTERACT_WITH_ENTITY;
		}
		return super.dynamiccrosshair$compute(context);
	}
}
