//? if yacl {
package mod.crend.dynamiccrosshair.config.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import mod.crend.dynamiccrosshair.DynamicCrosshairMod;
import mod.crend.dynamiccrosshair.style.CrosshairStyleManager;
import mod.crend.dynamiccrosshairapi.DynamicCrosshair;
import mod.crend.dynamiccrosshairapi.VersionUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

//? if <=1.21.4
import com.mojang.blaze3d.platform.GlStateManager;

public class PreviewCrosshairWidget extends AbstractWidget {
	//? if <1.20.5 {
	public static final Identifier BACKGROUND = DynamicCrosshair.identifier("textures/gui/sprites/preview-background.png");
	//?} else {
	/*public static final Identifier BACKGROUND = DynamicCrosshair.identifier("preview-background");
	 *///?}

	CrosshairStyleController control;

	public PreviewCrosshairWidget(Dimension<Integer> dim, CrosshairStyleController control) {
		super(dim);
		this.control = control;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		//? if >=1.21.2 {
		/*context.drawGuiTexture(VersionUtils.getGuiTextured(), BACKGROUND, getDimension().x(), getDimension().y(), getDimension().width(), getDimension().height());
		//? if <=1.21.5
		context.draw();
		*///?} else if >=1.20.6 {
		/*context.drawGuiTexture(BACKGROUND, getDimension().x(), getDimension().y(), getDimension().width(), getDimension().height());
		*///?} else {
		context.drawTexture(BACKGROUND, getDimension().x(), getDimension().y(), 0, 0, getDimension().width(), getDimension().height(), getDimension().width(), getDimension().height());
		//?}
		//? if <=1.21.4
		RenderSystem.enableBlend();
		int color = control.overrideColorOption.pendingValue() ? control.customColorOption.pendingValue().getRGB() : DynamicCrosshairMod.config.getDefaultStyle().color();
		setColor(color, control.enableBlendOption.pendingValue());
		CrosshairStyleManager.INSTANCE.get(control.styleOption.pendingValue()).draw(
				context,
				//? if >=1.21.2
				/*control.enableBlendOption.pendingValue() ? VersionUtils.getCrosshair() : VersionUtils.getGuiTextured(),*/
				getDimension().centerX() - 7,
				getDimension().centerY() - 7
				//? if >1.21.5
				/*, color*/
		);
		//? if <=1.21.5 {
		//? if >=1.21.2
		/*context.draw();*/
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		//?}
		//? if <=1.21.4
		RenderSystem.defaultBlendFunc();
	}

	private static void setColor(int argb, boolean enableBlend) {
		// convert ARGB hex to r, g, b, a floats
		float alpha = ((argb >> 24) & 0xFF) / 255.0f;
		//? if <=1.21.5
		RenderSystem.setShaderColor(((argb >> 16) & 0xFF) / 255.0f, ((argb >> 8) & 0xFF) / 255.0f, (argb & 0xFF) / 255.0f, alpha);
		//? if <=1.21.4 {
		if (enableBlend) {
			RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
		} else {
			RenderSystem.defaultBlendFunc();
		}
		//?}
	}

	@Override
	public void setFocused(boolean focused) {
	}

	@Override
	public boolean isFocused() {
		return false;
	}
}
//?}
