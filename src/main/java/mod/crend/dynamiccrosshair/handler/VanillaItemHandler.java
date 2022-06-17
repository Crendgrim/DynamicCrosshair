package mod.crend.dynamiccrosshair.handler;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.api.*;
import mod.crend.dynamiccrosshair.component.Crosshair;
import mod.crend.dynamiccrosshair.component.ModifierUse;
import mod.crend.dynamiccrosshair.component.CrosshairVariant;
import mod.crend.dynamiccrosshair.config.BlockCrosshairPolicy;
import mod.crend.dynamiccrosshair.config.RangedCrosshairPolicy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.*;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public class VanillaItemHandler {
    public static Crosshair checkTool(CrosshairContext context) {
        Item handItem = context.getItem();
        if (       handItem instanceof ToolItem
                || handItem instanceof FlintAndSteelItem
                || handItem instanceof ShearsItem
        ) {
            return Crosshair.TOOL;
        }
        if (handItem instanceof FishingRodItem) {
            return new Crosshair(CrosshairVariant.HoldingTool, ModifierUse.USE_ITEM).withFlag(Crosshair.Flag.FixedAll);
        }
        return null;
    }

    public static Crosshair checkThrowable(CrosshairContext context) {
        Item handItem = context.getItem();
        if (       handItem instanceof EggItem
                || handItem instanceof SnowballItem
                || handItem instanceof ThrowablePotionItem
                || handItem instanceof ExperienceBottleItem
                || (handItem instanceof EnderPearlItem && !context.player.getItemCooldownManager().isCoolingDown(handItem))
        ) {
            return Crosshair.THROWABLE;
        }
        return null;
    }

    public static Crosshair checkShield(CrosshairContext context) {
        if (context.getItem().getUseAction(context.getItemStack()) == UseAction.BLOCK) {
            return Crosshair.SHIELD;
        }
        return null;
    }

    public static Crosshair checkMeleeWeapon(CrosshairContext context) {
        Item handItem = context.getItem();

        if (handItem instanceof SwordItem) {
            if (context.canUseWeaponAsTool()) {
                BlockState blockState = context.getBlockState();
                if (handItem.getMiningSpeedMultiplier(context.getItemStack(), blockState) > 1.0f
                        && handItem.canMine(blockState, context.world, context.getBlockPos(), context.player)) {
                    return null;
                }
            }
            return Crosshair.MELEE_WEAPON;
        }
        if (handItem instanceof TridentItem) {
            return Crosshair.MELEE_WEAPON;
        }
        if (handItem instanceof AxeItem) {
            if (context.canUseWeaponAsTool()) {
                return null;
            }
            return Crosshair.MELEE_WEAPON;
        }

        return null;
    }

    public static Crosshair checkRangedWeapon(CrosshairContext context) {
        ItemStack itemStack = context.getItemStack();
        Item handItem = itemStack.getItem();
        if (DynamicCrosshair.config.dynamicCrosshairHoldingRangedWeapon() == RangedCrosshairPolicy.Always) {
            return switch (handItem.getUseAction(itemStack)) {
                case BOW, CROSSBOW, SPEAR -> Crosshair.RANGED_WEAPON;
                default -> null;
            };
        }
        // Policy: IfFullyDrawn
        if (handItem.getUseAction(itemStack) == UseAction.BOW) {
            if (context.isActiveItem()) {
                float progress = BowItem.getPullProgress(handItem.getMaxUseTime(itemStack) - context.player.getItemUseTimeLeft());
                if (progress == 1.0f) {
                    return Crosshair.RANGED_WEAPON;
                }
            }
            return Crosshair.REGULAR;
        }
        if (handItem.getUseAction(itemStack) == UseAction.CROSSBOW) {
            if (CrossbowItem.isCharged(itemStack)) {
                return Crosshair.RANGED_WEAPON;
            }
        }
        if (handItem.getUseAction(itemStack) == UseAction.SPEAR) {
            if (context.isActiveItem()) {
                int i = handItem.getMaxUseTime(itemStack) - context.player.getItemUseTimeLeft();
                if (i > 10) {
                    return Crosshair.RANGED_WEAPON;
                }
            }
            return null;
        }
        return null;
    }

    public static Crosshair checkBlockItem(CrosshairContext context) {
        Item handItem = context.getItem();
        if (handItem instanceof BlockItem) {
            if (DynamicCrosshair.config.dynamicCrosshairHoldingBlock() == BlockCrosshairPolicy.IfInteractable) {
                if (context.isWithBlock()) {
                    if (context.canPlaceItemAsBlock()) {
                        return new Crosshair(CrosshairVariant.HoldingBlock);
                    } else return Crosshair.NONE;
                }
            } else return Crosshair.HOLDING_BLOCK;
        }
        if (handItem instanceof ArmorStandItem) return Crosshair.HOLDING_BLOCK;
        if (handItem instanceof MinecartItem) {
            if (DynamicCrosshair.config.dynamicCrosshairHoldingBlock() == BlockCrosshairPolicy.IfInteractable) {
                if (context.isWithBlock()) {
                    if (context.getBlockState().isIn(BlockTags.RAILS)) return Crosshair.HOLDING_BLOCK;
                    return Crosshair.NONE.withFlag(Crosshair.Flag.FixedModifierUse);
                }
            } else return Crosshair.HOLDING_BLOCK;
        }
        if (handItem instanceof EndCrystalItem) {
            if (DynamicCrosshair.config.dynamicCrosshairHoldingBlock() == BlockCrosshairPolicy.IfInteractable) {
                if (context.isWithBlock()) {
                    Block block = context.getBlock();
                    if ((block == Blocks.OBSIDIAN || block == Blocks.BEDROCK) && context.world.isAir(context.getBlockPos().up())) {
                        return Crosshair.HOLDING_BLOCK;
                    }
                    return Crosshair.NONE.withFlag(Crosshair.Flag.FixedModifierUse);
                }
            } else return Crosshair.HOLDING_BLOCK;
        }
        if (handItem instanceof BoatItem) {
            BlockHitResult boatHitResult = context.raycastWithFluid();
            if (boatHitResult.getType() == HitResult.Type.BLOCK) {
                return Crosshair.HOLDING_BLOCK;
            }
        }
        return null;
    }
}
