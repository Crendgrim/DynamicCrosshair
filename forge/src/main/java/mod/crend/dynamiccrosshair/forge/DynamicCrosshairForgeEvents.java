package mod.crend.dynamiccrosshair.forge;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import mod.crend.dynamiccrosshair.AutoHudCompat;
import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairApi;
import mod.crend.dynamiccrosshair.component.CrosshairHandler;
import mod.crend.dynamiccrosshair.config.ConfigHandler;
import mod.crend.yaclx.forge.ConfigScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;

public class DynamicCrosshairForgeEvents {

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
			InterModComms.getMessages(DynamicCrosshair.MOD_ID, DynamicCrosshairForge.REGISTER_API::equals)
					.map(msg -> (DynamicCrosshairApi) msg.messageSupplier().get())
					.forEach(DynamicCrosshairForge::registerApi);
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
