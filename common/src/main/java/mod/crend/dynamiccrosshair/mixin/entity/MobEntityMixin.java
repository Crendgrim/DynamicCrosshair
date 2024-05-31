package mod.crend.dynamiccrosshair.mixin.entity;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairEntity;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity implements DynamicCrosshairEntity {

	protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Shadow public abstract boolean canBeLeashedBy(PlayerEntity player);

	@Shadow public abstract boolean hasArmorSlot();

	@Shadow public abstract boolean isHorseArmor(ItemStack stack);

	@Shadow public abstract boolean isWearingBodyArmor();

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		Item handItem = context.getItem();
		if (handItem instanceof SpawnEggItem) return InteractionType.USE_ITEM_ON_ENTITY;

		if (handItem == Items.LEAD) {
			if (this.canBeLeashedBy(context.getPlayer())) {
				return InteractionType.USE_ITEM_ON_ENTITY;
			}
			return InteractionType.NO_ACTION;
		}
		if (handItem == Items.NAME_TAG) {
			if (context.getItemStack().contains(DataComponentTypes.CUSTOM_NAME)) {
				// rename armor stand
				return InteractionType.USE_ITEM_ON_ENTITY;
			}

		}
		return InteractionType.EMPTY;
	}
}
