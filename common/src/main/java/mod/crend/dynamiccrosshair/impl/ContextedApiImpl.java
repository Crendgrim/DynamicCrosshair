package mod.crend.dynamiccrosshair.impl;

import mod.crend.dynamiccrosshair.component.CrosshairHandler;
import mod.crend.dynamiccrosshairapi.DynamicCrosshairApi;
import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.exception.CrosshairContextChange;
import mod.crend.dynamiccrosshairapi.internal.ContextedApi;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;

import java.util.function.Function;

public class ContextedApiImpl implements ContextedApi {

	private final CrosshairContext context;

	public ContextedApiImpl(CrosshairContext context) {
		this.context = context;
	}

	@Override
	public boolean test(Function<DynamicCrosshairApi, Boolean> lambda) {
		for (DynamicCrosshairApi api : context.apis()) {
			try {
				if (lambda.apply(api)) {
					return true;
				}
			} catch (NoSuchMethodError | NoSuchFieldError | NoClassDefFoundError | RuntimeException e) {
				if (e instanceof CrosshairContextChange) throw e;
				CrosshairHandler.LOGGER.error("Exception occurred during evaluation of API {}", api.getModId(), e);
			}
		}
		return false;
	}


	@Override
	public boolean isAlwaysInteractable(BlockState blockState) {
		return test(api -> api.isAlwaysInteractable(blockState));
	}

	@Override
	public boolean isAlwaysInteractableInCreativeMode(BlockState blockState) {
		return test(api -> api.isAlwaysInteractableInCreativeMode(blockState));
	}

	@Override
	public boolean isInteractable(BlockState blockState) {
		return test(api -> api.isInteractable(blockState));
	}


	@Override
	public boolean isAlwaysInteractable(EntityType<?> entityType) {
		return test(api -> api.isAlwaysInteractable(entityType));
	}

	@Override
	public boolean isInteractable(EntityType<?> entityType) {
		return test(api -> api.isInteractable(entityType));
	}


	@Override
	public boolean isAlwaysUsable(ItemStack itemStack) {
		return test(api -> api.isAlwaysUsable(itemStack));
	}

	@Override
	public boolean isAlwaysUsableOnBlock(ItemStack itemStack) {
		return test(api -> api.isAlwaysUsableOnBlock(itemStack));
	}

	@Override
	public boolean isAlwaysUsableOnEntity(ItemStack itemStack) {
		return test(api -> api.isAlwaysUsableOnEntity(itemStack));
	}

	@Override
	public boolean isAlwaysUsableOnMiss(ItemStack itemStack) {
		return test(api -> api.isAlwaysUsableOnMiss(itemStack));
	}

	@Override
	public boolean isUsable(ItemStack itemStack) {
		return test(api -> api.isUsable(itemStack));
	}

	@Override
	public boolean isBlock(ItemStack itemStack) {
		return test(api -> api.isBlock(itemStack));
	}

	@Override
	public boolean isMeleeWeapon(ItemStack itemStack) {
		return test(api -> api.isMeleeWeapon(itemStack));
	}

	@Override
	public boolean isRangedWeapon(ItemStack itemStack) {
		return test(api -> api.isRangedWeapon(itemStack));
	}

	@Override
	public boolean isShield(ItemStack itemStack) {
		return test(api -> api.isShield(itemStack));
	}

	@Override
	public boolean isThrowable(ItemStack itemStack) {
		return test(api -> api.isThrowable(itemStack));
	}

	@Override
	public boolean isTool(ItemStack itemStack) {
		return test(api -> api.isTool(itemStack));
	}
}
