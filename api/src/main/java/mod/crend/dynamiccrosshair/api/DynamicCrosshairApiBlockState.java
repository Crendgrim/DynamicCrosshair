package mod.crend.dynamiccrosshair.api;

import net.minecraft.block.BlockState;

public interface DynamicCrosshairApiBlockState {
	default boolean isAlwaysInteractable(BlockState blockState) { return false; }

	default boolean isAlwaysInteractableInCreativeMode(BlockState blockState) { return false; }

	default boolean isInteractable(BlockState blockState) { return false; }
}
