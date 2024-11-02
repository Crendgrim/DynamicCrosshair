package mod.crend.dynamiccrosshair.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mod.crend.dynamiccrosshair.AutoHudCompat;
import mod.crend.dynamiccrosshair.DynamicCrosshairMod;
import mod.crend.dynamiccrosshair.component.CrosshairComponent;
import mod.crend.dynamiccrosshair.component.CrosshairHandler;
import mod.crend.dynamiccrosshair.style.CrosshairStyle;
import mod.crend.dynamiccrosshair.style.CrosshairStyleManager;
import mod.crend.dynamiccrosshair.style.CrosshairStyledPart;
import mod.crend.libbamboo.render.CustomFramebufferRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Function;

public class CrosshairRenderer {
	public static boolean autoHudCompat = false;

	private static void setColor(int argb, boolean enableBlend) {
		// convert ARGB hex to r, g, b, a floats
		RenderSystem.setShaderColor(((argb >> 16) & 0xFF) / 255.0f, ((argb >> 8) & 0xFF) / 255.0f, (argb & 0xFF) / 255.0f, ((argb >> 24) & 0xFF) / 255.0f);
		if (!enableBlend || autoHudCompat) {
			RenderSystem.defaultBlendFunc();
		} else {
			RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
		}
	}

	public static void preRender() {
		CrosshairStyle defaultStyle = CrosshairHandler.getDefaultCrosshair();
		setColor(defaultStyle.color(), defaultStyle.enableBlend());
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

	public static void renderCrosshair(
			DrawContext context,
			Identifier style,
			//? if >=1.21.2
			/*Function<Identifier, RenderLayer> renderLayer,*/
			int x, int y
	) {
		CrosshairStyleManager.INSTANCE.get(style).draw(context,
				//? if >=1.21.2
				/*renderLayer,*/
				x, y
		);
	}
	public static void renderCrosshair(DrawContext context, CrosshairStyle style, int x, int y) {
		setColor(style.color(), style.enableBlend());
		renderCrosshair(
				context,
				style.identifier(),
				//? if >=1.21.2
				/*style.enableBlend() ? RenderLayer::getCrosshair : RenderLayer::getGuiTextured,*/
				x, y
		);

	}

	private static void preRenderHalf() {
		RenderSystem.defaultBlendFunc();
		CustomFramebufferRenderer.init();
	}
	private static void postRenderHalf(DrawContext context, boolean blend) {
		RenderSystem.enableBlend();
		if (blend) {
			RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
		} else {
			RenderSystem.defaultBlendFunc();
		}
		CustomFramebufferRenderer.draw(context);
		RenderSystem.defaultBlendFunc();
	}

	private static void renderStyles(DrawContext context, int x, int y, List<CrosshairStyledPart> styles) {
		for (var part : styles) {
			if (autoHudCompat) {
				AutoHudCompat.renderCrosshair(context, part.part(), part.style(), x, y);
			} else {
				renderCrosshair(context, part.style(), x, y);
			}
		}
	}



	public static void render(DrawContext context, int x, int y) {
		context.draw();

		CrosshairComponent crosshair = CrosshairHandler.getActiveCrosshair();

		// First, draw all styles with "enable blend" together.
		preRenderHalf();
		renderStyles(context, x, y, crosshair.getStylesWithBlend());
		postRenderHalf(context, true);

		// Then, draw the others on top.
		preRenderHalf();
		renderStyles(context, x, y, crosshair.getStylesWithoutBlend());
		postRenderHalf(context, false);
	}

	public static void postRender() {
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		RenderSystem.defaultBlendFunc();
	}

	public static void wrapRender(DrawContext context, int x, int y, Runnable originalRenderCall, Runnable noBlendRenderCall) {
		if (!CrosshairHandler.forceShowCrosshair && !CrosshairHandler.shouldShowCrosshair()) return;

		// Set up color first (and clean it up after) so that we can tint the vanilla crosshair even when dynamic identifier is off
		preRender();
		if (DynamicCrosshairMod.config.isFixCenteredCrosshair()) {
			fixCenteredCrosshairPre(context, x, y);
		}

		if (DynamicCrosshairMod.config.isDynamicCrosshairStyle()) {
			render(context, x, y);
		} else if (!CrosshairHandler.getDefaultCrosshair().enableBlend()) {
			noBlendRenderCall.run();
			context.draw();
		} else {
			originalRenderCall.run();
		}

		if (DynamicCrosshairMod.config.isFixCenteredCrosshair()) {
			fixCenteredCrosshairPost(context);
		}

		postRender();
	}
}
