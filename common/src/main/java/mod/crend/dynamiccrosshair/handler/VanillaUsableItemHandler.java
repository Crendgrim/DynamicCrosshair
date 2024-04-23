package mod.crend.dynamiccrosshair.handler;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.PlatformUtils;
import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.component.Crosshair;
import mod.crend.dynamiccrosshair.component.CrosshairVariant;
import mod.crend.dynamiccrosshair.component.ModifierUse;
import mod.crend.dynamiccrosshair.config.BlockCrosshairPolicy;
import mod.crend.dynamiccrosshair.mixin.AxeItemAccessor;
import mod.crend.dynamiccrosshair.mixin.HoeItemAccessor;
import mod.crend.dynamiccrosshair.mixin.ShovelItemAccessor;
import mod.crend.libbamboo.type.ItemOrTag;
import net.minecraft.block.*;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.component.ComponentHolder;
import net.minecraft.component.DataComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potions;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;

import java.util.List;

public class VanillaUsableItemHandler {

    public static boolean isAlwaysUsableItem(ItemStack handItemStack) {
        Item handItem = handItemStack.getItem();
        return handItem.getUseAction(handItemStack) == UseAction.DRINK
                || ItemOrTag.isContainedIn(handItem, DynamicCrosshair.config.getAdditionalUsableItems());
    }

    public static boolean isUsableItem(ItemStack handItemStack) {
        Item handItem = handItemStack.getItem();
        return (handItemStack.contains(DataComponentTypes.FOOD)
                || handItem instanceof ArmorItem
                || handItem instanceof ElytraItem
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
                || handItem instanceof WrittenBookItem
                || handItem instanceof GoatHornItem
                || handItem instanceof BundleItem
                || handItem instanceof BrushItem
        );
    }

    public static Crosshair checkUsableItem(CrosshairContext context) {
        ItemStack handItemStack = context.getItemStack();
        Item handItem = handItemStack.getItem();
        // Enable crosshair on food and drinks also when not targeting if "when interactable" is chosen
        if (handItemStack.contains(DataComponentTypes.FOOD)) {
            // Special case: sweet and glow berries can sometimes be placed
            if (DynamicCrosshair.config.dynamicCrosshairHoldingBlock() != BlockCrosshairPolicy.Disabled
                    && (handItem == Items.SWEET_BERRIES || handItem == Items.GLOW_BERRIES)) {
                if (context.isWithBlock()) {
                    if (context.canPlaceItemAsBlock()) return Crosshair.HOLDING_BLOCK;
                }
            }
            if (context.player.canConsume(false) || handItemStack.get(DataComponentTypes.FOOD).canAlwaysEat()) {
                return Crosshair.USABLE;
            }
        }
        if (handItem instanceof ArmorItem || handItem instanceof ElytraItem) {
            EquipmentSlot slot = MobEntity.getPreferredEquipmentSlot(context.getItemStack());
            if (EnchantmentHelper.hasBindingCurse(context.player.getEquippedStack(slot))) {
                return null;
            }
            return Crosshair.USABLE;
        }

        if (handItem instanceof FireworkRocketItem) {
            if (context.isWithBlock() || context.player.isFallFlying()) {
                return Crosshair.USABLE;
            }
        }

        if (handItem instanceof GoatHornItem) {
            if (context.player.getItemCooldownManager().isCoolingDown(handItem)) {
                return null;
            }
            return Crosshair.USABLE;
        }
        if (handItem instanceof BundleItem) {
            BundleContentsComponent bundleContentsComponent = handItemStack.get(DataComponentTypes.BUNDLE_CONTENTS);
            if (bundleContentsComponent != null && !bundleContentsComponent.isEmpty()) {
                return Crosshair.USABLE;
            }
        }

        if (handItem instanceof SpyglassItem) {
            if (DynamicCrosshair.config.dynamicCrosshairForceHoldingSpyglass()) {
                return Crosshair.REGULAR;
            }
        }

        // Liquid interactions ignore block hit, cast extra rays
        // This getting called for entity hits is on purpose, as liquid interactions overwrite entity interactions
        if (handItem instanceof GlassBottleItem) {
            // Dragon's breath
            List<AreaEffectCloudEntity> list = context.world.getEntitiesByClass(
                    AreaEffectCloudEntity.class,
                    context.player.getBoundingBox().expand(2.0),
                    entity -> entity != null
                            && entity.isAlive()
                            && entity.getParticleType().getType() == ParticleTypes.DRAGON_BREATH
            );
            if (!list.isEmpty()) {
                return Crosshair.USABLE;
            }

            BlockHitResult blockHitResult = context.raycastWithFluid(RaycastContext.FluidHandling.ANY);
            if (context.world.getFluidState(blockHitResult.getBlockPos()).isIn(FluidTags.WATER))
                return Crosshair.USABLE;
        }
        if (handItem == Items.BUCKET) {
            BlockHitResult blockHitResult = context.raycastWithFluid(RaycastContext.FluidHandling.SOURCE_ONLY);
            if (blockHitResult.getType() == HitResult.Type.BLOCK) {
                FluidState bucketFluidState = context.world.getFluidState(blockHitResult.getBlockPos());
                if (!bucketFluidState.isEmpty() && bucketFluidState.isStill()) {
                    return Crosshair.USABLE;
                }
                Block bucketBlock = context.world.getBlockState(blockHitResult.getBlockPos()).getBlock();
                if (bucketBlock instanceof FluidDrainable) {
                    if (!(bucketBlock instanceof Waterloggable || bucketBlock instanceof FluidBlock)) {
                        return Crosshair.USABLE;
                    }
                }
            }
        }

        if (context.isWithBlock()) {
            return checkUsableItemOnBlock(context);
        }
        if (!context.isTargeting()) {
            return checkUsableItemOnMiss(context);
        }
        return null;
    }

