package mod.crend.yaclx.auto;

import dev.isxander.yacl.api.*;
import mod.crend.yaclx.YaclX;
import mod.crend.yaclx.auto.annotation.AutoYaclConfig;
import mod.crend.dynamiccrosshair.config.ConfigHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ConfigScreenFactory {

	public static Screen makeScreen(Class<?> configClass, Screen parent) {
		if (YaclX.HAS_YACL) {
			return YetAnotherConfigLib.create(ConfigHandler.CONFIG_STORE.withYacl().instance,
					(defaults, config, builder) -> AutoYacl.parse(configClass, defaults, config, builder)
			).generateScreen(parent);
		} else {
			AutoYaclConfig ayc = configClass.getAnnotation(AutoYaclConfig.class);
			return new NoticeScreen(
					() -> MinecraftClient.getInstance().setScreen(parent),
					Text.translatable(ayc.translationKey()),
					Text.translatable(ayc.modid() + ".requireYaclForConfigScreen")
			);
		}
	}
}
