package mod.crend.dynamiccrosshair.handler;

import mod.crend.dynamiccrosshair.api.*;

public class VanillaApiImpl implements DynamicCrosshairApi {
    VanillaBlockHandler blockHandler = new VanillaBlockHandler();
    VanillaEntityHandler entityHandler = new VanillaEntityHandler();
    VanillaItemHandler itemHandler = new VanillaItemHandler();
    VanillaUsableItemHandler usableItemHandler = new VanillaUsableItemHandler();

    @Override
    public IBlockBreakHandler getBlockBreakHandler() {
        return blockHandler;
    }

    @Override
    public IBlockInteractHandler getBlockInteractHandler() {
        return blockHandler;
    }

    @Override
    public IBlockItemHandler getBlockItemHandler() {
        return itemHandler;
    }

    @Override
    public IEntityHandler getEntityHandler() {
        return entityHandler;
    }

    @Override
    public IRangedWeaponHandler getRangedWeaponHandler() {
        return itemHandler;
    }

    @Override
    public IThrowableItemHandler getThrowableItemHandler() {
        return itemHandler;
    }

    @Override
    public IToolItemHandler getToolItemHandler() {
        return itemHandler;
    }

    @Override
    public IUsableItemHandler getUsableItemHandler() {
        return usableItemHandler;
    }
}
