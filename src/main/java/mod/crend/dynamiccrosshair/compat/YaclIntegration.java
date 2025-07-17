package mod.crend.dynamiccrosshair.compat;

import mod.crend.dynamiccrosshair.component.CrosshairComponent;
import mod.crend.dynamiccrosshair.config.ConfigHandler;

public class YaclIntegration {
	public static void listen() {
//? if yacl {
		ConfigHandler.CONFIG_STORE.withYacl().configChangeEvent.register(YaclIntegration::onChange);
//?}
	}

	public static void onChange() {
		CrosshairComponent.init();
	}
}