    public static Crosshair checkUsableTool(CrosshairContext context) {
        Item handItem = context.getItem();
        BlockState blockState = context.getBlockState();
        Block block = blockState.getBlock();
        if (context.isWithBlock()) {
            if (handItem instanceof ToolItem) {
                if (handItem instanceof AxeItem) {
                    if (AxeItemAccessor.getSTRIPPED_BLOCKS().get(block) != null
                            || Oxidizable.getDecreasedOxidationBlock(block).isPresent()
                            || HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get().get(block) != null) {
                        return Crosshair.USABLE;
                    }
                } else if (handItem instanceof ShovelItem) {
                    if (ShovelItemAccessor.getPATH_STATES().get(block) != null) {
                        return Crosshair.USABLE;
                    }
                } else if (handItem instanceof HoeItem) {
                    if (HoeItemAccessor.getTILLING_ACTIONS().get(block) != null) {
                        return Crosshair.USABLE;
                    }
                }
                return null;
            }
            if (handItem instanceof ShearsItem) {
                if (block instanceof AbstractPlantStemBlock plantStemBlock && !plantStemBlock.hasMaxAge(blockState)) {
                    return Crosshair.USABLE;
                }
                if (!context.player.shouldCancelInteraction() && block instanceof BeehiveBlock && blockState.get(BeehiveBlock.HONEY_LEVEL) >= 5) {
                    return Crosshair.USABLE;
                }
                return null;
            }
        }
        return null;
    }

