package mod.crend.dynamiccrosshair.config.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.DynamicCrosshairMod;
import mod.crend.dynamiccrosshair.style.CrosshairStyleManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class PreviewCrosshairWidget extends AbstractWidget {
	public static final Identifier BACKGROUND = new Identifier(DynamicCrosshair.MOD_ID, "preview-background");

	CrosshairStyleController control;

	public PreviewCrosshairWidget(Dimension<Integer> dim, CrosshairStyleController control) {
		super(dim);
		this.control = control;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		context.drawGuiTexture(BACKGROUND, getDimension().x(), getDimension().y(), getDimension().width(), getDimension().height());
		RenderSystem.enableBlend();
		int color = control.overrideColorOption.pendingValue() ? control.customColorOption.pendingValue().getRGB() : DynamicCrosshairMod.config.getDefaultStyle().color();
		setColor(color, control.enableBlendOption.pendingValue());
		CrosshairStyleManager.INSTANCE.get(control.styleOption.pendingValue()).draw(context, getDimension().centerX() - 7, getDimension().centerY() - 7);
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		RenderSystem.defaultBlendFunc();
	}

	private static void setColor(int argb, boolean enableBlend) {
		// convert ARGB hex to r, g, b, a floats
		float alpha = ((argb >> 24) & 0xFF) / 255.0f;
		RenderSystem.setShaderColor(((argb >> 16) & 0xFF) / 255.0f, ((argb >> 8) & 0xFF) / 255.0f, (argb & 0xFF) / 255.0f, alpha);
		if (enableBlend) {
			RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
		} else {
			RenderSystem.defaultBlendFunc();
		}
	}

	@Override
	public void setFocused(boolean focused) {
	}

	@Override
	public boolean isFocused() {
		return false;
	}
}
