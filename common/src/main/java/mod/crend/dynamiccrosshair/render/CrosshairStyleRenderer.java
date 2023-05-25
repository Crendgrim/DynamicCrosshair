package mod.crend.dynamiccrosshair.render;

import mod.crend.yaclx.controller.annotation.Decorate;
import mod.crend.dynamiccrosshair.component.CrosshairHandler;
import mod.crend.dynamiccrosshair.config.CrosshairConfigStyle;
import net.minecraft.client.gui.DrawContext;

public class CrosshairStyleRenderer implements Decorate.Decorator<CrosshairConfigStyle> {
	public void render(CrosshairConfigStyle style, DrawContext context, int x, int y) {
		context.drawTexture(CrosshairHandler.crosshairTexture, x, y, style.getX(), style.getY(), 15, 15);
	}
}
