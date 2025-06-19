package mod.crend.dynamiccrosshair;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.Components;
import mod.crend.autohud.component.state.BooleanComponentState;
import mod.crend.autohud.render.AutoHudRenderLayer;
import mod.crend.autohud.render.ComponentRenderer;
import mod.crend.dynamiccrosshair.component.CrosshairHandler;
import mod.crend.dynamiccrosshair.render.CrosshairRenderer;
import mod.crend.dynamiccrosshair.style.CrosshairStyle;
import mod.crend.dynamiccrosshairapi.DynamicCrosshair;
import mod.crend.dynamiccrosshairapi.VersionUtils;import mod.crend.dynamiccrosshairapi.crosshair.CrosshairPart;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;

public class AutoHudCompat implements AutoHudApi {
	@Override
	public String modId() {
		return DynamicCrosshair.MOD_ID;
	}

	public static final Component CROSSHAIR_SECONDARY = Component.builder(DynamicCrosshair.AUTOHUD_COMPONENT_SECONDARY).config(AutoHud.config.crosshair()).build();
	public static final Component CROSSHAIR_MODIFIER = Component.builder(DynamicCrosshair.AUTOHUD_COMPONENT_MODIFIER).config(AutoHud.config.crosshair()).build();

	public static final ComponentRenderer CROSSHAIR_RENDER_PRIMARY = ComponentRenderer.builder(Components.Crosshair)
			//? if <=1.21.4 {
			.fade()
			.beginRender(context -> AutoHudRenderLayer.FADE_MODE.preRender(Components.Crosshair, context))
			.endRender(context -> {
				context.draw();
				AutoHudRenderLayer.FADE_MODE.postRender(Components.Crosshair, context);
			})
			//?}
			.build();
	public static final ComponentRenderer CROSSHAIR_RENDER_SECONDARY = ComponentRenderer.builder(CROSSHAIR_SECONDARY)
			//? if <=1.21.4 {
			.fade()
			.beginRender(context -> AutoHudRenderLayer.FADE_MODE.preRender(CROSSHAIR_SECONDARY, context))
			.endRender(context -> {
				context.draw();
				AutoHudRenderLayer.FADE_MODE.postRender(CROSSHAIR_SECONDARY, context);
			})
			//?}
			.build();
	public static final ComponentRenderer CROSSHAIR_RENDER_MODIFIER = ComponentRenderer.builder(CROSSHAIR_MODIFIER)
			//? if <=1.21.4 {
			.fade()
			.beginRender(context -> AutoHudRenderLayer.FADE_MODE.preRender(CROSSHAIR_MODIFIER, context))
			.endRender(context -> {
				context.draw();
				AutoHudRenderLayer.FADE_MODE.postRender(CROSSHAIR_MODIFIER, context);
			})
			//?}
			.build();

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
		Components.Crosshair.state = new BooleanComponentState(Components.Crosshair, AutoHudCompat::shouldRenderCrosshairPrimary);
		CROSSHAIR_SECONDARY.state = new BooleanComponentState(CROSSHAIR_SECONDARY, AutoHudCompat::shouldRenderCrosshairSecondary);
		CROSSHAIR_MODIFIER.state = new BooleanComponentState(CROSSHAIR_MODIFIER, AutoHudCompat::shouldRenderCrosshairModifier);
	}

	@Override
	public void tickState(ClientPlayerEntity player) {
		CrosshairRenderer.autoHudCompat = AutoHud.config.crosshair().active();
		CrosshairHandler.forceShowCrosshair = CrosshairRenderer.autoHudCompat;
	}

	static ComponentRenderer getRenderer(CrosshairPart part) {
		return switch (part) {
			case PRIMARY -> CROSSHAIR_RENDER_PRIMARY;
			case SECONDARY -> CROSSHAIR_RENDER_SECONDARY;
			case MODIFIER -> CROSSHAIR_RENDER_MODIFIER;
		};
	}

	public static void renderCrosshair(DrawContext context, CrosshairPart part, CrosshairStyle style, int x, int y) {
		getRenderer(part).wrap(context, () -> {
			int argb = style.color();
			//? if <=1.21.5 {
			float alphaMultiplier = RenderSystem.getShaderColor()[3];
			RenderSystem.setShaderColor(((argb >> 16) & 0xFF) / 255.0f, ((argb >> 8) & 0xFF) / 255.0f, (argb & 0xFF) / 255.0f, alphaMultiplier * ((argb >> 24) & 0xFF) / 255.0f);
			//?}
			CrosshairRenderer.renderCrosshair(
					context,
					style.identifier(),
					//? if >=1.21.2
					/*style.enableBlend() ? VersionUtils.getCrosshair() : VersionUtils.getGuiTextured(),*/
					x, y
					//? if >1.21.5
					/*, argb*/
			);
		});
	}

}
