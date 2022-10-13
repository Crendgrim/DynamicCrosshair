package mod.crend.dynamiccrosshair.handler;

import mod.crend.dynamiccrosshair.api.*;
import mod.crend.dynamiccrosshair.component.Crosshair;
import net.minecraft.entity.EntityType;
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

    @Override
    public boolean forceInvalidate(CrosshairContext context) {
        if (context.isWithEntity() && context.getEntity().getType() == EntityType.ARMOR_STAND) {
            return true;
        }
        return false;
    }

    @Override
    public Crosshair checkBlockBreaking(CrosshairContext context) {
        return VanillaBlockHandler.checkBlockBreaking(context);
    }

    @Override
    public Crosshair checkBlockInteractable(CrosshairContext context) {
        return VanillaBlockHandler.checkBlockInteractable(context);
    }

    @Override
    public Crosshair checkEntity(CrosshairContext context) {
        return VanillaEntityHandler.checkEntity(context);
    }

    @Override
    public Crosshair checkBlockItem(CrosshairContext context) {
        return VanillaItemHandler.checkBlockItem(context);
    }

    @Override
    public Crosshair checkMeleeWeapon(CrosshairContext context) {
        return VanillaItemHandler.checkMeleeWeapon(context);
    }

    @Override
    public Crosshair checkRangedWeapon(CrosshairContext context) {
        return VanillaItemHandler.checkRangedWeapon(context);
    }

    @Override
    public Crosshair checkShield(CrosshairContext context) {
        return VanillaItemHandler.checkShield(context);
    }

    @Override
    public Crosshair checkThrowable(CrosshairContext context) {
        return VanillaItemHandler.checkThrowable(context);
    }

    @Override
    public Crosshair checkTool(CrosshairContext context) {
        return VanillaItemHandler.checkTool(context);
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
    public Crosshair checkUsableItem(CrosshairContext context) {
        return VanillaUsableItemHandler.checkUsableItem(context);
    }
}
