package mod.crend.dynamiccrosshair.handler;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.api.IBlockItemHandler;
import mod.crend.dynamiccrosshair.api.IRangedWeaponHandler;
import mod.crend.dynamiccrosshair.api.IThrowableItemHandler;
import mod.crend.dynamiccrosshair.api.IToolItemHandler;
import mod.crend.dynamiccrosshair.component.Crosshair;
import mod.crend.dynamiccrosshair.component.ModifierUse;
import mod.crend.dynamiccrosshair.component.Style;
import mod.crend.dynamiccrosshair.config.BlockCrosshairPolicy;
import mod.crend.dynamiccrosshair.mixin.IBlockItemMixin;
import mod.crend.dynamiccrosshair.mixin.IItemMixin;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;

public class VanillaItemHandler implements IToolItemHandler, IThrowableItemHandler, IRangedWeaponHandler, IBlockItemHandler {
    @Override
    public Crosshair checkTool(ClientPlayerEntity player, ItemStack itemStack) {
        Item handItem = itemStack.getItem();
        if (       handItem instanceof ToolItem
                || handItem instanceof FlintAndSteelItem
                || handItem instanceof ShearsItem
        ) {
            return Crosshair.TOOL;
        }
        if (handItem instanceof FishingRodItem) {
            return new Crosshair(Style.HoldingTool, ModifierUse.USE_ITEM);
        }
        return null;
    }

    @Override
    public Crosshair checkThrowable(ClientPlayerEntity player, ItemStack itemStack) {
        Item handItem = itemStack.getItem();
        if (       handItem instanceof EggItem
                || handItem instanceof SnowballItem
                || handItem instanceof ThrowablePotionItem
                || handItem instanceof ExperienceBottleItem
                || (handItem instanceof EnderPearlItem && !player.getItemCooldownManager().isCoolingDown(handItem))
        ) {
            return Crosshair.THROWABLE;
        }
        return null;
    }

    @Override
    public Crosshair checkRangedWeapon(ClientPlayerEntity player, ItemStack itemStack) {
        Item handItem = itemStack.getItem();
        if (handItem instanceof RangedWeaponItem || handItem instanceof TridentItem) {
            return Crosshair.RANGED_WEAPON;
        }
        return null;
    }

    @Override
    public Crosshair checkBlock(ClientPlayerEntity player, ItemStack itemStack) {
        Item handItem = itemStack.getItem();
        HitResult hitResult = MinecraftClient.getInstance().crosshairTarget;
        if (handItem instanceof BlockItem) {
            if (DynamicCrosshair.config.dynamicCrosshairHoldingBlock() == BlockCrosshairPolicy.IfInteractable) {
                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    IBlockItemMixin blockItem = (IBlockItemMixin) handItem;
                    ItemPlacementContext itemPlacementContext = new ItemPlacementContext(player, player.getActiveHand(), itemStack, (BlockHitResult) hitResult);
                    BlockState blockState = blockItem.invokeGetPlacementState(itemPlacementContext);
                    if (blockState != null && blockItem.invokeCanPlace(itemPlacementContext, blockState)) return Crosshair.HOLDING_BLOCK;
                }
            } else return Crosshair.HOLDING_BLOCK;
        }
        if (handItem instanceof ArmorStandItem) return Crosshair.HOLDING_BLOCK;
        if (handItem instanceof MinecartItem) {
            if (DynamicCrosshair.config.dynamicCrosshairHoldingBlock() == BlockCrosshairPolicy.IfInteractable) {
                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
                    BlockState blockState = MinecraftClient.getInstance().world.getBlockState(blockPos);
                    if (blockState.isIn(BlockTags.RAILS)) return Crosshair.HOLDING_BLOCK;
                }
            } else return Crosshair.HOLDING_BLOCK;
        }
        if (handItem instanceof EndCrystalItem) {
            if (DynamicCrosshair.config.dynamicCrosshairHoldingBlock() == BlockCrosshairPolicy.IfInteractable) {
                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
                    BlockState blockState = MinecraftClient.getInstance().world.getBlockState(blockPos);
                    Block block = blockState.getBlock();
                    if ((block == Blocks.OBSIDIAN || block == Blocks.BEDROCK) && MinecraftClient.getInstance().world.isAir(blockPos.up())) {
                        return Crosshair.HOLDING_BLOCK;
                    }
                }
            } else return Crosshair.HOLDING_BLOCK;
        }
        if (handItem instanceof BoatItem) {
            BlockHitResult boatHitResult = IItemMixin.invokeRaycast(MinecraftClient.getInstance().world, player, RaycastContext.FluidHandling.ANY);
            if (boatHitResult.getType() == HitResult.Type.BLOCK) {
                return Crosshair.HOLDING_BLOCK;
            }
        }
        return null;
    }
}
