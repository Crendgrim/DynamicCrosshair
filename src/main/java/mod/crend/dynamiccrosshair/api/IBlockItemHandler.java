package mod.crend.dynamiccrosshair.api;

import mod.crend.dynamiccrosshair.component.Crosshair;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;

public interface IBlockItemHandler {
    /**
     * Set the crosshair based on whether the player has a block equipped.
     *
     * @param player The player entity
     * @param itemStack The item in the player's main hand
     * @return a Crosshair object overwriting the crosshair settings
     */
    Crosshair checkBlock(ClientPlayerEntity player, ItemStack itemStack);
}
