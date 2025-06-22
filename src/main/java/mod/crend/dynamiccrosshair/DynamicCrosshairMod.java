package mod.crend.dynamiccrosshair;

import mod.crend.dynamiccrosshair.component.CrosshairHandler;
import mod.crend.dynamiccrosshair.config.ConfigHandler;
import mod.crend.dynamiccrosshair.impl.VanillaApiImpl;
import mod.crend.dynamiccrosshair.style.CrosshairStyleManager;
import mod.crend.dynamiccrosshairapi.DynamicCrosshair;
import mod.crend.dynamiccrosshairapi.DynamicCrosshairApi;
import mod.crend.libbamboo.PlatformUtils;

//? if fabric {
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
//?} else if forge {
/*import mod.crend.libbamboo.forge.ConfigScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
//? if >=1.21.1
/^import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;^/
//? if <1.21.6 {
import net.minecraftforge.eventbus.api.SubscribeEvent;
//?} else
/^import net.minecraftforge.eventbus.api.listener.SubscribeEvent;^/
*///?} else if neoforge {
/*import mod.crend.libbamboo.neoforge.ConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
*///?}

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//? if forge || neoforge
/*@Mod(DynamicCrosshair.MOD_ID)*/
public class DynamicCrosshairMod /*? if fabric {*/implements ClientModInitializer/*?}*/ {
    public static ConfigHandler config;
    public static final Map<String, DynamicCrosshairApi> apis = new HashMap<>();
    public static final Set<String> alwaysCheckedApis = new HashSet<>();
    public static final DynamicCrosshairApi vanillaApi = new VanillaApiImpl();

    public static void registerApi(DynamicCrosshairApi apiImpl) {
        if (PlatformUtils.isModLoaded(apiImpl.getModId())) {
            apiImpl.init();
            final String identifier = apiImpl.getNamespace();
            DynamicCrosshairMod.apis.put(identifier, apiImpl);
            if (apiImpl.forceCheck()) {
                DynamicCrosshairMod.alwaysCheckedApis.add(identifier);
            }
        }
    }

    public static void init() {
        config = new ConfigHandler();
    }

    //? if fabric {
    @Override
    public void onInitializeClient() {
        init();

        ClientTickEvents.END_CLIENT_TICK.register(event -> CrosshairHandler.tick());
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> CrosshairStyleManager.INSTANCE.init());

        FabricLoader.getInstance().getEntrypointContainers(DynamicCrosshair.MOD_ID, DynamicCrosshairApi.class)
                .stream()
                .map(EntrypointContainer::getEntrypoint)
                .filter(api -> FabricLoader.getInstance().isModLoaded(api.getModId()))
                .forEach(DynamicCrosshairMod::registerApi);
    }
    //?}
    //? if forge {
    /*public DynamicCrosshairMod(/^? if >=1.21.1 {^//^FMLJavaModLoadingContext context^//^?}^/) {
        ConfigScreen.register(
                //? if <1.21.1 {
                ModLoadingContext.get(),
                //?} else
                /^context,^/
                () -> ConfigHandler.CONFIG_STORE
        );
    }
    *///?}

    //? if forge || neoforge {
    /*//? if forge {
    /^@Mod.EventBusSubscriber(modid = DynamicCrosshair.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    ^///?} else {
    @EventBusSubscriber(modid = DynamicCrosshair.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    //?}
    public static class ModBus {

        @SubscribeEvent
        static void onClientSetup(FMLClientSetupEvent event) {
            System.err.println("DYNAMIC CROSSHAIR ON CLIENT SETUP");
            DynamicCrosshairMod.init();
            CrosshairStyleManager.INSTANCE.init();
            //? if neoforge {
            /^ConfigScreen.register(ConfigHandler.CONFIG_STORE);
            NeoForge.EVENT_BUS.addListener(ModBus::onClientTick);
            ^///?} else if <1.21.6 {
            MinecraftForge.EVENT_BUS.addListener(ModBus::onClientTick);
            //?} else
            /^TickEvent.ClientTickEvent.Post.BUS.addListener(ModBus::onClientTick);^/
        }

        @SubscribeEvent
        static void onInterModEnqueue(InterModEnqueueEvent event) {
            if (ModList.get().isLoaded("autohud")) {
                InterModComms.sendTo("autohud", "register_api", AutoHudCompat::new);
            }
        }

        @SubscribeEvent
        static void onInterModProcess(InterModProcessEvent event) {
            InterModComms.getMessages(DynamicCrosshair.MOD_ID, DynamicCrosshair.REGISTER_API::equals)
                    .map(msg -> (DynamicCrosshairApi) msg.messageSupplier().get())
                    .forEach(DynamicCrosshairMod::registerApi);
        }
        static void onClientTick(
                //? if neoforge {
                /^ClientTickEvent.Post event
                ^///?} else if <1.21.6 {
                TickEvent.ClientTickEvent event
                //?} else
                /^TickEvent.ClientTickEvent.Post event^/
        ) {
            //? if forge && <1.21.6
            /^if (event.phase == TickEvent.Phase.START) return;^/
            CrosshairHandler.tick();
        }
    }

    *///?}
}
