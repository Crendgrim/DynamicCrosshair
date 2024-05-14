package mod.crend.dynamiccrosshair.impl;

import mod.crend.dynamiccrosshair.DynamicCrosshairMod;
import mod.crend.dynamiccrosshair.api.Crosshair;
import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairApi;
import mod.crend.dynamiccrosshair.config.UsableCrosshairPolicy;
import mod.crend.dynamiccrosshair.registry.DynamicCrosshairBlockTags;
import mod.crend.dynamiccrosshair.registry.DynamicCrosshairEntityTags;
import mod.crend.dynamiccrosshair.registry.DynamicCrosshairItemTags;
import mod.crend.libbamboo.type.BlockOrTag;
import mod.crend.libbamboo.type.ItemOrTag;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;

public class VanillaApiImpl implements DynamicCrosshairApi {

    @Override
    public String getNamespace() {
        return Identifier.DEFAULT_NAMESPACE;
    }

    @Override
    public boolean forceCheck() {
        // Vanilla behaviour should always be checked, so mods inheriting from vanilla items/blocks/entities just work.
        return true;
    }

    private boolean fishHookStatus;

    @Override
    public boolean forceInvalidate(CrosshairContext context) {
        if (context.isWithEntity() && context.getEntity().getType() == EntityType.ARMOR_STAND) {
            return true;
        }
        if (context.isWithBlock() && context.getBlock() instanceof ChiseledBookshelfBlock) {
            return true;
        }
        if (context.getItem() instanceof FishingRodItem) {
            boolean newFishHookStatus = (context.getPlayer().fishHook != null);
            if (newFishHookStatus != fishHookStatus) {
                fishHookStatus = newFishHookStatus;
                return true;
            }
        }
        return false;
    }

    @Override
    public Crosshair computeFromItem(CrosshairContext context) {
        // Special handling for ranged weapons
        if (context.includeRangedWeapon()) {
            ItemStack itemStack = context.getItemStack();
            Item handItem = itemStack.getItem();
            if (context.api().isRangedWeapon(context.getItemStack())) {
                if (DynamicCrosshairMod.config.dynamicCrosshairHoldingRangedWeapon() == UsableCrosshairPolicy.Always) {
                    return Crosshair.RANGED_WEAPON;
                }

                // Policy: IfFullyDrawn
                if (handItem.getUseAction(itemStack) == UseAction.BOW) {
                    if (context.isActiveItem()) {
                        float progress = BowItem.getPullProgress(handItem.getMaxUseTime(itemStack) - context.getPlayer().getItemUseTimeLeft());
                        if (progress == 1.0f) {
                            return Crosshair.RANGED_WEAPON;
                        }
                    }
                    return Crosshair.REGULAR.withFlag(Crosshair.Flag.FixedModifierUse);
                }
                if (handItem.getUseAction(itemStack) == UseAction.CROSSBOW) {
                    if (CrossbowItem.isCharged(itemStack)) {
                        return Crosshair.RANGED_WEAPON;
                    }
                    return Crosshair.REGULAR.withFlag(Crosshair.Flag.FixedModifierUse);
                }
                if (handItem.getUseAction(itemStack) == UseAction.SPEAR) {
                    if (context.isActiveItem()) {
                        int i = handItem.getMaxUseTime(itemStack) - context.getPlayer().getItemUseTimeLeft();
                        if (i > 10) {
                            return Crosshair.RANGED_WEAPON;
                        }
                    }
                    return null;
                }
                return Crosshair.RANGED_WEAPON;
            }
        }
        return null;
    }


    // Blocks

    @Override
    public boolean isAlwaysInteractable(BlockState blockState) {
        return blockState.isIn(DynamicCrosshairBlockTags.IS_ALWAYS_INTERACTABLE)
                || BlockOrTag.isContainedIn(blockState.getBlock(), DynamicCrosshairMod.config.getAdditionalInteractableBlocks())
                ;
    }

