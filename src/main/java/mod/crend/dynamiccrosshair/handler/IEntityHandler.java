package mod.crend.dynamiccrosshair.handler;

import mod.crend.dynamiccrosshair.component.Crosshair;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public interface IEntityHandler {

    /**
     * Set the crosshair based on the targeted entity.
     *
     * @param player The player entity
     * @param entity The targeted entity
     * @return a Crosshair object overwriting the crosshair settings
     */
    Crosshair checkEntity(ClientPlayerEntity player, ItemStack itemStack, Entity entity);
}
