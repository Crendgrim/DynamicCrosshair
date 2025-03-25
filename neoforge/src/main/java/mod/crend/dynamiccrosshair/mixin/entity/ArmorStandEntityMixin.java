package mod.crend.dynamiccrosshair.mixin.entity;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairEntity;

//? if >=1.20.6
/*import net.minecraft.component.DataComponentTypes;*/
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ArmorStandEntity.class)
public abstract class ArmorStandEntityMixin extends LivingEntity implements DynamicCrosshairEntity {
	@Shadow protected abstract EquipmentSlot getSlotFromPosition(Vec3d hitPos);

	@Shadow protected abstract boolean isSlotDisabled(EquipmentSlot slot);

	@Shadow public abstract boolean shouldShowArms();

	protected ArmorStandEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (context.isMainHand() || context.getItemStack(Hand.MAIN_HAND).isOf(Items.NAME_TAG)) {
			ItemStack itemStack = context.getItemStack();
			if (itemStack.isEmpty()) {
				Vec3d hitPos = context.getHitResult().getPos().subtract(this.getPos());
				EquipmentSlot slot = this.getSlotFromPosition(hitPos);
				if (this.hasStackEquipped(slot)) {
					return InteractionType.TAKE_ITEM_FROM_ENTITY;
				}
			} else if (itemStack.isOf(Items.NAME_TAG)) {
				if (
						//? if >=1.21.5 {
						/*itemStack.has(DataComponentTypes.CUSTOM_NAME)
						*///?} else if >=1.20.6 {
						/*itemStack.contains(DataComponentTypes.CUSTOM_NAME)
						*///?} else {
						itemStack.hasCustomName()
						//?}
				) {
					// rename armor stand
					return InteractionType.USE_ITEM_ON_ENTITY;
				}
			} else {
				EquipmentSlot slot = getPreferredEquipmentSlot(itemStack);
				if (!this.isSlotDisabled(slot) && (slot.getType() != EquipmentSlot.Type.HAND || this.shouldShowArms())) {
					if (!this.hasStackEquipped(slot) || itemStack.getCount() == 1) {
						return InteractionType.PLACE_ITEM_ON_ENTITY;
					}
				}
			}
		}
		return InteractionType.NO_ACTION;
	}
}
