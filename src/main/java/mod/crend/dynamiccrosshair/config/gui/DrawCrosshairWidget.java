//? if yacl {
package mod.crend.dynamiccrosshair.config.gui;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.debug.DebugProperties;
import dev.isxander.yacl3.gui.AbstractWidget;
import mod.crend.dynamiccrosshairapi.DynamicCrosshair;
import mod.crend.dynamiccrosshairapi.VersionUtils;import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

//? if <=1.21.4 {
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
//?}

//? if >1.21.8 {
/*import net.minecraft.client.gui.Click;
import net.minecraft.client.input.KeyInput;
*///?}

public class DrawCrosshairWidget extends AbstractWidget {
	boolean focused = false;
	boolean wasMouseOver = false;
	SelectCrosshairController control;

	//? if <1.20.5 {
	public static final Identifier BACKGROUND = DynamicCrosshair.identifier("textures/gui/sprites/crosshair-background.png");
	//?} else {
	/*public static final Identifier BACKGROUND = DynamicCrosshair.identifier("crosshair-background");
	 *///?}

	public DrawCrosshairWidget(Dimension<Integer> dim, SelectCrosshairController control) {
		super(dim);
		this.control = control;
		control.init(false);
	}

	public boolean isMouseOverCanvas(int mouseX, int mouseY) {
		return super.isMouseOver(mouseX, mouseY);
	}

	private int getHoveredPixelI(int mouseX) {
		return ((mouseX - getDimension().x()) / 3);
	}
	private int getHoveredPixelJ(int mouseY) {
		return ((mouseY - getDimension().y()) / 3);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		if (control.editStyle == null) return;
		int x = getDimension().x();
		int y = getDimension().y();
		//? if <=1.21.5 {
		context.getMatrices().push();
		context.getMatrices().translate(getDimension().x(), getDimension().y(), 105);
		context.getMatrices().scale(3, 3, 0);
		//?} else {
		/*context.getMatrices().pushMatrix();
		context.getMatrices().setTranslation(getDimension().x(), getDimension().y());
		context.getMatrices().scale(3, 3);
		*///?}
		//? if <=1.21.4 {
		if (DebugProperties.IMAGE_FILTERING) {
			GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MAG_FILTER, GlConst.GL_LINEAR);
			GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MIN_FILTER, GlConst.GL_LINEAR);
		}//?}
		//? if >=1.21.2 {
		/*context.drawGuiTexture(VersionUtils.getGuiTextured(), BACKGROUND, 0, 0, 15, 15);
		context.drawTexture(VersionUtils.getCrosshair(), control.editStyle.identifier, 0, 0, 0, 0, 15, 15, 15, 15);
		*///?} else if >=1.20.6 {
		/*context.drawGuiTexture(BACKGROUND, 0, 0, 15, 15);
		context.drawTexture(control.editStyle.identifier, 0, 0, 0, 0, 15, 15, 15, 15);
		*///?} else {
		context.drawTexture(BACKGROUND, 0, 0, 0, 0, 15, 15, 15, 15);
		context.drawTexture(control.editStyle.identifier, 0, 0, 0, 0, 15, 15, 15, 15);
		//?}
		context.getMatrices()./*? if <=1.21.5 {*/pop/*?} else {*//*popMatrix*//*?}*/();
		if (isMouseOverCanvas(mouseX, mouseY)) {
			if (!wasMouseOver) {
				wasMouseOver = true;
				GLFW.glfwSetInputMode(client.getWindow().getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
			}
			int hoveredPixelX = getHoveredPixelI(mouseX) * 3;
			int hoveredPixelY = getHoveredPixelJ(mouseY) * 3;
			context.fill(
					x + hoveredPixelX,
					y + hoveredPixelY,
					x + hoveredPixelX + 3,
					y + hoveredPixelY + 3,
					/*? if <=1.21.5 {*/110,/*?}*/
					0xCCCCCCCC
			);
		} else if (wasMouseOver) {
			wasMouseOver = false;
			GLFW.glfwSetInputMode(client.getWindow().getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
		}
	}

	@Override
	//? if <=1.21.8 {
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
	//?} else {
	/*public boolean mouseClicked(Click mouseButtonEvent, boolean doubleClick) {
		double mouseX = mouseButtonEvent.x();
		double mouseY = mouseButtonEvent.y();
		int button = mouseButtonEvent.button();
	*///?}
		if (isMouseOverCanvas((int) mouseX, (int) mouseY)
				&& (button == GLFW.GLFW_MOUSE_BUTTON_1 || button == GLFW.GLFW_MOUSE_BUTTON_2)
		) {
			int i = getHoveredPixelI((int) mouseX);
			int j = getHoveredPixelJ((int) mouseY);
			if (i < 0 || i >= 15 || j < 0 || j >= 15) return false;
			if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
				control.editImage.setRGB(i, j, 0xFFFFFFFF);
			} else {
				control.editImage.setRGB(i, j, 0x00000000);
			}
			control.registerTexture();
			return true;
		}
		return false;
	}

	@Override
	//? if <=1.21.8 {
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
	//?} else {
	/*public boolean mouseDragged(Click mouseButtonEvent, double deltaX, double deltaY) {
		double mouseX = mouseButtonEvent.x();
		double mouseY = mouseButtonEvent.y();
		int button = mouseButtonEvent.button();
	*///?}
		if (isMouseOverCanvas((int) mouseX, (int) mouseY)
				&& (button == GLFW.GLFW_MOUSE_BUTTON_1 || button == GLFW.GLFW_MOUSE_BUTTON_2)
		) {

			/* Algorithm from https://en.wikipedia.org/wiki/Bresenham's_line_algorithm#All_cases */
			int x0 = getHoveredPixelI((int) mouseX);
			int y0 = getHoveredPixelJ((int) mouseY);
			int x1 = getHoveredPixelI((int) (mouseX + deltaX));
			int y1 = getHoveredPixelJ((int) (mouseY + deltaY));

			int dx = Math.abs(x1 - x0);
			int dy = Math.abs(y1 - y0);
			int sx = (x0 < x1) ? 1 : -1;
			int sy = (y0 < y1) ? 1 : -1;
			int error = dx + dy;

			int color = (button == GLFW.GLFW_MOUSE_BUTTON_1 ? 0xFFFFFFFF : 0x00000000);

			while (true) {
				if (x0 < 0 || x0 >= 15 || y0 < 0 || y0 >= 15) break;
				control.editImage.setRGB(x0, y0, color);

				if (x0 == x1 && y0 == y1) break;

				int e2 = 2 * error;

				if (e2 >= dy) {
					if (x0 == x1) break;
					error = error + dy;
					x0 = x0 + sx;
				}

				if (e2 <= dx) {
					if (y0 == y1) break;
					error = error + dx;
					y0 = y0 + sy;
				}
			}
			control.registerTexture();
			return true;
		}
		return false;
	}

	@Override
	public void setFocused(boolean focused) {
		this.focused = focused;
	}

	@Override
	public boolean isFocused() {
		return focused;
	}
}
//?}
