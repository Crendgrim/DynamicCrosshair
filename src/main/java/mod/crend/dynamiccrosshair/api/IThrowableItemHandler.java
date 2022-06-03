package mod.crend.dynamiccrosshair.api;

import mod.crend.dynamiccrosshair.component.Crosshair;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;

public interface IThrowableItemHandler {
    /**
     * Set the crosshair based on whether the player has a throwable item equipped.
     *
     * @param player The player entity
     * @param itemStack The item in the player's main hand
     * @return a Crosshair object overwriting the crosshair settings
     */
    Crosshair checkThrowable(ClientPlayerEntity player, ItemStack itemStack);
}
