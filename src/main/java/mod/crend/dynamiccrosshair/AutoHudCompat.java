package mod.crend.dynamiccrosshair;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.state.BooleanComponentState;
import mod.crend.autohud.render.AutoHudRenderer;
import mod.crend.dynamiccrosshair.component.CrosshairHandler;
import mod.crend.dynamiccrosshair.render.CrosshairRenderer;
import mod.crend.dynamiccrosshairapi.DynamicCrosshair;
import net.minecraft.client.network.ClientPlayerEntity;

public class AutoHudCompat implements AutoHudApi {
	@Override
	public String modId() {
		return DynamicCrosshair.MOD_ID;
	}

	@Override
	public void initState(ClientPlayerEntity player) {
		Component.Crosshair.state = new BooleanComponentState(Component.Crosshair, CrosshairHandler::shouldShowCrosshair);
	}

	@Override
	public void tickState(ClientPlayerEntity player) {
		CrosshairRenderer.autoHudCompat = getAlpha() < 1.0f && AutoHud.config.crosshair().active();
		CrosshairHandler.forceShowCrosshair = CrosshairRenderer.autoHudCompat;
	}

	public static float getAlpha() {
		return Math.max(Component.Crosshair.getAlpha(1), getMinimumAlpha());
	}

	public static float getMinimumAlpha() {
		return (float) AutoHud.config.crosshair().maximumFade();
	}

}
