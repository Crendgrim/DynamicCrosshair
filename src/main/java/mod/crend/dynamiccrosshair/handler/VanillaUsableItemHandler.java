package mod.crend.dynamiccrosshair.handler;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.api.IUsableItemHandler;
import mod.crend.dynamiccrosshair.component.Crosshair;
import mod.crend.dynamiccrosshair.component.ModifierUse;
import mod.crend.dynamiccrosshair.component.Style;
import mod.crend.dynamiccrosshair.config.BlockCrosshairPolicy;
import mod.crend.dynamiccrosshair.mixin.*;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;

import java.util.List;

public class VanillaUsableItemHandler implements IUsableItemHandler {

    @Override
    public boolean isUsableItem(ItemStack handItemStack) {
        Item handItem = handItemStack.getItem();
        return (handItem.isFood()
                || handItem.getUseAction(handItemStack) == UseAction.DRINK
                || handItem instanceof ArmorItem
                || handItem instanceof ElytraItem
                || handItem instanceof ShieldItem
                || handItem instanceof FireworkRocketItem
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
            } else {
                // Special case: sweet and glow berries can sometimes be placed
                if (DynamicCrosshair.config.dynamicCrosshairHoldingBlock() != BlockCrosshairPolicy.Disabled
                        && (handItem == Items.SWEET_BERRIES || handItem == Items.GLOW_BERRIES)) {
                    HitResult hitResult = MinecraftClient.getInstance().crosshairTarget;
                    if (hitResult.getType() == HitResult.Type.BLOCK) {
                        IBlockItemMixin blockItem = (IBlockItemMixin) handItem;
                        ItemPlacementContext itemPlacementContext = new ItemPlacementContext(player, player.getActiveHand(), handItemStack, (BlockHitResult) hitResult);
                        BlockState blockState = blockItem.invokeGetPlacementState(itemPlacementContext);
                        if (blockState != null && blockItem.invokeCanPlace(itemPlacementContext, blockState)) return Crosshair.HOLDING_BLOCK;
                    }
                }
                if (player.getHungerManager().isNotFull() || handItem.getFoodComponent().isAlwaysEdible()) {
                    return Crosshair.USE_ITEM;
                }
            }
        }
        if (handItem.getUseAction(handItemStack) == UseAction.DRINK) return Crosshair.USE_ITEM;
        if (handItem instanceof ArmorItem armorItem) {
            EquipmentSlot slot = armorItem.getSlotType();
            if (player.hasStackEquipped(slot)) {
                return null;
            }
            return Crosshair.USE_ITEM;
        }
        if (handItem instanceof ElytraItem) {
            if (player.hasStackEquipped(EquipmentSlot.CHEST)) {
                return null;
            }
            return Crosshair.USE_ITEM;
        }

        if (handItem instanceof ShieldItem) {
            return Crosshair.USE_ITEM;
        }

        if (handItem instanceof FireworkRocketItem) {
            HitResult hitResult = MinecraftClient.getInstance().crosshairTarget;
            if (hitResult.getType() == HitResult.Type.BLOCK || player.isFallFlying()) {
                return Crosshair.USE_ITEM;
            }
        }

        // Liquid interactions ignore block hit, cast extra rays
        // This getting called for entity hits is on purpose, as liquid interactions overwrite entity interactions
        if (handItem instanceof GlassBottleItem) {
            // Dragon's breath
            List<AreaEffectCloudEntity> list = MinecraftClient.getInstance().world.getEntitiesByClass(AreaEffectCloudEntity.class, player.getBoundingBox().expand(2.0), entity -> {
                return entity != null && entity.isAlive() && entity.getParticleType().getType() == ParticleTypes.DRAGON_BREATH;
            });
            if (!list.isEmpty()) {
                return Crosshair.USE_ITEM;
            }

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
        if (handItem instanceof EnderEyeItem) {
            if (!block.equals(Blocks.END_PORTAL_FRAME) || !blockState.get(Properties.EYE)) {
                return Crosshair.USE_ITEM;
            }
        }
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
