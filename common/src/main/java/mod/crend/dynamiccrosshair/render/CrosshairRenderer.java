package mod.crend.dynamiccrosshair.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mod.crend.dynamiccrosshair.AutoHudCompat;
import mod.crend.dynamiccrosshair.DynamicCrosshairMod;
import mod.crend.dynamiccrosshair.api.Crosshair;
import mod.crend.dynamiccrosshair.api.InteractionType;
import mod.crend.dynamiccrosshair.component.CrosshairComponent;
import mod.crend.dynamiccrosshair.component.CrosshairHandler;
import mod.crend.dynamiccrosshair.config.CrosshairColor;
import mod.crend.dynamiccrosshair.config.CrosshairModifier;
import mod.crend.dynamiccrosshair.config.CrosshairStyle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;

public class CrosshairRenderer {
	public static boolean autoHudCompat = false;

	private static void setColor(final CrosshairColor color, boolean forcedCrosshair) {
		int argb = color.getColor();
		// convert ARGB hex to r, g, b, a floats
		float alpha = ((argb >> 24) & 0xFF) / 255.0f;
		if (autoHudCompat) {
			if (forcedCrosshair) alpha *= AutoHudCompat.getMinimumAlpha();
			else alpha *= AutoHudCompat.getAlpha();
		}
		RenderSystem.setShaderColor(((argb >> 16) & 0xFF) / 255.0f, ((argb >> 8) & 0xFF) / 255.0f, (argb & 0xFF) / 255.0f, alpha);
		if (color.forced() || autoHudCompat) {
			RenderSystem.defaultBlendFunc();
		} else {
			RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
		}
	}

	public static void preRender() {
		setColor(DynamicCrosshairMod.config.getColor(), false);
	}

	public static void fixCenteredCrosshairPre(DrawContext context, int x, int y) {
		/*
		   The vanilla crosshair is centered using integer coordinates. Since it is 15x15 pixels wide, this very
		   often means that the crosshair is not properly centered.
		   Calculate the actual center of the screen, and offset the rendering by the delta to the integer "center"
		   the game is trying to draw the crosshair at.
		 */
		Window window = MinecraftClient.getInstance().getWindow();
		double scale = window.getScaleFactor();
		double i = (window.getFramebufferWidth()) / scale;
		double j = (window.getFramebufferHeight()) / scale;
		double dx = (i - 15) / 2.0 - x;
		double dy = (j - 15) / 2.0 - y;
		context.getMatrices().push();
		context.getMatrices().translate(dx, dy, 0);
	}
	public static void fixCenteredCrosshairPost(DrawContext context) {
		context.getMatrices().pop();
	}

	public static void render(DrawContext context, int x, int y) {
		CrosshairComponent crosshair = new CrosshairComponent(CrosshairHandler.getActiveCrosshair());
		if (crosshair.hasStyle()) {
			CrosshairStyle crosshairStyle = crosshair.getCrosshairStyle();
			setColor(crosshairStyle.getColor(), false);
			context.drawGuiTexture(crosshairStyle.getStyle().getIdentifier(), x, y, 15, 15);
		} else if (CrosshairHandler.forceShowCrosshair) {
			CrosshairStyle crosshairStyle = CrosshairComponent.FORCE_CROSSHAIR.getCrosshairStyle();
			setColor(crosshairStyle.getColor(), true);
			context.drawGuiTexture(crosshairStyle.getStyle().getIdentifier(), x, y, 15, 15);
		}
		for (CrosshairModifier modifier : crosshair.getModifiers()) {
			setColor(modifier.getColor(), false);
			context.drawGuiTexture(modifier.getStyle().getIdentifier(), x, y, 15, 15);
		}
	}

	public static void postRender() {
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
	}
}