    public static Crosshair checkUsableItemOnBlock(CrosshairContext context) {
        ItemStack handItemStack = context.getItemStack();
        Item handItem = handItemStack.getItem();

        if (handItem instanceof SpawnEggItem) return Crosshair.USABLE;

        BlockState blockState = context.getBlockState();
        Block block = blockState.getBlock();
        Crosshair crosshair = checkUsableTool(context);
        if (crosshair != null) return crosshair;

        if (handItem instanceof FlintAndSteelItem || handItem instanceof FireChargeItem) {
            if (CampfireBlock.canBeLit(blockState) || CandleBlock.canBeLit(blockState) || CandleCakeBlock.canBeLit(blockState)) {
                return Crosshair.USABLE;
            }
            BlockPos firePos = context.getBlockPos().offset(((BlockHitResult) context.hitResult).getSide());
            if (AbstractFireBlock.canPlaceAt(context.world, firePos, context.player.getHorizontalFacing())) {
                if (DynamicCrosshair.config.dynamicCrosshairHoldingBlock() != BlockCrosshairPolicy.Disabled) {
                    return Crosshair.HOLDING_BLOCK;
                }
                return Crosshair.USABLE;
            }
        }
        if (handItem instanceof MusicDiscItem && block instanceof JukeboxBlock) return Crosshair.USABLE;
        if (handItem instanceof HoneycombItem && HoneycombItem.UNWAXED_TO_WAXED_BLOCKS.get().get(block) != null)
            return Crosshair.USABLE;
        if (handItem instanceof EnderEyeItem) {
            if (!block.equals(Blocks.END_PORTAL_FRAME) || !blockState.get(Properties.EYE)) {
                return Crosshair.USABLE;
            }
        }
        if (handItem instanceof EntityBucketItem) {
            if (DynamicCrosshair.config.dynamicCrosshairHoldingBlock() != BlockCrosshairPolicy.Disabled) {
                return new Crosshair(CrosshairVariant.HoldingBlock, ModifierUse.USE_ITEM);
            }
            return Crosshair.USABLE;
        }
        if (block instanceof AbstractCauldronBlock cauldron && !context.player.shouldCancelInteraction()) {
            if (handItem instanceof BucketItem bucketItem) {
                Fluid fluid = PlatformUtils.getFluidFromBucket(bucketItem);
                if (fluid == Fluids.WATER || fluid == Fluids.LAVA) {
                    return Crosshair.USABLE;
                }
                if (fluid == Fluids.EMPTY && cauldron.isFull(blockState)) {
                    return Crosshair.USABLE;
                }
            }
            if (handItem instanceof PowderSnowBucketItem) {
                return Crosshair.USABLE;
            }
            if (block.equals(Blocks.WATER_CAULDRON)) {
                if (handItem instanceof GlassBottleItem) {
                    return Crosshair.USABLE;
                }
                if (handItem instanceof PotionItem) {
                    PotionContentsComponent potionContentsComponent = handItemStack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
                    if (potionContentsComponent.matches(Potions.WATER)) {
                        return Crosshair.USABLE;
                    }
                }
                if (handItem instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock sbb && sbb.getColor() != null) {
                    return Crosshair.USABLE;
                }
                if (handItemStack.isIn(ItemTags.DYEABLE) && !handItemStack.contains(DataComponentTypes.DYED_COLOR)) {
                    return Crosshair.USABLE;
                }
                if (handItem instanceof BannerItem) {
                    BannerPatternsComponent bannerPatternsComponent = handItemStack.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT);
                    if (!bannerPatternsComponent.layers().isEmpty()) {
                        return Crosshair.USABLE;
                    }
                }
            }
        }
        if (handItem instanceof GlassBottleItem) {
            if (block instanceof BeehiveBlock && blockState.get(BeehiveBlock.HONEY_LEVEL) >= 5 && !context.player.shouldCancelInteraction()) return Crosshair.USABLE;
            return null;
        }
        if (handItem instanceof BucketItem bucketItem) {
            Fluid fluid = PlatformUtils.getFluidFromBucket(bucketItem);
            if (fluid != Fluids.EMPTY) {
                if (DynamicCrosshair.config.dynamicCrosshairHoldingBlock() != BlockCrosshairPolicy.Disabled) {
                    return Crosshair.HOLDING_BLOCK;
                }
                return Crosshair.USABLE;
            }
            else {
                if (block instanceof Waterloggable) {
                    if (blockState.contains(Properties.WATERLOGGED) && blockState.get(Properties.WATERLOGGED)) return Crosshair.USABLE;
                } else if (block instanceof FluidDrainable) return Crosshair.USABLE;
            }
        }
        if (handItem instanceof BoneMealItem) {
            if (block instanceof Fertilizable fertilizable) {
                if (fertilizable.isFertilizable(context.world, context.getBlockPos(), blockState)) {
                    return Crosshair.USABLE;
                } else if (block instanceof PitcherCropBlock && blockState.get(PitcherCropBlock.AGE) < 4) { // mojang pls (MC-261619)
                    BlockPos pos = context.getBlockPos();
                    BlockState pitcherCropState = context.world.getBlockState(blockState.get(PitcherCropBlock.HALF) == DoubleBlockHalf.LOWER ? pos.up() : pos);
                    if (pitcherCropState.isAir() || pitcherCropState.isOf(Blocks.PITCHER_CROP)) {
                        return Crosshair.USABLE;
                    }
                }
            }
            if (context.getBlockState().isOf(Blocks.WATER) && context.getFluidState().getLevel() == 8) {
                return Crosshair.USABLE;
            }
            return null;
        }
        if (handItem instanceof CompassItem) {
            if (block == Blocks.LODESTONE) {
                return Crosshair.USABLE;
            }
        }
        if (handItem instanceof WritableBookItem || handItem instanceof WrittenBookItem) return Crosshair.USABLE;
        if (handItem instanceof BrushItem) {
            if (block instanceof BrushableBlock) {
                return Crosshair.USABLE;
            }
        }

        return null;
    }

    public static Crosshair checkUsableItemOnMiss(CrosshairContext context) {
        Item handItem = context.getItem();
        if (DynamicCrosshair.config.dynamicCrosshairHoldingBlock() == BlockCrosshairPolicy.Always) {
            if (handItem instanceof EntityBucketItem) {
                return new Crosshair(CrosshairVariant.HoldingBlock, ModifierUse.USE_ITEM);
            }
            //noinspection ConstantValue
            if (handItem instanceof BucketItem bucketItem && PlatformUtils.getFluidFromBucket(bucketItem) != Fluids.EMPTY) {
                return Crosshair.HOLDING_BLOCK;
            }
        }
        return null;
    }
}
