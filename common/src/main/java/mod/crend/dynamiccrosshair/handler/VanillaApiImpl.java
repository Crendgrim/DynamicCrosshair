package mod.crend.dynamiccrosshair.handler;

import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairApi;
import mod.crend.dynamiccrosshair.api.ItemCategory;
import mod.crend.dynamiccrosshair.component.Crosshair;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

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
            boolean newFishHookStatus = (context.player.fishHook != null);
            if (newFishHookStatus != fishHookStatus) {
                fishHookStatus = newFishHookStatus;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isAlwaysUsableItem(ItemStack itemStack) {
        return VanillaUsableItemHandler.isAlwaysUsableItem(itemStack);
    }

    @Override
    public boolean isUsableItem(ItemStack itemStack) {
        return VanillaUsableItemHandler.isUsableItem(itemStack);
    }

    @Override
    public ItemCategory getItemCategory(ItemStack itemStack) {
        if (VanillaUsableItemHandler.isAlwaysUsableItem(itemStack) || VanillaUsableItemHandler.isUsableItem(itemStack)) {
            return ItemCategory.USABLE;
        }
        if (VanillaItemHandler.isMeleeWeapon(itemStack)) {
            return ItemCategory.MELEE_WEAPON;
        }
        if (VanillaItemHandler.isTool(itemStack)) {
            return ItemCategory.TOOL;
        }
        if (VanillaItemHandler.isRangedWeapon(itemStack)) {
            return ItemCategory.RANGED_WEAPON;
        }
        if (VanillaItemHandler.isThrowable(itemStack)) {
            return ItemCategory.THROWABLE;
        }
        if (VanillaItemHandler.isShield(itemStack)) {
            return ItemCategory.SHIELD;
        }
        if (VanillaItemHandler.isBlockItem(itemStack)) {
            return ItemCategory.BLOCK;
        }
        return ItemCategory.NONE;
    }

    @Override
    public boolean isInteractableEntity(Entity entity) {
        return VanillaEntityHandler.isEntityInteractable(entity);
    }

    @Override
    public boolean isAlwaysInteractableBlock(BlockState blockState) {
        return VanillaBlockHandler.isAlwaysInteractableBlock(blockState);
    }

    @Override
    public boolean isInteractableBlock(BlockState blockState) {
        return VanillaBlockHandler.isInteractableBlock(blockState);
    }

    @Override
    public Crosshair computeFromEntity(CrosshairContext context) {
        return VanillaEntityHandler.checkEntity(context);
    }

    @Override
    public Crosshair computeFromBlock(CrosshairContext context) {
        return VanillaBlockHandler.checkBlockInteractable(context);
    }

    @Override
    public Crosshair computeFromItem(CrosshairContext context) {
        Crosshair crosshair = null;

        if (context.includeUsableItem()) {
            crosshair = VanillaUsableItemHandler.checkUsableItem(context);
            // Fall through to tool check, because this returns ModifierUse.USE_ITEM for e.g. strippable blocks
        }

        if (context.includeRangedWeapon() && crosshair == null) {
            crosshair = VanillaItemHandler.checkRangedWeapon(context);
            if (crosshair != null) return crosshair;
        }
        if (context.includeMeleeWeapon() && crosshair == null) {
            // Swords will return "null" here if the block in front of them can be broken like a tool, fallthrough
            // to tool section as well.
            crosshair = VanillaItemHandler.checkMeleeWeapon(context);
            if (crosshair != null) return crosshair;
        }

        if (context.includeTool()) {
            Crosshair toolCrosshair = VanillaItemHandler.checkTool(context);
            if (toolCrosshair != null) {
                if (context.isWithBlock()) {
                    toolCrosshair = Crosshair.combine(toolCrosshair, VanillaBlockHandler.checkToolWithBlock(context));
                }
                crosshair = Crosshair.combine(crosshair, toolCrosshair);
            }
        }
        // End fallthrough section.
        if (crosshair != null) {
            return crosshair;
        }

        if (context.includeThrowable()) {
            crosshair = VanillaItemHandler.checkThrowable(context);
            if (crosshair != null) return crosshair;
        }

        if (context.includeShield()) {
            crosshair = VanillaItemHandler.checkShield(context);
            if (crosshair != null) return crosshair;
        }

        if (context.includeHoldingBlock()) {
            crosshair = VanillaItemHandler.checkBlockItem(context);
        }

        return crosshair;
    }
}