    @Override
    public boolean isAlwaysInteractableInCreativeMode(BlockState blockState) {
        return blockState.isIn(DynamicCrosshairBlockTags.IS_ALWAYS_INTERACTABLE_IN_CREATIVE_MODE);
    }

    @Override
    public boolean isInteractable(BlockState blockState) {
        return blockState.isIn(DynamicCrosshairBlockTags.IS_INTERACTABLE);
    }


    // Entities

    @Override
    public boolean isAlwaysInteractable(EntityType<?> entityType) {
        return entityType.isIn(DynamicCrosshairEntityTags.IS_ALWAYS_INTERACTABLE);
    }

    @Override
    public boolean isInteractable(EntityType<?> entityType) {
        return entityType.isIn(DynamicCrosshairEntityTags.IS_INTERACTABLE);
    }


    // Items

    @Override
    public boolean isAlwaysUsable(ItemStack itemStack) {
        return itemStack.isIn(DynamicCrosshairItemTags.ALWAYS_USABLE)
                || itemStack.getItem().getUseAction(itemStack) == UseAction.DRINK
                || ItemOrTag.isContainedIn(itemStack.getItem(), DynamicCrosshairMod.config.getAdditionalUsableItems())
                ;
    }

    @Override
    public boolean isAlwaysUsableOnBlock(ItemStack itemStack) {
        return itemStack.isIn(DynamicCrosshairItemTags.ALWAYS_USABLE_ON_BLOCK);
    }

    @Override
    public boolean isAlwaysUsableOnEntity(ItemStack itemStack) {
        return itemStack.isIn(DynamicCrosshairItemTags.ALWAYS_USABLE_ON_ENTITY);
    }

    @Override
    public boolean isAlwaysUsableOnMiss(ItemStack itemStack) {
        return itemStack.isIn(DynamicCrosshairItemTags.ALWAYS_USABLE_ON_MISS);
    }

    @Override
    public boolean isUsable(ItemStack itemStack) {
        return itemStack.isIn(DynamicCrosshairItemTags.USABLE)
                || itemStack.contains(DataComponentTypes.FOOD)
                ;
    }

    @Override
    public boolean isBlock(ItemStack itemStack) {
        return itemStack.getItem() instanceof BlockItem
                || itemStack.isIn(DynamicCrosshairItemTags.BLOCKS)
                ;
    }

    @Override
    public boolean isMeleeWeapon(ItemStack itemStack) {
        return itemStack.isIn(DynamicCrosshairItemTags.MELEE_WEAPONS)
                || ItemOrTag.isContainedIn(itemStack.getItem(), DynamicCrosshairMod.config.getAdditionalMeleeWeapons())
                ;
    }

    @Override
    public boolean isRangedWeapon(ItemStack itemStack) {
        if (itemStack.isIn(DynamicCrosshairItemTags.RANGED_WEAPONS)) {
            return true;
        }
        if (ItemOrTag.isContainedIn(itemStack.getItem(), DynamicCrosshairMod.config.getAdditionalRangedWeapons())) {
            return true;
        }
        return switch (itemStack.getItem().getUseAction(itemStack)) {
            case BOW, CROSSBOW, SPEAR -> true;
            default -> false;
        };
    }

    @Override
    public boolean isShield(ItemStack itemStack) {
        return itemStack.isIn(DynamicCrosshairItemTags.SHIELDS)
                || itemStack.getItem().getUseAction(itemStack) == UseAction.BLOCK
                ;
    }

    @Override
    public boolean isThrowable(ItemStack itemStack) {
        return itemStack.isIn(DynamicCrosshairItemTags.THROWABLES)
                || ItemOrTag.isContainedIn(itemStack.getItem(), DynamicCrosshairMod.config.getAdditionalThrowables())
                ;
    }

    @Override
    public boolean isTool(ItemStack itemStack) {
        return itemStack.isIn(DynamicCrosshairItemTags.TOOLS)
                || ItemOrTag.isContainedIn(itemStack.getItem(), DynamicCrosshairMod.config.getAdditionalTools())
                ;
    }
}
