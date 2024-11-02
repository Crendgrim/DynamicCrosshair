package mod.crend.dynamiccrosshair.impl;

import mod.crend.dynamiccrosshair.DynamicCrosshairMod;
import mod.crend.dynamiccrosshair.config.UsableCrosshairPolicy;
import mod.crend.dynamiccrosshairapi.DynamicCrosshairApi;
import mod.crend.dynamiccrosshairapi.crosshair.Crosshair;
import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.registry.DynamicCrosshairBlockTags;
import mod.crend.dynamiccrosshairapi.registry.DynamicCrosshairEntityTags;
import mod.crend.dynamiccrosshairapi.registry.DynamicCrosshairItemTags;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairRangedItem;
import mod.crend.libbamboo.tag.ClientTags;
import mod.crend.libbamboo.type.BlockOrTag;
import mod.crend.libbamboo.type.ItemOrTag;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChiseledBookshelfBlock;
//? if >=1.20.6
/*import net.minecraft.component.DataComponentTypes;*/
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
//? if >=1.21.2
/*import net.minecraft.item.consume.UseAction;*/
import net.minecraft.util.Identifier;
//? if <1.21.2
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
        boolean isRangedMeleeWeapon = false;
        if (context.api().isMeleeWeapon(context.getItemStack())
                && context.includeMeleeWeapon()
                && interactionType != InteractionType.USABLE_TOOL
        ) {
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

            if (context.api().isRangedWeapon(context.getItemStack())) {
                isRangedMeleeWeapon = true;
                interactionType = InteractionType.RANGED_WEAPON;
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
                if (DynamicCrosshairMod.config.dynamicCrosshairHoldingRangedWeapon() == UsableCrosshairPolicy.IfInteractable) {
                    DynamicCrosshairRangedItem rangedItem = (DynamicCrosshairRangedItem) context.getItem();
                    if (rangedItem.dynamiccrosshair$isCharged(context)) {
                        if (isRangedMeleeWeapon) return new Crosshair(InteractionType.MELEE_WEAPON, InteractionType.RANGED_WEAPON_CHARGED);
                        return new Crosshair(InteractionType.RANGED_WEAPON_CHARGED);
                    } else if (rangedItem.dynamiccrosshair$isCharging(context)) {
                        if (isRangedMeleeWeapon) return new Crosshair(InteractionType.MELEE_WEAPON, InteractionType.RANGED_WEAPON_CHARGING);
                        return new Crosshair(InteractionType.RANGED_WEAPON_CHARGING);
                    } else {
                        if (isRangedMeleeWeapon) return new Crosshair(InteractionType.MELEE_WEAPON);
                        return new Crosshair(InteractionType.EMPTY);
                    }
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
        return ClientTags.isInWithLocalFallback(DynamicCrosshairBlockTags.ALWAYS_INTERACTABLE, blockState.getRegistryEntry())
                || BlockOrTag.isContainedIn(blockState.getBlock(), DynamicCrosshairMod.config.getAdditionalInteractableBlocks())
                ;
    }

    @Override
    public boolean isAlwaysInteractableInCreativeMode(BlockState blockState) {
        return ClientTags.isInWithLocalFallback(DynamicCrosshairBlockTags.ALWAYS_INTERACTABLE_IN_CREATIVE_MODE, blockState.getRegistryEntry());
    }

    @Override
    public boolean isInteractable(BlockState blockState) {
        return ClientTags.isInWithLocalFallback(DynamicCrosshairBlockTags.INTERACTABLE, blockState.getRegistryEntry());
    }


    // Entities

    @Override
    public boolean isAlwaysInteractable(EntityType<?> entityType) {
        return ClientTags.isInWithLocalFallback(DynamicCrosshairEntityTags.ALWAYS_INTERACTABLE, entityType);
    }

    @Override
    public boolean isInteractable(EntityType<?> entityType) {
        return ClientTags.isInWithLocalFallback(DynamicCrosshairEntityTags.INTERACTABLE, entityType);
    }


    // Items

    @Override
    public boolean isAlwaysUsable(ItemStack itemStack) {
        return ClientTags.isInWithLocalFallback(DynamicCrosshairItemTags.ALWAYS_USABLE, itemStack.getRegistryEntry())
                || itemStack.getItem().getUseAction(itemStack) == UseAction.DRINK
                || ItemOrTag.isContainedIn(itemStack.getItem(), DynamicCrosshairMod.config.getAdditionalUsableItems())
                ;
    }

    @Override
    public boolean isAlwaysUsableOnBlock(ItemStack itemStack) {
        return ClientTags.isInWithLocalFallback(DynamicCrosshairItemTags.ALWAYS_USABLE_ON_BLOCK, itemStack.getRegistryEntry());
    }

    @Override
    public boolean isAlwaysUsableOnEntity(ItemStack itemStack) {
        return ClientTags.isInWithLocalFallback(DynamicCrosshairItemTags.ALWAYS_USABLE_ON_ENTITY, itemStack.getRegistryEntry());
    }

    @Override
    public boolean isAlwaysUsableOnMiss(ItemStack itemStack) {
        return ClientTags.isInWithLocalFallback(DynamicCrosshairItemTags.ALWAYS_USABLE_ON_MISS, itemStack.getRegistryEntry());
    }

    @Override
    public boolean isUsable(ItemStack itemStack) {
        return ClientTags.isInWithLocalFallback(DynamicCrosshairItemTags.USABLE, itemStack.getRegistryEntry())
                //? if >=1.20.6 {
                /*|| itemStack.contains(DataComponentTypes.FOOD)
                *///?} else {
                || itemStack.isFood()
                //?}
                //? if >=1.21
                /*|| itemStack.contains(DataComponentTypes.JUKEBOX_PLAYABLE)*/
                ;
    }

    @Override
    public boolean isBlock(ItemStack itemStack) {
        return itemStack.getItem() instanceof BlockItem
                || ClientTags.isInWithLocalFallback(DynamicCrosshairItemTags.BLOCKS, itemStack.getRegistryEntry())
                ;
    }

    @Override
    public boolean isMeleeWeapon(ItemStack itemStack) {
        return ClientTags.isInWithLocalFallback(DynamicCrosshairItemTags.MELEE_WEAPONS, itemStack.getRegistryEntry())
                || ItemOrTag.isContainedIn(itemStack.getItem(), DynamicCrosshairMod.config.getAdditionalMeleeWeapons())
                ;
    }

    @Override
    public boolean isRangedWeapon(ItemStack itemStack) {
        if (ClientTags.isInWithLocalFallback(DynamicCrosshairItemTags.RANGED_WEAPONS, itemStack.getRegistryEntry())) {
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
        return ClientTags.isInWithLocalFallback(DynamicCrosshairItemTags.SHIELDS, itemStack.getRegistryEntry())
                || itemStack.getItem().getUseAction(itemStack) == UseAction.BLOCK
                ;
    }

    @Override
    public boolean isThrowable(ItemStack itemStack) {
        return ClientTags.isInWithLocalFallback(DynamicCrosshairItemTags.THROWABLES, itemStack.getRegistryEntry())
                || ItemOrTag.isContainedIn(itemStack.getItem(), DynamicCrosshairMod.config.getAdditionalThrowables())
                ;
    }

    @Override
    public boolean isTool(ItemStack itemStack) {
        return ClientTags.isInWithLocalFallback(DynamicCrosshairItemTags.TOOLS, itemStack.getRegistryEntry())
                || ItemOrTag.isContainedIn(itemStack.getItem(), DynamicCrosshairMod.config.getAdditionalTools())
                ;
    }
}
