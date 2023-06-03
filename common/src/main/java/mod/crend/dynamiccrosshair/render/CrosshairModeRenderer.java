package mod.crend.dynamiccrosshair.render;

import dev.isxander.yacl.gui.ImageRenderer;
import mod.crend.dynamiccrosshair.config.CrosshairMode;
import mod.crend.yaclx.auto.annotation.DescriptionImage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class CrosshairModeRenderer implements ImageRenderer {

	Text text;
	public CrosshairModeRenderer(Text text) {
		this.text = text;
	}

	@Override
	public int render(DrawContext graphics, int x, int y, int renderWidth) {
		graphics.drawText(MinecraftClient.getInstance().textRenderer, text, x, y, 0xFF8000, true);
		return 10;
	}

	@Override
	public void close() { }

	public static class CrosshairModeRendererFactory implements DescriptionImage.DescriptionImageRendererFactory<CrosshairMode> {
		@Override
		public CrosshairModeRenderer create(CrosshairMode value) {
			return new CrosshairModeRenderer(value.getDisplayName());
		}
	}
}
