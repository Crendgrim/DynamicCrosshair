package mod.crend.dynamiccrosshair.mixin.entity;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairEntity;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AllayEntity.class)
public abstract class AllayEntityMixin extends MobEntityMixin implements DynamicCrosshairEntity {

	protected AllayEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		ItemStack allayItemStack = this.getStackInHand(Hand.MAIN_HAND);
		ItemStack handItemStack = context.getItemStack();
		if (allayItemStack.isEmpty() && !handItemStack.isEmpty()) {
			return InteractionType.PLACE_ITEM_ON_ENTITY;
		}
		if (!allayItemStack.isEmpty() && context.isMainHand() && handItemStack.isEmpty()) {
			return InteractionType.TAKE_ITEM_FROM_ENTITY;
		}
		return super.dynamiccrosshair$compute(context);
	}
}
