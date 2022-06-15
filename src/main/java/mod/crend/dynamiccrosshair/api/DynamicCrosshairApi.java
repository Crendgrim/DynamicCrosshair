package mod.crend.dynamiccrosshair.api;

import mod.crend.dynamiccrosshair.component.Crosshair;
import net.minecraft.item.ItemStack;

public interface DynamicCrosshairApi {

    String getNamespace();

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
        return new IUsableItemHandler() {
            @Override
            public boolean isUsableItem(ItemStack itemStack) {
                return false;
            }

            @Override
            public Crosshair checkUsableItem(CrosshairContext context) {
                return null;
            }

            @Override
            public Crosshair checkUsableItemOnBlock(CrosshairContext context) {
                return null;
            }

            @Override
            public Crosshair checkUsableItemOnMiss(CrosshairContext context) {
                return null;
            }
        };
    }

}
