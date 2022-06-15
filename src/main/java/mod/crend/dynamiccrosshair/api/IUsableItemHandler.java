package mod.crend.dynamiccrosshair.api;

import mod.crend.dynamiccrosshair.component.Crosshair;
import net.minecraft.item.ItemStack;

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
     * @param context A context that is guaranteed to contain an item
     * @return a Crosshair object overwriting the crosshair settings
     */
    default Crosshair checkUsableItem(CrosshairContext context) { return null; }

    /**
     * Set the crosshair based on whether the given item is usable when targeting a block.
     *
     * @param context A context that is guaranteed to contain an item and a targeted block
     * @return a Crosshair object overwriting the crosshair settings
     */
    default Crosshair checkUsableItemOnBlock(CrosshairContext context) { return null; }

    /**
     * Set the crosshair based on whether the given item is usable when targeting nothing.
     *
     * @param context A context that is guaranteed to contain an item
     * @return a Crosshair object overwriting the crosshair settings
     */
    default Crosshair checkUsableItemOnMiss(CrosshairContext context) { return null; }
}
