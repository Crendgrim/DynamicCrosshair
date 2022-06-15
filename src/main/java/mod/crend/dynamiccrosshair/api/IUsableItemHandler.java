package mod.crend.dynamiccrosshair.api;

import mod.crend.dynamiccrosshair.component.Crosshair;

public interface IUsableItemHandler {
    /**
     * Set the crosshair based on whether the given item is usable.
     *
     * This method is called regardless of crosshair target (entity, block, miss), and should be used to check for
     * always usable items such as food.
     *
     * @param context A context that is guaranteed to contain an item.
     *                If `context.withBlock()` return true, it also contains a targeted block.
     * @return a Crosshair object overwriting the crosshair settings
     */
    Crosshair checkUsableItem(CrosshairContext context);
}
