package mod.crend.dynamiccrosshair;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.state.BooleanComponentState;
import mod.crend.autohud.render.AutoHudRenderer;
import mod.crend.dynamiccrosshair.component.CrosshairHandler;
import mod.crend.dynamiccrosshair.render.CrosshairRenderer;
import net.minecraft.client.network.ClientPlayerEntity;

public class AutoHudCompat implements AutoHudApi {
	@Override
	public String modId() {
		return DynamicCrosshair.MOD_ID;
	}

	@Override
	public void initState(ClientPlayerEntity player) {
		Component.Crosshair.state = new BooleanComponentState(Component.Crosshair, CrosshairHandler::shouldShowCrosshair);
		CrosshairRenderer.autoHudCompat = true;
	}

	@Override
	public void tickState(ClientPlayerEntity player) {
		CrosshairHandler.forceShowCrosshair = AutoHud.config.crosshair().active();
	}

	public static float getAlpha() {
		return AutoHudRenderer.alpha;
	}

}
