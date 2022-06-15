package mod.crend.dynamiccrosshair.api;

import mod.crend.dynamiccrosshair.component.Crosshair;

public interface IEntityHandler {

    /**
     * Set the crosshair based on the targeted entity.
     *
     * @param context A context that is guaranteed to contain an item and a targeted entity
     * @return a Crosshair object overwriting the crosshair settings
     */
    Crosshair checkEntity(CrosshairContext context);
}
