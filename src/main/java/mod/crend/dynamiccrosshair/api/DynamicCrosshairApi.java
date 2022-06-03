package mod.crend.dynamiccrosshair.api;

import mod.crend.dynamiccrosshair.component.Crosshair;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public interface DynamicCrosshairApi {

    String getNamespace();

    default IBlockBreakHandler getBlockBreakHandler() {
        return (player, itemStack, blockPos, blockState) -> null;
    }

    default IBlockInteractHandler getBlockInteractHandler() {
        return (player, itemStack, blockPos, blockState) -> null;
    }

    default IBlockItemHandler getBlockItemHandler() {
        return (player, itemStack) -> null;
    }

    default IEntityHandler getEntityHandler() {
        return (player, itemStack, entity) -> null;
    }

    default IRangedWeaponHandler getRangedWeaponHandler() {
        return (player, itemStack) -> null;
    }

    default IThrowableItemHandler getThrowableItemHandler() {
        return (player, itemStack) -> null;
    }

    default IToolItemHandler getToolItemHandler() {
        return (player, itemStack) -> null;
    }

    default IUsableItemHandler getUsableItemHandler() {
        return new IUsableItemHandler() {
            @Override
            public boolean isUsableItem(ItemStack itemStack) {
                return false;
            }

            @Override
            public Crosshair checkUsableItem(ClientPlayerEntity player, ItemStack itemStack) {
                return null;
            }

            @Override
            public Crosshair checkUsableItemOnBlock(ClientPlayerEntity player, ItemStack itemStack, BlockPos blockPos, BlockState blockState) {
                return null;
            }

            @Override
            public Crosshair checkUsableItemOnMiss(ClientPlayerEntity player, ItemStack itemStack) {
                return null;
            }
        };
    }

}