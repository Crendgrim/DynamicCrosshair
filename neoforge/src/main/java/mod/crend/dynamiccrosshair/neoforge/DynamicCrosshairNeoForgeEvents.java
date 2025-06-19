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
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.common.NeoForge;
//? if <1.20.5 {
/*import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent;
*///?} else {
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
//?}

public class DynamicCrosshairNeoForgeEvents {

	//? if <1.20.5 {
	/*@Mod.EventBusSubscriber(modid = DynamicCrosshair.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	*///?} else
	@EventBusSubscriber(modid = DynamicCrosshair.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ModBus {

		@SubscribeEvent
		static void onClientSetup(FMLClientSetupEvent event) {
			DynamicCrosshairMod.init();
			ConfigScreen.register(ConfigHandler.CONFIG_STORE);
			// Delay initialising the client tick event, see that method.
			NeoForge.EVENT_BUS.addListener(ModBus::onClientTick);
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

		// Do not use @SubscribeEvent on the game bus because the mod "placebo" forces the game bus to run early for some reason.
		static void onClientTick(
				//? if <1.20.5 {
				/*TickEvent.ClientTickEvent event
				*///?} else
				ClientTickEvent.Post event
		) {
			//? if <1.20.5
			/*if (event.phase == TickEvent.Phase.START) return;*/
			CrosshairHandler.tick();
		}
	}

}
