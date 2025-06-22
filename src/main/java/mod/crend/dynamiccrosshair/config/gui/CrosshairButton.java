//? if yacl {
package mod.crend.dynamiccrosshair.config.gui;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import mod.crend.dynamiccrosshair.style.AbstractCrosshairStyle;
import mod.crend.dynamiccrosshairapi.VersionUtils;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

public class CrosshairButton extends AbstractWidget {

	protected boolean focused = false;
	protected boolean hovered = false;

	protected SelectCrosshairController control;
	protected AbstractCrosshairStyle style;

	public CrosshairButton(SelectCrosshairController control, AbstractCrosshairStyle style, Dimension<Integer> dim) {
		super(dim);
		this.control = control;
		this.style = style;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		hovered = isMouseOver(mouseX, mouseY);

		drawButtonRect(
				context,
				getDimension().x(),
				getDimension().y(),
				getDimension().xLimit(),
				getDimension().yLimit(),
				hovered || focused,
				!control.option().pendingValue().equals(style.identifier)
		);
		style.draw(context,
				//? if >=1.21.2
				/*VersionUtils.getGuiTextured(),*/
				getDimension().x() + 4, getDimension().y() + 4);
	}

	@Override
	public void setFocused(boolean focused) {
		this.focused = focused;
	}

	@Override
	public boolean isFocused() {
		return focused;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (!hovered) return false;

		if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
			control.option().requestSet(style.identifier);
		}

		return true;
	}
}
//?}