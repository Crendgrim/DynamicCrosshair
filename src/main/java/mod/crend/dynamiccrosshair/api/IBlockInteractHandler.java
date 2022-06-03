package mod.crend.dynamiccrosshair.api;

import mod.crend.dynamiccrosshair.component.Crosshair;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public interface IBlockInteractHandler {
    /**
     * Set the crosshair based on whether the targeted block can be interacted with.
     *
     * @param player The player entity
     * @param itemStack The tool in the player's main hand
     * @param blockPos The position in the world of the targeted block
     * @param blockState The targeted block's state
     * @return a Crosshair object overwriting the crosshair settings
     */
    Crosshair checkBlockInteractable(ClientPlayerEntity player, ItemStack itemStack, BlockPos blockPos, BlockState blockState);
}
