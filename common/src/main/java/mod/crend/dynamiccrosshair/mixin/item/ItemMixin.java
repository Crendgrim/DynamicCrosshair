package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairItem;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairRangedItem;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Objects;

@Mixin(Item.class)
public abstract class ItemMixin implements DynamicCrosshairItem, DynamicCrosshairRangedItem {

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		ItemStack itemStack = context.getItemStack();

		if (itemStack.contains(DataComponentTypes.FOOD)) {
			if (context.getPlayer().canConsume(false) || Objects.requireNonNull(itemStack.get(DataComponentTypes.FOOD)).canAlwaysEat()) {
				return InteractionType.CONSUME_ITEM;
			}
		}

		InteractionType interactionType = context.withApisUntilNonNull(api -> {
			if (api.isAlwaysUsable(itemStack)) return InteractionType.USE_ITEM;
			if (api.isAlwaysUsableOnBlock(itemStack) && context.isWithBlock()) return InteractionType.USE_ITEM_ON_BLOCK;
			if (api.isAlwaysUsableOnEntity(itemStack) && context.isWithEntity()) return InteractionType.USE_ITEM_ON_ENTITY;
			if (api.isAlwaysUsableOnMiss(itemStack) && !context.isTargeting()) return InteractionType.USE_ITEM;

			if (api.isTool(itemStack)) return InteractionType.TOOL;
			if (api.isThrowable(itemStack)) return InteractionType.THROW_ITEM;
			if (api.isShield(itemStack)) return InteractionType.SHIELD;
			if (api.isMeleeWeapon(itemStack)) return InteractionType.MELEE_WEAPON;
			if (api.isRangedWeapon(itemStack)) return InteractionType.RANGED_WEAPON;
			if (api.isBlock(itemStack)) return InteractionType.PLACE_BLOCK;

			return null;
		});
		if (interactionType == null) return InteractionType.EMPTY;
		return interactionType;
	}

	@Override
	public boolean dynamiccrosshair$isCharging(CrosshairContext context) {
		return context.isActiveItem() && context.getPlayer().getItemUseTimeLeft() > 0;
	}

	@Override
	public boolean dynamiccrosshair$isCharged(CrosshairContext context) {
		return true;
	}
}
