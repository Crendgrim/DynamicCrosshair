package mod.crend.dynamiccrosshair;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.state.BooleanComponentState;
import mod.crend.autohud.render.AutoHudRenderer;
import mod.crend.autohud.render.RenderWrapper;
import mod.crend.dynamiccrosshair.component.CrosshairHandler;
import mod.crend.dynamiccrosshair.render.CrosshairRenderer;
import mod.crend.dynamiccrosshair.style.CrosshairStyle;
import mod.crend.dynamiccrosshairapi.DynamicCrosshair;
import mod.crend.dynamiccrosshairapi.crosshair.CrosshairPart;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;

import java.util.function.Consumer;

public class AutoHudCompat implements AutoHudApi {
	@Override
	public String modId() {
		return DynamicCrosshair.MOD_ID;
	}

	public static final Component CROSSHAIR_SECONDARY = Component.builder("dynamiccrosshair:secondary").config(AutoHud.config.crosshair()).build();
	public static final Component CROSSHAIR_MODIFIER = Component.builder("dynamiccrosshair:modifier").config(AutoHud.config.crosshair()).build();

	static Consumer<DrawContext> getPreRenderFor(Component component) {
		return (context) -> AutoHudRenderer.preInjectFade(component, (float) component.config.maximumFade());
	}
	static void postRender(DrawContext context) {
		context.draw();
		AutoHudRenderer.postInjectFade(context);
	}
	public static final RenderWrapper CROSSHAIR_RENDER_PRIMARY = new RenderWrapper.CustomRenderer(
			Component.Crosshair::isActive,
			() -> true,
			getPreRenderFor(Component.Crosshair),
			AutoHudCompat::postRender
	);
	public static final RenderWrapper CROSSHAIR_RENDER_SECONDARY = new RenderWrapper.CustomRenderer(
			CROSSHAIR_SECONDARY::isActive,
			() -> true,
			getPreRenderFor(CROSSHAIR_SECONDARY),
			AutoHudCompat::postRender
	);
	public static final RenderWrapper CROSSHAIR_RENDER_MODIFIER = new RenderWrapper.CustomRenderer(
			CROSSHAIR_MODIFIER::isActive,
			() -> true,
			getPreRenderFor(CROSSHAIR_MODIFIER),
			AutoHudCompat::postRender
	);

	public static boolean shouldRenderCrosshairPrimary() {
		return CrosshairHandler.getActiveCrosshair().getCrosshair().hasPrimaryInteraction();
	}
	public static boolean shouldRenderCrosshairSecondary() {
		return CrosshairHandler.getActiveCrosshair().getCrosshair().hasSecondaryInteraction();
	}
	public static boolean shouldRenderCrosshairModifier() {
		// Bind modifiers to the primary crosshair so their fade status syncs up.
		return shouldRenderCrosshairPrimary();
	}

	@Override
	public void initState(ClientPlayerEntity player) {
		Component.Crosshair.state = new BooleanComponentState(Component.Crosshair, AutoHudCompat::shouldRenderCrosshairPrimary);
		CROSSHAIR_SECONDARY.state = new BooleanComponentState(CROSSHAIR_SECONDARY, AutoHudCompat::shouldRenderCrosshairSecondary);
		CROSSHAIR_MODIFIER.state = new BooleanComponentState(CROSSHAIR_MODIFIER, AutoHudCompat::shouldRenderCrosshairModifier);
		Component.registerComponent(CROSSHAIR_SECONDARY);
		Component.registerComponent(CROSSHAIR_MODIFIER);
	}

	@Override
	public void tickState(ClientPlayerEntity player) {
		CrosshairRenderer.autoHudCompat = AutoHud.config.crosshair().active();
		CrosshairHandler.forceShowCrosshair = CrosshairRenderer.autoHudCompat;
	}

	static RenderWrapper getWrapper(CrosshairPart part) {
		return switch (part) {
			case PRIMARY -> CROSSHAIR_RENDER_PRIMARY;
			case SECONDARY -> CROSSHAIR_RENDER_SECONDARY;
			case MODIFIER -> CROSSHAIR_RENDER_MODIFIER;
		};
	}

	public static void renderCrosshair(DrawContext context, CrosshairPart part, CrosshairStyle style, int x, int y) {
		getWrapper(part).wrap(context, () -> {
			int argb = style.color();
			float alphaMultiplier = RenderSystem.getShaderColor()[3];
			RenderSystem.setShaderColor(((argb >> 16) & 0xFF) / 255.0f, ((argb >> 8) & 0xFF) / 255.0f, (argb & 0xFF) / 255.0f, alphaMultiplier * ((argb >> 24) & 0xFF) / 255.0f);
			CrosshairRenderer.renderCrosshair(
					context,
					style.identifier(),
					//? if >=1.21.2
					/*RenderLayer::getGuiTextured,*/
					x, y
			);
		});
	}

}
