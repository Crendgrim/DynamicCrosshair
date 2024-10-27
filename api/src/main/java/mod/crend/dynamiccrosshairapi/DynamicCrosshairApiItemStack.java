package mod.crend.dynamiccrosshairapi;

import net.minecraft.item.ItemStack;

public interface DynamicCrosshairApiItemStack {
	default boolean isAlwaysUsable(ItemStack itemStack) { return false; }

	default boolean isAlwaysUsableOnBlock(ItemStack itemStack) { return false; }

	default boolean isAlwaysUsableOnEntity(ItemStack itemStack) { return false; }

	default boolean isAlwaysUsableOnMiss(ItemStack itemStack) { return false; }

	default boolean isUsable(ItemStack itemStack) { return false; }

	default boolean isBlock(ItemStack itemStack) { return false; }

	default boolean isMeleeWeapon(ItemStack itemStack) { return false; }

	default boolean isRangedWeapon(ItemStack itemStack) { return false; }

	default boolean isShield(ItemStack itemStack) { return false; }

	default boolean isThrowable(ItemStack itemStack) { return false; }

	default boolean isTool(ItemStack itemStack) { return false; }
}
