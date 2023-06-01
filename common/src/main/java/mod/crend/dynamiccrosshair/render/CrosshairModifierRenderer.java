package mod.crend.dynamiccrosshair.render;

import mod.crend.yaclx.controller.DecoratedEnumController;
import mod.crend.dynamiccrosshair.component.CrosshairHandler;
import mod.crend.dynamiccrosshair.config.CrosshairConfigModifier;
import net.minecraft.client.gui.DrawContext;

public class CrosshairModifierRenderer implements DecoratedEnumController.Decorator<CrosshairConfigModifier> {
	public void render(CrosshairConfigModifier style, DrawContext context, int x, int y) {
		context.drawTexture(CrosshairHandler.crosshairTexture, x, y, style.getX(), style.getY(), 15, 15);
	}
}
