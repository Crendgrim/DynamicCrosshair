package mod.crend.dynamiccrosshair.fabric;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairApi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class DynamicCrosshairFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        DynamicCrosshair.init();

        FabricLoader.getInstance().getEntrypointContainers(DynamicCrosshair.MOD_ID, DynamicCrosshairApi.class).forEach(entrypoint -> {
            DynamicCrosshairApi apiImpl = entrypoint.getEntrypoint();
            if (FabricLoader.getInstance().isModLoaded(apiImpl.getModId())) {
                DynamicCrosshair.registerApi(apiImpl);
            }
        });
    }
}
