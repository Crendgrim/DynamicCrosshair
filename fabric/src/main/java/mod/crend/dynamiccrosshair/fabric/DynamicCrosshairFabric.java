package mod.crend.dynamiccrosshair.fabric;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.DynamicCrosshairMod;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairApi;
import mod.crend.dynamiccrosshair.component.CrosshairHandler;
import mod.crend.dynamiccrosshair.style.CrosshairStyleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;

public class DynamicCrosshairFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        DynamicCrosshairMod.init();

        ClientTickEvents.END_CLIENT_TICK.register(event -> CrosshairHandler.tick());
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> CrosshairStyleManager.INSTANCE.init());

        FabricLoader.getInstance().getEntrypointContainers(DynamicCrosshair.MOD_ID, DynamicCrosshairApi.class)
                .stream()
                .map(EntrypointContainer::getEntrypoint)
                .filter(api -> FabricLoader.getInstance().isModLoaded(api.getModId()))
                .forEach(DynamicCrosshairMod::registerApi);
    }
}
