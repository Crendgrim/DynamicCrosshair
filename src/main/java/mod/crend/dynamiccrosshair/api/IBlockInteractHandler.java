package mod.crend.dynamiccrosshair.api;

import mod.crend.dynamiccrosshair.component.Crosshair;

public interface IBlockInteractHandler {
    /**
     * Set the crosshair based on whether the targeted block can be interacted with.
     *
     * @param context A context that is guaranteed to contain an item and a targeted block
     * @return a Crosshair object overwriting the crosshair settings
     */
    Crosshair checkBlockInteractable(CrosshairContext context);
}
