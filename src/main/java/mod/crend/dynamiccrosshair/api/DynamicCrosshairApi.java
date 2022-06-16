package mod.crend.dynamiccrosshair.api;

import net.minecraft.item.ItemStack;

public interface DynamicCrosshairApi {

    String getNamespace();

    /**
     * Only overwrite this if it differs from the namespace
     */
    default String getModId() {
        return getNamespace();
    };

    /**
     * Usually, APIs are only checked if any of (held item, targeted entity, targeted block) are under the namespace
     * this API is registered under. This is to massively reduce unnecessary checks. However, this can pose a problem
     * if a mod overwrites vanilla behaviour.
     * Override this method if this API should always be checked.
     *
     * @return true if this API should always be checked.
     */
    default boolean forceCheck() {
        return false;
    }

    default IBlockBreakHandler getBlockBreakHandler() {
        return (context) -> null;
    }

    default IBlockInteractHandler getBlockInteractHandler() {
        return (context) -> null;
    }

    default IBlockItemHandler getBlockItemHandler() {
        return (context) -> null;
    }

    default IEntityHandler getEntityHandler() {
        return (context) -> null;
    }

    default IMeleeWeaponHandler getMeleeWeaponHandler() {
        return (context, canBeToolCrosshair) -> null;
    }

    default IRangedWeaponHandler getRangedWeaponHandler() {
        return (context) -> null;
    }

    default IThrowableItemHandler getThrowableItemHandler() {
        return (context) -> null;
    }

    default IShieldItemHandler getShieldItemHandler() {
        return (context) -> null;
    }

    default IToolItemHandler getToolItemHandler() {
        return (context) -> null;
    }

    default IUsableItemHandler getUsableItemHandler() {
        return (context) -> null;
    }


    /**
     * Checks whether the given item is always usable.
     *
     * This method should return true if and only if an item is usable regardless of context.
     * Anything handled here does not have to be checked in isUsableItem or an IUsableItemHandler implementation.
     *
     * @param itemStack The tool in the player's main hand
     * @return a Crosshair object overwriting the crosshair settings
     */
    default boolean isAlwaysUsableItem(ItemStack itemStack) { return false; }


    /**
     * Checks whether the given item is usable.
     *
     * This method is called in a context of "always show crosshair for usable items", so no further
     * restrictions over "is this item type usable" should take place here.
     *
     * @param itemStack The tool in the player's main hand
     * @return a Crosshair object overwriting the crosshair settings
     */
    default boolean isUsableItem(ItemStack itemStack) { return false; }

}
