package mod.crend.dynamiccrosshair.handler;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.component.Crosshair;
import mod.crend.dynamiccrosshair.component.ModifierUse;
import mod.crend.dynamiccrosshair.component.Style;
import mod.crend.dynamiccrosshair.config.BlockCrosshairPolicy;
import mod.crend.dynamiccrosshair.mixin.IAxeItemMixin;
import mod.crend.dynamiccrosshair.mixin.IHoeItemMixin;
import mod.crend.dynamiccrosshair.mixin.IItemMixin;
import mod.crend.dynamiccrosshair.mixin.IShovelItemMixin;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;

public class VanillaUsableItemHandler implements IUsableItemHandler {

    @Override
    public boolean isUsableItem(ItemStack handItemStack) {
        Item handItem = handItemStack.getItem();
        return (handItem.isFood()
                || handItem.getUseAction(handItemStack) == UseAction.DRINK
                || handItem instanceof SpawnEggItem
                || handItem instanceof FireChargeItem
                || handItem instanceof MusicDiscItem
                || handItem instanceof HoneycombItem
                || handItem instanceof EnderEyeItem
                || handItem instanceof GlassBottleItem
                || handItem instanceof PotionItem
                || handItem instanceof BucketItem
                || handItem instanceof BoneMealItem
                || handItem instanceof WritableBookItem
                || handItem instanceof WrittenBookItem);
    }

    @Override
    public Crosshair checkUsableItem(ClientPlayerEntity player, ItemStack handItemStack) {
        Item handItem = handItemStack.getItem();
        // Enable crosshair on food and drinks also when not targeting if "when interactable" is chosen
        if (handItem.isFood()) {
            if (handItem instanceof ChorusFruitItem) {
                if (!player.getItemCooldownManager().isCoolingDown(handItem)) return Crosshair.USE_ITEM;
            } else if (player.getHungerManager().isNotFull() || handItem.getFoodComponent().isAlwaysEdible())
                return Crosshair.USE_ITEM;
        }
        if (handItem.getUseAction(handItemStack) == UseAction.DRINK) return Crosshair.USE_ITEM;

        // Liquid interactions ignore block hit, cast extra rays
        // This getting called for entity hits is on purpose, as liquid interactions overwrite entity interactions
        if (handItem instanceof GlassBottleItem) {
            BlockHitResult blockHitResult = IItemMixin.invokeRaycast(MinecraftClient.getInstance().world, MinecraftClient.getInstance().player, RaycastContext.FluidHandling.ANY);
            if (MinecraftClient.getInstance().world.getFluidState(blockHitResult.getBlockPos()).isIn(FluidTags.WATER))
                return Crosshair.USE_ITEM;
        }
        if (handItem instanceof BucketItem) {
            BlockHitResult blockHitResult = IItemMixin.invokeRaycast(MinecraftClient.getInstance().world, MinecraftClient.getInstance().player, RaycastContext.FluidHandling.SOURCE_ONLY);
            if (!MinecraftClient.getInstance().world.getFluidState(blockHitResult.getBlockPos()).isEmpty())
                return Crosshair.USE_ITEM;
        }
        return null;
    }

