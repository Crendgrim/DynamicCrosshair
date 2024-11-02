package mod.crend.dynamiccrosshair.config.gui;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import mod.crend.dynamiccrosshair.render.CrosshairRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class CrosshairStyleControllerElement extends ControllerWidget<CrosshairStyleController> {

	public CrosshairStyleControllerElement(CrosshairStyleController control, YACLScreen screen, Dimension<Integer> dim) {
		super(control, screen, dim);
	}

	protected void openCrosshairStyleScreen() {
		playDownSound();
		client.setScreen(new CrosshairStyleScreen(control, control.nestedYacl, screen));
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (!isMouseOver(mouseX, mouseY)
				|| (button != GLFW.GLFW_MOUSE_BUTTON_1 && button != GLFW.GLFW_MOUSE_BUTTON_2)
				|| !isAvailable()
		)
			return false;

		openCrosshairStyleScreen();

		return true;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (!focused)
			return false;

		switch (keyCode) {
			case InputUtil.GLFW_KEY_ENTER, InputUtil.GLFW_KEY_SPACE, InputUtil.GLFW_KEY_KP_ENTER -> openCrosshairStyleScreen();
			default -> {
				return false;
			}
		}

		return true;
	}

	@Override
	protected int getHoveredControlWidth() {
		return getUnhoveredControlWidth();
	}

	protected int getDecorationPadding() {
		return 15;
	}

	@Override
	protected void drawValueText(DrawContext context, int mouseX, int mouseY, float delta) {
		CrosshairRenderer.renderCrosshair(context,
				control.option().pendingValue().style,
				//? if >=1.21.2
				/*RenderLayer::getGuiTextured,*/
				this.getDimension().xLimit() - this.getXPadding() - this.getDecorationPadding() + 2,
				this.getDimension().y() + 2);
	}

	@Override
	protected int getControlWidth() {
		return super.getControlWidth() + this.getDecorationPadding();
	}
}
