package mod.crend.dynamiccrosshair.render;

import mod.crend.libbamboo.controller.DecoratedEnumController;
import mod.crend.dynamiccrosshair.config.CrosshairConfigStyle;
import net.minecraft.client.gui.DrawContext;

public class CrosshairStyleRenderer implements DecoratedEnumController.Decorator<CrosshairConfigStyle> {
	public void render(CrosshairConfigStyle style, DrawContext context, int x, int y) {
		context.drawGuiTexture(style.getIdentifier(), x, y, 15, 15);
	}
}
