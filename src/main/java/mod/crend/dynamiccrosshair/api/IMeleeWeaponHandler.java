package mod.crend.dynamiccrosshair.api;

import mod.crend.dynamiccrosshair.component.Crosshair;

public interface IMeleeWeaponHandler {
    /**
     * Set the crosshair based on whether the player has a melee weapon equipped.
     *
     * @param context A context that is guaranteed to contain an item
     * @param canBeToolCrosshair If "true", delegate some decisions to the tool crosshair.
                                 For example, an axe will return "null" if targeting a block.
     * @return a Crosshair object overwriting the crosshair settings
     */
    Crosshair checkMeleeWeapon(CrosshairContext context, boolean canBeToolCrosshair);
}
