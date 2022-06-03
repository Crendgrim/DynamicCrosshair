package mod.crend.dynamiccrosshair.handler;

import mod.crend.dynamiccrosshair.component.Crosshair;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;

public interface IRangedWeaponHandler {
    /**
     * Set the crosshair based on whether the player has a ranged weapon equipped.
     *
     * @param player The player entity
     * @param itemStack The item in the player's main hand
     * @return a Crosshair object overwriting the crosshair settings
     */
    Crosshair checkRangedWeapon(ClientPlayerEntity player, ItemStack itemStack);
}