    @Override
    public Crosshair checkUsableItemOnBlock(ClientPlayerEntity player, ItemStack handItemStack, BlockPos blockPos, BlockState blockState) {
        Item handItem = handItemStack.getItem();

        if (handItem instanceof SpawnEggItem) return Crosshair.USE_ITEM;

        Block block = blockState.getBlock();
        if (handItem instanceof ToolItem) {
            if (handItem instanceof AxeItem) {
                if (IAxeItemMixin.getSTRIPPED_BLOCKS().get(blockState.getBlock()) != null
                        || Oxidizable.getDecreasedOxidationBlock(blockState.getBlock()).isPresent()
                        || HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get().get(blockState.getBlock()) != null) {
                    return Crosshair.USE_ITEM;
                }
            } else if (handItem instanceof ShovelItem) {
                if (IShovelItemMixin.getPATH_STATES().get(blockState.getBlock()) != null) {
                    return Crosshair.USE_ITEM;
                }
            } else if (handItem instanceof HoeItem) {
                if (IHoeItemMixin.getTILLING_ACTIONS().get(blockState.getBlock()) != null) {
                    return Crosshair.USE_ITEM;
                }
            }
            return null;
        }
        if (handItem instanceof ShearsItem) {
            if (blockState.getBlock() instanceof AbstractPlantStemBlock && !((AbstractPlantStemBlock)blockState.getBlock()).hasMaxAge(blockState)) {
                return Crosshair.USE_ITEM;
            }
            if (!player.shouldCancelInteraction() && blockState.getBlock() instanceof BeehiveBlock && blockState.get(BeehiveBlock.HONEY_LEVEL) >= 5) {
                return Crosshair.USE_ITEM;
            }
            return null;
        }

        if (handItem instanceof FlintAndSteelItem) return Crosshair.USE_ITEM;
        if (handItem instanceof FireChargeItem) return Crosshair.USE_ITEM;
        if (handItem instanceof MusicDiscItem && block.equals(Blocks.JUKEBOX)) return Crosshair.USE_ITEM;
        if (handItem instanceof HoneycombItem && HoneycombItem.UNWAXED_TO_WAXED_BLOCKS.get().get(block) != null)
            return Crosshair.USE_ITEM;
        if (handItem instanceof EnderEyeItem && block.equals(Blocks.END_PORTAL_FRAME)) return Crosshair.USE_ITEM;
        if (handItem instanceof GlassBottleItem) {
            if (block.equals(Blocks.WATER_CAULDRON) && !player.shouldCancelInteraction()) return Crosshair.USE_ITEM;
            if (block instanceof BeehiveBlock && blockState.get(BeehiveBlock.HONEY_LEVEL) >= 5 && !player.shouldCancelInteraction()) return Crosshair.USE_ITEM;
            return null;
        }
        if (handItem instanceof PotionItem && PotionUtil.getPotion(handItemStack) == Potions.WATER) {
            if (block.equals(Blocks.CAULDRON) && !player.shouldCancelInteraction()) return Crosshair.USE_ITEM;
            return null;
        }
        if (handItem instanceof BucketItem) {
            if (handItem instanceof EntityBucketItem) {
                if (DynamicCrosshair.config.dynamicCrosshairHoldingBlock() != BlockCrosshairPolicy.Disabled) {
                    return new Crosshair(Style.HoldingBlock, ModifierUse.USE_ITEM);
                }
                return Crosshair.USE_ITEM;
            }
            if (block.equals(Blocks.WATER_CAULDRON) && !player.shouldCancelInteraction()) return Crosshair.USE_ITEM;
            if (block.equals(Blocks.LAVA_CAULDRON) && !player.shouldCancelInteraction()) return Crosshair.USE_ITEM;
            if (block.equals(Blocks.POWDER_SNOW_CAULDRON) && !player.shouldCancelInteraction()) return Crosshair.USE_ITEM;
            if (handItem == Items.WATER_BUCKET || handItem == Items.LAVA_BUCKET) {
                if (block.equals(Blocks.CAULDRON) && !player.shouldCancelInteraction()) return Crosshair.USE_ITEM;
                if (DynamicCrosshair.config.dynamicCrosshairHoldingBlock() != BlockCrosshairPolicy.Disabled) {
                    return Crosshair.HOLDING_BLOCK;
                }
                return Crosshair.USE_ITEM;
            } else if (block.equals(Blocks.POWDER_SNOW)) return Crosshair.USE_ITEM;
        }
        if (handItem instanceof PowderSnowBucketItem) {
            if (block.equals(Blocks.CAULDRON) && !player.shouldCancelInteraction()) return Crosshair.USE_ITEM;
            if (block.equals(Blocks.WATER_CAULDRON) && !player.shouldCancelInteraction()) return Crosshair.USE_ITEM;
            if (block.equals(Blocks.LAVA_CAULDRON) && !player.shouldCancelInteraction()) return Crosshair.USE_ITEM;
            if (block.equals(Blocks.POWDER_SNOW_CAULDRON) && !player.shouldCancelInteraction()) return Crosshair.USE_ITEM;
            return null; // crosshair will be updated later because PowderSnowBucketItem is also a BlockItem
        }
        if (handItem instanceof BoneMealItem) {
            if (BoneMealItem.useOnFertilizable(handItemStack, MinecraftClient.getInstance().world, blockPos)) {
                return Crosshair.USE_ITEM;
            }
            if (BoneMealItem.useOnGround(handItemStack, MinecraftClient.getInstance().world, blockPos, null)) {
                return Crosshair.USE_ITEM;
            }
            return null;
        }
        if (handItem instanceof CompassItem) {
            if (block == Blocks.LODESTONE) {
                return Crosshair.USE_ITEM;
            }
        }
        if (handItem instanceof WritableBookItem || handItem instanceof WrittenBookItem) return Crosshair.USE_ITEM;

        if (block instanceof BlockWithEntity) {
            if (block instanceof AbstractSignBlock) {
                if (!player.shouldCancelInteraction() && (handItem instanceof DyeItem || handItem.equals(Items.GLOW_INK_SAC) || handItem.equals(Items.INK_SAC))) {
                    BlockEntity blockEntity = MinecraftClient.getInstance().world.getBlockEntity(blockPos);
                    if (blockEntity instanceof SignBlockEntity) {
                        if (handItem.equals(Items.GLOW_INK_SAC) && !((SignBlockEntity) blockEntity).isGlowingText()) return Crosshair.USE_ITEM;
                        if (handItem.equals(Items.INK_SAC) && ((SignBlockEntity) blockEntity).isGlowingText()) return Crosshair.USE_ITEM;
                        if (handItem instanceof DyeItem && ((SignBlockEntity) blockEntity).getTextColor() != ((DyeItem) handItem).getColor()) return Crosshair.USE_ITEM;
                    }
                }
            }
            else if (block instanceof LecternBlock) {
                if (handItem.equals(Items.WRITTEN_BOOK)
                        || handItem.equals(Items.WRITABLE_BOOK)
                        || (!player.shouldCancelInteraction() && blockState.get(LecternBlock.HAS_BOOK)))
                    return Crosshair.USE_ITEM;
            }
            else if (block instanceof CampfireBlock && !player.shouldCancelInteraction()) {
                BlockEntity blockEntity = MinecraftClient.getInstance().world.getBlockEntity(blockPos);
                if (blockEntity instanceof CampfireBlockEntity && (((CampfireBlockEntity) blockEntity).getRecipeFor(handItemStack)).isPresent())
                    return Crosshair.USE_ITEM;
            }
        }
        return null;
    }

    @Override
    public Crosshair checkUsableItemOnMiss(ClientPlayerEntity player, ItemStack handItemStack) {
        Item handItem = handItemStack.getItem();
        if (DynamicCrosshair.config.dynamicCrosshairHoldingBlock() == BlockCrosshairPolicy.Always) {
            if (handItem == Items.WATER_BUCKET || handItem == Items.LAVA_BUCKET) {
                return Crosshair.HOLDING_BLOCK;
            }
            if (handItem instanceof EntityBucketItem) {
                return new Crosshair(Style.HoldingBlock, ModifierUse.USE_ITEM);
            }
        }
        return null;
    }
}
