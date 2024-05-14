package mod.crend.dynamiccrosshair.api;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.hit.HitResult;

/**
 * Defines a handler for a given mod that can influence the crosshair.
 *
 * <p>The computation order is as follows:
 * Phase 1: Target
 * - if targeted entity: computeFromEntity()
 * - if targeted block: computeFromBlock()
 * Phase 2: Hand item
 * - if isAlwaysUsableItem(), use USABLE crosshair
 * - otherwise: computeFromItem()
 * Phase 3: Off hand
 * - if the crosshair after main hand evaluation still allows for either a style or a use modifier,
 *   repeat phases 1 & 2 for the off hand.
 * The result of each phase is combined into the final crosshair.
 *
 * <p>Each phase is evaluated by calling all relevant handlers for the phase, before proceeding to the
 * next phase. A handler is considered relevant iff. any of the following conditions holds true:
 * - The targeted block or entity is in the same namespace as the handler.
 * - An item held in either hand is in the same namespace as the handler.
 * - The handler overrides forceCheck() to return true.
 *
 * <p>Note that the vanilla handler will always be checked last in each phase. This allows for mods to
 * override vanilla behaviour.
 */
public interface DynamicCrosshairApi extends DynamicCrosshairApiBlockState, DynamicCrosshairApiEntityType, DynamicCrosshairApiItemStack {

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
     * Allows an API implementation to override the world; by default the client world is used.
     * The first API to return a non-null value will be respected.
     * The order in which APIs are called (after vanilla) is not guaranteed.
     * <p>
     * Use with care!
     *
     * @return null if no specific override is required; ClientWorld instance otherwise.
     */
    default ClientWorld overrideWorld() { return null; }

    /**
     * Computes a crosshair for the targeted entity.
     * This is used for the advanced crosshair computation. As many conditions as possible
     * should be checked to ensure an accurate crosshair.
     *
     * <p>This method will only be called if an entity is targeted, i.e. context.isWithEntity()
     * is true. The targeted entity may be accessed by calling context.getEntity().
     *
     * @param context A context describing the current state.
     * @return a Crosshair object overwriting the crosshair settings, or null.
     */
    default Crosshair computeFromEntity(CrosshairContext context) { return null; }

    /**
     * Computes a crosshair for the targeted block.
     * This is used for the advanced crosshair computation. As many conditions as possible
     * should be checked to ensure an accurate crosshair.
     *
     * <p>This method will only be called if a block is targeted, i.e. context.isWithBlock() is
     * true. The targeted block state may be accessed by calling context.getBlockState(). Its
     * associated block entity (if any) is easily queryable through context.getBlockEntity().
     *
     * @param context A context describing the current state.
     * @return a Crosshair object overwriting the crosshair settings, or null.
     */
    default Crosshair computeFromBlock(CrosshairContext context) { return null; }

    /**
     * Computes a crosshair for the given held item.
     * This is used for the advanced crosshair computation. As many conditions as possible
     * should be checked to ensure an accurate crosshair.
     *
     * <p>This method is not called if isAlwaysUsableItem() already returned true for the given
     * evaluated hand item.
     *
     * <p>Commonly used helper functions are context.isWithBlock(), context.isWithEntity(), and
     * context.isMainHand(). See CrosshairContext for further helpers.
     *
     * <p>If a melee weapon doubles as a tool (such as axes), context.canUseWeaponAsTool() will
     * return true if a block is targeted and tool crosshair is enabled. API implementations
     * may use this to defer crosshair computation (return "null" from this handler).
     *
     * @param context A context describing the current state.
     * @return a Crosshair object overwriting the crosshair settings, or null.
     */
    default Crosshair computeFromItem(CrosshairContext context) { return null; }

}
