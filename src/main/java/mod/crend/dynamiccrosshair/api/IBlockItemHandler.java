package mod.crend.dynamiccrosshair.api;

import mod.crend.dynamiccrosshair.component.Crosshair;

public interface IBlockItemHandler {
    /**
     * Set the crosshair based on whether the player has a block equipped.
     *
     * @param context A context that is guaranteed to contain an item
     * @return a Crosshair object overwriting the crosshair settings
     */
    Crosshair checkBlock(CrosshairContext context);
}
