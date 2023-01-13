package mod.crend.dynamiccrosshair.forge;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairApi;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;

@Mod.EventBusSubscriber(modid = DynamicCrosshair.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DynamicCrosshairForgeEvents {
	public static final String REGISTER_API = "register_api";

	@SubscribeEvent
	static void onClientSetup(FMLClientSetupEvent event) {
		DynamicCrosshairForge.init();
	}

	@SubscribeEvent
	static void onInterModProcess(InterModProcessEvent event) {
		InterModComms.getMessages(DynamicCrosshair.MOD_ID, REGISTER_API::equals)
				.map(msg -> (DynamicCrosshairApi) msg.messageSupplier().get())
				.forEach(DynamicCrosshairForge::registerApi);
	}
}
