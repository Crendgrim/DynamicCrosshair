package mod.crend.dynamiccrosshair.compat.arcanus;

import dev.cammiescorner.arcanus.Arcanus;
import dev.cammiescorner.arcanus.common.blocks.DisplayCaseBlock;
import dev.cammiescorner.arcanus.common.blocks.FillableBookshelfBlock;
import dev.cammiescorner.arcanus.common.blocks.entities.DisplayCaseBlockEntity;
import dev.cammiescorner.arcanus.common.items.WandItem;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairApi;
import mod.crend.dynamiccrosshair.api.IBlockInteractHandler;
import mod.crend.dynamiccrosshair.api.IToolItemHandler;
import mod.crend.dynamiccrosshair.component.Crosshair;
import mod.crend.dynamiccrosshair.component.ModifierUse;
import mod.crend.dynamiccrosshair.component.Style;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;

public class ArcanusApiImpl implements DynamicCrosshairApi {

    @Override
    public String getNamespace() {
        return Arcanus.MOD_ID;
    }

    @Override
    public IBlockInteractHandler getBlockInteractHandler() {
        return (player, itemStack, blockPos, blockState) -> {
            Block block = blockState.getBlock();
            if (block instanceof DisplayCaseBlock) {
                if (blockState.get(DisplayCaseBlock.OPEN)) {
                    if (player.isSneaking()) {
                        return Crosshair.INTERACTABLE;
                    }
                    BlockEntity blockEntity = MinecraftClient.getInstance().world.getBlockEntity(blockPos);
                    if (((DisplayCaseBlockEntity) blockEntity).isEmpty()) {
                        if (itemStack.isEmpty()) {
                            return Crosshair.NONE.withFlag(Crosshair.Flag.FixedModifierUse);
                        }
                    }
                    return Crosshair.USE_ITEM;
                }
                if (player.isSneaking()) {
                    return Crosshair.INTERACTABLE;
                }
                return Crosshair.NONE.withFlag(Crosshair.Flag.FixedModifierUse);
            }
            if (block instanceof FillableBookshelfBlock) {
                return Crosshair.INTERACTABLE;
            }

            return null;
        };
    }

    @Override
    public IToolItemHandler getToolItemHandler() {
        return (player, itemStack) -> {
            Item item = itemStack.getItem();
            if (item instanceof WandItem) {
                return new Crosshair(Style.HoldingTool, ModifierUse.USE_ITEM);
            }

            return null;
        };
    }
}
