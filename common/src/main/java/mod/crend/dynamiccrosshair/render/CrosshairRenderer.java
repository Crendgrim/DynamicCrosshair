package mod.crend.dynamiccrosshair.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.component.Crosshair;
import mod.crend.dynamiccrosshair.component.CrosshairHandler;
import mod.crend.dynamiccrosshair.config.CrosshairColor;
import mod.crend.dynamiccrosshair.config.CrosshairModifier;
import mod.crend.dynamiccrosshair.config.CrosshairStyle;
import net.minecraft.client.gui.DrawContext;

public class CrosshairRenderer {
	private static void setColor(final CrosshairColor color) {
		int argb = color.getColor();
		// convert ARGB hex to r, g, b, a floats
		RenderSystem.setShaderColor(((argb >> 16) & 0xFF) / 255.0f, ((argb >> 8) & 0xFF) / 255.0f, (argb & 0xFF) / 255.0f, ((argb >> 24) & 0xFF) / 255.0f);
		if (color.forced()) {
			RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
		} else {
			RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
		}
	}

	public static void preRender() {
		setColor(DynamicCrosshair.config.getColor());
	}

	public static void render(DrawContext context, int x, int y) {
		Crosshair crosshair = CrosshairHandler.getActiveCrosshair();
		if (crosshair.hasStyle()) {
			CrosshairStyle crosshairStyle = crosshair.getCrosshairStyle();
			setColor(crosshairStyle.getColor());
			context.drawTexture(CrosshairHandler.crosshairTexture, x, y, crosshairStyle.getStyle().getX(), crosshairStyle.getStyle().getY(), 15, 15);
		}
		for (CrosshairModifier modifier : crosshair.getModifiers()) {
			setColor(modifier.getColor());
			context.drawTexture(CrosshairHandler.crosshairTexture, x, y, modifier.getStyle().getX(), modifier.getStyle().getY(), 15, 15);
		}
	}

	public static void postRender() {
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
	}
}
