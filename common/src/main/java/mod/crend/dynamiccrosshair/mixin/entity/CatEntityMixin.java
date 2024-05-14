package mod.crend.dynamiccrosshair.mixin.entity;

import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairEntity;
import mod.crend.dynamiccrosshair.api.InteractionType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.util.DyeColor;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CatEntity.class)
public abstract class CatEntityMixin extends TameableEntityMixin implements DynamicCrosshairEntity {
	protected CatEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Shadow public abstract DyeColor getCollarColor();

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (this.isTamed() && this.isOwner(context.getPlayer())) {
			if (context.getItem() instanceof DyeItem dye && this.getCollarColor() != dye.getColor()) {
				return InteractionType.USE_ITEM_ON_ENTITY;
			}
			return InteractionType.INTERACT_WITH_ENTITY;
		}
		return super.dynamiccrosshair$compute(context);
	}
}
