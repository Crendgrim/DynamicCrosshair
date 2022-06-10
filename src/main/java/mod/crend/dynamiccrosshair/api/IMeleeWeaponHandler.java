package mod.crend.dynamiccrosshair.api;

import mod.crend.dynamiccrosshair.component.Crosshair;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;

public interface IMeleeWeaponHandler {
    /**
     * Set the crosshair based on whether the player has a melee weapon equipped.
     *
     * @param player The player entity
     * @param itemStack The item in the player's main hand
     * @param canBeToolCrosshair If "true", delegate some decisions to the tool crosshair.
                                 For example, an axe will return "null" if targeting a block.
     * @return a Crosshair object overwriting the crosshair settings
     */
    Crosshair checkMeleeWeapon(ClientPlayerEntity player, ItemStack itemStack, boolean canBeToolCrosshair);
}
