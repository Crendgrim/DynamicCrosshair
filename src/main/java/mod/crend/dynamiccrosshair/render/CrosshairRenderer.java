package mod.crend.dynamiccrosshair.render;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.crend.dynamiccrosshair.compat.AutoHudCompat;
import mod.crend.dynamiccrosshair.DynamicCrosshairMod;
import mod.crend.dynamiccrosshair.component.CrosshairComponent;
import mod.crend.dynamiccrosshair.component.CrosshairHandler;
import mod.crend.dynamiccrosshair.style.CrosshairStyle;
import mod.crend.dynamiccrosshair.style.CrosshairStyleManager;
import mod.crend.dynamiccrosshair.style.CrosshairStyledPart;
import mod.crend.dynamiccrosshairapi.VersionUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Function;

//? if <=1.21.4 {
import mod.crend.libbamboo.render.CustomFramebufferRenderer;
import com.mojang.blaze3d.platform.GlStateManager;
//?}
//? if >1.21.5 {
/*import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gl.RenderPipelines;
*///?}

public class CrosshairRenderer {
	public static boolean autoHudCompat = false;

	private static void setColor(int argb, boolean enableBlend) {
		// convert ARGB hex to r, g, b, a floats
		//? if <1.21.6
		RenderSystem.setShaderColor(((argb >> 16) & 0xFF) / 255.0f, ((argb >> 8) & 0xFF) / 255.0f, (argb & 0xFF) / 255.0f, ((argb >> 24) & 0xFF) / 255.0f);
		//? if <=1.21.4 {
		if (!enableBlend || autoHudCompat) {
			RenderSystem.defaultBlendFunc();
		} else {
			RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
		}
		//?}
		//? if >1.21.5 {
		//?}
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
		//? if <1.21.6 {
		context.getMatrices().push();
		context.getMatrices().translate(dx, dy, 0);
		//?} else {
		/*context.getMatrices().pushMatrix();
		context.getMatrices().setTranslation((float) dx, (float) dy);
		*///?}
	}
	public static void fixCenteredCrosshairPost(DrawContext context) {
		context.getMatrices()./*? if <=1.21.5 {*/pop/*?} else {*//*popMatrix*//*?}*/();
	}

	public static void renderCrosshair(
			DrawContext context,
			Identifier style,
			//? if >=1.21.6 {
			/*RenderPipeline renderLayer,
			*///?} else if >=1.21.2
			/*Function<Identifier, RenderLayer> renderLayer,*/
			int x, int y
			//? if >1.21.5
			/*, int color*/
	) {
		CrosshairStyleManager.INSTANCE.get(style).draw(context,
				//? if >=1.21.2
				/*renderLayer,*/
				x, y
				//? if >1.21.5
				/*, color*/
		);
	}
	public static void renderCrosshair(DrawContext context, CrosshairStyle style, int x, int y) {
		setColor(style.color(), style.enableBlend());
		renderCrosshair(
				context,
				style.identifier(),
				//? if >=1.21.2
				/*style.enableBlend() ? VersionUtils.getCrosshair() : VersionUtils.getGuiTextured(),*/
				x, y
				//? if >1.21.5
				/*, style.color()*/
		);
	}

	private static void preRenderHalf() {
		//? if <=1.21.4 {
		RenderSystem.defaultBlendFunc();
		CustomFramebufferRenderer.init();
		//?}
	}
	private static void postRenderHalf(DrawContext context, boolean blend) {
		//? if <=1.21.4 {
		RenderSystem.enableBlend();
		if (blend) {
			RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
		} else {
			RenderSystem.defaultBlendFunc();
		}
		CustomFramebufferRenderer.draw(context);
		//?}
		//? if <=1.21.5
		context.draw();
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
		//? if <=1.21.5
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
		//? if <1.21.6
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		//? if <1.21.2 {
		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
		//?} else if <=1.21.4
		/*RenderSystem.defaultBlendFunc();*/
	}

	//? if <=1.21.5 {
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
			//? if <1.21.6
			context.draw();
		} else {
			originalRenderCall.run();
		}

		if (DynamicCrosshairMod.config.isFixCenteredCrosshair()) {
			fixCenteredCrosshairPost(context);
		}

		postRender();
	}
	//?} else {
	/*public static void wrapRender(DrawContext context, int x, int y, Identifier oTexture, int oWidth, int oHeight, Runnable originalRenderCall) {
		if (!CrosshairHandler.forceShowCrosshair && !CrosshairHandler.shouldShowCrosshair()) return;

		if (DynamicCrosshairMod.config.isFixCenteredCrosshair()) {
			fixCenteredCrosshairPre(context, x, y);
		}

		if (DynamicCrosshairMod.config.isDynamicCrosshairStyle()) {
			render(context, x, y);
		} else if (CrosshairHandler.getDefaultCrosshair().color() != 0xFFFFFFFF) {
			context.drawGuiTexture(
					CrosshairHandler.getDefaultCrosshair().enableBlend() ? VersionUtils.getCrosshair() : VersionUtils.getGuiTextured(),
					oTexture, x, y, oWidth, oHeight,
					CrosshairHandler.getDefaultCrosshair().color()
			);
		} else if (!CrosshairHandler.getDefaultCrosshair().enableBlend()) {
			context.drawGuiTexture(VersionUtils.getGuiTextured(), oTexture, x, y, oWidth, oHeight);
		} else {
			// if we change neither colour nor style, use the original call instead of a direct drawGuiTexture invocation in case some other mod wraps this too
			originalRenderCall.run();
		}

		if (DynamicCrosshairMod.config.isFixCenteredCrosshair()) {
			fixCenteredCrosshairPost(context);
		}

		postRender();
	}
	*///?}
}
