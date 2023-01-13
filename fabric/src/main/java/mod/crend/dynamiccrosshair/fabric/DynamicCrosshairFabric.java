package mod.crend.dynamiccrosshair.fabric;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairApi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;

public class DynamicCrosshairFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        DynamicCrosshair.init();

        FabricLoader.getInstance().getEntrypointContainers(DynamicCrosshair.MOD_ID, DynamicCrosshairApi.class)
                .stream()
                .map(EntrypointContainer::getEntrypoint)
                .filter(api -> FabricLoader.getInstance().isModLoaded(api.getModId()))
                .forEach(DynamicCrosshair::registerApi);
    }
}
