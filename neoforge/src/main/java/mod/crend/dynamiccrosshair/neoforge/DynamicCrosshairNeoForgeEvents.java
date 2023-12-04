package mod.crend.dynamiccrosshair.neoforge;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import mod.crend.dynamiccrosshair.AutoHudCompat;
import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairApi;
import mod.crend.dynamiccrosshair.component.CrosshairHandler;
import mod.crend.dynamiccrosshair.config.ConfigHandler;
import mod.crend.yaclx.neoforge.ConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.event.TickEvent;

public class DynamicCrosshairNeoForgeEvents {

	@Mod.EventBusSubscriber(modid = DynamicCrosshair.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ModBus {

		@SubscribeEvent
		static void onClientSetup(FMLClientSetupEvent event) {
			MixinExtrasBootstrap.init();
			DynamicCrosshair.init();
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

	@Mod.EventBusSubscriber(modid = DynamicCrosshair.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
	public static class ForgeBus {

		@SubscribeEvent
		static void onClientTick(TickEvent.ClientTickEvent event) {
			if (event.phase == TickEvent.Phase.END) {
				CrosshairHandler.tick();
			}
		}
	}
}
