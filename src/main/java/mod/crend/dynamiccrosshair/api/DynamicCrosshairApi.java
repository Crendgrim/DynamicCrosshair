package mod.crend.dynamiccrosshair.api;

import mod.crend.dynamiccrosshair.component.Crosshair;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;

public interface DynamicCrosshairApi {

    /**
     * The key the mod's items, blocks, entities,... are registered under. Usually equals the mod's ID.
     * It can be worth defining a second API implementation for forced checks, if your API is rather large and
     * you would like to reduce unnecessary checks. In this case, make sure getModId() still returns the ID of
     * the mod, and getNamespace() returns a unique string for the forced check. As no objects are registered to
     * this namespace, it will only get called at the end of other checks if forceCheck() returns true.
     *
     * @return The mod's namespace
     */
    String getNamespace();

    /**
     * Only overwrite this if it differs from the namespace.
     * @return The mod's ID (as defined in its fabric.mod.json) if it differs from the namespace it uses.
     */
    default String getModId() {
        return getNamespace();
    }

    /**
     * Overwrite this to initialize some logic for the API.
     * This method will only be called if the mod is present and the API will be checked.
     */
    default void init() { }

    /**
     * Usually, APIs are only checked if any of (held item, targeted entity, targeted block) are under the namespace
     * this API is registered under. This is to massively reduce unnecessary checks. However, this can pose a problem
     * if a mod overwrites vanilla behaviour.
     * Override this method if this API should always be checked. This might for example be because the mod overwrites
     * vanilla behaviour, or because it provides items for other mods to inherit from.
     *
     * @return true if this API should always be checked.
     */
    default boolean forceCheck() {
        return false;
    }

    /**
     * The crosshair is only recalculated when something changes, such as targeted block, held item, or similar.
     * This method may be used to forcefully invalidate the status every tick. Such behaviour may be necessary for
     * blocks which have different interaction rules based on where on the block the cursor is, for example.
     * Implementations of this method should check isWithBlock() and isWithEntity() before accessing these fields.
     *
     * @param context A context that will hold the held items, and targeted block or entity if any.
     * @return true if the crosshair should be recalculated every tick.
     */
    default boolean forceInvalidate(CrosshairContext context) { return false; }

    /**
     * Allows an API implementation to override the hit result, by default vanilla's crosshair target.
     * The order in which APIs are called (after vanilla) is not guaranteed.
     *
     * @param context A context that will hold the held items, and targeted block or entity if any. Note that this is
     *                the state before the new hit result has been evaluated.
     * @param hitResult the currently active hit result. May have come from another API.
     * @return the parameter if nothing should change, or a replacement hit result.
     */
    default HitResult overrideHitResult(CrosshairContext context, HitResult hitResult) { return hitResult; }

    /**
     * Set the crosshair based on whether the targeted block can be broken.
     *
     * @param context A context that is guaranteed to contain an item and a targeted block
     * @return a Crosshair object overwriting the crosshair settings
     */
    default Crosshair checkBlockBreaking(CrosshairContext context) { return null; }

    /**
     * Set the crosshair based on whether the targeted block can be interacted with.
     *
     * @param context A context that is guaranteed to contain an item and a targeted block
     * @return a Crosshair object overwriting the crosshair settings
     */
    default Crosshair checkBlockInteractable(CrosshairContext context) { return null; }

    /**
     * Set the crosshair based on the targeted entity.
     *
     * @param context A context that is guaranteed to contain an item and a targeted entity
     * @return a Crosshair object overwriting the crosshair settings
     */
    default Crosshair checkEntity(CrosshairContext context) { return null; }

    /**
     * Set the crosshair based on whether the player has a block equipped.
     *
     * @param context A context that is guaranteed to contain an item
     * @return a Crosshair object overwriting the crosshair settings
     */
    default Crosshair checkBlockItem(CrosshairContext context) { return null; }

    /**
     * Set the crosshair based on whether the player has a melee weapon equipped.
     * If a melee weapon doubles as a tool (such as axes), context.canUseWeaponAsTool() will
     * return true if a block is targeted and tool crosshair is enabled. API implementations
     * may use this to defer crosshair computation (return "null" from this handler).
     *
     * @param context A context that is guaranteed to contain an item
     * @return a Crosshair object overwriting the crosshair settings
     */
    default Crosshair checkMeleeWeapon(CrosshairContext context) { return null; }

    /**
     * Set the crosshair based on whether the player has a ranged weapon equipped.
     *
     * @param context A context that is guaranteed to contain an item
     * @return a Crosshair object overwriting the crosshair settings
     */
    default Crosshair checkRangedWeapon(CrosshairContext context) { return null; }

    /**
     * Set the crosshair based on whether the player has a shield equipped.
     *
     * @param context A context that is guaranteed to contain an item
     * @return a Crosshair object overwriting the crosshair settings
     */
    default Crosshair checkShield(CrosshairContext context) { return null; }

    /**
     * Set the crosshair based on whether the player has a throwable item equipped.
     *
     * @param context A context that is guaranteed to contain an item
     * @return a Crosshair object overwriting the crosshair settings
     */
    default Crosshair checkThrowable(CrosshairContext context) { return null; }

    /**
     * Set the crosshair based on whether the player has a tool equipped.
     *
     * @param context A context that is guaranteed to contain an item
     * @return a Crosshair object overwriting the crosshair settings
     */
    default Crosshair checkTool(CrosshairContext context) { return null; }

    /**
     * Checks whether the given item is always usable.
     *
     * This method should return true if and only if an item is usable regardless of context.
     * Anything handled here does not have to be checked in isUsableItem() or checkUsableItem().
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

    /**
     * Set the crosshair based on whether the given item is usable.
     *
     * This method is called regardless of crosshair target (entity, block, miss), and should be used to check for
     * always usable items such as food.
     *
     * @param context A context that is guaranteed to contain an item.
     *                If `context.withBlock()` return true, it also contains a targeted block.
     * @return a Crosshair object overwriting the crosshair settings
     */
    default Crosshair checkUsableItem(CrosshairContext context) { return null; }

}
