package mod.crend.dynamiccrosshair.api;

import mod.crend.dynamiccrosshair.component.Crosshair;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public interface IUsableItemHandler {

    /**
     * Checks whether the given item is usable.
     *
     * This method is called in a context of "always show crosshair for usable items", so no further
     * restrictions over "is this item type usable" should take place here.
     *
     * @param itemStack The tool in the player's main hand
     * @return a Crosshair object overwriting the crosshair settings
     */
    default boolean isUsableItem(ItemStack itemStack) { return false; }

    /**
     * Set the crosshair based on whether the given item is usable.
     *
     * This method is called regardless of crosshair target (entity, block, miss), and should be used to check for
     * always usable items such as food.
     *
     * @param player The player entity
     * @param itemStack The item in the player's main hand
     * @return a Crosshair object overwriting the crosshair settings
     */
    default Crosshair checkUsableItem(ClientPlayerEntity player, ItemStack itemStack) { return null; }

    /**
     * Set the crosshair based on whether the given item is usable when targeting a block.
     *
     * @param player The player entity
     * @param itemStack The item in the player's main hand
     * @param blockPos The position of the targeted block in the world
     * @param blockState The targeted block's state
     * @return a Crosshair object overwriting the crosshair settings
     */
    default Crosshair checkUsableItemOnBlock(ClientPlayerEntity player, ItemStack itemStack, BlockPos blockPos, BlockState blockState) { return null; }

    /**
     * Set the crosshair based on whether the given item is usable when targeting nothing.
     *
     * @param player The player entity
     * @param itemStack The item in the player's main hand
     * @return a Crosshair object overwriting the crosshair settings
     */
    default Crosshair checkUsableItemOnMiss(ClientPlayerEntity player, ItemStack itemStack) { return null; }
}
