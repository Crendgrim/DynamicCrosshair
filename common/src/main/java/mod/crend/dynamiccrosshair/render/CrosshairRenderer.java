package mod.crend.dynamiccrosshair.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mod.crend.dynamiccrosshair.AutoHudCompat;
import mod.crend.dynamiccrosshair.DynamicCrosshairMod;
import mod.crend.dynamiccrosshair.component.CrosshairComponent;
import mod.crend.dynamiccrosshair.component.CrosshairHandler;
import mod.crend.dynamiccrosshair.style.CrosshairStyle;
import mod.crend.dynamiccrosshair.style.CrosshairStyleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;

public class CrosshairRenderer {
	public static boolean autoHudCompat = false;

	private static void setColor(int argb, boolean enableBlend, boolean forcedCrosshair) {
		// convert ARGB hex to r, g, b, a floats
		float alpha = ((argb >> 24) & 0xFF) / 255.0f;
		if (autoHudCompat) {
			if (forcedCrosshair) alpha *= AutoHudCompat.getMinimumAlpha();
			else alpha *= AutoHudCompat.getAlpha();
		}
		RenderSystem.setShaderColor(((argb >> 16) & 0xFF) / 255.0f, ((argb >> 8) & 0xFF) / 255.0f, (argb & 0xFF) / 255.0f, alpha);
		if (!enableBlend || autoHudCompat) {
			RenderSystem.defaultBlendFunc();
		} else {
			RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
		}
	}

	public static void preRender() {
		CrosshairStyle defaultStyle = CrosshairHandler.getDefaultCrosshair();
		setColor(defaultStyle.color(), defaultStyle.enableBlend(), false);
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

	public static void renderCrosshair(DrawContext context, Identifier style, int x, int y) {
		CrosshairStyleManager.INSTANCE.get(style).draw(context, x, y);
	}

	public static void render(DrawContext context, int x, int y) {
		CrosshairComponent crosshair = CrosshairHandler.getActiveCrosshair();
		CrosshairStyle primaryStyle = crosshair.getPrimaryStyle();
		CrosshairStyle secondaryStyle = crosshair.getSecondaryStyle();
		if (primaryStyle != null) {
			setColor(primaryStyle.color(), primaryStyle.enableBlend(), false);
			renderCrosshair(context, primaryStyle.identifier(), x, y);
		} else if (CrosshairHandler.forceShowCrosshair && (secondaryStyle == null || secondaryStyle.isModifier())) {
			CrosshairStyle crosshairStyle = CrosshairComponent.FORCE_CROSSHAIR.getPrimaryStyle();
			setColor(crosshairStyle.color(), crosshairStyle.enableBlend(), true);
			renderCrosshair(context, crosshairStyle.identifier(), x, y);
		}
		if (secondaryStyle != null) {
			setColor(secondaryStyle.color(), secondaryStyle.enableBlend(), false);
			renderCrosshair(context, secondaryStyle.identifier(), x, y);
		}
		for (CrosshairStyle modifier : crosshair.getModifiers()) {
			setColor(modifier.color(), modifier.enableBlend(), false);
			renderCrosshair(context, modifier.identifier(), x, y);
		}
	}

	public static void postRender() {
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
	}
}
