package mod.crend.dynamiccrosshair.mixin.entity;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairEntity;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ParrotEntity.class)
public abstract class ParrotEntityMixin extends TameableEntityMixin implements DynamicCrosshairEntity {
	protected ParrotEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Shadow public abstract boolean isInAir();

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (!this.isTamed() && context.getItemStack().isIn(ItemTags.PARROT_FOOD)) {
			return InteractionType.USE_ITEM_ON_ENTITY;
		}
		if (context.getItem() == Items.COOKIE) {
			// :'(
			return InteractionType.USE_ITEM_ON_ENTITY;
		}
		if (!this.isInAir() && this.isTamed() && this.isOwner(context.getPlayer())) {
			return InteractionType.INTERACT_WITH_ENTITY;
		}
		return InteractionType.NO_ACTION;
	}
}
