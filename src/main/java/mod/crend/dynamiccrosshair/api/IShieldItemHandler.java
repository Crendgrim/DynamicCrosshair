package mod.crend.dynamiccrosshair.api;

import mod.crend.dynamiccrosshair.component.Crosshair;

public interface IShieldItemHandler {
    /**
     * Set the crosshair based on whether the player has a shield equipped.
     *
     * @param context A context that is guaranteed to contain an item
     * @return a Crosshair object overwriting the crosshair settings
     */
    Crosshair checkShield(CrosshairContext context);
}
