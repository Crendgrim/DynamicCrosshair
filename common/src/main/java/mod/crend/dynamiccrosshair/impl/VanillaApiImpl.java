package mod.crend.dynamiccrosshair.impl;

import mod.crend.dynamiccrosshair.DynamicCrosshairMod;
import mod.crend.dynamiccrosshair.api.Crosshair;
import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairApi;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairRangedItem;
import mod.crend.dynamiccrosshair.api.InteractionType;
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
import net.minecraft.item.FishingRodItem;
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
    public Crosshair overrideFromItem(CrosshairContext context, InteractionType interactionType) {
        // Special handling for some item categories

        if (context.getItemStack().isIn(DynamicCrosshairItemTags.MELEE_WEAPONS) && context.includeMeleeWeapon() && interactionType != InteractionType.USABLE_TOOL) {
            if (context.canUseWeaponAsTool()) {
                BlockState blockState = context.getBlockState();
                if (blockState.getHardness(context.getWorld(), context.getBlockPos()) == 0.0f) {
                    if (DynamicCrosshairMod.config.dynamicCrosshairMeleeWeaponOnBreakableBlock()) {
                        return new Crosshair(InteractionType.MELEE_WEAPON);
                    } else {
                        return new Crosshair(InteractionType.CORRECT_TOOL);
                    }
                }
            }

            if (context.isWithEntity() && !DynamicCrosshairMod.config.dynamicCrosshairMeleeWeaponOnEntity()) {
                return new Crosshair(InteractionType.NO_ACTION);
            }
        }

        switch (interactionType) {
            case TOOL, USABLE_TOOL -> {
                Crosshair crosshair = new Crosshair(interactionType);
                if (context.isWithBlock()) {
                    return Crosshair.combine(crosshair, new Crosshair(context.checkToolWithBlock()));
                }
            }

            case RANGED_WEAPON -> {
                DynamicCrosshairRangedItem rangedItem = (DynamicCrosshairRangedItem) context.getItem();
                if (rangedItem.dynamiccrosshair$isCharged(context)) {
                    return new Crosshair(InteractionType.RANGED_WEAPON_CHARGED);
                } else if (rangedItem.dynamiccrosshair$isCharging(context)) {
                    return new Crosshair(InteractionType.RANGED_WEAPON_CHARGING);
                }
            }

            case EMPTY -> {
                UsableCrosshairPolicy usableItemPolicy = DynamicCrosshairMod.config.dynamicCrosshairHoldingUsableItem();
                if (usableItemPolicy != UsableCrosshairPolicy.Disabled) {
                    ItemStack itemStack = context.getItemStack();
                    if ((usableItemPolicy == UsableCrosshairPolicy.Always || !context.isCoolingDown()) && context.api().isAlwaysUsable(itemStack)) {
                        return new Crosshair(InteractionType.USE_ITEM);
                    }
                    if (usableItemPolicy == UsableCrosshairPolicy.Always && context.api().isUsable(itemStack)) {
                        return new Crosshair(InteractionType.USE_ITEM);
                    }
                }
            }
        }

        return null;
    }


    // Blocks

    @Override
    public boolean isAlwaysInteractable(BlockState blockState) {
        return blockState.isIn(DynamicCrosshairBlockTags.ALWAYS_INTERACTABLE)
                || BlockOrTag.isContainedIn(blockState.getBlock(), DynamicCrosshairMod.config.getAdditionalInteractableBlocks())
                ;
    }

    @Override
    public boolean isAlwaysInteractableInCreativeMode(BlockState blockState) {
        return blockState.isIn(DynamicCrosshairBlockTags.ALWAYS_INTERACTABLE_IN_CREATIVE_MODE);
    }

    @Override
    public boolean isInteractable(BlockState blockState) {
        return blockState.isIn(DynamicCrosshairBlockTags.INTERACTABLE);
    }


    // Entities

    @Override
    public boolean isAlwaysInteractable(EntityType<?> entityType) {
        return entityType.isIn(DynamicCrosshairEntityTags.ALWAYS_INTERACTABLE);
    }

    @Override
    public boolean isInteractable(EntityType<?> entityType) {
        return entityType.isIn(DynamicCrosshairEntityTags.INTERACTABLE);
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
