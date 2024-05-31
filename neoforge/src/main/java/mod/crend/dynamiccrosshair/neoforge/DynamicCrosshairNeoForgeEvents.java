package mod.crend.dynamiccrosshair.neoforge;

import mod.crend.dynamiccrosshair.AutoHudCompat;
import mod.crend.dynamiccrosshair.DynamicCrosshairMod;
import mod.crend.dynamiccrosshair.component.CrosshairHandler;
import mod.crend.dynamiccrosshair.config.ConfigHandler;
import mod.crend.dynamiccrosshairapi.DynamicCrosshair;
import mod.crend.dynamiccrosshairapi.DynamicCrosshairApi;
import mod.crend.libbamboo.neoforge.ConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public class DynamicCrosshairNeoForgeEvents {

	@EventBusSubscriber(modid = DynamicCrosshair.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ModBus {

		@SubscribeEvent
		static void onClientSetup(FMLClientSetupEvent event) {
			DynamicCrosshairMod.init();
			ConfigScreen.register(ConfigHandler.CONFIG_STORE);
		}

		@SubscribeEvent
		static void onInterModEnqueue(InterModEnqueueEvent event) {
			if (ModList.get().isLoaded("autohud")) {
				InterModComms.sendTo("autohud", "register_api", AutoHudCompat::new);
			}
		}

		@SubscribeEvent
		static void onInterModProcess(InterModProcessEvent event) {
			InterModComms.getMessages(DynamicCrosshair.MOD_ID, DynamicCrosshairNeoForge.REGISTER_API::equals)
					.map(msg -> (DynamicCrosshairApi) msg.messageSupplier().get())
					.forEach(DynamicCrosshairNeoForge::registerApi);
		}
	}

	@EventBusSubscriber(modid = DynamicCrosshair.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
	public static class ForgeBus {

		@SubscribeEvent
		static void onClientTick(ClientTickEvent.Post event) {
			CrosshairHandler.tick();
		}
	}
}
