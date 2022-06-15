package mod.crend.dynamiccrosshair.handler;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.IUsableItemHandler;
import mod.crend.dynamiccrosshair.component.Crosshair;
import mod.crend.dynamiccrosshair.component.ModifierUse;
import mod.crend.dynamiccrosshair.component.Style;
import mod.crend.dynamiccrosshair.config.BlockCrosshairPolicy;
import mod.crend.dynamiccrosshair.mixin.IAxeItemMixin;
import mod.crend.dynamiccrosshair.mixin.IBucketItemMixin;
import mod.crend.dynamiccrosshair.mixin.IHoeItemMixin;
import mod.crend.dynamiccrosshair.mixin.IShovelItemMixin;
import net.minecraft.block.*;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
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
        );
    }

    @Override
    public Crosshair checkUsableItem(CrosshairContext context) {
        Item handItem = context.getItem();
        // Enable crosshair on food and drinks also when not targeting if "when interactable" is chosen
        if (handItem.isFood()) {
            if (handItem instanceof ChorusFruitItem) {
                if (!context.player.getItemCooldownManager().isCoolingDown(handItem)) return Crosshair.USE_ITEM;
            } else {
                // Special case: sweet and glow berries can sometimes be placed
                if (DynamicCrosshair.config.dynamicCrosshairHoldingBlock() != BlockCrosshairPolicy.Disabled
                        && (handItem == Items.SWEET_BERRIES || handItem == Items.GLOW_BERRIES)) {
                    if (context.isWithBlock()) {
                        if (context.canPlaceItemAsBlock()) return Crosshair.HOLDING_BLOCK;
                    }
                }
                if (context.player.canConsume(false) || handItem.getFoodComponent().isAlwaysEdible()) {
                    return Crosshair.USE_ITEM;
                }
            }
        }
        if (handItem.getUseAction(context.getItemStack()) == UseAction.DRINK) return Crosshair.USE_ITEM;
        if (handItem instanceof ArmorItem armorItem) {
            EquipmentSlot slot = armorItem.getSlotType();
            if (context.player.hasStackEquipped(slot)) {
                return null;
            }
            return Crosshair.USE_ITEM;
        }
        if (handItem instanceof ElytraItem) {
            if (context.player.hasStackEquipped(EquipmentSlot.CHEST)) {
                return null;
            }
            return Crosshair.USE_ITEM;
        }

        if (handItem instanceof FireworkRocketItem) {
            if (context.isWithBlock() || context.player.isFallFlying()) {
                return Crosshair.USE_ITEM;
            }
        }

        if (handItem instanceof GoatHornItem) {
            if (context.player.getItemCooldownManager().isCoolingDown(handItem)) {
                return null;
            }
            return Crosshair.USE_ITEM;
        }

        // Liquid interactions ignore block hit, cast extra rays
        // This getting called for entity hits is on purpose, as liquid interactions overwrite entity interactions
        if (handItem instanceof GlassBottleItem) {
            // Dragon's breath
            List<AreaEffectCloudEntity> list = context.world.getEntitiesByClass(AreaEffectCloudEntity.class, context.player.getBoundingBox().expand(2.0), entity -> {
                return entity != null && entity.isAlive() && entity.getParticleType().getType() == ParticleTypes.DRAGON_BREATH;
            });
            if (!list.isEmpty()) {
                return Crosshair.USE_ITEM;
            }

            BlockHitResult blockHitResult = context.raycastWithFluid(RaycastContext.FluidHandling.ANY);
            if (context.world.getFluidState(blockHitResult.getBlockPos()).isIn(FluidTags.WATER))
                return Crosshair.USE_ITEM;
        }
        if (handItem == Items.BUCKET) {
            BlockHitResult blockHitResult = context.raycastWithFluid(RaycastContext.FluidHandling.SOURCE_ONLY);
            if (!context.world.getFluidState(blockHitResult.getBlockPos()).isEmpty())
                return Crosshair.USE_ITEM;
        }
        return null;
    }

    @Override
    public Crosshair checkUsableItemOnBlock(CrosshairContext context) {
        Item handItem = context.getItem();

        if (handItem instanceof SpawnEggItem) return Crosshair.USE_ITEM;

        BlockState blockState = context.getBlockState();
        Block block = blockState.getBlock();
        if (handItem instanceof ToolItem) {
            if (handItem instanceof AxeItem) {
                if (IAxeItemMixin.getSTRIPPED_BLOCKS().get(block) != null
                        || Oxidizable.getDecreasedOxidationBlock(block).isPresent()
                        || HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get().get(block) != null) {
                    return Crosshair.USE_ITEM;
                }
            } else if (handItem instanceof ShovelItem) {
                if (IShovelItemMixin.getPATH_STATES().get(block) != null) {
                    return Crosshair.USE_ITEM;
                }
            } else if (handItem instanceof HoeItem) {
                if (IHoeItemMixin.getTILLING_ACTIONS().get(block) != null) {
                    return Crosshair.USE_ITEM;
                }
            }
            return null;
        }
        if (handItem instanceof ShearsItem) {
            if (block instanceof AbstractPlantStemBlock plantStemBlock && !plantStemBlock.hasMaxAge(blockState)) {
                return Crosshair.USE_ITEM;
            }
            if (!context.player.shouldCancelInteraction() && block instanceof BeehiveBlock && blockState.get(BeehiveBlock.HONEY_LEVEL) >= 5) {
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
        if (handItem instanceof EntityBucketItem) {
            if (DynamicCrosshair.config.dynamicCrosshairHoldingBlock() != BlockCrosshairPolicy.Disabled) {
                return new Crosshair(Style.HoldingBlock, ModifierUse.USE_ITEM);
            }
            return Crosshair.USE_ITEM;
        }
        if (block instanceof AbstractCauldronBlock cauldron && !context.player.shouldCancelInteraction()) {
            if (handItem instanceof BucketItem bucketItem) {
                Fluid fluid = ((IBucketItemMixin) bucketItem).getFluid();
                if (fluid == Fluids.WATER || fluid == Fluids.LAVA) {
                    return Crosshair.USE_ITEM;
                }
                if (fluid == Fluids.EMPTY && cauldron.isFull(blockState)) {
                    return Crosshair.USE_ITEM;
                }
            }
            if (handItem instanceof PowderSnowBucketItem) {
                return Crosshair.USE_ITEM;
            }
            if (block.equals(Blocks.WATER_CAULDRON)) {
                if (handItem instanceof GlassBottleItem) {
                    return Crosshair.USE_ITEM;
                }
                if (handItem instanceof PotionItem && PotionUtil.getPotion(context.getItemStack()) == Potions.WATER) {
                    return Crosshair.USE_ITEM;
                }
                if (handItem instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock sbb && sbb.getColor() != null) {
                    return Crosshair.USE_ITEM;
                }
                if (handItem instanceof DyeableItem dyeableItem && dyeableItem.hasColor(context.getItemStack())) {
                    return Crosshair.USE_ITEM;
                }
                if (handItem instanceof BannerItem && BannerBlockEntity.getPatternCount(context.getItemStack()) > 0) {
                    return Crosshair.USE_ITEM;
                }
            }
        }
        if (handItem instanceof GlassBottleItem) {
            if (block instanceof BeehiveBlock && blockState.get(BeehiveBlock.HONEY_LEVEL) >= 5 && !context.player.shouldCancelInteraction()) return Crosshair.USE_ITEM;
            return null;
        }
        if (handItem instanceof BucketItem bucketItem) {
            Fluid fluid = ((IBucketItemMixin) bucketItem).getFluid();
            if (fluid != Fluids.EMPTY) {
                if (DynamicCrosshair.config.dynamicCrosshairHoldingBlock() != BlockCrosshairPolicy.Disabled) {
                    return Crosshair.HOLDING_BLOCK;
                }
                return Crosshair.USE_ITEM;
            }
            else if (block.equals(Blocks.POWDER_SNOW)) return Crosshair.USE_ITEM;
        }
        if (handItem instanceof BoneMealItem) {
            if (block instanceof Fertilizable fertilizable && fertilizable.isFertilizable(context.world, context.getBlockPos(), blockState, true)) {
                return Crosshair.USE_ITEM;
            }
            if (context.getBlockState().isOf(Blocks.WATER) && context.getFluidState().getLevel() == 8) {
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
    public Crosshair checkUsableItemOnMiss(CrosshairContext context) {
        Item handItem = context.getItem();
        if (DynamicCrosshair.config.dynamicCrosshairHoldingBlock() == BlockCrosshairPolicy.Always) {
            if (handItem instanceof EntityBucketItem) {
                return new Crosshair(Style.HoldingBlock, ModifierUse.USE_ITEM);
            }
            if (handItem instanceof BucketItem bucketItem && ((IBucketItemMixin) bucketItem).getFluid() != Fluids.EMPTY) {
                return Crosshair.HOLDING_BLOCK;
            }
        }
        return null;
    }
}
